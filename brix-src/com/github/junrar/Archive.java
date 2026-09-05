package com.github.junrar;

import com.github.junrar.crypt.Rijndael;
import com.github.junrar.exception.BadRarArchiveException;
import com.github.junrar.exception.CorruptHeaderException;
import com.github.junrar.exception.CrcErrorException;
import com.github.junrar.exception.HeaderNotInArchiveException;
import com.github.junrar.exception.InitDeciphererFailedException;
import com.github.junrar.exception.MainHeaderNullException;
import com.github.junrar.exception.NotRarArchiveException;
import com.github.junrar.exception.RarException;
import com.github.junrar.exception.UnsupportedRarEncryptedException;
import com.github.junrar.exception.UnsupportedRarV5Exception;
import com.github.junrar.io.RawDataIo;
import com.github.junrar.io.SeekableReadOnlyByteChannel;
import com.github.junrar.rarfile.AVHeader;
import com.github.junrar.rarfile.BaseBlock;
import com.github.junrar.rarfile.BlockHeader;
import com.github.junrar.rarfile.CommentHeader;
import com.github.junrar.rarfile.EAHeader;
import com.github.junrar.rarfile.EndArcHeader;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.rarfile.MacInfoHeader;
import com.github.junrar.rarfile.MainHeader;
import com.github.junrar.rarfile.MarkHeader;
import com.github.junrar.rarfile.ProtectHeader;
import com.github.junrar.rarfile.RARVersion;
import com.github.junrar.rarfile.SignHeader;
import com.github.junrar.rarfile.SubBlockHeader;
import com.github.junrar.rarfile.SubBlockHeaderType;
import com.github.junrar.rarfile.UnixOwnersHeader;
import com.github.junrar.rarfile.UnrarHeadertype;
import com.github.junrar.unpack.ComprDataIO;
import com.github.junrar.unpack.Unpack;
import com.github.junrar.volume.FileVolumeManager;
import com.github.junrar.volume.InputStreamVolumeManager;
import com.github.junrar.volume.Volume;
import com.github.junrar.volume.VolumeManager;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.crypto.Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class Archive implements Closeable, Iterable<FileHeader> {
    private static final int MAX_HEADER_SIZE = 20971520;
    private SeekableReadOnlyByteChannel channel;
    private int currentHeaderIndex;
    private final ComprDataIO dataIO;
    private final List<BaseBlock> headers;
    private MarkHeader markHead;
    private MainHeader newMhd;
    private FileHeader nextFileHeader;
    private String password;
    private long totalPackedRead;
    private long totalPackedSize;
    private Unpack unpack;
    private final UnrarCallback unrarCallback;
    private Volume volume;
    private VolumeManager volumeManager;
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) Archive.class);
    private static final int PIPE_BUFFER_SIZE = ((Integer) getPropertyAs("junrar.extractor.buffer-size", new Function() { // from class: com.github.junrar.Archive$$ExternalSyntheticLambda0
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(Integer.parseInt((String) obj));
        }
    }, 32768)).intValue();
    private static final boolean USE_EXECUTOR = ((Boolean) getPropertyAs("junrar.extractor.use-executor", new Function() { // from class: com.github.junrar.Archive$$ExternalSyntheticLambda1
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Boolean.valueOf(Boolean.parseBoolean((String) obj));
        }
    }, true)).booleanValue();

    public Archive(VolumeManager volumeManager, UnrarCallback unrarCallback, String password) throws Exception {
        this.headers = new ArrayList();
        this.markHead = null;
        this.newMhd = null;
        this.totalPackedSize = 0L;
        this.totalPackedRead = 0L;
        this.volumeManager = volumeManager;
        this.unrarCallback = unrarCallback;
        this.password = password;
        try {
            setVolume(this.volumeManager.nextVolume(this, null));
            this.dataIO = new ComprDataIO(this);
        } catch (RarException | IOException e) {
            try {
                close();
            } catch (IOException e2) {
                logger.error("Failed to close the archive after an internal error!");
            }
            throw e;
        }
    }

    public Archive(File firstVolume) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), (UnrarCallback) null, (String) null);
    }

    public Archive(File firstVolume, UnrarCallback unrarCallback) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), unrarCallback, (String) null);
    }

    public Archive(File firstVolume, String password) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), (UnrarCallback) null, password);
    }

    public Archive(File firstVolume, UnrarCallback unrarCallback, String password) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), unrarCallback, password);
    }

    public Archive(InputStream rarAsStream) throws RarException, IOException {
        this(new InputStreamVolumeManager(rarAsStream), (UnrarCallback) null, (String) null);
    }

    public Archive(InputStream rarAsStream, UnrarCallback unrarCallback) throws RarException, IOException {
        this(new InputStreamVolumeManager(rarAsStream), unrarCallback, (String) null);
    }

    public Archive(InputStream rarAsStream, String password) throws RarException, IOException {
        this(new InputStreamVolumeManager(rarAsStream), (UnrarCallback) null, password);
    }

    public Archive(InputStream rarAsStream, UnrarCallback unrarCallback, String password) throws RarException, IOException {
        this(new InputStreamVolumeManager(rarAsStream), unrarCallback, password);
    }

    private void setChannel(SeekableReadOnlyByteChannel channel, long length) throws RarException, IOException {
        this.totalPackedSize = 0L;
        this.totalPackedRead = 0L;
        close();
        this.channel = channel;
        try {
            readHeaders(length);
        } catch (BadRarArchiveException e) {
            e = e;
            logger.warn("exception in archive constructor maybe file is encrypted, corrupt or support not yet implemented", (Throwable) e);
            throw e;
        } catch (CorruptHeaderException e2) {
            e = e2;
            logger.warn("exception in archive constructor maybe file is encrypted, corrupt or support not yet implemented", (Throwable) e);
            throw e;
        } catch (UnsupportedRarEncryptedException e3) {
            e = e3;
            logger.warn("exception in archive constructor maybe file is encrypted, corrupt or support not yet implemented", (Throwable) e);
            throw e;
        } catch (UnsupportedRarV5Exception e4) {
            e = e4;
            logger.warn("exception in archive constructor maybe file is encrypted, corrupt or support not yet implemented", (Throwable) e);
            throw e;
        } catch (Exception e5) {
            logger.warn("exception in archive constructor maybe file is encrypted, corrupt or support not yet implemented", (Throwable) e5);
        }
        for (BaseBlock block : this.headers) {
            if (block.getHeaderType() == UnrarHeadertype.FileHeader) {
                this.totalPackedSize += ((FileHeader) block).getFullPackSize();
            }
        }
        if (this.unrarCallback != null) {
            this.unrarCallback.volumeProgressChanged(this.totalPackedRead, this.totalPackedSize);
        }
    }

    public void bytesReadRead(int count) {
        if (count > 0) {
            this.totalPackedRead += (long) count;
            if (this.unrarCallback != null) {
                this.unrarCallback.volumeProgressChanged(this.totalPackedRead, this.totalPackedSize);
            }
        }
    }

    public SeekableReadOnlyByteChannel getChannel() {
        return this.channel;
    }

    public List<BaseBlock> getHeaders() {
        return new ArrayList(this.headers);
    }

    public List<FileHeader> getFileHeaders() {
        List<FileHeader> list = new ArrayList<>();
        for (BaseBlock block : this.headers) {
            if (block.getHeaderType().equals(UnrarHeadertype.FileHeader)) {
                list.add((FileHeader) block);
            }
        }
        return list;
    }

    public FileHeader nextFileHeader() {
        int n = this.headers.size();
        while (this.currentHeaderIndex < n) {
            List<BaseBlock> list = this.headers;
            int i = this.currentHeaderIndex;
            this.currentHeaderIndex = i + 1;
            BaseBlock block = list.get(i);
            if (block.getHeaderType() == UnrarHeadertype.FileHeader) {
                return (FileHeader) block;
            }
        }
        return null;
    }

    public UnrarCallback getUnrarCallback() {
        return this.unrarCallback;
    }

    public boolean isEncrypted() throws RarException {
        if (this.newMhd != null) {
            return this.newMhd.isEncrypted();
        }
        throw new MainHeaderNullException();
    }

    public boolean isPasswordProtected() throws RarException {
        if (isEncrypted()) {
            return true;
        }
        return getFileHeaders().stream().anyMatch(new Predicate() { // from class: com.github.junrar.Archive$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((FileHeader) obj).isEncrypted();
            }
        });
    }

    private void readHeaders(long fileLength) throws RarException, IOException {
        EndArcHeader endArcHead;
        Archive archive;
        Archive archive2 = this;
        byte[] bArr = null;
        archive2.markHead = null;
        archive2.newMhd = null;
        archive2.headers.clear();
        archive2.currentHeaderIndex = 0;
        int toRead = 0;
        Set<Long> processedPositions = new HashSet<>();
        while (true) {
            RawDataIo rawData = new RawDataIo(archive2.channel);
            byte[] baseBlockBuffer = safelyAllocate(7L, MAX_HEADER_SIZE);
            if (archive2.newMhd != null && archive2.newMhd.isEncrypted()) {
                byte[] salt = new byte[8];
                rawData.readFully(salt, 8);
                try {
                    Cipher cipher = Rijndael.buildDecipherer(archive2.password, salt);
                    rawData.setCipher(cipher);
                } catch (Exception e) {
                    throw new InitDeciphererFailedException(e);
                }
            }
            long position = archive2.channel.getPosition();
            if (position < fileLength) {
                int size = rawData.readFully(baseBlockBuffer, baseBlockBuffer.length);
                if (size != 0) {
                    BaseBlock block = new BaseBlock(baseBlockBuffer);
                    block.setPositionInFile(position);
                    UnrarHeadertype headerType = block.getHeaderType();
                    if (headerType == null) {
                        logger.warn("unknown block header!");
                        throw new CorruptHeaderException();
                    }
                    switch (headerType) {
                        case MarkHeader:
                            archive2.markHead = new MarkHeader(block);
                            if (!archive2.markHead.isSignature()) {
                                if (archive2.markHead.getVersion() == RARVersion.V5) {
                                    logger.warn("Support for rar version 5 is not yet implemented!");
                                    throw new UnsupportedRarV5Exception();
                                }
                                throw new BadRarArchiveException();
                            }
                            if (!archive2.markHead.isValid()) {
                                throw new CorruptHeaderException("Invalid Mark Header");
                            }
                            archive2.headers.add(archive2.markHead);
                            archive = archive2;
                            break;
                            break;
                        case MainHeader:
                            int toRead2 = block.hasEncryptVersion() ? 7 : 6;
                            byte[] mainbuff = safelyAllocate(toRead2, MAX_HEADER_SIZE);
                            rawData.readFully(mainbuff, mainbuff.length);
                            MainHeader mainhead = new MainHeader(block, mainbuff);
                            archive2.headers.add(mainhead);
                            archive2.newMhd = mainhead;
                            archive = archive2;
                            toRead = toRead2;
                            continue;
                            archive2 = archive;
                            bArr = null;
                            break;
                        case SignHeader:
                            byte[] signBuff = safelyAllocate(8, MAX_HEADER_SIZE);
                            rawData.readFully(signBuff, signBuff.length);
                            SignHeader signHead = new SignHeader(block, signBuff);
                            archive2.headers.add(signHead);
                            archive = archive2;
                            toRead = 8;
                            continue;
                            archive2 = archive;
                            bArr = null;
                            break;
                        case AvHeader:
                            byte[] avBuff = safelyAllocate(7, MAX_HEADER_SIZE);
                            rawData.readFully(avBuff, avBuff.length);
                            AVHeader avHead = new AVHeader(block, avBuff);
                            archive2.headers.add(avHead);
                            archive = archive2;
                            toRead = 7;
                            continue;
                            archive2 = archive;
                            bArr = null;
                            break;
                        case CommHeader:
                            byte[] commBuff = safelyAllocate(6, MAX_HEADER_SIZE);
                            rawData.readFully(commBuff, commBuff.length);
                            CommentHeader commHead = new CommentHeader(block, commBuff);
                            archive2.headers.add(commHead);
                            long newpos = ((long) commHead.getHeaderSize(archive2.isEncrypted())) + commHead.getPositionInFile();
                            archive2.channel.setPosition(newpos);
                            if (processedPositions.contains(Long.valueOf(newpos))) {
                                throw new BadRarArchiveException();
                            }
                            processedPositions.add(Long.valueOf(newpos));
                            archive = archive2;
                            toRead = 6;
                            continue;
                            archive2 = archive;
                            bArr = null;
                            break;
                            break;
                        case EndArcHeader:
                            int toRead3 = 0;
                            if (block.hasArchiveDataCRC()) {
                                toRead3 = 0 + 4;
                            }
                            if (block.hasVolumeNumber()) {
                                toRead3 += 2;
                            }
                            if (toRead3 > 0) {
                                byte[] endArchBuff = safelyAllocate(toRead3, MAX_HEADER_SIZE);
                                rawData.readFully(endArchBuff, endArchBuff.length);
                                endArcHead = new EndArcHeader(block, endArchBuff);
                            } else {
                                endArcHead = new EndArcHeader(block, bArr);
                            }
                            if (!archive2.newMhd.isMultiVolume() && !endArcHead.isValid()) {
                                throw new CorruptHeaderException("Invalid End Archive Header");
                            }
                            archive2.headers.add(endArcHead);
                            return;
                        default:
                            byte[] blockHeaderBuffer = safelyAllocate(4L, MAX_HEADER_SIZE);
                            rawData.readFully(blockHeaderBuffer, blockHeaderBuffer.length);
                            BlockHeader blockHead = new BlockHeader(block, blockHeaderBuffer);
                            switch (blockHead.getHeaderType()) {
                                case NewSubHeader:
                                case FileHeader:
                                    archive = archive2;
                                    int toRead4 = (blockHead.getHeaderSize(false) - 7) - 4;
                                    byte[] fileHeaderBuffer = safelyAllocate(toRead4, MAX_HEADER_SIZE);
                                    try {
                                        rawData.readFully(fileHeaderBuffer, fileHeaderBuffer.length);
                                        FileHeader fh = new FileHeader(blockHead, fileHeaderBuffer);
                                        archive.headers.add(fh);
                                        long newpos2 = fh.getFullPackSize() + fh.getPositionInFile() + ((long) fh.getHeaderSize(archive.isEncrypted()));
                                        archive.channel.setPosition(newpos2);
                                        if (processedPositions.contains(Long.valueOf(newpos2))) {
                                            throw new BadRarArchiveException();
                                        }
                                        processedPositions.add(Long.valueOf(newpos2));
                                        toRead = toRead4;
                                        continue;
                                        archive2 = archive;
                                        bArr = null;
                                    } catch (EOFException e2) {
                                        throw new CorruptHeaderException("Unexpected end of file");
                                    }
                                    break;
                                case ProtectHeader:
                                    archive = archive2;
                                    int toRead5 = (blockHead.getHeaderSize(false) - 7) - 4;
                                    byte[] protectHeaderBuffer = safelyAllocate(toRead5, MAX_HEADER_SIZE);
                                    rawData.readFully(protectHeaderBuffer, protectHeaderBuffer.length);
                                    ProtectHeader ph = new ProtectHeader(blockHead, protectHeaderBuffer);
                                    long newpos3 = ph.getDataSize() + ph.getPositionInFile() + ((long) ph.getHeaderSize(archive.isEncrypted()));
                                    archive.channel.setPosition(newpos3);
                                    if (processedPositions.contains(Long.valueOf(newpos3))) {
                                        throw new BadRarArchiveException();
                                    }
                                    processedPositions.add(Long.valueOf(newpos3));
                                    toRead = toRead5;
                                    continue;
                                    archive2 = archive;
                                    bArr = null;
                                    break;
                                    break;
                                case SubHeader:
                                    byte[] subHeadbuffer = safelyAllocate(3L, MAX_HEADER_SIZE);
                                    rawData.readFully(subHeadbuffer, subHeadbuffer.length);
                                    SubBlockHeader subHead = new SubBlockHeader(blockHead, subHeadbuffer);
                                    subHead.print();
                                    SubBlockHeaderType subType = subHead.getSubType();
                                    if (subType == null) {
                                        archive = this;
                                        break;
                                    } else {
                                        switch (subType) {
                                            case MAC_HEAD:
                                                archive = this;
                                                byte[] macHeaderbuffer = safelyAllocate(8L, MAX_HEADER_SIZE);
                                                rawData.readFully(macHeaderbuffer, macHeaderbuffer.length);
                                                MacInfoHeader macHeader = new MacInfoHeader(subHead, macHeaderbuffer);
                                                macHeader.print();
                                                archive.headers.add(macHeader);
                                                break;
                                            case BEEA_HEAD:
                                                archive = this;
                                                break;
                                            case EA_HEAD:
                                                archive = this;
                                                byte[] eaHeaderBuffer = safelyAllocate(10L, MAX_HEADER_SIZE);
                                                rawData.readFully(eaHeaderBuffer, eaHeaderBuffer.length);
                                                EAHeader eaHeader = new EAHeader(subHead, eaHeaderBuffer);
                                                eaHeader.print();
                                                archive.headers.add(eaHeader);
                                                break;
                                            case NTACL_HEAD:
                                                archive = this;
                                                break;
                                            case STREAM_HEAD:
                                                archive = this;
                                                break;
                                            case UO_HEAD:
                                                int toRead6 = subHead.getHeaderSize(false);
                                                toRead = ((toRead6 - 7) - 4) - 3;
                                                byte[] uoHeaderBuffer = safelyAllocate(toRead, MAX_HEADER_SIZE);
                                                rawData.readFully(uoHeaderBuffer, uoHeaderBuffer.length);
                                                UnixOwnersHeader uoHeader = new UnixOwnersHeader(subHead, uoHeaderBuffer);
                                                uoHeader.print();
                                                this.headers.add(uoHeader);
                                                archive = this;
                                                continue;
                                                archive2 = archive;
                                                bArr = null;
                                                break;
                                            default:
                                                archive = this;
                                                break;
                                        }
                                    }
                                    break;
                                default:
                                    logger.warn("Unknown Header");
                                    throw new NotRarArchiveException();
                            }
                            break;
                    }
                    toRead = toRead;
                    archive2 = archive;
                    bArr = null;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private static byte[] safelyAllocate(long len, int maxSize) throws RarException {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxsize must be >= 0");
        }
        if (len < 0 || len > maxSize) {
            throw new BadRarArchiveException();
        }
        return new byte[(int) len];
    }

    public void extractFile(FileHeader hd, OutputStream os) throws RarException {
        if (!this.headers.contains(hd)) {
            throw new HeaderNotInArchiveException();
        }
        try {
            doExtractFile(hd, os);
        } catch (Exception e) {
            if (e instanceof RarException) {
                throw ((RarException) e);
            }
            throw new RarException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class ExtractorExecutorHolder {
        private static final AtomicLong threadIndex = new AtomicLong();
        private static final ExecutorService cachedExecutorService = new ThreadPoolExecutor(0, getMaxThreads(), getThreadKeepAlive(), TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactory() { // from class: com.github.junrar.Archive$ExtractorExecutorHolder$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return Archive.ExtractorExecutorHolder.lambda$static$0(runnable);
            }
        });

        private ExtractorExecutorHolder() {
        }

        static /* synthetic */ Thread lambda$static$0(Runnable r) {
            Thread t = new Thread(r, "junrar-extractor-" + threadIndex.getAndIncrement());
            t.setDaemon(true);
            return t;
        }

        private static int getMaxThreads() {
            return ((Integer) Archive.getPropertyAs("junrar.extractor.max-threads", new Archive$ExtractorExecutorHolder$$ExternalSyntheticLambda0(), Integer.MAX_VALUE)).intValue();
        }

        private static int getThreadKeepAlive() {
            return ((Integer) Archive.getPropertyAs("junrar.extractor.thread-keep-alive-seconds", new Archive$ExtractorExecutorHolder$$ExternalSyntheticLambda0(), 5)).intValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> T getPropertyAs(String key, Function<String, T> function, T defaultValue) {
        Objects.requireNonNull(defaultValue, "default value must not be null");
        try {
            String integerString = System.getProperty(key);
            if (integerString != null && !integerString.isEmpty()) {
                return function.apply(integerString);
            }
        } catch (NumberFormatException | SecurityException e) {
            logger.error("Could not parse the System Property '{}' into an '{}'. Defaulting to '{}'", key, defaultValue.getClass().getTypeName(), defaultValue, e);
        }
        return defaultValue;
    }

    private static final class EmptyInputStream extends InputStream {
        private EmptyInputStream() {
        }

        @Override // java.io.InputStream
        public int available() {
            return 0;
        }

        @Override // java.io.InputStream
        public int read() {
            return -1;
        }
    }

    public InputStream getInputStream(final FileHeader hd) throws IOException {
        if (hd.getFullUnpackSize() <= 0) {
            return new EmptyInputStream();
        }
        int bufferSize = (int) Math.max(Math.min(hd.getFullUnpackSize(), PIPE_BUFFER_SIZE), 1L);
        PipedInputStream in = new PipedInputStream(bufferSize);
        final PipedOutputStream out = new PipedOutputStream(in);
        Runnable r = new Runnable() { // from class: com.github.junrar.Archive$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m359lambda$getInputStream$0$comgithubjunrarArchive(hd, out);
            }
        };
        if (USE_EXECUTOR) {
            ExtractorExecutorHolder.cachedExecutorService.submit(r);
        } else {
            new Thread(r).start();
        }
        return in;
    }

    /* JADX INFO: renamed from: lambda$getInputStream$0$com-github-junrar-Archive, reason: not valid java name */
    /* synthetic */ void m359lambda$getInputStream$0$comgithubjunrarArchive(FileHeader hd, PipedOutputStream out) {
        try {
            extractFile(hd, out);
        } catch (RarException e) {
        } catch (Throwable th) {
            try {
                out.close();
            } catch (IOException e2) {
            }
            throw th;
        }
        try {
            out.close();
        } catch (IOException e3) {
        }
    }

    private void doExtractFile(FileHeader hd, OutputStream os) throws RarException, IOException {
        this.dataIO.init(os);
        this.dataIO.init(hd);
        this.dataIO.setUnpFileCRC(isOldFormat() ? 0L : -1L);
        if (this.unpack == null) {
            this.unpack = new Unpack(this.dataIO);
        }
        if (!hd.isSolid()) {
            this.unpack.init(null);
        }
        this.unpack.setDestSize(hd.getFullUnpackSize());
        try {
            this.unpack.doUnpack(hd.getUnpVersion(), hd.isSolid());
            FileHeader hd2 = this.dataIO.getSubHeader();
            long actualCRC = hd2.isSplitAfter() ? ~this.dataIO.getPackedCRC() : ~this.dataIO.getUnpFileCRC();
            int expectedCRC = hd2.getFileCRC();
            if (actualCRC != expectedCRC) {
                throw new CrcErrorException();
            }
        } catch (Exception e) {
            this.unpack.cleanUp();
            if (e instanceof RarException) {
                throw ((RarException) e);
            }
            throw new RarException(e);
        }
    }

    public MainHeader getMainHeader() {
        return this.newMhd;
    }

    public boolean isOldFormat() {
        return this.markHead.isOldFormat();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.channel != null) {
            this.channel.close();
            this.channel = null;
        }
        if (this.unpack != null) {
            this.unpack.cleanUp();
        }
    }

    public VolumeManager getVolumeManager() {
        return this.volumeManager;
    }

    public void setVolumeManager(VolumeManager volumeManager) {
        this.volumeManager = volumeManager;
    }

    public Volume getVolume() {
        return this.volume;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVolume(Volume volume) throws RarException, IOException {
        this.volume = volume;
        setChannel(volume.getChannel(), volume.getLength());
    }

    @Override // java.lang.Iterable
    public Iterator<FileHeader> iterator() {
        return new Iterator<FileHeader>() { // from class: com.github.junrar.Archive.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public FileHeader next() {
                if (Archive.this.nextFileHeader != null) {
                    FileHeader next = Archive.this.nextFileHeader;
                    return next;
                }
                FileHeader next2 = Archive.this.nextFileHeader();
                return next2;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                Archive.this.nextFileHeader = Archive.this.nextFileHeader();
                return Archive.this.nextFileHeader != null;
            }
        };
    }
}

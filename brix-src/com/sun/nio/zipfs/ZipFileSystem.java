package com.sun.nio.zipfs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import kotlin.UByte;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.SystemProperties;

/* JADX INFO: loaded from: classes2.dex */
public class ZipFileSystem extends FileSystem {
    private static final String GLOB_SYNTAX = "glob";
    private static final String REGEX_SYNTAX = "regex";
    final byte[] cen;
    private final SeekableByteChannel ch;
    private final boolean createNew;
    private final String defaultDir;
    private final ZipPath defaultdir;
    private END end;
    private LinkedHashMap<IndexNode, IndexNode> inodes;
    private long locpos;
    private final String nameEncoding;
    private final ZipFileSystemProvider provider;
    private boolean readOnly;
    private IndexNode root;
    private final boolean useTempFile;
    final ZipCoder zc;
    private final Path zfpath;
    private static final boolean isWindows = System.getProperty(SystemProperties.OS_NAME).startsWith("Windows");
    private static final Set<String> supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet(Arrays.asList("basic", ArchiveStreamFactory.ZIP)));
    private static byte[] ROOTPATH = new byte[0];
    private final int tempFileCreationThreshold = 10485760;
    private Set<InputStream> streams = Collections.synchronizedSet(new HashSet());
    private Set<ExChannelCloser> exChClosers = new HashSet();
    private Set<Path> tmppaths = Collections.synchronizedSet(new HashSet());
    private volatile boolean isOpen = true;
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private boolean hasUpdate = false;
    private final IndexNode LOOKUPKEY = IndexNode.keyOf(null);
    private final int MAX_FLATER = 20;
    private final List<Inflater> inflaters = new ArrayList();
    private final List<Deflater> deflaters = new ArrayList();

    ZipFileSystem(ZipFileSystemProvider provider, Path zfpath, Map<String, ?> env) throws IOException {
        this.readOnly = false;
        this.createNew = "true".equals(env.get("create"));
        this.nameEncoding = env.containsKey("encoding") ? (String) env.get("encoding") : CharEncoding.UTF_8;
        this.useTempFile = Boolean.TRUE.equals(env.get("useTempFile"));
        this.defaultDir = env.containsKey("default.dir") ? (String) env.get("default.dir") : "/";
        if (this.defaultDir.charAt(0) != '/') {
            throw new IllegalArgumentException("default dir should be absolute");
        }
        this.provider = provider;
        this.zfpath = zfpath;
        if (Files.notExists(zfpath, new LinkOption[0])) {
            if (this.createNew) {
                OutputStream os = Files.newOutputStream(zfpath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                try {
                    new END().write(os, 0L);
                    if (os != null) {
                        os.close();
                    }
                } catch (Throwable th) {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } else {
                throw new FileSystemNotFoundException(zfpath.toString());
            }
        }
        zfpath.getFileSystem().provider().checkAccess(zfpath, AccessMode.READ);
        if (!Files.isWritable(zfpath)) {
            this.readOnly = true;
        }
        this.zc = ZipCoder.get(this.nameEncoding);
        this.defaultdir = new ZipPath(this, getBytes(this.defaultDir));
        this.ch = Files.newByteChannel(zfpath, StandardOpenOption.READ);
        this.cen = initCEN();
    }

    @Override // java.nio.file.FileSystem
    public FileSystemProvider provider() {
        return this.provider;
    }

    @Override // java.nio.file.FileSystem
    public String getSeparator() {
        return "/";
    }

    @Override // java.nio.file.FileSystem
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override // java.nio.file.FileSystem
    public boolean isReadOnly() {
        return this.readOnly;
    }

    private void checkWritable() throws IOException {
        if (this.readOnly) {
            throw new ReadOnlyFileSystemException();
        }
    }

    @Override // java.nio.file.FileSystem
    public Iterable<Path> getRootDirectories() {
        ArrayList<Path> pathArr = new ArrayList<>();
        pathArr.add(new ZipPath(this, new byte[]{47}));
        return pathArr;
    }

    ZipPath getDefaultDir() {
        return this.defaultdir;
    }

    @Override // java.nio.file.FileSystem
    public ZipPath getPath(String first, String... more) {
        String path;
        if (more.length == 0) {
            path = first;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(first);
            for (String segment : more) {
                if (segment.length() > 0) {
                    if (sb.length() > 0) {
                        sb.append(IOUtils.DIR_SEPARATOR_UNIX);
                    }
                    sb.append(segment);
                }
            }
            path = sb.toString();
        }
        return new ZipPath(this, path);
    }

    @Override // java.nio.file.FileSystem
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.file.FileSystem
    public WatchService newWatchService() {
        throw new UnsupportedOperationException();
    }

    FileStore getFileStore(ZipPath path) {
        return new ZipFileStore(path);
    }

    @Override // java.nio.file.FileSystem
    public Iterable<FileStore> getFileStores() {
        ArrayList<FileStore> list = new ArrayList<>(1);
        list.add(new ZipFileStore(new ZipPath(this, new byte[]{47})));
        return list;
    }

    @Override // java.nio.file.FileSystem
    public Set<String> supportedFileAttributeViews() {
        return supportedFileAttributeViews;
    }

    public String toString() {
        return this.zfpath.toString();
    }

    Path getZipFile() {
        return this.zfpath;
    }

    @Override // java.nio.file.FileSystem
    public PathMatcher getPathMatcher(String syntaxAndInput) {
        String expr;
        int pos = syntaxAndInput.indexOf(58);
        if (pos <= 0 || pos == syntaxAndInput.length()) {
            throw new IllegalArgumentException();
        }
        String syntax = syntaxAndInput.substring(0, pos);
        String input = syntaxAndInput.substring(pos + 1);
        if (syntax.equals(GLOB_SYNTAX)) {
            expr = ZipUtils.toRegexPattern(input);
        } else if (syntax.equals(REGEX_SYNTAX)) {
            expr = input;
        } else {
            throw new UnsupportedOperationException("Syntax '" + syntax + "' not recognized");
        }
        final Pattern pattern = Pattern.compile(expr);
        return new PathMatcher() { // from class: com.sun.nio.zipfs.ZipFileSystem.1
            @Override // java.nio.file.PathMatcher
            public boolean matches(Path path) {
                return pattern.matcher(path.toString()).matches();
            }
        };
    }

    @Override // java.nio.file.FileSystem, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        beginWrite();
        try {
            if (this.isOpen) {
                this.isOpen = false;
                endWrite();
                if (!this.streams.isEmpty()) {
                    Set<InputStream> copy = new HashSet<>(this.streams);
                    for (InputStream is : copy) {
                        is.close();
                    }
                }
                beginWrite();
                try {
                    sync();
                    this.ch.close();
                    endWrite();
                    synchronized (this.inflaters) {
                        for (Inflater inf : this.inflaters) {
                            inf.end();
                        }
                    }
                    synchronized (this.deflaters) {
                        for (Deflater def : this.deflaters) {
                            def.end();
                        }
                    }
                    beginWrite();
                    try {
                        this.inodes = null;
                        endWrite();
                        IOException ioe = null;
                        synchronized (this.tmppaths) {
                            for (Path p : this.tmppaths) {
                                try {
                                    Files.deleteIfExists(p);
                                } catch (IOException x) {
                                    if (ioe == null) {
                                        ioe = x;
                                    } else {
                                        ioe.addSuppressed(x);
                                    }
                                }
                            }
                        }
                        this.provider.removeFileSystem(this.zfpath, this);
                        if (ioe != null) {
                            throw ioe;
                        }
                        return;
                    } catch (Throwable ioe2) {
                        endWrite();
                        throw ioe2;
                    }
                } catch (Throwable th) {
                    endWrite();
                    throw th;
                }
            }
            endWrite();
        } catch (Throwable th2) {
            endWrite();
            throw th2;
        }
    }

    ZipFileAttributes getFileAttributes(byte[] path) throws IOException {
        beginRead();
        try {
            ensureOpen();
            Entry e = getEntry0(path);
            if (e == null) {
                IndexNode inode = getInode(path);
                if (inode != null) {
                    e = new Entry(inode.name);
                    e.method = 0;
                    e.ctime = -1L;
                    e.atime = -1L;
                    e.mtime = -1L;
                } else {
                    return null;
                }
            }
            return new ZipFileAttributes(e);
        } finally {
            endRead();
        }
    }

    void setTimes(byte[] path, FileTime mtime, FileTime atime, FileTime ctime) throws IOException {
        checkWritable();
        beginWrite();
        try {
            ensureOpen();
            Entry e = getEntry0(path);
            if (e == null) {
                throw new NoSuchFileException(getString(path));
            }
            if (e.type == 1) {
                e.type = 4;
            }
            if (mtime != null) {
                e.mtime = mtime.toMillis();
            }
            if (atime != null) {
                e.atime = atime.toMillis();
            }
            if (ctime != null) {
                e.ctime = ctime.toMillis();
            }
            update(e);
            endWrite();
        } catch (Throwable th) {
            endWrite();
            throw th;
        }
    }

    boolean exists(byte[] path) throws IOException {
        beginRead();
        try {
            ensureOpen();
            return getInode(path) != null;
        } finally {
            endRead();
        }
    }

    boolean isDirectory(byte[] path) throws IOException {
        beginRead();
        try {
            IndexNode n = getInode(path);
            return n != null && n.isDir();
        } finally {
            endRead();
        }
    }

    private ZipPath toZipPath(byte[] path) {
        byte[] p = new byte[path.length + 1];
        p[0] = 47;
        System.arraycopy(path, 0, p, 1, path.length);
        return new ZipPath(this, p);
    }

    Iterator<Path> iteratorOf(byte[] path, DirectoryStream.Filter<? super Path> filter) throws IOException {
        beginWrite();
        try {
            ensureOpen();
            IndexNode inode = getInode(path);
            if (inode == null) {
                throw new NotDirectoryException(getString(path));
            }
            List<Path> list = new ArrayList<>();
            for (IndexNode child = inode.child; child != null; child = child.sibling) {
                ZipPath zp = toZipPath(child.name);
                if (filter == null || filter.accept(zp)) {
                    list.add(zp);
                }
            }
            Iterator<Path> it = list.iterator();
            endWrite();
            return it;
        } catch (Throwable th) {
            endWrite();
            throw th;
        }
    }

    void createDirectory(byte[] dir, FileAttribute<?>... attrs) throws IOException {
        checkWritable();
        byte[] dir2 = ZipUtils.toDirectoryPath(dir);
        beginWrite();
        try {
            ensureOpen();
            if (dir2.length == 0 || exists(dir2)) {
                throw new FileAlreadyExistsException(getString(dir2));
            }
            checkParents(dir2);
            Entry e = new Entry(dir2, 2);
            e.method = 0;
            update(e);
            endWrite();
        } catch (Throwable th) {
            endWrite();
            throw th;
        }
    }

    void copyFile(boolean deletesrc, byte[] src, byte[] dst, CopyOption... options) throws IOException {
        checkWritable();
        if (Arrays.equals(src, dst)) {
            return;
        }
        beginWrite();
        try {
            ensureOpen();
            Entry eSrc = getEntry0(src);
            if (eSrc == null) {
                throw new NoSuchFileException(getString(src));
            }
            if (eSrc.isDir()) {
                createDirectory(dst, new FileAttribute[0]);
                endWrite();
                return;
            }
            boolean hasReplace = false;
            boolean hasCopyAttrs = false;
            for (CopyOption opt : options) {
                if (opt == StandardCopyOption.REPLACE_EXISTING) {
                    hasReplace = true;
                } else if (opt == StandardCopyOption.COPY_ATTRIBUTES) {
                    hasCopyAttrs = true;
                }
            }
            Entry eDst = getEntry0(dst);
            if (eDst != null) {
                if (!hasReplace) {
                    throw new FileAlreadyExistsException(getString(dst));
                }
            } else {
                checkParents(dst);
            }
            Entry u = new Entry(eSrc, 4);
            u.name(dst);
            if (eSrc.type == 2 || eSrc.type == 3) {
                u.type = eSrc.type;
                if (deletesrc) {
                    u.bytes = eSrc.bytes;
                    u.file = eSrc.file;
                } else if (eSrc.bytes != null) {
                    u.bytes = Arrays.copyOf(eSrc.bytes, eSrc.bytes.length);
                } else if (eSrc.file != null) {
                    u.file = getTempPathForEntry(null);
                    Files.copy(eSrc.file, u.file, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            if (!hasCopyAttrs) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                u.ctime = jCurrentTimeMillis;
                u.atime = jCurrentTimeMillis;
                u.mtime = jCurrentTimeMillis;
            }
            update(u);
            if (deletesrc) {
                updateDelete(eSrc);
            }
            endWrite();
        } catch (Throwable th) {
            endWrite();
            throw th;
        }
    }

    OutputStream newOutputStream(byte[] path, OpenOption... options) throws IOException {
        checkWritable();
        boolean hasCreateNew = false;
        boolean hasCreate = false;
        boolean hasAppend = false;
        boolean hasTruncate = false;
        for (OpenOption opt : options) {
            if (opt == StandardOpenOption.READ) {
                throw new IllegalArgumentException("READ not allowed");
            }
            if (opt == StandardOpenOption.CREATE_NEW) {
                hasCreateNew = true;
            }
            if (opt == StandardOpenOption.CREATE) {
                hasCreate = true;
            }
            if (opt == StandardOpenOption.APPEND) {
                hasAppend = true;
            }
            if (opt == StandardOpenOption.TRUNCATE_EXISTING) {
                hasTruncate = true;
            }
        }
        if (hasAppend && hasTruncate) {
            throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
        }
        beginRead();
        try {
            ensureOpen();
            Entry e = getEntry0(path);
            if (e == null) {
                if (!hasCreate && !hasCreateNew) {
                    throw new NoSuchFileException(getString(path));
                }
                checkParents(path);
                OutputStream outputStream = getOutputStream(new Entry(path, 2));
                endRead();
                return outputStream;
            }
            if (e.isDir() || hasCreateNew) {
                throw new FileAlreadyExistsException(getString(path));
            }
            if (!hasAppend) {
                OutputStream outputStream2 = getOutputStream(new Entry(e, 2));
                endRead();
                return outputStream2;
            }
            InputStream is = getInputStream(e);
            OutputStream os = getOutputStream(new Entry(e, 2));
            copyStream(is, os);
            is.close();
            endRead();
            return os;
        } catch (Throwable th) {
            endRead();
            throw th;
        }
    }

    InputStream newInputStream(byte[] path) throws IOException {
        beginRead();
        try {
            ensureOpen();
            Entry e = getEntry0(path);
            if (e == null) {
                throw new NoSuchFileException(getString(path));
            }
            if (e.isDir()) {
                throw new FileSystemException(getString(path), "is a directory", null);
            }
            InputStream inputStream = getInputStream(e);
            endRead();
            return inputStream;
        } catch (Throwable th) {
            endRead();
            throw th;
        }
    }

    private void checkOptions(Set<? extends OpenOption> options) {
        for (OpenOption option : options) {
            if (option == null) {
                throw new NullPointerException();
            }
            if (!(option instanceof StandardOpenOption)) {
                throw new IllegalArgumentException();
            }
        }
        if (options.contains(StandardOpenOption.APPEND) && options.contains(StandardOpenOption.TRUNCATE_EXISTING)) {
            throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
        }
    }

    SeekableByteChannel newByteChannel(byte[] path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        Entry e;
        checkOptions(options);
        if (options.contains(StandardOpenOption.WRITE) || options.contains(StandardOpenOption.APPEND)) {
            checkWritable();
            beginRead();
            try {
                final WritableByteChannel wbc = Channels.newChannel(newOutputStream(path, (OpenOption[]) options.toArray(new OpenOption[0])));
                long leftover = 0;
                if (options.contains(StandardOpenOption.APPEND) && (e = getEntry0(path)) != null && e.size >= 0) {
                    leftover = e.size;
                }
                final long offset = leftover;
                return new SeekableByteChannel() { // from class: com.sun.nio.zipfs.ZipFileSystem.2
                    long written;

                    {
                        this.written = offset;
                    }

                    @Override // java.nio.channels.Channel
                    public boolean isOpen() {
                        return wbc.isOpen();
                    }

                    @Override // java.nio.channels.SeekableByteChannel
                    public long position() throws IOException {
                        return this.written;
                    }

                    @Override // java.nio.channels.SeekableByteChannel
                    public SeekableByteChannel position(long pos) throws IOException {
                        throw new UnsupportedOperationException();
                    }

                    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
                    public int read(ByteBuffer dst) throws IOException {
                        throw new UnsupportedOperationException();
                    }

                    @Override // java.nio.channels.SeekableByteChannel
                    public SeekableByteChannel truncate(long size) throws IOException {
                        throw new UnsupportedOperationException();
                    }

                    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
                    public int write(ByteBuffer src) throws IOException {
                        int n = wbc.write(src);
                        this.written += (long) n;
                        return n;
                    }

                    @Override // java.nio.channels.SeekableByteChannel
                    public long size() throws IOException {
                        return this.written;
                    }

                    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
                    public void close() throws IOException {
                        wbc.close();
                    }
                };
            } finally {
                endRead();
            }
        }
        beginRead();
        try {
            ensureOpen();
            Entry e2 = getEntry0(path);
            if (e2 == null || e2.isDir()) {
                throw new NoSuchFileException(getString(path));
            }
            final ReadableByteChannel rbc = Channels.newChannel(getInputStream(e2));
            final long size = e2.size;
            SeekableByteChannel seekableByteChannel = new SeekableByteChannel() { // from class: com.sun.nio.zipfs.ZipFileSystem.3
                long read = 0;

                @Override // java.nio.channels.Channel
                public boolean isOpen() {
                    return rbc.isOpen();
                }

                @Override // java.nio.channels.SeekableByteChannel
                public long position() throws IOException {
                    return this.read;
                }

                @Override // java.nio.channels.SeekableByteChannel
                public SeekableByteChannel position(long pos) throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
                public int read(ByteBuffer dst) throws IOException {
                    int n = rbc.read(dst);
                    if (n > 0) {
                        this.read += (long) n;
                    }
                    return n;
                }

                @Override // java.nio.channels.SeekableByteChannel
                public SeekableByteChannel truncate(long size2) throws IOException {
                    throw new NonWritableChannelException();
                }

                @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
                public int write(ByteBuffer src) throws IOException {
                    throw new NonWritableChannelException();
                }

                @Override // java.nio.channels.SeekableByteChannel
                public long size() throws IOException {
                    return size;
                }

                @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    rbc.close();
                }
            };
            endRead();
            return seekableByteChannel;
        } catch (Throwable th) {
            endRead();
            throw th;
        }
    }

    FileChannel newFileChannel(byte[] path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        checkOptions(options);
        final boolean forWrite = options.contains(StandardOpenOption.WRITE) || options.contains(StandardOpenOption.APPEND);
        beginRead();
        try {
            ensureOpen();
            Entry e = getEntry0(path);
            if (forWrite) {
                checkWritable();
                if (e == null) {
                    if (!options.contains(StandardOpenOption.CREATE) && !options.contains(StandardOpenOption.CREATE_NEW)) {
                        throw new NoSuchFileException(getString(path));
                    }
                } else {
                    if (options.contains(StandardOpenOption.CREATE_NEW)) {
                        throw new FileAlreadyExistsException(getString(path));
                    }
                    if (e.isDir()) {
                        throw new FileAlreadyExistsException("directory <" + getString(path) + "> exists");
                    }
                }
                options = new HashSet(options);
                options.remove(StandardOpenOption.CREATE_NEW);
            } else if (e == null || e.isDir()) {
                throw new NoSuchFileException(getString(path));
            }
            final boolean isFCH = e != null && e.type == 3;
            final Path tmpfile = isFCH ? e.file : getTempPathForEntry(path);
            final FileChannel fch = tmpfile.getFileSystem().provider().newFileChannel(tmpfile, options, attrs);
            final Entry u = isFCH ? e : new Entry(path, tmpfile, 3);
            if (forWrite) {
                u.flag = 8;
                u.method = 8;
            }
            FileChannel fileChannel = new FileChannel() { // from class: com.sun.nio.zipfs.ZipFileSystem.4
                @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
                public int write(ByteBuffer src) throws IOException {
                    return fch.write(src);
                }

                @Override // java.nio.channels.FileChannel, java.nio.channels.GatheringByteChannel
                public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
                    return fch.write(srcs, offset, length);
                }

                @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
                public long position() throws IOException {
                    return fch.position();
                }

                @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
                public FileChannel position(long newPosition) throws IOException {
                    fch.position(newPosition);
                    return this;
                }

                @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
                public long size() throws IOException {
                    return fch.size();
                }

                @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
                public FileChannel truncate(long size) throws IOException {
                    fch.truncate(size);
                    return this;
                }

                @Override // java.nio.channels.FileChannel
                public void force(boolean metaData) throws IOException {
                    fch.force(metaData);
                }

                @Override // java.nio.channels.FileChannel
                public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
                    return fch.transferTo(position, count, target);
                }

                @Override // java.nio.channels.FileChannel
                public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
                    return fch.transferFrom(src, position, count);
                }

                @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
                public int read(ByteBuffer dst) throws IOException {
                    return fch.read(dst);
                }

                @Override // java.nio.channels.FileChannel
                public int read(ByteBuffer dst, long position) throws IOException {
                    return fch.read(dst, position);
                }

                @Override // java.nio.channels.FileChannel, java.nio.channels.ScatteringByteChannel
                public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
                    return fch.read(dsts, offset, length);
                }

                @Override // java.nio.channels.FileChannel
                public int write(ByteBuffer src, long position) throws IOException {
                    return fch.write(src, position);
                }

                @Override // java.nio.channels.FileChannel
                public MappedByteBuffer map(FileChannel.MapMode mode, long position, long size) throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override // java.nio.channels.FileChannel
                public FileLock lock(long position, long size, boolean shared) throws IOException {
                    return fch.lock(position, size, shared);
                }

                @Override // java.nio.channels.FileChannel
                public FileLock tryLock(long position, long size, boolean shared) throws IOException {
                    return fch.tryLock(position, size, shared);
                }

                @Override // java.nio.channels.spi.AbstractInterruptibleChannel
                protected void implCloseChannel() throws IOException {
                    fch.close();
                    if (forWrite) {
                        u.mtime = System.currentTimeMillis();
                        u.size = Files.size(u.file);
                        ZipFileSystem.this.update(u);
                        return;
                    }
                    if (!isFCH) {
                        ZipFileSystem.this.removeTempPathForEntry(tmpfile);
                    }
                }
            };
            endRead();
            return fileChannel;
        } catch (Throwable th) {
            endRead();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Path getTempPathForEntry(byte[] path) throws IOException {
        Path tmpPath = createTempFileInSameDirectoryAs(this.zfpath);
        if (path != null) {
            Entry e = getEntry0(path);
            if (e != null) {
                InputStream is = newInputStream(path);
                try {
                    Files.copy(is, tmpPath, StandardCopyOption.REPLACE_EXISTING);
                    if (is != null) {
                        is.close();
                    }
                } catch (Throwable th) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
        }
        return tmpPath;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeTempPathForEntry(Path path) throws IOException {
        Files.delete(path);
        this.tmppaths.remove(path);
    }

    private void checkParents(byte[] path) throws IOException {
        beginRead();
        do {
            try {
                byte[] parent = getParent(path);
                path = parent;
                if (parent == null || path.length == 0) {
                    endRead();
                    return;
                }
            } catch (Throwable th) {
                endRead();
                throw th;
            }
        } while (this.inodes.containsKey(IndexNode.keyOf(path)));
        throw new NoSuchFileException(getString(path));
    }

    private static byte[] getParent(byte[] path) {
        int off = path.length - 1;
        if (off > 0 && path[off] == 47) {
            off--;
        }
        while (off > 0 && path[off] != 47) {
            off--;
        }
        if (off <= 0) {
            return ROOTPATH;
        }
        return Arrays.copyOf(path, off + 1);
    }

    private final void beginWrite() {
        this.rwlock.writeLock().lock();
    }

    private final void endWrite() {
        this.rwlock.writeLock().unlock();
    }

    private final void beginRead() {
        this.rwlock.readLock().lock();
    }

    private final void endRead() {
        this.rwlock.readLock().unlock();
    }

    final byte[] getBytes(String name) {
        return this.zc.getBytes(name);
    }

    final String getString(byte[] name) {
        return this.zc.toString(name);
    }

    protected void finalize() throws IOException {
        close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getDataPos(Entry e) throws IOException {
        if (e.locoff == -1) {
            Entry e2 = getEntry0(e.name);
            if (e2 == null) {
                throw new ZipException("invalid loc for entry <" + e.name + ">");
            }
            e.locoff = e2.locoff;
        }
        byte[] buf = new byte[30];
        if (readFullyAt(buf, 0, buf.length, e.locoff) != buf.length) {
            throw new ZipException("invalid loc for entry <" + e.name + ">");
        }
        return this.locpos + e.locoff + 30 + ((long) ZipConstants.LOCNAM(buf)) + ((long) ZipConstants.LOCEXT(buf));
    }

    final long readFullyAt(byte[] buf, int off, long len, long pos) throws IOException {
        ByteBuffer bb = ByteBuffer.wrap(buf);
        bb.position(off);
        bb.limit((int) (((long) off) + len));
        return readFullyAt(bb, pos);
    }

    private final long readFullyAt(ByteBuffer bb, long pos) throws IOException {
        long j;
        synchronized (this.ch) {
            j = this.ch.position(pos).read(bb);
        }
        return j;
    }

    private END findEND() throws IOException {
        byte[] buf = new byte[128];
        ZipFileSystem zipFileSystem = this;
        long ziplen = zipFileSystem.ch.size();
        long j = 0;
        long minHDR = ziplen - 65557 > 0 ? ziplen - 65557 : 0L;
        long minPos = minHDR - ((long) (buf.length - 22));
        long pos = ziplen - ((long) buf.length);
        while (pos >= minPos) {
            int off = 0;
            if (pos < j) {
                off = (int) (-pos);
                Arrays.fill(buf, 0, off, (byte) 0);
            }
            int len = buf.length - off;
            long j2 = ((long) off) + pos;
            long pos2 = pos;
            ZipFileSystem zipFileSystem2 = zipFileSystem;
            int off2 = off;
            int len2 = len;
            long fullyAt = zipFileSystem2.readFullyAt(buf, off2, len, j2);
            int off3 = off2;
            if (fullyAt != len2) {
                zerror("zip END header not found");
            }
            int i = buf.length - 22;
            while (i >= 0) {
                if (buf[i + 0] != 80 || buf[i + 1] != 75 || buf[i + 2] != 5 || buf[i + 3] != 6 || pos2 + ((long) i) + 22 + ((long) ZipConstants.ENDCOM(buf, i)) != ziplen) {
                    i--;
                    off3 = off3;
                    len2 = len2;
                } else {
                    byte[] buf2 = Arrays.copyOfRange(buf, i, i + 22);
                    END end = new END();
                    end.endsub = ZipConstants.ENDSUB(buf2);
                    end.centot = ZipConstants.ENDTOT(buf2);
                    end.cenlen = ZipConstants.ENDSIZ(buf2);
                    end.cenoff = ZipConstants.ENDOFF(buf2);
                    end.comlen = ZipConstants.ENDCOM(buf2);
                    end.endpos = pos2 + ((long) i);
                    if (end.cenlen != 4294967295L && end.cenoff != 4294967295L && end.centot != 65535) {
                        return end;
                    }
                    byte[] loc64 = new byte[20];
                    if (readFullyAt(loc64, 0, loc64.length, end.endpos - 20) != loc64.length) {
                        return end;
                    }
                    long end64pos = ZipConstants.ZIP64_LOCOFF(loc64);
                    byte[] end64buf = new byte[56];
                    if (readFullyAt(end64buf, 0, end64buf.length, end64pos) != end64buf.length) {
                        return end;
                    }
                    end.cenlen = ZipConstants.ZIP64_ENDSIZ(end64buf);
                    end.cenoff = ZipConstants.ZIP64_ENDOFF(end64buf);
                    end.centot = (int) ZipConstants.ZIP64_ENDTOT(end64buf);
                    end.endpos = end64pos;
                    return end;
                }
            }
            pos = pos2 - ((long) (buf.length - 22));
            j = 0;
            zipFileSystem = this;
        }
        zerror("zip END header not found");
        return null;
    }

    private byte[] initCEN() throws IOException {
        this.end = findEND();
        if (this.end.endpos == 0) {
            this.inodes = new LinkedHashMap<>(10);
            this.locpos = 0L;
            buildNodeTree();
            return null;
        }
        if (this.end.cenlen > this.end.endpos) {
            zerror("invalid END header (bad central directory size)");
        }
        long cenpos = this.end.endpos - this.end.cenlen;
        this.locpos = cenpos - this.end.cenoff;
        if (this.locpos < 0) {
            zerror("invalid END header (bad central directory offset)");
        }
        byte[] cen = new byte[(int) (this.end.cenlen + 22)];
        if (readFullyAt(cen, 0, cen.length, cenpos) != this.end.cenlen + 22) {
            zerror("read CEN tables failed");
        }
        this.inodes = new LinkedHashMap<>(this.end.centot + 1);
        int pos = 0;
        int limit = cen.length - 22;
        while (pos < limit) {
            if (!ZipConstants.cenSigAt(cen, pos)) {
                zerror("invalid CEN header (bad signature)");
            }
            int method = ZipConstants.CENHOW(cen, pos);
            int nlen = ZipConstants.CENNAM(cen, pos);
            int elen = ZipConstants.CENEXT(cen, pos);
            int clen = ZipConstants.CENCOM(cen, pos);
            if ((ZipConstants.CENFLG(cen, pos) & 1) != 0) {
                zerror("invalid CEN header (encrypted entry)");
            }
            if (method != 0 && method != 8) {
                zerror("invalid CEN header (unsupported compression method: " + method + ")");
            }
            if (pos + 46 + nlen > limit) {
                zerror("invalid CEN header (bad header size)");
            }
            byte[] name = Arrays.copyOfRange(cen, pos + 46, pos + 46 + nlen);
            IndexNode inode = new IndexNode(decodeCenName(name, ZipConstants.CENFLG(cen, pos)), pos);
            this.inodes.put(inode, inode);
            pos += nlen + 46 + elen + clen;
        }
        if (pos + 22 != cen.length) {
            zerror("invalid CEN header (bad header size)");
        }
        buildNodeTree();
        return cen;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] decodeCenName(byte[] name, int flag) {
        if (this.zc.isUTF8() || (flag & 2048) == 0) {
            return name;
        }
        return this.zc.getBytes(this.zc.toStringUTF8(name, name.length));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureOpen() throws IOException {
        if (!this.isOpen) {
            throw new ClosedFileSystemException();
        }
    }

    private Path createTempFileInSameDirectoryAs(Path path) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        Path dir = parent == null ? path.getFileSystem().getPath(".", new String[0]) : parent;
        Path tmpPath = Files.createTempFile(dir, "zipfstmp", null, new FileAttribute[0]);
        this.tmppaths.add(tmpPath);
        return tmpPath;
    }

    private void updateDelete(IndexNode inode) {
        beginWrite();
        try {
            removeFromTree(inode);
            this.inodes.remove(inode);
            this.hasUpdate = true;
        } finally {
            endWrite();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update(Entry e) {
        beginWrite();
        try {
            IndexNode old = this.inodes.put(e, e);
            if (old != null) {
                removeFromTree(old);
            }
            if (e.type == 2 || e.type == 3 || e.type == 4) {
                IndexNode parent = this.inodes.get(this.LOOKUPKEY.as(getParent(e.name)));
                e.sibling = parent.child;
                parent.child = e;
            }
            this.hasUpdate = true;
        } finally {
            endWrite();
        }
    }

    private long copyLOCEntry(Entry e, boolean updateHeader, OutputStream os, long written, byte[] buf) throws IOException {
        long size;
        long locoff;
        long size2;
        long written2;
        long locoff2 = e.locoff;
        e.locoff = written;
        if ((e.flag & 8) == 0) {
            size = 0;
        } else if (e.size >= 4294967295L || e.csize >= 4294967295L) {
            size = 24;
        } else {
            size = 16;
        }
        if (readFullyAt(buf, 0, 30L, locoff2) != 30) {
            throw new ZipException("loc: reading failed");
        }
        if (updateHeader) {
            locoff = locoff2 + ((long) (ZipConstants.LOCNAM(buf) + 30 + ZipConstants.LOCEXT(buf)));
            size2 = size + e.csize;
            written2 = ((long) e.writeLOC(os)) + size2;
        } else {
            os.write(buf, 0, 30);
            locoff = locoff2 + 30;
            size2 = size + ((long) (ZipConstants.LOCNAM(buf) + ZipConstants.LOCEXT(buf))) + e.csize;
            written2 = size2 + 30;
        }
        while (size2 > 0) {
            int fullyAt = (int) readFullyAt(buf, 0, buf.length, locoff);
            int n = fullyAt;
            if (fullyAt == -1) {
                break;
            }
            if (size2 < n) {
                n = (int) size2;
            }
            os.write(buf, 0, n);
            size2 -= (long) n;
            locoff += (long) n;
        }
        return written2;
    }

    /* JADX WARN: Code duplicated, block: B:173:0x0153 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private void sync() throws Throwable {
        Throwable th;
        Throwable th2;
        Throwable th3;
        if (!this.exChClosers.isEmpty()) {
            for (ExChannelCloser ecc : this.exChClosers) {
                if (ecc.streams.isEmpty()) {
                    ecc.ch.close();
                    Files.delete(ecc.path);
                    this.exChClosers.remove(ecc);
                }
            }
        }
        if (this.hasUpdate) {
            PosixFileAttributes attrs = getPosixAttributes(this.zfpath);
            Path tmpFile = createTempFileInSameDirectoryAs(this.zfpath);
            OutputStream os = new BufferedOutputStream(Files.newOutputStream(tmpFile, StandardOpenOption.WRITE));
            try {
                ArrayList<Entry> elist = new ArrayList<>(this.inodes.size());
                byte[] buf = new byte[8192];
                long written = 0;
                for (IndexNode inode : this.inodes.values()) {
                    int i = -1;
                    if (inode instanceof Entry) {
                        try {
                            Entry e = (Entry) inode;
                            try {
                                if (e.type == 4) {
                                    try {
                                        written += copyLOCEntry(e, true, os, written, buf);
                                        os = os;
                                        try {
                                            try {
                                                elist.add(e);
                                            } catch (Throwable th4) {
                                                th = th4;
                                                os = os;
                                                try {
                                                    os.close();
                                                    throw th;
                                                } catch (Throwable th5) {
                                                    th.addSuppressed(th5);
                                                    throw th;
                                                }
                                            }
                                        } catch (IOException e2) {
                                            x = e2;
                                            x.printStackTrace();
                                        }
                                    } catch (IOException e3) {
                                        x = e3;
                                        os = os;
                                        x.printStackTrace();
                                    }
                                } else {
                                    e.locoff = written;
                                    written += (long) e.writeLOC(os);
                                    try {
                                        if (e.bytes != null) {
                                            os.write(e.bytes);
                                            written += (long) e.bytes.length;
                                            os = os;
                                        } else if (e.file != null) {
                                            InputStream is = Files.newInputStream(e.file, new OpenOption[0]);
                                            try {
                                                if (e.type == 2) {
                                                    while (true) {
                                                        try {
                                                            int n = is.read(buf);
                                                            if (n == i) {
                                                                break;
                                                            }
                                                            os.write(buf, 0, n);
                                                            written += (long) n;
                                                            os = os;
                                                            i = -1;
                                                        } catch (Throwable th6) {
                                                            os = os;
                                                            th2 = th6;
                                                            if (is != null) {
                                                                try {
                                                                    is.close();
                                                                } catch (Throwable th7) {
                                                                    th2.addSuppressed(th7);
                                                                }
                                                            }
                                                            throw th2;
                                                        }
                                                    }
                                                    os = os;
                                                } else {
                                                    os = os;
                                                    try {
                                                        if (e.type == 3) {
                                                            os = os;
                                                            OutputStream os2 = new EntryOutputStream(e, os);
                                                            while (true) {
                                                                try {
                                                                    int n2 = is.read(buf);
                                                                    if (n2 == -1) {
                                                                        break;
                                                                    }
                                                                    try {
                                                                        os2.write(buf, 0, n2);
                                                                    } catch (Throwable th8) {
                                                                        th3 = th8;
                                                                        try {
                                                                            try {
                                                                                os2.close();
                                                                            } catch (Throwable th9) {
                                                                                th3.addSuppressed(th9);
                                                                            }
                                                                            throw th3;
                                                                        } catch (Throwable th10) {
                                                                            th2 = th10;
                                                                            written = written;
                                                                        }
                                                                    }
                                                                } catch (Throwable th11) {
                                                                    th3 = th11;
                                                                }
                                                                if (is != null) {
                                                                    is.close();
                                                                }
                                                                throw th2;
                                                            }
                                                            os2.close();
                                                            try {
                                                                long written2 = e.csize;
                                                                written += written2;
                                                                if ((e.flag & 8) != 0) {
                                                                    os = os;
                                                                    written += (long) e.writeEXT(os);
                                                                } else {
                                                                    os = os;
                                                                }
                                                            } catch (Throwable th12) {
                                                                os = os;
                                                                th2 = th12;
                                                                written = written;
                                                                if (is != null) {
                                                                    is.close();
                                                                }
                                                                throw th2;
                                                            }
                                                        }
                                                    } catch (Throwable th13) {
                                                        th2 = th13;
                                                    }
                                                }
                                                if (is != null) {
                                                    is.close();
                                                }
                                                Files.delete(e.file);
                                                this.tmppaths.remove(e.file);
                                            } catch (Throwable th14) {
                                                os = os;
                                                th2 = th14;
                                            }
                                        } else {
                                            os = os;
                                        }
                                        elist.add(e);
                                    } catch (IOException e4) {
                                        x = e4;
                                        os = os;
                                        x.printStackTrace();
                                    }
                                }
                            } catch (IOException e5) {
                                x = e5;
                                os = os;
                            }
                            os = os;
                        } catch (Throwable th15) {
                            th = th15;
                        }
                    } else {
                        OutputStream os3 = os;
                        try {
                            if (inode.pos == -1) {
                                os = os3;
                            } else {
                                Entry e6 = Entry.readCEN(this, inode.pos);
                                os = os3;
                                try {
                                    written += copyLOCEntry(e6, false, os, written, buf);
                                    elist.add(e6);
                                } catch (IOException x) {
                                    x.printStackTrace();
                                }
                            }
                        } catch (Throwable th16) {
                            os = os3;
                            th = th16;
                            os.close();
                            throw th;
                        }
                    }
                }
                this.end.cenoff = written;
                for (Entry entry : elist) {
                    written += (long) entry.writeCEN(os);
                }
                this.end.centot = elist.size();
                this.end.cenlen = written - this.end.cenoff;
                this.end.write(os, written);
                os.close();
                if (this.streams.isEmpty()) {
                    this.ch.close();
                    Files.delete(this.zfpath);
                } else {
                    ExChannelCloser ecc2 = new ExChannelCloser(createTempFileInSameDirectoryAs(this.zfpath), this.ch, this.streams);
                    Files.move(this.zfpath, ecc2.path, StandardCopyOption.REPLACE_EXISTING);
                    this.exChClosers.add(ecc2);
                    this.streams = Collections.synchronizedSet(new HashSet());
                }
                if (attrs != null) {
                    try {
                        Files.setPosixFilePermissions(tmpFile, attrs.permissions());
                    } catch (Throwable th17) {
                    }
                }
                Files.move(tmpFile, this.zfpath, StandardCopyOption.REPLACE_EXISTING);
                this.hasUpdate = false;
            } catch (Throwable th18) {
                th = th18;
                os.close();
                throw th;
            }
        }
    }

    private PosixFileAttributes getPosixAttributes(Path path) throws IOException {
        try {
            PosixFileAttributeView view = (PosixFileAttributeView) Files.getFileAttributeView(path, PosixFileAttributeView.class, new LinkOption[0]);
            if (view == null) {
                return null;
            }
            return view.readAttributes();
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    private IndexNode getInode(byte[] path) {
        if (path == null) {
            throw new NullPointerException("path");
        }
        IndexNode key = IndexNode.keyOf(path);
        IndexNode inode = this.inodes.get(key);
        if (inode != null) {
            return inode;
        }
        if (path.length == 0 || path[path.length - 1] != 47) {
            byte[] path2 = Arrays.copyOf(path, path.length + 1);
            path2[path2.length - 1] = 47;
            return this.inodes.get(key.as(path2));
        }
        return inode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Entry getEntry0(byte[] path) throws IOException {
        IndexNode inode = getInode(path);
        if (inode instanceof Entry) {
            return (Entry) inode;
        }
        if (inode == null || inode.pos == -1) {
            return null;
        }
        return Entry.readCEN(this, inode.pos);
    }

    public void deleteFile(byte[] path, boolean failIfNotExists) throws IOException {
        checkWritable();
        IndexNode inode = getInode(path);
        if (inode == null) {
            if (path != null && path.length == 0) {
                throw new ZipException("root directory </> can't not be delete");
            }
            if (failIfNotExists) {
                throw new NoSuchFileException(getString(path));
            }
            return;
        }
        if (inode.isDir() && inode.child != null) {
            throw new DirectoryNotEmptyException(getString(path));
        }
        updateDelete(inode);
    }

    private static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] copyBuf = new byte[8192];
        while (true) {
            int n = is.read(copyBuf);
            if (n != -1) {
                os.write(copyBuf, 0, n);
            } else {
                return;
            }
        }
    }

    private OutputStream getOutputStream(Entry e) throws IOException {
        OutputStream os;
        if (e.mtime == -1) {
            e.mtime = System.currentTimeMillis();
        }
        if (e.method == -1) {
            e.method = 8;
        }
        e.flag = 0;
        if (this.zc.isUTF8()) {
            e.flag |= 2048;
        }
        if (this.useTempFile || e.size >= 10485760) {
            e.file = getTempPathForEntry(null);
            os = Files.newOutputStream(e.file, StandardOpenOption.WRITE);
        } else {
            os = new FileRolloverOutputStream(e);
        }
        return new EntryOutputStream(e, os);
    }

    private class FileRolloverOutputStream extends OutputStream {
        private ByteArrayOutputStream baos;
        private final Entry entry;
        private OutputStream tmpFileOS;
        private long totalWritten;

        private FileRolloverOutputStream(Entry e) {
            this.baos = new ByteArrayOutputStream(8192);
            this.totalWritten = 0L;
            this.entry = e;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            if (this.tmpFileOS != null) {
                writeToFile(b);
            } else if (this.totalWritten + 1 < 10485760) {
                this.baos.write(b);
                this.totalWritten++;
            } else {
                transferToFile();
                writeToFile(b);
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            if (this.tmpFileOS != null) {
                writeToFile(b, off, len);
            } else if (this.totalWritten + ((long) len) < 10485760) {
                this.baos.write(b, off, len);
                this.totalWritten += (long) len;
            } else {
                transferToFile();
                writeToFile(b, off, len);
            }
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            if (this.tmpFileOS != null) {
                this.tmpFileOS.flush();
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.baos = null;
            if (this.tmpFileOS != null) {
                this.tmpFileOS.close();
            }
        }

        private void writeToFile(int b) throws IOException {
            this.tmpFileOS.write(b);
            this.totalWritten++;
        }

        private void writeToFile(byte[] b, int off, int len) throws IOException {
            this.tmpFileOS.write(b, off, len);
            this.totalWritten += (long) len;
        }

        private void transferToFile() throws IOException {
            this.entry.file = ZipFileSystem.this.getTempPathForEntry(null);
            this.tmpFileOS = new BufferedOutputStream(Files.newOutputStream(this.entry.file, new OpenOption[0]));
            this.baos.writeTo(this.tmpFileOS);
            this.baos = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public byte[] toByteArray() {
            if (this.baos == null) {
                return null;
            }
            return this.baos.toByteArray();
        }
    }

    private InputStream getInputStream(Entry e) throws IOException {
        InputStream eis;
        ZipFileSystem zipFileSystem;
        if (e.type == 2) {
            if (e.bytes != null) {
                eis = new ByteArrayInputStream(e.bytes);
            } else if (e.file != null) {
                eis = Files.newInputStream(e.file, new OpenOption[0]);
            } else {
                throw new ZipException("update entry data is missing");
            }
        } else {
            if (e.type == 3) {
                InputStream eis2 = Files.newInputStream(e.file, new OpenOption[0]);
                return eis2;
            }
            eis = new EntryInputStream(e, this.ch);
        }
        if (e.method == 8) {
            long bufSize = e.size + 2;
            if (bufSize > 65536) {
                bufSize = 8192;
            }
            final long size = e.size;
            zipFileSystem = this;
            eis = new InflaterInputStream(eis, getInflater(), (int) bufSize) { // from class: com.sun.nio.zipfs.ZipFileSystem.5
                private boolean eof;
                private boolean isClosed = false;

                @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    if (!this.isClosed) {
                        ZipFileSystem.this.releaseInflater(this.inf);
                        this.in.close();
                        this.isClosed = true;
                        ZipFileSystem.this.streams.remove(this);
                    }
                }

                @Override // java.util.zip.InflaterInputStream
                protected void fill() throws IOException {
                    if (this.eof) {
                        throw new EOFException("Unexpected end of ZLIB input stream");
                    }
                    this.len = this.in.read(this.buf, 0, this.buf.length);
                    if (this.len == -1) {
                        this.buf[0] = 0;
                        this.len = 1;
                        this.eof = true;
                    }
                    this.inf.setInput(this.buf, 0, this.len);
                }

                @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
                public int available() throws IOException {
                    if (this.isClosed) {
                        return 0;
                    }
                    long avail = size - this.inf.getBytesWritten();
                    if (avail > 2147483647L) {
                        return Integer.MAX_VALUE;
                    }
                    return (int) avail;
                }
            };
        } else {
            zipFileSystem = this;
            if (e.method != 0) {
                throw new ZipException("invalid compression method");
            }
        }
        zipFileSystem.streams.add(eis);
        return eis;
    }

    private class EntryInputStream extends InputStream {
        private long pos;
        protected long rem;
        protected final long size;
        private final SeekableByteChannel zfch;

        EntryInputStream(Entry e, SeekableByteChannel zfch) throws IOException {
            this.zfch = zfch;
            this.rem = e.csize;
            this.size = e.size;
            this.pos = ZipFileSystem.this.getDataPos(e);
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            long n;
            ZipFileSystem.this.ensureOpen();
            if (this.rem == 0) {
                return -1;
            }
            if (len <= 0) {
                return 0;
            }
            if (len > this.rem) {
                len = (int) this.rem;
            }
            ByteBuffer bb = ByteBuffer.wrap(b);
            bb.position(off);
            bb.limit(off + len);
            synchronized (this.zfch) {
                n = this.zfch.position(this.pos).read(bb);
            }
            if (n > 0) {
                this.pos += n;
                this.rem -= n;
            }
            if (this.rem == 0) {
                close();
            }
            return (int) n;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            byte[] b = new byte[1];
            if (read(b, 0, 1) == 1) {
                return b[0] & UByte.MAX_VALUE;
            }
            return -1;
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            ZipFileSystem.this.ensureOpen();
            if (n > this.rem) {
                n = this.rem;
            }
            this.pos += n;
            this.rem -= n;
            if (this.rem == 0) {
                close();
            }
            return n;
        }

        @Override // java.io.InputStream
        public int available() {
            if (this.rem > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) this.rem;
        }

        public long size() {
            return this.size;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.rem = 0L;
            ZipFileSystem.this.streams.remove(this);
        }
    }

    class EntryOutputStream extends DeflaterOutputStream {
        private CRC32 crc;
        private Entry e;
        private boolean isClosed;
        private long written;

        EntryOutputStream(Entry e, OutputStream os) throws IOException {
            super(os, ZipFileSystem.this.getDeflater());
            this.isClosed = false;
            if (e == null) {
                throw new NullPointerException("Zip entry is null");
            }
            this.e = e;
            this.crc = new CRC32();
        }

        @Override // java.util.zip.DeflaterOutputStream, java.io.FilterOutputStream, java.io.OutputStream
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            if (this.e.type != 3) {
                ZipFileSystem.this.ensureOpen();
            }
            if (this.isClosed) {
                throw new IOException("Stream closed");
            }
            if (off < 0 || len < 0 || off > b.length - len) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            switch (this.e.method) {
                case 0:
                    this.written += (long) len;
                    this.out.write(b, off, len);
                    break;
                case 8:
                    super.write(b, off, len);
                    break;
                default:
                    throw new ZipException("invalid compression method");
            }
            this.crc.update(b, off, len);
        }

        @Override // java.util.zip.DeflaterOutputStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public synchronized void close() throws IOException {
            if (this.isClosed) {
                return;
            }
            this.isClosed = true;
            switch (this.e.method) {
                case 0:
                    Entry entry = this.e;
                    Entry entry2 = this.e;
                    long j = this.written;
                    entry2.csize = j;
                    entry.size = j;
                    this.e.crc = this.crc.getValue();
                    break;
                case 8:
                    finish();
                    this.e.size = this.def.getBytesRead();
                    this.e.csize = this.def.getBytesWritten();
                    this.e.crc = this.crc.getValue();
                    break;
                default:
                    throw new ZipException("invalid compression method");
            }
            if (this.out instanceof FileRolloverOutputStream) {
                FileRolloverOutputStream fros = (FileRolloverOutputStream) this.out;
                if (fros.tmpFileOS == null) {
                    this.e.bytes = fros.toByteArray();
                }
            }
            if (this.e.type == 3) {
                ZipFileSystem.this.releaseDeflater(this.def);
                return;
            }
            super.close();
            ZipFileSystem.this.releaseDeflater(this.def);
            ZipFileSystem.this.update(this.e);
        }
    }

    static void zerror(String msg) {
        throw new ZipError(msg);
    }

    private Inflater getInflater() {
        synchronized (this.inflaters) {
            int size = this.inflaters.size();
            if (size > 0) {
                Inflater inf = this.inflaters.remove(size - 1);
                return inf;
            }
            return new Inflater(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseInflater(Inflater inf) {
        synchronized (this.inflaters) {
            if (this.inflaters.size() < 20) {
                inf.reset();
                this.inflaters.add(inf);
            } else {
                inf.end();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Deflater getDeflater() {
        synchronized (this.deflaters) {
            int size = this.deflaters.size();
            if (size > 0) {
                Deflater def = this.deflaters.remove(size - 1);
                return def;
            }
            return new Deflater(-1, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseDeflater(Deflater def) {
        synchronized (this.deflaters) {
            if (this.deflaters.size() < 20) {
                def.reset();
                this.deflaters.add(def);
            } else {
                def.end();
            }
        }
    }

    static class END {
        long cenlen;
        long cenoff;
        int centot;
        int comlen;
        byte[] comment;
        int diskNum;
        int disknum;
        int disktot;
        long endpos;
        int endsub;
        int sdisknum;

        END() {
        }

        void write(OutputStream os, long offset) throws IOException {
            boolean hasZip64 = false;
            long xlen = this.cenlen;
            long xoff = this.cenoff;
            if (xlen >= 4294967295L) {
                xlen = 4294967295L;
                hasZip64 = true;
            }
            if (xoff >= 4294967295L) {
                xoff = 4294967295L;
                hasZip64 = true;
            }
            int count = this.centot;
            if (count >= 65535) {
                count = 65535;
                hasZip64 = true;
            }
            if (hasZip64) {
                ZipUtils.writeInt(os, 101075792L);
                ZipUtils.writeLong(os, 44L);
                ZipUtils.writeShort(os, 45);
                ZipUtils.writeShort(os, 45);
                ZipUtils.writeInt(os, 0L);
                ZipUtils.writeInt(os, 0L);
                ZipUtils.writeLong(os, this.centot);
                ZipUtils.writeLong(os, this.centot);
                ZipUtils.writeLong(os, this.cenlen);
                ZipUtils.writeLong(os, this.cenoff);
                ZipUtils.writeInt(os, 117853008L);
                ZipUtils.writeInt(os, 0L);
                ZipUtils.writeLong(os, offset);
                ZipUtils.writeInt(os, 1L);
            }
            long off64 = ZipConstants.ENDSIG;
            ZipUtils.writeInt(os, off64);
            ZipUtils.writeShort(os, 0);
            ZipUtils.writeShort(os, 0);
            ZipUtils.writeShort(os, count);
            ZipUtils.writeShort(os, count);
            ZipUtils.writeInt(os, xlen);
            ZipUtils.writeInt(os, xoff);
            if (this.comment != null) {
                ZipUtils.writeShort(os, this.comment.length);
                ZipUtils.writeBytes(os, this.comment);
            } else {
                ZipUtils.writeShort(os, 0);
            }
        }
    }

    static class IndexNode {
        IndexNode child;
        int hashcode;
        byte[] name;
        int pos;
        IndexNode sibling;

        IndexNode(byte[] name, int pos) {
            this.pos = -1;
            name(name);
            this.pos = pos;
        }

        static final IndexNode keyOf(byte[] name) {
            return new IndexNode(name, -1);
        }

        final void name(byte[] name) {
            this.name = name;
            this.hashcode = Arrays.hashCode(name);
        }

        final IndexNode as(byte[] name) {
            name(name);
            return this;
        }

        boolean isDir() {
            return this.name != null && (this.name.length == 0 || this.name[this.name.length - 1] == 47);
        }

        public boolean equals(Object other) {
            if (!(other instanceof IndexNode)) {
                return false;
            }
            return Arrays.equals(this.name, ((IndexNode) other).name);
        }

        public int hashCode() {
            return this.hashcode;
        }

        IndexNode() {
            this.pos = -1;
        }
    }

    static class Entry extends IndexNode {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        static final int CEN = 1;
        static final int COPY = 4;
        static final int FILECH = 3;
        static final int NEW = 2;
        long atime;
        int attrs;
        long attrsEx;
        byte[] bytes;
        byte[] comment;
        long crc;
        long csize;
        long ctime;
        int disk;
        byte[] extra;
        Path file;
        int flag;
        long locoff;
        int method;
        long mtime;
        long size;
        int type;
        int version;
        int versionMade;

        Entry() {
            this.type = 1;
            this.method = -1;
            this.mtime = -1L;
            this.atime = -1L;
            this.ctime = -1L;
            this.crc = -1L;
            this.csize = -1L;
            this.size = -1L;
        }

        Entry(byte[] name) {
            this.type = 1;
            this.method = -1;
            this.mtime = -1L;
            this.atime = -1L;
            this.ctime = -1L;
            this.crc = -1L;
            this.csize = -1L;
            this.size = -1L;
            name(name);
            long jCurrentTimeMillis = System.currentTimeMillis();
            this.atime = jCurrentTimeMillis;
            this.ctime = jCurrentTimeMillis;
            this.mtime = jCurrentTimeMillis;
            this.crc = 0L;
            this.size = 0L;
            this.csize = 0L;
            this.method = 8;
        }

        Entry(byte[] name, int type) {
            this(name);
            this.type = type;
        }

        Entry(Entry e, int type) {
            this.type = 1;
            this.method = -1;
            this.mtime = -1L;
            this.atime = -1L;
            this.ctime = -1L;
            this.crc = -1L;
            this.csize = -1L;
            this.size = -1L;
            name(e.name);
            this.version = e.version;
            this.ctime = e.ctime;
            this.atime = e.atime;
            this.mtime = e.mtime;
            this.crc = e.crc;
            this.size = e.size;
            this.csize = e.csize;
            this.method = e.method;
            this.extra = e.extra;
            this.versionMade = e.versionMade;
            this.disk = e.disk;
            this.attrs = e.attrs;
            this.attrsEx = e.attrsEx;
            this.locoff = e.locoff;
            this.comment = e.comment;
            this.type = type;
        }

        Entry(byte[] name, Path file, int type) {
            this(name, type);
            this.file = file;
            this.method = 0;
        }

        int version() throws ZipException {
            if (this.method == 8) {
                return 20;
            }
            if (this.method == 0) {
                return 10;
            }
            throw new ZipException("unsupported compression method");
        }

        static Entry readCEN(ZipFileSystem zipfs, int pos) throws IOException {
            return new Entry().cen(zipfs, pos);
        }

        private Entry cen(ZipFileSystem zipfs, int pos) throws IOException {
            byte[] cen = zipfs.cen;
            if (!ZipConstants.cenSigAt(cen, pos)) {
                ZipFileSystem.zerror("invalid CEN header (bad signature)");
            }
            this.versionMade = ZipConstants.CENVEM(cen, pos);
            this.version = ZipConstants.CENVER(cen, pos);
            this.flag = ZipConstants.CENFLG(cen, pos);
            this.method = ZipConstants.CENHOW(cen, pos);
            this.mtime = ZipUtils.dosToJavaTime(ZipConstants.CENTIM(cen, pos));
            this.crc = ZipConstants.CENCRC(cen, pos);
            this.csize = ZipConstants.CENSIZ(cen, pos);
            this.size = ZipConstants.CENLEN(cen, pos);
            int nlen = ZipConstants.CENNAM(cen, pos);
            int elen = ZipConstants.CENEXT(cen, pos);
            int clen = ZipConstants.CENCOM(cen, pos);
            this.disk = ZipConstants.CENDSK(cen, pos);
            this.attrs = ZipConstants.CENATT(cen, pos);
            this.attrsEx = ZipConstants.CENATX(cen, pos);
            this.locoff = ZipConstants.CENOFF(cen, pos);
            int pos2 = pos + 46;
            name(zipfs.decodeCenName(Arrays.copyOfRange(cen, pos2, pos2 + nlen), this.flag));
            int pos3 = pos2 + nlen;
            if (elen > 0) {
                this.extra = Arrays.copyOfRange(cen, pos3, pos3 + elen);
                pos3 += elen;
                readExtra(zipfs);
            }
            if (clen > 0) {
                this.comment = Arrays.copyOfRange(cen, pos3, pos3 + clen);
            }
            return this;
        }

        int writeCEN(OutputStream os) throws IOException {
            long csize0;
            long size0;
            long locoff0;
            int eoff;
            int elen64;
            int elenNTFS;
            int elenEXTT;
            int elenNTFS2;
            int elenEXTT2;
            int i;
            int version0 = version();
            long csize1 = this.csize;
            long size1 = this.size;
            long locoff1 = this.locoff;
            int elen65 = 0;
            int tag = 0;
            int elenEXTT3 = 0;
            boolean foundExtraTime = false;
            int nlen = this.name != null ? this.name.length : 0;
            int elen = this.extra != null ? this.extra.length : 0;
            int clen = this.comment != null ? this.comment.length : 0;
            long csize2 = this.csize;
            if (csize2 < 4294967295L) {
                csize0 = csize1;
            } else {
                csize0 = 4294967295L;
                elen65 = 0 + 8;
            }
            long size2 = this.size;
            if (size2 < 4294967295L) {
                size0 = size1;
            } else {
                size0 = 4294967295L;
                elen65 += 8;
            }
            long locoff2 = this.locoff;
            if (locoff2 >= 4294967295L) {
                locoff0 = 4294967295L;
                elen65 += 8;
            }
            if (elen65 != 0) {
                locoff0 = locoff1;
                eoff = elen65 + 4;
                elen64 = 0;
            } else {
                locoff0 = locoff1;
                eoff = elen65;
                elen64 = 0;
            }
            while (true) {
                elenNTFS = tag;
                elenEXTT = elenEXTT3;
                if (elen64 + 4 >= elen) {
                    break;
                }
                int tag2 = ZipConstants.SH(this.extra, elen64);
                int eoff2 = elen64;
                int sz = ZipConstants.SH(this.extra, eoff2 + 2);
                if (tag2 == 21589 || tag2 == 10) {
                    foundExtraTime = true;
                }
                elen64 = eoff2 + sz + 4;
                tag = elenNTFS;
                elenEXTT3 = elenEXTT;
            }
            if (foundExtraTime) {
                elenNTFS2 = elenNTFS;
                elenEXTT2 = elenEXTT;
            } else if (ZipFileSystem.isWindows) {
                elenNTFS2 = 36;
                elenEXTT2 = elenEXTT;
            } else {
                elenEXTT2 = 9;
                elenNTFS2 = elenNTFS;
            }
            int elenNTFS3 = elenNTFS2;
            ZipUtils.writeInt(os, ZipConstants.CENSIG);
            if (eoff != 0) {
                ZipUtils.writeShort(os, 45);
                ZipUtils.writeShort(os, 45);
            } else {
                ZipUtils.writeShort(os, version0);
                ZipUtils.writeShort(os, version0);
            }
            ZipUtils.writeShort(os, this.flag);
            ZipUtils.writeShort(os, this.method);
            ZipUtils.writeInt(os, (int) ZipUtils.javaToDosTime(this.mtime));
            ZipUtils.writeInt(os, this.crc);
            ZipUtils.writeInt(os, csize0);
            ZipUtils.writeInt(os, size0);
            ZipUtils.writeShort(os, this.name.length);
            ZipUtils.writeShort(os, elen + eoff + elenNTFS3 + elenEXTT2);
            if (this.comment != null) {
                ZipUtils.writeShort(os, Math.min(clen, 65535));
                i = 0;
            } else {
                i = 0;
                ZipUtils.writeShort(os, 0);
            }
            ZipUtils.writeShort(os, i);
            ZipUtils.writeShort(os, i);
            ZipUtils.writeInt(os, 0L);
            ZipUtils.writeInt(os, locoff0);
            ZipUtils.writeBytes(os, this.name);
            if (eoff != 0) {
                ZipUtils.writeShort(os, 1);
                ZipUtils.writeShort(os, eoff - 4);
                if (size0 == 4294967295L) {
                    ZipUtils.writeLong(os, this.size);
                }
                if (csize0 == 4294967295L) {
                    ZipUtils.writeLong(os, this.csize);
                }
                if (locoff0 == 4294967295L) {
                    ZipUtils.writeLong(os, this.locoff);
                }
            }
            if (elenNTFS3 != 0) {
                ZipUtils.writeShort(os, 10);
                ZipUtils.writeShort(os, elenNTFS3 - 4);
                ZipUtils.writeInt(os, 0L);
                ZipUtils.writeShort(os, 1);
                ZipUtils.writeShort(os, 24);
                ZipUtils.writeLong(os, ZipUtils.javaToWinTime(this.mtime));
                ZipUtils.writeLong(os, ZipUtils.javaToWinTime(this.atime));
                ZipUtils.writeLong(os, ZipUtils.javaToWinTime(this.ctime));
            }
            if (elenEXTT2 != 0) {
                ZipUtils.writeShort(os, 21589);
                ZipUtils.writeShort(os, elenEXTT2 - 4);
                if (this.ctime == -1) {
                    os.write(3);
                } else {
                    os.write(7);
                }
                ZipUtils.writeInt(os, ZipUtils.javaToUnixTime(this.mtime));
            }
            if (this.extra != null) {
                ZipUtils.writeBytes(os, this.extra);
            }
            if (this.comment != null) {
                ZipUtils.writeBytes(os, this.comment);
            }
            return nlen + 46 + elen + clen + eoff + elenNTFS3 + elenEXTT2;
        }

        static Entry readLOC(ZipFileSystem zipfs, long pos) throws IOException {
            return readLOC(zipfs, pos, new byte[1024]);
        }

        static Entry readLOC(ZipFileSystem zipfs, long pos, byte[] buf) throws IOException {
            return new Entry().loc(zipfs, pos, buf);
        }

        Entry loc(ZipFileSystem zipfs, long pos, byte[] buf) throws IOException {
            if (buf.length < 30) {
                throw new AssertionError();
            }
            if (zipfs.readFullyAt(buf, 0, 30L, pos) != 30) {
                throw new ZipException("loc: reading failed");
            }
            if (!ZipConstants.locSigAt(buf, 0)) {
                throw new ZipException("loc: wrong sig ->" + Long.toString(ZipConstants.getSig(buf, 0), 16));
            }
            this.version = ZipConstants.LOCVER(buf);
            this.flag = ZipConstants.LOCFLG(buf);
            this.method = ZipConstants.LOCHOW(buf);
            this.mtime = ZipUtils.dosToJavaTime(ZipConstants.LOCTIM(buf));
            this.crc = ZipConstants.LOCCRC(buf);
            this.csize = ZipConstants.LOCSIZ(buf);
            this.size = ZipConstants.LOCLEN(buf);
            int nlen = ZipConstants.LOCNAM(buf);
            int elen = ZipConstants.LOCEXT(buf);
            this.name = new byte[nlen];
            if (zipfs.readFullyAt(this.name, 0, nlen, pos + 30) != nlen) {
                throw new ZipException("loc: name reading failed");
            }
            if (elen > 0) {
                this.extra = new byte[elen];
                if (zipfs.readFullyAt(this.extra, 0, elen, pos + 30 + ((long) nlen)) != elen) {
                    throw new ZipException("loc: ext reading failed");
                }
            }
            long pos2 = ((long) (nlen + 30 + elen)) + pos;
            if ((this.flag & 8) != 0) {
                Entry e = zipfs.getEntry0(this.name);
                if (e == null) {
                    throw new ZipException("loc: name not found in cen");
                }
                this.size = e.size;
                this.csize = e.csize;
                long pos3 = pos2 + (this.method == 0 ? this.size : this.csize);
                if (this.size >= 4294967295L || this.csize >= 4294967295L) {
                    long j = pos3 + 24;
                } else {
                    long j2 = pos3 + 16;
                }
            } else {
                if (this.extra != null && (this.size == 4294967295L || this.csize == 4294967295L)) {
                    int off = 0;
                    while (off + 20 < elen) {
                        int sz = ZipConstants.SH(this.extra, off + 2);
                        if (ZipConstants.SH(this.extra, off) == 1 && sz == 16) {
                            this.size = ZipConstants.LL(this.extra, off + 4);
                            this.csize = ZipConstants.LL(this.extra, off + 12);
                            break;
                        }
                        off += sz + 4;
                    }
                }
                long j3 = pos2 + (this.method == 0 ? this.size : this.csize);
            }
            return this;
        }

        int writeLOC(OutputStream os) throws IOException {
            long j;
            ZipUtils.writeInt(os, ZipConstants.LOCSIG);
            version();
            if (this.name != null) {
                int length = this.name.length;
            }
            int elen = this.extra != null ? this.extra.length : 0;
            boolean foundExtraTime = false;
            int eoff = 0;
            int elen64 = 0;
            int elenEXTT = 0;
            int elenNTFS = 0;
            if ((this.flag & 8) != 0) {
                ZipUtils.writeShort(os, version());
                ZipUtils.writeShort(os, this.flag);
                ZipUtils.writeShort(os, this.method);
                ZipUtils.writeInt(os, (int) ZipUtils.javaToDosTime(this.mtime));
                ZipUtils.writeInt(os, 0L);
                ZipUtils.writeInt(os, 0L);
                ZipUtils.writeInt(os, 0L);
            } else {
                if (this.csize >= 4294967295L || this.size >= 4294967295L) {
                    elen64 = 20;
                    ZipUtils.writeShort(os, 45);
                } else {
                    ZipUtils.writeShort(os, version());
                }
                ZipUtils.writeShort(os, this.flag);
                ZipUtils.writeShort(os, this.method);
                ZipUtils.writeInt(os, (int) ZipUtils.javaToDosTime(this.mtime));
                ZipUtils.writeInt(os, this.crc);
                if (elen64 != 0) {
                    ZipUtils.writeInt(os, 4294967295L);
                    ZipUtils.writeInt(os, 4294967295L);
                } else {
                    ZipUtils.writeInt(os, this.csize);
                    ZipUtils.writeInt(os, this.size);
                }
            }
            while (eoff + 4 < elen) {
                int tag = ZipConstants.SH(this.extra, eoff);
                int sz = ZipConstants.SH(this.extra, eoff + 2);
                if (tag == 21589 || tag == 10) {
                    foundExtraTime = true;
                }
                eoff += sz + 4;
            }
            if (foundExtraTime) {
                j = -1;
            } else if (ZipFileSystem.isWindows) {
                elenNTFS = 36;
                j = -1;
            } else {
                elenEXTT = 9;
                j = -1;
                if (this.atime != -1) {
                    elenEXTT = 9 + 4;
                }
                if (this.ctime != -1) {
                    elenEXTT += 4;
                }
            }
            ZipUtils.writeShort(os, this.name.length);
            ZipUtils.writeShort(os, elen + elen64 + elenNTFS + elenEXTT);
            ZipUtils.writeBytes(os, this.name);
            if (elen64 != 0) {
                ZipUtils.writeShort(os, 1);
                ZipUtils.writeShort(os, 16);
                ZipUtils.writeLong(os, this.size);
                ZipUtils.writeLong(os, this.csize);
            }
            if (elenNTFS != 0) {
                ZipUtils.writeShort(os, 10);
                ZipUtils.writeShort(os, elenNTFS - 4);
                ZipUtils.writeInt(os, 0L);
                ZipUtils.writeShort(os, 1);
                ZipUtils.writeShort(os, 24);
                ZipUtils.writeLong(os, ZipUtils.javaToWinTime(this.mtime));
                ZipUtils.writeLong(os, ZipUtils.javaToWinTime(this.atime));
                ZipUtils.writeLong(os, ZipUtils.javaToWinTime(this.ctime));
            }
            if (elenEXTT != 0) {
                ZipUtils.writeShort(os, 21589);
                ZipUtils.writeShort(os, elenEXTT - 4);
                int fbyte = 1;
                if (this.atime != j) {
                    fbyte = 1 | 2;
                }
                if (this.ctime != j) {
                    fbyte |= 4;
                }
                os.write(fbyte);
                ZipUtils.writeInt(os, ZipUtils.javaToUnixTime(this.mtime));
                if (this.atime != j) {
                    ZipUtils.writeInt(os, ZipUtils.javaToUnixTime(this.atime));
                }
                if (this.ctime != j) {
                    ZipUtils.writeInt(os, ZipUtils.javaToUnixTime(this.ctime));
                }
            }
            if (this.extra != null) {
                ZipUtils.writeBytes(os, this.extra);
            }
            return this.name.length + 30 + elen + elen64 + elenNTFS + elenEXTT;
        }

        int writeEXT(OutputStream os) throws IOException {
            ZipUtils.writeInt(os, ZipConstants.EXTSIG);
            ZipUtils.writeInt(os, this.crc);
            if (this.csize >= 4294967295L || this.size >= 4294967295L) {
                ZipUtils.writeLong(os, this.csize);
                ZipUtils.writeLong(os, this.size);
                return 24;
            }
            ZipUtils.writeInt(os, this.csize);
            ZipUtils.writeInt(os, this.size);
            return 16;
        }

        /* JADX WARN: Code duplicated, block: B:101:0x01ac A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:66:0x0188  */
        /* JADX WARN: Code duplicated, block: B:69:0x018d  */
        /* JADX WARN: Code duplicated, block: B:70:0x0197 A[PHI: r4
  0x0197: PHI (r4v9 'pos' int) = (r4v8 'pos' int), (r4v11 'pos' int) binds: [B:65:0x0186, B:69:0x018d] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARN: Code duplicated, block: B:72:0x019d  */
        /* JADX WARN: Code duplicated, block: B:95:0x01ac A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
        void readExtra(ZipFileSystem zipfs) throws IOException {
            if (this.extra == null) {
                return;
            }
            int elen = this.extra.length;
            int off = 0;
            int newOff = 0;
            while (off + 4 < elen) {
                int pos = off;
                int tag = ZipConstants.SH(this.extra, pos);
                int sz = ZipConstants.SH(this.extra, pos + 2);
                int pos2 = pos + 4;
                if (pos2 + sz <= elen) {
                    switch (tag) {
                        case 1:
                            if (this.size == 4294967295L) {
                                if (pos2 + 8 <= elen) {
                                    this.size = ZipConstants.LL(this.extra, pos2);
                                    pos2 += 8;
                                    if (this.csize != 4294967295L) {
                                        if (pos2 + 8 > elen) {
                                            this.csize = ZipConstants.LL(this.extra, pos2);
                                            pos2 += 8;
                                            if (this.locoff == 4294967295L) {
                                            }
                                        }
                                    } else if (this.locoff == 4294967295L) {
                                    }
                                }
                            } else if (this.csize != 4294967295L) {
                                if (pos2 + 8 > elen) {
                                    this.csize = ZipConstants.LL(this.extra, pos2);
                                    pos2 += 8;
                                    if (this.locoff == 4294967295L) {
                                    }
                                }
                            } else if (this.locoff == 4294967295L && pos2 + 8 <= elen) {
                                this.locoff = ZipConstants.LL(this.extra, pos2);
                                int i = pos2 + 8;
                            }
                            break;
                        case 10:
                            if (sz >= 32) {
                                int pos3 = pos2 + 4;
                                if (ZipConstants.SH(this.extra, pos3) == 1 && ZipConstants.SH(this.extra, pos3 + 2) == 24) {
                                    this.mtime = ZipUtils.winToJavaTime(ZipConstants.LL(this.extra, pos3 + 4));
                                    this.atime = ZipUtils.winToJavaTime(ZipConstants.LL(this.extra, pos3 + 12));
                                    this.ctime = ZipUtils.winToJavaTime(ZipConstants.LL(this.extra, pos3 + 20));
                                }
                            }
                            break;
                        case 21589:
                            byte[] buf = new byte[30];
                            if (zipfs.readFullyAt(buf, 0, buf.length, this.locoff) != buf.length) {
                                throw new ZipException("loc: reading failed");
                            }
                            if (!ZipConstants.locSigAt(buf, 0)) {
                                throw new ZipException("loc: wrong sig ->" + Long.toString(ZipConstants.getSig(buf, 0), 16));
                            }
                            int locElen = ZipConstants.LOCEXT(buf);
                            if (locElen < 9) {
                                continue;
                            } else {
                                int locNlen = ZipConstants.LOCNAM(buf);
                                byte[] buf2 = new byte[locElen];
                                if (zipfs.readFullyAt(buf2, 0, buf2.length, this.locoff + 30 + ((long) locNlen)) != buf2.length) {
                                    throw new ZipException("loc extra: reading failed");
                                }
                                int locPos = 0;
                                while (locPos + 4 < buf2.length) {
                                    int locTag = ZipConstants.SH(buf2, locPos);
                                    int locSZ = ZipConstants.SH(buf2, locPos + 2);
                                    int locPos2 = locPos + 4;
                                    if (locTag != 21589) {
                                        locPos = locPos2 + locSZ;
                                    } else {
                                        int end = (locPos2 + locSZ) - 4;
                                        int locPos3 = locPos2 + 1;
                                        int flag = ZipConstants.CH(buf2, locPos2);
                                        if ((flag & 1) != 0 && locPos3 <= end) {
                                            this.mtime = ZipUtils.unixToJavaTime(ZipConstants.LG(buf2, locPos3));
                                            locPos3 += 4;
                                        }
                                        if ((flag & 2) != 0 && locPos3 <= end) {
                                            this.atime = ZipUtils.unixToJavaTime(ZipConstants.LG(buf2, locPos3));
                                            locPos3 += 4;
                                        }
                                        if ((flag & 4) != 0 && locPos3 <= end) {
                                            this.ctime = ZipUtils.unixToJavaTime(ZipConstants.LG(buf2, locPos3));
                                            int i2 = locPos3 + 4;
                                        }
                                    }
                                }
                            }
                            break;
                            break;
                        default:
                            System.arraycopy(this.extra, off, this.extra, newOff, sz + 4);
                            newOff += sz + 4;
                            break;
                    }
                    off += sz + 4;
                } else if (newOff != 0 || newOff == this.extra.length) {
                    this.extra = null;
                } else {
                    this.extra = Arrays.copyOf(this.extra, newOff);
                    return;
                }
            }
            if (newOff != 0) {
            }
            this.extra = null;
        }
    }

    private static class ExChannelCloser {
        SeekableByteChannel ch;
        Path path;
        Set<InputStream> streams;

        ExChannelCloser(Path path, SeekableByteChannel ch, Set<InputStream> streams) {
            this.path = path;
            this.ch = ch;
            this.streams = streams;
        }
    }

    private void addToTree(IndexNode inode, HashSet<IndexNode> dirs) {
        IndexNode parent;
        if (dirs.contains(inode)) {
            return;
        }
        byte[] name = inode.name;
        byte[] pname = getParent(name);
        if (this.inodes.containsKey(this.LOOKUPKEY.as(pname))) {
            parent = this.inodes.get(this.LOOKUPKEY);
        } else {
            parent = new IndexNode(pname, -1);
            this.inodes.put(parent, parent);
        }
        addToTree(parent, dirs);
        inode.sibling = parent.child;
        parent.child = inode;
        if (name[name.length - 1] == 47) {
            dirs.add(inode);
        }
    }

    private void removeFromTree(IndexNode inode) {
        IndexNode last;
        IndexNode parent = this.inodes.get(this.LOOKUPKEY.as(getParent(inode.name)));
        IndexNode child = parent.child;
        if (child.equals(inode)) {
            parent.child = child.sibling;
            return;
        }
        do {
            last = child;
            IndexNode indexNode = child.sibling;
            child = indexNode;
            if (indexNode == null) {
                return;
            }
        } while (!child.equals(inode));
        last.sibling = child.sibling;
    }

    private void buildNodeTree() throws IOException {
        beginWrite();
        try {
            HashSet<IndexNode> dirs = new HashSet<>();
            IndexNode root = new IndexNode(ROOTPATH, -1);
            this.inodes.put(root, root);
            dirs.add(root);
            for (IndexNode node : (IndexNode[]) this.inodes.keySet().toArray(new IndexNode[0])) {
                addToTree(node, dirs);
            }
            endWrite();
        } catch (Throwable th) {
            endWrite();
            throw th;
        }
    }
}

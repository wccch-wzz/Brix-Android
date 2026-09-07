package com.brixcore.util.io;

import com.brixcore.util.Lang;
import com.brixcore.util.platform.OperatingSystem;
import com.brixcore.util.tree.ZipFileTree;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.sun.nio.zipfs.ZipFileSystemProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.CharEncoding;

/* JADX INFO: loaded from: classes3.dex */
public final class CompressingUtils {
    private static final FileSystemProvider ZIPFS_PROVIDER = new ZipFileSystemProvider();

    private CompressingUtils() {
    }

    private static CharsetDecoder newCharsetDecoder(Charset charset) {
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    public static boolean testEncoding(Path zipFile, Charset encoding) throws IOException {
        ZipFile zf = openZipFile(zipFile, encoding);
        try {
            boolean zTestEncoding = testEncoding(zf, encoding);
            if (zf != null) {
                zf.close();
            }
            return zTestEncoding;
        } catch (Throwable th) {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static boolean testEncoding(ZipFile zipFile, Charset encoding) {
        CharsetDecoder cd = newCharsetDecoder(encoding);
        CharBuffer cb = CharBuffer.allocate(32);
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            if (!entry.getGeneralPurposeBit().usesUTF8ForNames()) {
                cd.reset();
                byte[] ba = entry.getRawName();
                int clen = (int) (ba.length * cd.maxCharsPerByte());
                if (clen != 0) {
                    if (clen <= cb.capacity()) {
                        cb.clear();
                    } else {
                        cb = CharBuffer.allocate(clen);
                    }
                    ByteBuffer bb = ByteBuffer.wrap(ba, 0, ba.length);
                    CoderResult cr = cd.decode(bb, cb, true);
                    if (!cr.isUnderflow()) {
                        return false;
                    }
                    CoderResult cr2 = cd.flush(cb);
                    if (!cr2.isUnderflow()) {
                        return false;
                    }
                } else {
                    continue;
                }
            }
        }
        return true;
    }

    public static Charset findSuitableEncoding(Path zipFile) throws IOException {
        ZipFile zf = openZipFile(zipFile, StandardCharsets.UTF_8);
        try {
            Charset charsetFindSuitableEncoding = findSuitableEncoding(zf);
            if (zf != null) {
                zf.close();
            }
            return charsetFindSuitableEncoding;
        } catch (Throwable th) {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static Charset findSuitableEncoding(ZipFile zipFile) throws IOException {
        if (testEncoding(zipFile, StandardCharsets.UTF_8)) {
            return StandardCharsets.UTF_8;
        }
        if (OperatingSystem.NATIVE_CHARSET != StandardCharsets.UTF_8 && testEncoding(zipFile, OperatingSystem.NATIVE_CHARSET)) {
            return OperatingSystem.NATIVE_CHARSET;
        }
        String[] candidates = {"GB18030", "Big5", "Shift_JIS", "EUC-JP", "ISO-2022-JP", "EUC-KR", "ISO-2022-KR", "KOI8-R", "windows-1251", "x-MacCyrillic", "IBM855", "IBM866", "windows-1252", CharEncoding.ISO_8859_1, "ISO-8859-5", "ISO-8859-7", "ISO-8859-8", CharEncoding.UTF_16LE, CharEncoding.UTF_16BE, "UTF-32LE", "UTF-32BE"};
        for (String candidate : candidates) {
            try {
                Charset charset = Charset.forName(candidate);
                if (!charset.equals(OperatingSystem.NATIVE_CHARSET) && testEncoding(zipFile, charset)) {
                    return charset;
                }
            } catch (IllegalArgumentException e) {
            }
        }
        throw new IOException("Cannot find suitable encoding for the zip.");
    }

    public static ZipFileTree openZipTree(Path zipFile) throws IOException {
        return new ZipFileTree(openZipFile(zipFile));
    }

    public static ZipFileTree openZipTree(Path zipFile, Charset charset) throws IOException {
        return new ZipFileTree(openZipFile(zipFile, charset));
    }

    public static ZipFile openZipFile(Path zipFile) throws IOException {
        return openZipFileWithPossibleEncoding(zipFile, StandardCharsets.UTF_8);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static ZipFile openZipFile(Path zipFile, Charset charset) throws IOException {
        return ((ZipFile.Builder) ZipFile.builder().setPath(zipFile)).setCharset(charset).get();
    }

    public static ZipFile openZipFileWithPossibleEncoding(Path zipFile, Charset possibleEncoding) throws Throwable {
        Charset suitableEncoding;
        if (possibleEncoding == null) {
            possibleEncoding = StandardCharsets.UTF_8;
        }
        ZipFile zipReader = ZipFile.builder().setSeekableByteChannel(Files.newByteChannel(zipFile, new OpenOption[0])).get();
        try {
            if (possibleEncoding != StandardCharsets.UTF_8 && testEncoding(zipReader, possibleEncoding)) {
                suitableEncoding = possibleEncoding;
            } else {
                suitableEncoding = findSuitableEncoding(zipReader);
                if (suitableEncoding == StandardCharsets.UTF_8) {
                    return zipReader;
                }
            }
            zipReader.close();
            return ZipFile.builder().setSeekableByteChannel(Files.newByteChannel(zipFile, new OpenOption[0])).setCharset(suitableEncoding).get();
        } catch (Throwable e) {
            IOUtils.closeQuietly(zipReader, e);
            throw e;
        }
    }

    public static final class Builder {
        private final boolean create;
        private final Path zip;
        private boolean autoDetectEncoding = false;
        private Charset encoding = StandardCharsets.UTF_8;
        private boolean useTempFile = false;

        public Builder(Path zip, boolean create) {
            this.zip = zip;
            this.create = create;
        }

        public Builder setAutoDetectEncoding(boolean autoDetectEncoding) {
            this.autoDetectEncoding = autoDetectEncoding;
            return this;
        }

        public Builder setEncoding(Charset encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder setUseTempFile(boolean useTempFile) {
            this.useTempFile = useTempFile;
            return this;
        }

        public FileSystem build() throws IOException {
            if (this.autoDetectEncoding && !CompressingUtils.testEncoding(this.zip, this.encoding)) {
                this.encoding = CompressingUtils.findSuitableEncoding(this.zip);
            }
            return CompressingUtils.createZipFileSystem(this.zip, this.create, this.useTempFile, this.encoding);
        }
    }

    public static Builder readonly(Path zipFile) {
        return new Builder(zipFile, false);
    }

    public static Builder writable(Path zipFile) {
        return new Builder(zipFile, true).setUseTempFile(true);
    }

    public static FileSystem createReadOnlyZipFileSystem(Path zipFile) throws IOException {
        return createReadOnlyZipFileSystem(zipFile, null);
    }

    public static FileSystem createReadOnlyZipFileSystem(Path zipFile, Charset charset) throws IOException {
        return createZipFileSystem(zipFile, false, false, charset);
    }

    public static FileSystem createWritableZipFileSystem(Path zipFile) throws IOException {
        return createWritableZipFileSystem(zipFile, null);
    }

    public static FileSystem createWritableZipFileSystem(Path zipFile, Charset charset) throws IOException {
        return createZipFileSystem(zipFile, true, true, charset);
    }

    public static FileSystem createZipFileSystem(Path zipFile, boolean create, boolean useTempFile, Charset encoding) throws IOException {
        Map<String, Object> env = new HashMap<>();
        if (create) {
            env.put("create", "true");
        }
        if (encoding != null) {
            env.put("encoding", encoding.name());
        }
        if (useTempFile) {
            env.put("useTempFile", true);
        }
        try {
            if (ZIPFS_PROVIDER == null) {
                throw new FileSystemNotFoundException("Module jdk.zipfs does not exist");
            }
            return ZIPFS_PROVIDER.newFileSystem(zipFile, (Map<String, ?>) env);
        } catch (UnsupportedOperationException e) {
            throw new ZipException("Not a zip file");
        } catch (FileSystemNotFoundException ex) {
            throw ((ZipException) Lang.apply(new ZipException("Java Environment is broken"), new Consumer() { // from class: com.brixcore.util.io.CompressingUtils$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZipException) obj).initCause(ex);
                }
            }));
        } catch (ZipError e2) {
            ZipException exception = new ZipException("Corrupted zip file");
            exception.initCause(e2);
            throw exception;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String readTextZipEntry(Path zipFile, String name) throws Throwable {
        ZipFile s = ((ZipFile.Builder) ZipFile.builder().setPath(zipFile)).get();
        try {
            String textZipEntry = readTextZipEntry(s, name);
            if (s != null) {
                s.close();
            }
            return textZipEntry;
        } catch (Throwable th) {
            if (s != null) {
                try {
                    s.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static String readTextZipEntry(ZipFile zipFile, String name) throws IOException {
        return IOUtils.readFullyAsString(zipFile.getInputStream(zipFile.getEntry(name)));
    }

    public static String readTextZipEntry(Path zipFile, String name, Charset encoding) throws IOException {
        ZipFile s = openZipFile(zipFile, encoding);
        try {
            String fullyAsString = IOUtils.readFullyAsString(s.getInputStream(s.getEntry(name)));
            if (s != null) {
                s.close();
            }
            return fullyAsString;
        } catch (Throwable th) {
            if (s != null) {
                try {
                    s.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static Optional<String> readTextZipEntryQuietly(Path file, String name, Charset encoding) {
        try {
            return Optional.of(readTextZipEntry(file, name, encoding));
        } catch (IOException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public static void extract(File archive, File destination) throws Throwable {
        String name = archive.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".zip") || name.endsWith(".jar") || name.endsWith(".mrpack")) {
            extractZip(archive, destination);
        } else if (name.endsWith(".7z")) {
            extract7z(archive, destination);
        } else {
            if (name.endsWith(".rar")) {
                extractRar(archive, destination);
                return;
            }
            throw new IOException("Unsupported archive format: " + archive.getName());
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0069 */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void extractZip(java.io.File r8, java.io.File r9) throws java.lang.Throwable {
        /*
            org.apache.commons.compress.archivers.zip.ZipFile$Builder r0 = org.apache.commons.compress.archivers.zip.ZipFile.builder()
            org.apache.commons.io.build.AbstractOriginSupplier r0 = r0.setFile(r8)
            org.apache.commons.compress.archivers.zip.ZipFile$Builder r0 = (org.apache.commons.compress.archivers.zip.ZipFile.Builder) r0
            org.apache.commons.compress.archivers.zip.ZipFile r0 = r0.get()
            java.util.Enumeration r1 = r0.getEntries()     // Catch: java.lang.Throwable -> L69
        L12:
            boolean r2 = r1.hasMoreElements()     // Catch: java.lang.Throwable -> L69
            if (r2 == 0) goto L63
            java.lang.Object r2 = r1.nextElement()     // Catch: java.lang.Throwable -> L69
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry r2 = (org.apache.commons.compress.archivers.zip.ZipArchiveEntry) r2     // Catch: java.lang.Throwable -> L69
            java.io.File r3 = new java.io.File     // Catch: java.lang.Throwable -> L69
            java.lang.String r4 = r2.getName()     // Catch: java.lang.Throwable -> L69
            r3.<init>(r9, r4)     // Catch: java.lang.Throwable -> L69
            boolean r4 = r2.isDirectory()     // Catch: java.lang.Throwable -> L69
            if (r4 == 0) goto L31
            r3.mkdirs()     // Catch: java.lang.Throwable -> L69
            goto L4c
        L31:
            java.io.File r4 = r3.getParentFile()     // Catch: java.lang.Throwable -> L69
            r4.mkdirs()     // Catch: java.lang.Throwable -> L69
            java.io.InputStream r4 = r0.getInputStream(r2)     // Catch: java.lang.Throwable -> L69
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L57
            r5.<init>(r3)     // Catch: java.lang.Throwable -> L57
            com.brixcore.util.io.IOUtils.copyTo(r4, r5)     // Catch: java.lang.Throwable -> L4d
            r5.close()     // Catch: java.lang.Throwable -> L57
            if (r4 == 0) goto L4c
            r4.close()     // Catch: java.lang.Throwable -> L69
        L4c:
            goto L12
        L4d:
            r6 = move-exception
            r5.close()     // Catch: java.lang.Throwable -> L52
            goto L56
        L52:
            r7 = move-exception
            r6.addSuppressed(r7)     // Catch: java.lang.Throwable -> L57
        L56:
            throw r6     // Catch: java.lang.Throwable -> L57
        L57:
            r5 = move-exception
            if (r4 == 0) goto L62
            r4.close()     // Catch: java.lang.Throwable -> L5e
            goto L62
        L5e:
            r6 = move-exception
            r5.addSuppressed(r6)     // Catch: java.lang.Throwable -> L69
        L62:
            throw r5     // Catch: java.lang.Throwable -> L69
        L63:
            if (r0 == 0) goto L68
            r0.close()
        L68:
            return
        L69:
            r1 = move-exception
            if (r0 == 0) goto L74
            r0.close()     // Catch: java.lang.Throwable -> L70
            goto L74
        L70:
            r2 = move-exception
            r1.addSuppressed(r2)
        L74:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.brixcore.util.io.CompressingUtils.extractZip(java.io.File, java.io.File):void");
    }

    /* JADX WARN: Code duplicated, block: B:39:0x005b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    public static void extract7z(File sevenZFile, File destination) throws IOException {
        SevenZFile zf = ((SevenZFile.Builder) SevenZFile.builder().setFile(sevenZFile)).get();
        while (true) {
            try {
                SevenZArchiveEntry entry = zf.getNextEntry();
                if (entry == null) {
                    break;
                }
                File out = new File(destination, entry.getName());
                if (entry.isDirectory()) {
                    out.mkdirs();
                } else {
                    out.getParentFile().mkdirs();
                    FileOutputStream os = new FileOutputStream(out);
                    try {
                        byte[] buffer = new byte[8192];
                        while (true) {
                            int len = zf.read(buffer);
                            if (len <= 0) {
                                break;
                            } else {
                                os.write(buffer, 0, len);
                            }
                            if (zf != null) {
                                try {
                                    zf.close();
                                } catch (Throwable th) {
                                    th.addSuppressed(th);
                                }
                            }
                            throw th;
                        }
                        os.close();
                    } catch (Throwable th2) {
                        try {
                            os.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                        throw th2;
                    }
                }
            } catch (Throwable th4) {
                if (zf != null) {
                    zf.close();
                }
                throw th4;
            }
        }
        if (zf != null) {
            zf.close();
        }
    }

    public static void extractRar(File rarFile, File destination) throws IOException {
        try {
            Archive archive = new Archive(rarFile);
            while (true) {
                try {
                    FileHeader fh = archive.nextFileHeader();
                    if (fh != null) {
                        String fileName = fh.getFileName();
                        File out = new File(destination, fileName.replace(org.apache.commons.io.IOUtils.DIR_SEPARATOR_WINDOWS, org.apache.commons.io.IOUtils.DIR_SEPARATOR_UNIX));
                        if (fh.isDirectory()) {
                            out.mkdirs();
                        } else {
                            out.getParentFile().mkdirs();
                            FileOutputStream os = new FileOutputStream(out);
                            try {
                                archive.extractFile(fh, os);
                                os.close();
                            } catch (Throwable th) {
                                try {
                                    os.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        }
                    } else {
                        archive.close();
                        return;
                    }
                } catch (Throwable th3) {
                    try {
                        archive.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
                throw new IOException("Failed to extract RAR file: " + rarFile.getAbsolutePath(), e);
            }
        } catch (RarException e) {
            throw new IOException("Failed to extract RAR file: " + rarFile.getAbsolutePath(), e);
        }
    }
}

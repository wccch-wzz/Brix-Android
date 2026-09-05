package com.brixcore.util.io;

import com.brixcore.util.function.ExceptionalPredicate;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/* JADX INFO: loaded from: classes3.dex */
public final class Zipper implements Closeable {
    private final byte[] buffer;
    private final ZipOutputStream zos;

    public Zipper(Path zipFile) throws IOException {
        this(zipFile, StandardCharsets.UTF_8);
    }

    public Zipper(Path zipFile, Charset encoding) throws IOException {
        this.buffer = new byte[8192];
        this.zos = new ZipOutputStream(Files.newOutputStream(zipFile, new OpenOption[0]), encoding);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String normalize(String path) {
        String path2 = path.replace(org.apache.commons.io.IOUtils.DIR_SEPARATOR_WINDOWS, org.apache.commons.io.IOUtils.DIR_SEPARATOR_UNIX);
        if (path2.startsWith("/")) {
            path2 = path2.substring(1);
        }
        if (path2.endsWith("/")) {
            return path2.substring(0, path2.length() - 1);
        }
        return path2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String resolve(String dir, String file) {
        if (dir.isEmpty()) {
            return file;
        }
        return file.isEmpty() ? dir : dir + "/" + file;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.zos.close();
    }

    public void putDirectory(Path source, String targetDir) throws IOException {
        putDirectory(source, targetDir, null);
    }

    public void putDirectory(final Path source, String targetDir, final ExceptionalPredicate<String, IOException> filter) throws IOException {
        final String root = normalize(targetDir);
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() { // from class: com.brixcore.util.io.Zipper.1
            @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (".DS_Store".equals(file.getFileName().toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                String relativePath = Zipper.normalize(source.relativize(file).normalize().toString());
                if (filter != null && !filter.test(relativePath)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Zipper.this.putFile(file, Zipper.resolve(root, relativePath));
                return FileVisitResult.CONTINUE;
            }

            @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String relativePath = Zipper.normalize(source.relativize(dir).normalize().toString());
                if (filter != null && !filter.test(relativePath)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                try {
                    Zipper.this.zos.putNextEntry(new ZipEntry(Zipper.resolve(root, relativePath) + "/"));
                    Zipper.this.zos.closeEntry();
                } catch (ZipException e) {
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void putFile(File file, String path) throws IOException {
        putFile(file.toPath(), path);
    }

    public void putFile(Path file, String path) throws IOException {
        String path2 = normalize(path);
        BasicFileAttributes attrs = Files.readAttributes(file, (Class<BasicFileAttributes>) BasicFileAttributes.class, new LinkOption[0]);
        ZipEntry entry = new ZipEntry(attrs.isDirectory() ? path2 + "/" : path2);
        entry.setCreationTime(attrs.creationTime());
        entry.setLastAccessTime(attrs.lastAccessTime());
        entry.setLastModifiedTime(attrs.lastModifiedTime());
        if (attrs.isDirectory()) {
            try {
                this.zos.putNextEntry(entry);
                this.zos.closeEntry();
                return;
            } catch (ZipException e) {
                return;
            }
        }
        InputStream input = Files.newInputStream(file, new OpenOption[0]);
        try {
            this.zos.putNextEntry(entry);
            IOUtils.copyTo(input, this.zos, this.buffer);
            this.zos.closeEntry();
            if (input != null) {
                input.close();
            }
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void putStream(InputStream in, String path) throws IOException {
        this.zos.putNextEntry(new ZipEntry(normalize(path)));
        IOUtils.copyTo(in, this.zos, this.buffer);
        this.zos.closeEntry();
    }

    public void putTextFile(String text, String path) throws IOException {
        putTextFile(text, StandardCharsets.UTF_8, path);
    }

    public void putTextFile(String text, Charset encoding, String path) throws IOException {
        this.zos.putNextEntry(new ZipEntry(normalize(path)));
        this.zos.write(text.getBytes(encoding));
        this.zos.closeEntry();
    }
}

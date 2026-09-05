package com.brixcore.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

/* JADX INFO: loaded from: classes3.dex */
public final class Unzipper {
    private final Path dest;
    private Charset encoding;
    private FileFilter filter;
    private boolean replaceExistentFile;
    private String subDirectory;
    private boolean terminateIfSubDirectoryNotExists;
    private final Path zipFile;

    public interface FileFilter {
        boolean accept(Path path, boolean z, Path path2, String str) throws IOException;
    }

    public Unzipper(Path zipFile, Path destDir) {
        this.replaceExistentFile = false;
        this.terminateIfSubDirectoryNotExists = false;
        this.subDirectory = "/";
        this.filter = null;
        this.encoding = StandardCharsets.UTF_8;
        this.zipFile = zipFile;
        this.dest = destDir;
    }

    public Unzipper(File zipFile, File destDir) {
        this(zipFile.toPath(), destDir.toPath());
    }

    public Unzipper setReplaceExistentFile(boolean replaceExistentFile) {
        this.replaceExistentFile = replaceExistentFile;
        return this;
    }

    public Unzipper setFilter(FileFilter filter) {
        this.filter = filter;
        return this;
    }

    public Unzipper setSubDirectory(String subDirectory) {
        this.subDirectory = FileUtils.normalizePath(subDirectory);
        return this;
    }

    public Unzipper setEncoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public Unzipper setTerminateIfSubDirectoryNotExists() {
        this.terminateIfSubDirectoryNotExists = true;
        return this;
    }

    public void unzip() throws IOException {
        Files.createDirectories(this.dest, new FileAttribute[0]);
        FileSystem fs = CompressingUtils.readonly(this.zipFile).setEncoding(this.encoding).setAutoDetectEncoding(true).build();
        try {
            final Path root = fs.getPath(this.subDirectory, new String[0]);
            if (!root.isAbsolute() || (this.subDirectory.length() > 1 && this.subDirectory.endsWith("/"))) {
                throw new IllegalArgumentException("Subdirectory for unzipper must be absolute");
            }
            if (!this.terminateIfSubDirectoryNotExists || !Files.notExists(root, new LinkOption[0])) {
                Files.walkFileTree(root, new SimpleFileVisitor<Path>() { // from class: com.brixcore.util.io.Unzipper.1
                    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String relativePath = root.relativize(file).toString();
                        Path destFile = Unzipper.this.dest.resolve(relativePath);
                        if (Unzipper.this.filter != null && !Unzipper.this.filter.accept(file, false, destFile, relativePath)) {
                            return FileVisitResult.CONTINUE;
                        }
                        try {
                            Files.copy(file, destFile, Unzipper.this.replaceExistentFile ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0]);
                        } catch (FileAlreadyExistsException e) {
                            if (Unzipper.this.replaceExistentFile) {
                                throw e;
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        String relativePath = root.relativize(dir).toString();
                        Path dirToCreate = Unzipper.this.dest.resolve(relativePath);
                        if (Unzipper.this.filter != null && !Unzipper.this.filter.accept(dir, true, dirToCreate, relativePath)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        Files.createDirectories(dirToCreate, new FileAttribute[0]);
                        return FileVisitResult.CONTINUE;
                    }
                });
                if (fs != null) {
                    fs.close();
                    return;
                }
                return;
            }
            if (fs != null) {
                fs.close();
            }
        } catch (Throwable th) {
            if (fs != null) {
                try {
                    fs.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}

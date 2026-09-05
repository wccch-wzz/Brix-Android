package com.brixcore.util.io;

import com.brixcore.util.Lang;
import com.brixcore.util.StringUtils;
import com.brixcore.util.function.ExceptionalConsumer;
import com.brixcore.util.function.ExceptionalSupplier;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes3.dex */
public final class FileUtils {
    private FileUtils() {
    }

    public static boolean canCreateDirectory(String path) {
        try {
            return canCreateDirectory(Paths.get(path, new String[0]));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    public static boolean canCreateDirectory(Path path) {
        if (Files.isDirectory(path, new LinkOption[0])) {
            return true;
        }
        if (Files.exists(path, new LinkOption[0])) {
            return false;
        }
        Path lastPath = path;
        Path path2 = path.getParent();
        while (path2 != null && !Files.exists(path2, new LinkOption[0])) {
            lastPath = path2;
            path2 = path2.getParent();
        }
        if (path2 == null || !Files.isDirectory(path2, new LinkOption[0])) {
            return false;
        }
        try {
            Files.createDirectory(lastPath, new FileAttribute[0]);
            Files.delete(lastPath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getNameWithoutExtension(String fileName) {
        return StringUtils.substringBeforeLast(fileName, '.');
    }

    public static String getNameWithoutExtension(File file) {
        return StringUtils.substringBeforeLast(file.getName(), '.');
    }

    public static String getNameWithoutExtension(Path file) {
        return StringUtils.substringBeforeLast(getName(file), '.');
    }

    public static String getExtension(File file) {
        return StringUtils.substringAfterLast(file.getName(), '.');
    }

    public static String getExtension(Path file) {
        return StringUtils.substringAfterLast(getName(file), '.');
    }

    public static String normalizePath(String path) {
        return StringUtils.addPrefix(StringUtils.removeSuffix(path, "/", "\\"), "/");
    }

    public static String getName(Path path) {
        return path.getFileName() == null ? "" : StringUtils.removeSuffix(path.getFileName().toString(), "/", "\\");
    }

    public static String getName(Path path, String candidate) {
        return path.getFileName() == null ? candidate : getName(path);
    }

    public static String readText(File file) throws IOException {
        return readText(file, StandardCharsets.UTF_8);
    }

    public static String readText(File file, Charset charset) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), charset);
    }

    public static String readText(Path file) throws IOException {
        return readText(file, StandardCharsets.UTF_8);
    }

    public static String readText(Path file, Charset charset) throws IOException {
        return new String(Files.readAllBytes(file), charset);
    }

    public static void writeTextWithAppendMode(File file, String text) throws IOException {
        writeBytesWithAppendMode(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeTextWithAppendMode(Path file, String text) throws IOException {
        writeBytesWithAppendMode(file, text.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeBytesWithAppendMode(Path file, byte[] data) throws IOException {
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        Files.write(file, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static void writeText(File file, String text) throws IOException {
        writeText(file, text, StandardCharsets.UTF_8);
    }

    public static void writeText(Path file, String text) throws IOException {
        writeText(file, text, StandardCharsets.UTF_8);
    }

    public static void writeText(File file, String text, Charset charset) throws IOException {
        writeBytes(file, text.getBytes(charset));
    }

    public static void writeText(Path file, String text, Charset charset) throws IOException {
        writeBytes(file, text.getBytes(charset));
    }

    public static void writeBytes(File file, byte[] data) throws IOException {
        writeBytes(file.toPath(), data);
    }

    public static void writeBytes(Path file, byte[] data) throws IOException {
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        Files.write(file, data, new OpenOption[0]);
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }
        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    public static boolean deleteDirectoryQuietly(File directory) {
        try {
            deleteDirectory(directory);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void copyDirectory(Path src, Path dest) throws IOException {
        copyDirectory(src, dest, new Predicate() { // from class: com.brixcore.util.io.FileUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return FileUtils.lambda$copyDirectory$0((String) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$copyDirectory$0(String path) {
        return true;
    }

    public static void copyDirectory(final Path src, final Path dest, final Predicate<String> filePredicate) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() { // from class: com.brixcore.util.io.FileUtils.1
            @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!filePredicate.test(src.relativize(file).toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Path destFile = dest.resolve(src.relativize(file).toString());
                Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!filePredicate.test(src.relativize(dir).toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Path destDir = dest.resolve(src.relativize(dir).toString());
                Files.createDirectories(destDir, new FileAttribute[0]);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            if (!makeDirectory(directory)) {
                throw new IOException("Failed to create directory: " + directory);
            }
            return;
        }
        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }
        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Failed to list contents of " + directory);
        }
        IOException exception = null;
        for (File file : files) {
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    public static boolean cleanDirectoryQuietly(File directory) {
        try {
            cleanDirectory(directory);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
            return;
        }
        boolean filePresent = file.exists();
        if (!file.delete()) {
            if (!filePresent) {
                throw new FileNotFoundException("File does not exist: " + file);
            }
            throw new IOException("Unable to delete file: " + file);
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        File canonicalDir;
        Objects.requireNonNull(file, "File must not be null");
        if (File.separatorChar == '\\') {
            return false;
        }
        if (file.getParent() == null) {
            canonicalDir = file;
        } else {
            File fileInCanonicalDir = file.getParentFile();
            File canonicalDir2 = fileInCanonicalDir.getCanonicalFile();
            canonicalDir = new File(canonicalDir2, file.getName());
        }
        return !canonicalDir.getCanonicalFile().equals(canonicalDir.getAbsoluteFile());
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        Objects.requireNonNull(srcFile, "Source must not be null");
        Objects.requireNonNull(destFile, "Destination must not be null");
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        File parentFile = destFile.getParentFile();
        if (parentFile != null && !makeDirectory(parentFile)) {
            throw new IOException("Destination '" + parentFile + "' directory cannot be created");
        }
        if (destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFile(Path srcFile, Path destFile) throws IOException {
        Objects.requireNonNull(srcFile, "Source must not be null");
        Objects.requireNonNull(destFile, "Destination must not be null");
        if (!Files.exists(srcFile, new LinkOption[0])) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (Files.isDirectory(srcFile, new LinkOption[0])) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        Path parentFile = destFile.getParent();
        Files.createDirectories(parentFile, new FileAttribute[0]);
        if (Files.exists(destFile, new LinkOption[0]) && !Files.isWritable(destFile)) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void moveFile(File srcFile, File destFile) throws IOException {
        copyFile(srcFile, destFile);
        srcFile.delete();
    }

    public static boolean makeDirectory(File directory) {
        directory.mkdirs();
        return directory.isDirectory();
    }

    public static boolean makeFile(final File file) {
        if (makeDirectory(file.getAbsoluteFile().getParentFile())) {
            if (!file.exists()) {
                Objects.requireNonNull(file);
                if (Lang.test(new ExceptionalSupplier() { // from class: com.brixcore.util.io.FileUtils$$ExternalSyntheticLambda1
                    @Override // com.brixcore.util.function.ExceptionalSupplier
                    public final Object get() {
                        return Boolean.valueOf(file.createNewFile());
                    }
                })) {
                }
            }
            return true;
        }
        return false;
    }

    public static List<File> listFilesByExtension(File file, String extension) {
        List<File> result = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File it : files) {
                if (extension.equals(getExtension(it))) {
                    result.add(it);
                }
            }
        }
        return result;
    }

    public static boolean isValidPath(File file) {
        try {
            file.toPath();
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }

    public static Optional<Path> tryGetPath(String first, String... more) {
        if (first == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Paths.get(first, more));
        } catch (InvalidPathException e) {
            return Optional.empty();
        }
    }

    public static Path tmpSaveFile(Path file) {
        return file.toAbsolutePath().resolveSibling("." + file.getFileName().toString() + ".tmp");
    }

    public static void saveSafely(Path file, String content) throws IOException {
        Path tmpFile = tmpSaveFile(file);
        BufferedWriter writer = Files.newBufferedWriter(tmpFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        try {
            writer.write(content);
            if (writer != null) {
                writer.close();
            }
            try {
                if (Files.exists(file, new LinkOption[0]) && Files.getAttribute(file, "dos:hidden", new LinkOption[0]) == Boolean.TRUE) {
                    Files.setAttribute(tmpFile, "dos:hidden", true, new LinkOption[0]);
                }
            } catch (Throwable th) {
            }
            Files.move(tmpFile, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable th2) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
            }
            throw th2;
        }
    }

    public static void saveSafely(Path file, ExceptionalConsumer<? super OutputStream, IOException> action) throws IOException {
        Path tmpFile = tmpSaveFile(file);
        OutputStream os = Files.newOutputStream(tmpFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        try {
            action.accept(os);
            if (os != null) {
                os.close();
            }
            try {
                if (Files.exists(file, new LinkOption[0]) && Files.getAttribute(file, "dos:hidden", new LinkOption[0]) == Boolean.TRUE) {
                    Files.setAttribute(tmpFile, "dos:hidden", true, new LinkOption[0]);
                }
            } catch (Throwable th) {
            }
            Files.move(tmpFile, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable th2) {
            if (os != null) {
                try {
                    os.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
            }
            throw th2;
        }
    }
}

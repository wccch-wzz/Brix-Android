package com.brixcore.util.tree;

import com.brixcore.util.Logging;
import com.brixcore.util.io.IOUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes15.dex */
public abstract class ArchiveFileTree<R, E extends ArchiveEntry> implements Closeable {
    protected final R reader;
    protected final Dir<E> root = new Dir<>("", "");

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public abstract void close() throws IOException;

    public abstract InputStream getInputStream(E e) throws IOException;

    public abstract String getLink(E e) throws IOException;

    public abstract boolean isExecutable(E e);

    public abstract boolean isLink(E e);

    public static ArchiveFileTree<?, ?> open(Path file) throws IOException {
        Path namePath = file.getFileName();
        if (namePath == null) {
            throw new IOException(file + " is not a valid archive file");
        }
        String name = namePath.toString();
        if (name.endsWith(".jar") || name.endsWith(".zip")) {
            return new ZipFileTree(new ZipFile(file));
        }
        if (name.endsWith(".tar") || name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
            return TarFileTree.open(file);
        }
        throw new IOException(file + " is not a valid archive file");
    }

    public ArchiveFileTree(R reader) {
        this.reader = reader;
    }

    public R getReader() {
        return this.reader;
    }

    public Dir<E> getRoot() {
        return this.root;
    }

    public E getEntry(String str) {
        Dir<E> dir = this.root;
        if (str.indexOf(47) < 0) {
            return dir.getFiles().get(str);
        }
        String[] strArrSplit = str.split("/");
        if (strArrSplit.length == 0) {
            return (E) this.root.getEntry();
        }
        for (int i = 0; i < strArrSplit.length - 1; i++) {
            String str2 = strArrSplit[i];
            if (!str2.isEmpty() && (dir = dir.getSubDirs().get(str2)) == null) {
                return null;
            }
        }
        return dir.getFiles().get(strArrSplit[strArrSplit.length - 1]);
    }

    public Dir<E> getDirectory(String dirPath) {
        Dir<E> dir = this.root;
        if (dirPath.isEmpty()) {
            return dir;
        }
        String[] path = dirPath.split("/");
        for (String item : path) {
            if (!item.isEmpty() && (dir = dir.getSubDirs().get(item)) == null) {
                return null;
            }
        }
        return dir;
    }

    protected void addEntry(E entry) throws IOException {
        String[] path = entry.getName().split("/");
        final List<String> pathList = Arrays.asList(path);
        Dir<E> dir = this.root;
        int end = entry.isDirectory() ? path.length : path.length - 1;
        for (int i = 0; i < end; i++) {
            String item = path[i];
            if (!item.equals(".")) {
                if (item.equals("..") || item.isEmpty()) {
                    throw new IOException("Invalid entry: " + entry.getName());
                }
                if (dir.files.containsKey(item)) {
                    throw new IOException("A file and a directory have the same name: " + entry.getName());
                }
                final int nameEnd = i + 1;
                dir = dir.subDirs.computeIfAbsent(item, new Function() { // from class: com.brixcore.util.tree.ArchiveFileTree$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ArchiveFileTree.lambda$addEntry$0(pathList, nameEnd, (String) obj);
                    }
                });
            }
        }
        if (entry.isDirectory()) {
            if (((Dir) dir).entry == null) {
                ((Dir) dir).entry = entry;
                return;
            } else {
                if (!((Dir) dir).entry.isDirectory()) {
                    throw new IOException("A file and a directory have the same name: " + entry.getName());
                }
                return;
            }
        }
        String fileName = path[path.length - 1];
        if (dir.subDirs.containsKey(fileName)) {
            throw new IOException("A file and a directory have the same name: " + entry.getName());
        }
        if (dir.files.containsKey(fileName)) {
            throw new IOException("Duplicate entry: " + entry.getName());
        }
        dir.files.put(fileName, entry);
    }

    static /* synthetic */ Dir lambda$addEntry$0(List pathList, int nameEnd, String name) {
        return new Dir(name, String.join("/", pathList.subList(0, nameEnd)));
    }

    public InputStream getInputStream(String entryPath) throws IOException {
        ArchiveEntry entry = getEntry(entryPath);
        if (entry == null) {
            throw new FileNotFoundException("Entry not found: " + entryPath);
        }
        return getInputStream(entry);
    }

    public BufferedReader getBufferedReader(E entry) throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(entry), StandardCharsets.UTF_8));
    }

    public BufferedReader getBufferedReader(String entryPath) throws IOException {
        ArchiveEntry entry = getEntry(entryPath);
        if (entry == null) {
            throw new FileNotFoundException("Entry not found: " + entryPath);
        }
        return getBufferedReader(entry);
    }

    public byte[] readBinaryEntry(E entry) throws IOException {
        InputStream input = getInputStream(entry);
        try {
            byte[] fully = IOUtils.readFully(input);
            if (input != null) {
                input.close();
            }
            return fully;
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

    public String readTextEntry(String entryPath) throws IOException {
        ArchiveEntry entry = getEntry(entryPath);
        if (entry == null) {
            throw new FileNotFoundException("Entry not found: " + entryPath);
        }
        return readTextEntry(entry);
    }

    public String readTextEntry(E entry) throws IOException {
        return new String(readBinaryEntry(entry), StandardCharsets.UTF_8);
    }

    protected void copyAttributes(E source, Path targetFile) throws IOException {
        Date date = source.getLastModifiedDate();
        FileTime lastModifiedTime = FileTime.from(date.toInstant());
        if (lastModifiedTime != null) {
            Files.setLastModifiedTime(targetFile, lastModifiedTime);
        }
    }

    public void extractTo(String entryPath, Path targetFile) throws IOException {
        ArchiveEntry entry = getEntry(entryPath);
        if (entry == null) {
            throw new FileNotFoundException("Entry not found: " + entryPath);
        }
        extractTo(entry, targetFile);
    }

    public void extractTo(E entry, Path targetFile) throws IOException {
        InputStream input = getInputStream(entry);
        try {
            Files.copy(input, targetFile, StandardCopyOption.REPLACE_EXISTING);
            if (input != null) {
                input.close();
            }
            try {
                copyAttributes(entry, targetFile);
            } catch (Throwable e) {
                Logging.LOG.warning("Failed to copy attributes to " + targetFile + StringUtils.LF + e);
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

    public static final class Dir<E extends ArchiveEntry> {
        private E entry;
        private final String fullName;
        private final String name;
        final Map<String, Dir<E>> subDirs = new HashMap();
        final Map<String, E> files = new HashMap();

        public Dir(String name, String fullName) {
            this.name = name;
            this.fullName = fullName;
        }

        public boolean isRoot() {
            return this.name.isEmpty();
        }

        public String getName() {
            return this.name;
        }

        public String getFullName() {
            return this.fullName;
        }

        public E getEntry() {
            return this.entry;
        }

        public Map<String, Dir<E>> getSubDirs() {
            return this.subDirs;
        }

        public Map<String, E> getFiles() {
            return this.files;
        }
    }
}

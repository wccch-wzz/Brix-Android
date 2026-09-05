package com.sun.nio.zipfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/* JADX INFO: loaded from: classes2.dex */
public class ZipFileStore extends FileStore {
    private final ZipFileSystem zfs;

    ZipFileStore(ZipPath zpath) {
        this.zfs = zpath.getFileSystem();
    }

    @Override // java.nio.file.FileStore
    public String name() {
        return this.zfs.toString() + "/";
    }

    @Override // java.nio.file.FileStore
    public String type() {
        return "zipfs";
    }

    @Override // java.nio.file.FileStore
    public boolean isReadOnly() {
        return this.zfs.isReadOnly();
    }

    @Override // java.nio.file.FileStore
    public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
        return type == BasicFileAttributeView.class || type == ZipFileAttributeView.class;
    }

    @Override // java.nio.file.FileStore
    public boolean supportsFileAttributeView(String name) {
        return name.equals("basic") || name.equals(ArchiveStreamFactory.ZIP);
    }

    @Override // java.nio.file.FileStore
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        return null;
    }

    @Override // java.nio.file.FileStore
    public long getTotalSpace() throws IOException {
        return new ZipFileStoreAttributes(this).totalSpace();
    }

    @Override // java.nio.file.FileStore
    public long getUsableSpace() throws IOException {
        return new ZipFileStoreAttributes(this).usableSpace();
    }

    @Override // java.nio.file.FileStore
    public long getUnallocatedSpace() throws IOException {
        return new ZipFileStoreAttributes(this).unallocatedSpace();
    }

    @Override // java.nio.file.FileStore
    public Object getAttribute(String attribute) throws IOException {
        if (attribute.equals("totalSpace")) {
            return Long.valueOf(getTotalSpace());
        }
        if (attribute.equals("usableSpace")) {
            return Long.valueOf(getUsableSpace());
        }
        if (attribute.equals("unallocatedSpace")) {
            return Long.valueOf(getUnallocatedSpace());
        }
        throw new UnsupportedOperationException("does not support the given attribute");
    }

    private static class ZipFileStoreAttributes {
        final FileStore fstore;
        final long size;

        public ZipFileStoreAttributes(ZipFileStore fileStore) throws IOException {
            Path path = FileSystems.getDefault().getPath(fileStore.name(), new String[0]);
            this.size = Files.size(path);
            this.fstore = Files.getFileStore(path);
        }

        public long totalSpace() {
            return this.size;
        }

        public long usableSpace() throws IOException {
            if (!this.fstore.isReadOnly()) {
                return this.fstore.getUsableSpace();
            }
            return 0L;
        }

        public long unallocatedSpace() throws IOException {
            if (!this.fstore.isReadOnly()) {
                return this.fstore.getUnallocatedSpace();
            }
            return 0L;
        }
    }
}

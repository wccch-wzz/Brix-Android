package com.sun.nio.zipfs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Formatter;

/* JADX INFO: loaded from: classes2.dex */
public class ZipFileAttributes implements BasicFileAttributes {
    private final ZipFileSystem.Entry e;

    ZipFileAttributes(ZipFileSystem.Entry e) {
        this.e = e;
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public FileTime creationTime() {
        if (this.e.ctime != -1) {
            return FileTime.fromMillis(this.e.ctime);
        }
        return null;
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public boolean isDirectory() {
        return this.e.isDir();
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public boolean isOther() {
        return false;
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public boolean isRegularFile() {
        return !this.e.isDir();
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public FileTime lastAccessTime() {
        if (this.e.atime != -1) {
            return FileTime.fromMillis(this.e.atime);
        }
        return null;
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public FileTime lastModifiedTime() {
        return FileTime.fromMillis(this.e.mtime);
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public long size() {
        return this.e.size;
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public boolean isSymbolicLink() {
        return false;
    }

    @Override // java.nio.file.attribute.BasicFileAttributes
    public Object fileKey() {
        return null;
    }

    public long compressedSize() {
        return this.e.csize;
    }

    public long crc() {
        return this.e.crc;
    }

    public int method() {
        return this.e.method;
    }

    public byte[] extra() {
        if (this.e.extra != null) {
            return Arrays.copyOf(this.e.extra, this.e.extra.length);
        }
        return null;
    }

    public byte[] comment() {
        if (this.e.comment != null) {
            return Arrays.copyOf(this.e.comment, this.e.comment.length);
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        Formatter fm = new Formatter(sb);
        if (creationTime() != null) {
            fm.format("    creationTime    : %tc%n", Long.valueOf(creationTime().toMillis()));
        } else {
            fm.format("    creationTime    : null%n", new Object[0]);
        }
        if (lastAccessTime() != null) {
            fm.format("    lastAccessTime  : %tc%n", Long.valueOf(lastAccessTime().toMillis()));
        } else {
            fm.format("    lastAccessTime  : null%n", new Object[0]);
        }
        fm.format("    lastModifiedTime: %tc%n", Long.valueOf(lastModifiedTime().toMillis()));
        fm.format("    isRegularFile   : %b%n", Boolean.valueOf(isRegularFile()));
        fm.format("    isDirectory     : %b%n", Boolean.valueOf(isDirectory()));
        fm.format("    isSymbolicLink  : %b%n", Boolean.valueOf(isSymbolicLink()));
        fm.format("    isOther         : %b%n", Boolean.valueOf(isOther()));
        fm.format("    fileKey         : %s%n", fileKey());
        fm.format("    size            : %d%n", Long.valueOf(size()));
        fm.format("    compressedSize  : %d%n", Long.valueOf(compressedSize()));
        fm.format("    crc             : %x%n", Long.valueOf(crc()));
        fm.format("    method          : %d%n", Integer.valueOf(method()));
        fm.close();
        return sb.toString();
    }
}

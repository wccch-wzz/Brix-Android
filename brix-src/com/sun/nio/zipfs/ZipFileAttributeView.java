package com.sun.nio.zipfs;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/* JADX INFO: loaded from: classes2.dex */
public class ZipFileAttributeView implements BasicFileAttributeView {
    private final boolean isZipView;
    private final ZipPath path;

    private enum AttrID {
        size,
        creationTime,
        lastAccessTime,
        lastModifiedTime,
        isDirectory,
        isRegularFile,
        isSymbolicLink,
        isOther,
        fileKey,
        compressedSize,
        crc,
        method
    }

    private ZipFileAttributeView(ZipPath path, boolean isZipView) {
        this.path = path;
        this.isZipView = isZipView;
    }

    static <V extends FileAttributeView> V get(ZipPath path, Class<V> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        if (type == BasicFileAttributeView.class) {
            return new ZipFileAttributeView(path, false);
        }
        if (type == ZipFileAttributeView.class) {
            return new ZipFileAttributeView(path, true);
        }
        return null;
    }

    static ZipFileAttributeView get(ZipPath path, String type) {
        if (type == null) {
            throw new NullPointerException();
        }
        if (type.equals("basic")) {
            return new ZipFileAttributeView(path, false);
        }
        if (type.equals(ArchiveStreamFactory.ZIP)) {
            return new ZipFileAttributeView(path, true);
        }
        return null;
    }

    @Override // java.nio.file.attribute.BasicFileAttributeView, java.nio.file.attribute.AttributeView
    public String name() {
        return this.isZipView ? ArchiveStreamFactory.ZIP : "basic";
    }

    @Override // java.nio.file.attribute.BasicFileAttributeView
    public ZipFileAttributes readAttributes() throws IOException {
        return this.path.getAttributes();
    }

    @Override // java.nio.file.attribute.BasicFileAttributeView
    public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
        this.path.setTimes(lastModifiedTime, lastAccessTime, createTime);
    }

    void setAttribute(String attribute, Object value) throws IOException {
        try {
            if (AttrID.valueOf(attribute) == AttrID.lastModifiedTime) {
                setTimes((FileTime) value, null, null);
            }
            if (AttrID.valueOf(attribute) == AttrID.lastAccessTime) {
                setTimes(null, (FileTime) value, null);
            }
            if (AttrID.valueOf(attribute) == AttrID.creationTime) {
                setTimes(null, null, (FileTime) value);
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("'" + attribute + "' is unknown or read-only attribute");
        }
    }

    Map<String, Object> readAttributes(String attributes) throws IOException {
        ZipFileAttributes zfas = readAttributes();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        int i = 0;
        if ("*".equals(attributes)) {
            AttrID[] attrIDArrValues = AttrID.values();
            int length = attrIDArrValues.length;
            while (i < length) {
                AttrID id = attrIDArrValues[i];
                try {
                    map.put(id.name(), attribute(id, zfas));
                } catch (IllegalArgumentException e) {
                }
                i++;
            }
        } else {
            String[] as = attributes.split(",");
            int length2 = as.length;
            while (i < length2) {
                String a = as[i];
                try {
                    map.put(a, attribute(AttrID.valueOf(a), zfas));
                } catch (IllegalArgumentException e2) {
                }
                i++;
            }
        }
        return map;
    }

    Object attribute(AttrID id, ZipFileAttributes zfas) {
        switch (id) {
            case size:
                return Long.valueOf(zfas.size());
            case creationTime:
                return zfas.creationTime();
            case lastAccessTime:
                return zfas.lastAccessTime();
            case lastModifiedTime:
                return zfas.lastModifiedTime();
            case isDirectory:
                return Boolean.valueOf(zfas.isDirectory());
            case isRegularFile:
                return Boolean.valueOf(zfas.isRegularFile());
            case isSymbolicLink:
                return Boolean.valueOf(zfas.isSymbolicLink());
            case isOther:
                return Boolean.valueOf(zfas.isOther());
            case fileKey:
                return zfas.fileKey();
            case compressedSize:
                if (this.isZipView) {
                    return Long.valueOf(zfas.compressedSize());
                }
                return null;
            case crc:
                if (this.isZipView) {
                    return Long.valueOf(zfas.crc());
                }
                return null;
            case method:
                if (this.isZipView) {
                    return Integer.valueOf(zfas.method());
                }
                return null;
            default:
                return null;
        }
    }
}

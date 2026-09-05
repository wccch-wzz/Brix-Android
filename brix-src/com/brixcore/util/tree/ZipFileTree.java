package com.brixcore.util.tree;

import com.brixcore.util.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* JADX INFO: loaded from: classes15.dex */
public final class ZipFileTree extends ArchiveFileTree<ZipFile, ZipArchiveEntry> {
    private final boolean closeReader;

    public ZipFileTree(ZipFile file) throws IOException {
        this(file, true);
    }

    public ZipFileTree(ZipFile file, boolean closeReader) throws IOException {
        super(file);
        this.closeReader = closeReader;
        try {
            Enumeration<ZipArchiveEntry> entries = file.getEntries();
            while (entries.hasMoreElements()) {
                addEntry(entries.nextElement());
            }
        } catch (Throwable e) {
            if (closeReader) {
                IOUtils.closeQuietly(file, e);
            }
            throw e;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.brixcore.util.tree.ArchiveFileTree, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closeReader) {
            ((ZipFile) this.reader).close();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.tree.ArchiveFileTree
    public void copyAttributes(ZipArchiveEntry source, Path targetFile) throws IOException {
        BasicFileAttributeView targetView = (BasicFileAttributeView) Files.getFileAttributeView(targetFile, PosixFileAttributeView.class, new LinkOption[0]);
        if (targetView == null) {
            targetView = (BasicFileAttributeView) Files.getFileAttributeView(targetFile, BasicFileAttributeView.class, new LinkOption[0]);
        }
        if (targetView == null) {
            return;
        }
        targetView.setTimes(source.getLastModifiedTime(), source.getLastAccessTime(), source.getCreationTime());
        int unixMode = source.getUnixMode();
        if (unixMode != 0 && (targetView instanceof PosixFileAttributeView)) {
            PosixFileAttributeView posixView = (PosixFileAttributeView) targetView;
            Set<PosixFilePermission> permissions = EnumSet.noneOf(PosixFilePermission.class);
            if ((unixMode & 256) != 0) {
                permissions.add(PosixFilePermission.OWNER_READ);
            }
            if ((unixMode & 128) != 0) {
                permissions.add(PosixFilePermission.OWNER_WRITE);
            }
            if ((unixMode & 64) != 0) {
                permissions.add(PosixFilePermission.OWNER_EXECUTE);
            }
            if ((unixMode & 32) != 0) {
                permissions.add(PosixFilePermission.GROUP_READ);
            }
            if ((unixMode & 16) != 0) {
                permissions.add(PosixFilePermission.GROUP_WRITE);
            }
            if ((unixMode & 8) != 0) {
                permissions.add(PosixFilePermission.GROUP_EXECUTE);
            }
            if ((unixMode & 4) != 0) {
                permissions.add(PosixFilePermission.OTHERS_READ);
            }
            if ((unixMode & 2) != 0) {
                permissions.add(PosixFilePermission.OTHERS_WRITE);
            }
            if ((unixMode & 1) != 0) {
                permissions.add(PosixFilePermission.OTHERS_EXECUTE);
            }
            posixView.setPermissions(permissions);
        }
    }

    @Override // com.brixcore.util.tree.ArchiveFileTree
    public InputStream getInputStream(ZipArchiveEntry entry) throws IOException {
        return getReader().getInputStream(entry);
    }

    @Override // com.brixcore.util.tree.ArchiveFileTree
    public boolean isLink(ZipArchiveEntry entry) {
        return entry.isUnixSymlink();
    }

    @Override // com.brixcore.util.tree.ArchiveFileTree
    public String getLink(ZipArchiveEntry entry) throws IOException {
        return getReader().getUnixSymlink(entry);
    }

    @Override // com.brixcore.util.tree.ArchiveFileTree
    public boolean isExecutable(ZipArchiveEntry entry) {
        return (entry.isDirectory() || entry.isUnixSymlink() || (entry.getUnixMode() & 64) == 0) ? false : true;
    }
}

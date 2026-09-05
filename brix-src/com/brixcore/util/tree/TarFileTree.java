package com.brixcore.util.tree;

import com.brixcore.util.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarFile;

/* JADX INFO: loaded from: classes15.dex */
public final class TarFileTree extends ArchiveFileTree<TarFile, TarArchiveEntry> {
    private final Thread shutdownHook;
    private final Path tempFile;

    public static TarFileTree open(Path file) throws IOException {
        String fileName = file.getFileName().toString();
        if (fileName.endsWith(".tar.gz") || fileName.endsWith(".tgz")) {
            Path tempFile = Files.createTempFile("hmcl-", ".tar", new FileAttribute[0]);
            try {
                GZIPInputStream input = new GZIPInputStream(Files.newInputStream(file, new OpenOption[0]));
                try {
                    OutputStream output = Files.newOutputStream(tempFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                    try {
                        IOUtils.copyTo(input, output);
                        TarFile tarFile = new TarFile(tempFile);
                        if (output != null) {
                            output.close();
                        }
                        input.close();
                        return new TarFileTree(tarFile, tempFile);
                    } catch (Throwable th) {
                        if (output != null) {
                            try {
                                output.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    try {
                        input.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
            } catch (Throwable e) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Throwable e2) {
                    e.addSuppressed(e2);
                }
                throw e;
            }
        }
        return new TarFileTree(new TarFile(file), null);
    }

    public TarFileTree(TarFile file, final Path tempFile) throws IOException {
        super(file);
        this.tempFile = tempFile;
        try {
            for (TarArchiveEntry entry : file.getEntries()) {
                addEntry(entry);
            }
            if (tempFile != null) {
                this.shutdownHook = new Thread(new Runnable() { // from class: com.brixcore.util.tree.TarFileTree$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() throws IOException {
                        Files.deleteIfExists(tempFile);
                    }
                });
                Runtime.getRuntime().addShutdownHook(this.shutdownHook);
            } else {
                this.shutdownHook = null;
            }
        } catch (Throwable e) {
            try {
                file.close();
            } catch (Throwable e2) {
                e.addSuppressed(e2);
            }
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    throw e;
                } catch (Throwable e3) {
                    e.addSuppressed(e3);
                    throw e;
                }
            }
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.tree.ArchiveFileTree
    public void copyAttributes(TarArchiveEntry source, Path targetFile) throws IOException {
        BasicFileAttributeView fileAttributeView = (BasicFileAttributeView) Files.getFileAttributeView(targetFile, BasicFileAttributeView.class, new LinkOption[0]);
        if (fileAttributeView == null) {
            return;
        }
        fileAttributeView.setTimes(source.getLastModifiedTime(), source.getLastAccessTime(), source.getCreationTime());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.brixcore.util.tree.ArchiveFileTree
    public InputStream getInputStream(TarArchiveEntry entry) throws IOException {
        return ((TarFile) this.reader).getInputStream(entry);
    }

    @Override // com.brixcore.util.tree.ArchiveFileTree
    public boolean isLink(TarArchiveEntry entry) {
        return entry.isSymbolicLink();
    }

    @Override // com.brixcore.util.tree.ArchiveFileTree
    public String getLink(TarArchiveEntry entry) throws IOException {
        return entry.getLinkName();
    }

    @Override // com.brixcore.util.tree.ArchiveFileTree
    public boolean isExecutable(TarArchiveEntry entry) {
        return entry.isFile() && (entry.getMode() & 64) != 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.brixcore.util.tree.ArchiveFileTree, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            ((TarFile) this.reader).close();
        } finally {
            if (this.tempFile != null) {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                Files.deleteIfExists(this.tempFile);
            }
        }
    }
}

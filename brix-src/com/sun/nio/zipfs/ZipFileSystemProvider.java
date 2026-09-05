package com.sun.nio.zipfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipError;

/* JADX INFO: loaded from: classes2.dex */
public class ZipFileSystemProvider extends FileSystemProvider {
    private final Map<Path, ZipFileSystem> filesystems = new HashMap();

    @Override // java.nio.file.spi.FileSystemProvider
    public String getScheme() {
        return "jar";
    }

    protected Path uriToPath(URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null || !scheme.equalsIgnoreCase(getScheme())) {
            throw new IllegalArgumentException("URI scheme is not '" + getScheme() + "'");
        }
        try {
            String spec = uri.getRawSchemeSpecificPart();
            int sep = spec.indexOf("!/");
            if (sep != -1) {
                spec = spec.substring(0, sep);
            }
            return Paths.get(new URI(spec)).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private boolean ensureFile(Path path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, (Class<BasicFileAttributes>) BasicFileAttributes.class, new LinkOption[0]);
            if (!attrs.isRegularFile()) {
                throw new UnsupportedOperationException();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        ZipFileSystem zipfs;
        Path path = uriToPath(uri);
        synchronized (this.filesystems) {
            Path realPath = null;
            if (ensureFile(path)) {
                realPath = path.toRealPath(new LinkOption[0]);
                if (this.filesystems.containsKey(realPath)) {
                    throw new FileSystemAlreadyExistsException();
                }
            }
            try {
                zipfs = new ZipFileSystem(this, path, env);
                this.filesystems.put(realPath, zipfs);
            } catch (ZipError ze) {
                String pname = path.toString();
                if (!pname.endsWith(".zip") && !pname.endsWith(".jar")) {
                    throw new UnsupportedOperationException();
                }
                throw ze;
            }
        }
        return zipfs;
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileSystem newFileSystem(Path path, Map<String, ?> env) throws IOException {
        if (path.getFileSystem() != FileSystems.getDefault()) {
            throw new UnsupportedOperationException();
        }
        ensureFile(path);
        try {
            return new ZipFileSystem(this, path, env);
        } catch (ZipError ze) {
            String pname = path.toString();
            if (pname.endsWith(".zip") || pname.endsWith(".jar")) {
                throw ze;
            }
            throw new UnsupportedOperationException();
        }
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public Path getPath(URI uri) {
        String spec = uri.getSchemeSpecificPart();
        int sep = spec.indexOf("!/");
        if (sep == -1) {
            throw new IllegalArgumentException("URI: " + uri + " does not contain path info ex. jar:file:/c:/foo.zip!/BAR");
        }
        return getFileSystem(uri).getPath(spec.substring(sep + 1), new String[0]);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileSystem getFileSystem(URI uri) {
        ZipFileSystem zipfs;
        synchronized (this.filesystems) {
            zipfs = null;
            try {
                zipfs = this.filesystems.get(uriToPath(uri).toRealPath(new LinkOption[0]));
            } catch (IOException e) {
            }
            if (zipfs == null) {
                throw new FileSystemNotFoundException();
            }
        }
        return zipfs;
    }

    static final ZipPath toZipPath(Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof ZipPath)) {
            throw new ProviderMismatchException();
        }
        return (ZipPath) path;
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        toZipPath(path).checkAccess(modes);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void copy(Path src, Path target, CopyOption... options) throws IOException {
        toZipPath(src).copy(toZipPath(target), options);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void createDirectory(Path path, FileAttribute<?>... attrs) throws IOException {
        toZipPath(path).createDirectory(attrs);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public final void delete(Path path) throws IOException {
        toZipPath(path).delete();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> cls, LinkOption... linkOptionArr) {
        return (V) ZipFileAttributeView.get(toZipPath(path), cls);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileStore getFileStore(Path path) throws IOException {
        return toZipPath(path).getFileStore();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public boolean isHidden(Path path) {
        return toZipPath(path).isHidden();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public boolean isSameFile(Path path, Path other) throws IOException {
        return toZipPath(path).isSameFile(other);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void move(Path src, Path target, CopyOption... options) throws IOException {
        toZipPath(src).move(toZipPath(target), options);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public AsynchronousFileChannel newAsynchronousFileChannel(Path path, Set<? extends OpenOption> options, ExecutorService exec, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return toZipPath(path).newByteChannel(options, attrs);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public DirectoryStream<Path> newDirectoryStream(Path path, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return toZipPath(path).newDirectoryStream(filter);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileChannel newFileChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return toZipPath(path).newFileChannel(options, attrs);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public InputStream newInputStream(Path path, OpenOption... options) throws IOException {
        return toZipPath(path).newInputStream(options);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
        return toZipPath(path).newOutputStream(options);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (type == BasicFileAttributes.class || type == ZipFileAttributes.class) {
            return toZipPath(path).getAttributes();
        }
        return null;
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public Map<String, Object> readAttributes(Path path, String attribute, LinkOption... options) throws IOException {
        return toZipPath(path).readAttributes(attribute, options);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public Path readSymbolicLink(Path link) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        toZipPath(path).setAttribute(attribute, value, options);
    }

    void removeFileSystem(Path zfpath, ZipFileSystem zfs) throws IOException {
        synchronized (this.filesystems) {
            Path zfpath2 = zfpath.toRealPath(new LinkOption[0]);
            if (this.filesystems.get(zfpath2) == zfs) {
                this.filesystems.remove(zfpath2);
            }
        }
    }
}

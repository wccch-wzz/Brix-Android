package com.sun.nio.zipfs;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;

/* JADX INFO: loaded from: classes2.dex */
public class JarFileSystemProvider extends ZipFileSystemProvider {
    @Override // com.sun.nio.zipfs.ZipFileSystemProvider, java.nio.file.spi.FileSystemProvider
    public String getScheme() {
        return "jar";
    }

    @Override // com.sun.nio.zipfs.ZipFileSystemProvider
    protected Path uriToPath(URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null || !scheme.equalsIgnoreCase(getScheme())) {
            throw new IllegalArgumentException("URI scheme is not '" + getScheme() + "'");
        }
        try {
            String uristr = uri.toString();
            int end = uristr.indexOf("!/");
            URI uri2 = new URI(uristr.substring(4, end == -1 ? uristr.length() : end));
            return Paths.get(new URI("file", uri2.getHost(), uri2.getPath(), null)).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override // com.sun.nio.zipfs.ZipFileSystemProvider, java.nio.file.spi.FileSystemProvider
    public Path getPath(URI uri) {
        String uristr;
        int off;
        FileSystem fs = getFileSystem(uri);
        String path = uri.getFragment();
        if (path == null && (off = (uristr = uri.toString()).indexOf("!/")) != -1) {
            path = uristr.substring(off + 2);
        }
        if (path != null) {
            return fs.getPath(path, new String[0]);
        }
        throw new IllegalArgumentException("URI: " + uri + " does not contain path fragment ex. jar:///c:/foo.zip!/BAR");
    }
}

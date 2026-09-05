package com.sun.nio.zipfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.UByte;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ZipPath implements Path {
    private int hashcode;
    private volatile int[] offsets;
    private final byte[] path;
    private volatile byte[] resolved;
    private final ZipFileSystem zfs;

    ZipPath(ZipFileSystem zfs, byte[] path) {
        this(zfs, path, false);
    }

    ZipPath(ZipFileSystem zfs, byte[] path, boolean normalized) {
        this.hashcode = 0;
        this.resolved = null;
        this.zfs = zfs;
        if (normalized) {
            this.path = path;
        } else if (zfs.zc.isUTF8()) {
            this.path = normalize(path);
        } else {
            this.path = normalize(zfs.getString(path));
        }
    }

    ZipPath(ZipFileSystem zfs, String path) {
        this.hashcode = 0;
        this.resolved = null;
        this.zfs = zfs;
        if (zfs.zc.isUTF8()) {
            this.path = normalize(zfs.getBytes(path));
        } else {
            this.path = normalize(path);
        }
    }

    @Override // java.nio.file.Path
    public ZipPath getRoot() {
        if (isAbsolute()) {
            return new ZipPath(this.zfs, new byte[]{this.path[0]});
        }
        return null;
    }

    @Override // java.nio.file.Path
    public Path getFileName() {
        initOffsets();
        int count = this.offsets.length;
        if (count == 0) {
            return null;
        }
        if (count == 1 && this.path[0] != 47) {
            return this;
        }
        int lastOffset = this.offsets[count - 1];
        int len = this.path.length - lastOffset;
        byte[] result = new byte[len];
        System.arraycopy(this.path, lastOffset, result, 0, len);
        return new ZipPath(this.zfs, result);
    }

    @Override // java.nio.file.Path
    public ZipPath getParent() {
        initOffsets();
        int count = this.offsets.length;
        if (count == 0) {
            return null;
        }
        int len = this.offsets[count - 1] - 1;
        if (len <= 0) {
            return getRoot();
        }
        byte[] result = new byte[len];
        System.arraycopy(this.path, 0, result, 0, len);
        return new ZipPath(this.zfs, result);
    }

    @Override // java.nio.file.Path
    public int getNameCount() {
        initOffsets();
        return this.offsets.length;
    }

    @Override // java.nio.file.Path
    public ZipPath getName(int index) {
        int len;
        initOffsets();
        if (index < 0 || index >= this.offsets.length) {
            throw new IllegalArgumentException();
        }
        int begin = this.offsets[index];
        if (index == this.offsets.length - 1) {
            len = this.path.length - begin;
        } else {
            len = (this.offsets[index + 1] - begin) - 1;
        }
        byte[] result = new byte[len];
        System.arraycopy(this.path, begin, result, 0, len);
        return new ZipPath(this.zfs, result);
    }

    @Override // java.nio.file.Path
    public ZipPath subpath(int beginIndex, int endIndex) {
        int len;
        initOffsets();
        if (beginIndex < 0 || beginIndex >= this.offsets.length || endIndex > this.offsets.length || beginIndex >= endIndex) {
            throw new IllegalArgumentException();
        }
        int begin = this.offsets[beginIndex];
        if (endIndex == this.offsets.length) {
            len = this.path.length - begin;
        } else {
            len = (this.offsets[endIndex] - begin) - 1;
        }
        byte[] result = new byte[len];
        System.arraycopy(this.path, begin, result, 0, len);
        return new ZipPath(this.zfs, result);
    }

    @Override // java.nio.file.Path
    public ZipPath toRealPath(LinkOption... options) throws IOException {
        ZipPath realPath = new ZipPath(this.zfs, getResolvedPath()).toAbsolutePath();
        realPath.checkAccess(new AccessMode[0]);
        return realPath;
    }

    boolean isHidden() {
        return false;
    }

    @Override // java.nio.file.Path
    public ZipPath toAbsolutePath() {
        byte[] t;
        if (isAbsolute()) {
            return this;
        }
        byte[] defaultdir = this.zfs.getDefaultDir().path;
        int defaultlen = defaultdir.length;
        boolean endsWith = defaultdir[defaultlen + (-1)] == 47;
        if (endsWith) {
            t = new byte[this.path.length + defaultlen];
        } else {
            t = new byte[defaultlen + 1 + this.path.length];
        }
        System.arraycopy(defaultdir, 0, t, 0, defaultlen);
        if (!endsWith) {
            t[defaultlen] = 47;
            defaultlen++;
        }
        System.arraycopy(this.path, 0, t, defaultlen, this.path.length);
        return new ZipPath(this.zfs, t, true);
    }

    @Override // java.nio.file.Path
    public URI toUri() {
        try {
            return new URI("jar", this.zfs.getZipFile().toUri() + XPath.NOT + this.zfs.getString(toAbsolutePath().path), null);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    private boolean equalsNameAt(ZipPath other, int index) {
        int mlen;
        int olen;
        int mbegin = this.offsets[index];
        if (index == this.offsets.length - 1) {
            mlen = this.path.length - mbegin;
        } else {
            mlen = (this.offsets[index + 1] - mbegin) - 1;
        }
        int obegin = other.offsets[index];
        if (index == other.offsets.length - 1) {
            olen = other.path.length - obegin;
        } else {
            olen = (other.offsets[index + 1] - obegin) - 1;
        }
        if (mlen != olen) {
            return false;
        }
        for (int n = 0; n < mlen; n++) {
            if (this.path[mbegin + n] != other.path[obegin + n]) {
                return false;
            }
        }
        return true;
    }

    @Override // java.nio.file.Path
    public Path relativize(Path other) {
        ZipPath o = checkPath(other);
        if (o.equals(this)) {
            return new ZipPath(getFileSystem(), new byte[0], true);
        }
        if (isAbsolute() != o.isAbsolute()) {
            throw new IllegalArgumentException();
        }
        int mc = getNameCount();
        int oc = o.getNameCount();
        int n = Math.min(mc, oc);
        int i = 0;
        while (i < n && equalsNameAt(o, i)) {
            i++;
        }
        int dotdots = mc - i;
        int len = (dotdots * 3) - 1;
        if (i < oc) {
            len += (o.path.length - o.offsets[i]) + 1;
        }
        byte[] result = new byte[len];
        int pos = 0;
        while (dotdots > 0) {
            int pos2 = pos + 1;
            result[pos] = 46;
            pos = pos2 + 1;
            result[pos2] = 46;
            if (pos < len) {
                result[pos] = 47;
                pos++;
            }
            dotdots--;
        }
        if (i < oc) {
            System.arraycopy(o.path, o.offsets[i], result, pos, o.path.length - o.offsets[i]);
        }
        return new ZipPath(getFileSystem(), result);
    }

    @Override // java.nio.file.Path
    public ZipFileSystem getFileSystem() {
        return this.zfs;
    }

    @Override // java.nio.file.Path
    public boolean isAbsolute() {
        return this.path.length > 0 && this.path[0] == 47;
    }

    @Override // java.nio.file.Path
    public ZipPath resolve(Path other) {
        byte[] resolved;
        ZipPath o = checkPath(other);
        if (o.isAbsolute()) {
            return o;
        }
        if (this.path[this.path.length - 1] == 47) {
            resolved = new byte[this.path.length + o.path.length];
            System.arraycopy(this.path, 0, resolved, 0, this.path.length);
            System.arraycopy(o.path, 0, resolved, this.path.length, o.path.length);
        } else {
            resolved = new byte[this.path.length + 1 + o.path.length];
            System.arraycopy(this.path, 0, resolved, 0, this.path.length);
            resolved[this.path.length] = 47;
            System.arraycopy(o.path, 0, resolved, this.path.length + 1, o.path.length);
        }
        return new ZipPath(this.zfs, resolved);
    }

    @Override // java.nio.file.Path
    public Path resolveSibling(Path other) {
        if (other == null) {
            throw new NullPointerException();
        }
        Path parent = getParent();
        return parent == null ? other : parent.resolve(other);
    }

    @Override // java.nio.file.Path
    public boolean startsWith(Path other) {
        ZipPath o = checkPath(other);
        if (o.isAbsolute() != isAbsolute() || o.path.length > this.path.length) {
            return false;
        }
        int olast = o.path.length;
        for (int i = 0; i < olast; i++) {
            if (o.path[i] != this.path[i]) {
                return false;
            }
        }
        int olast2 = olast - 1;
        return o.path.length == this.path.length || o.path[olast2] == 47 || this.path[olast2 + 1] == 47;
    }

    @Override // java.nio.file.Path
    public boolean endsWith(Path other) {
        ZipPath o = checkPath(other);
        int olast = o.path.length - 1;
        if (olast > 0 && o.path[olast] == 47) {
            olast--;
        }
        int last = this.path.length - 1;
        if (last > 0 && this.path[last] == 47) {
            last--;
        }
        if (olast == -1) {
            return last == -1;
        }
        if ((o.isAbsolute() && (!isAbsolute() || olast != last)) || last < olast) {
            return false;
        }
        while (olast >= 0) {
            if (o.path[olast] != this.path[last]) {
                return false;
            }
            olast--;
            last--;
        }
        return o.path[olast + 1] == 47 || last == -1 || this.path[last] == 47;
    }

    @Override // java.nio.file.Path
    public ZipPath resolve(String other) {
        return resolve((Path) getFileSystem().getPath(other, new String[0]));
    }

    @Override // java.nio.file.Path
    public final Path resolveSibling(String other) {
        return resolveSibling(getFileSystem().getPath(other, new String[0]));
    }

    @Override // java.nio.file.Path
    public final boolean startsWith(String other) {
        return startsWith(getFileSystem().getPath(other, new String[0]));
    }

    @Override // java.nio.file.Path
    public final boolean endsWith(String other) {
        return endsWith(getFileSystem().getPath(other, new String[0]));
    }

    @Override // java.nio.file.Path
    public Path normalize() {
        byte[] resolved = getResolved();
        if (resolved == this.path) {
            return this;
        }
        return new ZipPath(this.zfs, resolved, true);
    }

    private ZipPath checkPath(Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof ZipPath)) {
            throw new ProviderMismatchException();
        }
        return (ZipPath) path;
    }

    private void initOffsets() {
        if (this.offsets == null) {
            int count = 0;
            int index = 0;
            while (index < this.path.length) {
                int index2 = index + 1;
                byte c = this.path[index];
                if (c == 47) {
                    index = index2;
                } else {
                    count++;
                    while (index2 < this.path.length && this.path[index2] != 47) {
                        index2++;
                    }
                    index = index2;
                }
            }
            int[] result = new int[count];
            int count2 = 0;
            int index3 = 0;
            while (index3 < this.path.length) {
                byte c2 = this.path[index3];
                if (c2 == 47) {
                    index3++;
                } else {
                    int count3 = count2 + 1;
                    int index4 = index3 + 1;
                    result[count2] = index3;
                    while (index4 < this.path.length && this.path[index4] != 47) {
                        index4++;
                    }
                    count2 = count3;
                    index3 = index4;
                }
            }
            synchronized (this) {
                if (this.offsets == null) {
                    this.offsets = result;
                }
            }
        }
    }

    byte[] getResolvedPath() {
        byte[] r;
        byte[] r2 = this.resolved;
        if (r2 == null) {
            if (isAbsolute()) {
                r = getResolved();
            } else {
                r = toAbsolutePath().getResolvedPath();
            }
            if (r[0] == 47) {
                r = Arrays.copyOfRange(r, 1, r.length);
            }
            this.resolved = r;
        }
        return this.resolved;
    }

    private byte[] normalize(byte[] path) {
        if (path.length == 0) {
            return path;
        }
        byte prevC = 0;
        for (int i = 0; i < path.length; i++) {
            byte c = path[i];
            if (c == 92) {
                return normalize(path, i);
            }
            if (c == 47 && prevC == 47) {
                return normalize(path, i - 1);
            }
            if (c == 0) {
                throw new InvalidPathException(this.zfs.getString(path), "Path: nul character not allowed");
            }
            prevC = c;
        }
        return path;
    }

    private byte[] normalize(byte[] path, int off) {
        byte[] to = new byte[path.length];
        int n = 0;
        while (n < off) {
            to[n] = path[n];
            n++;
        }
        int m = n;
        byte prevC = 0;
        while (n < path.length) {
            n++;
            byte c = path[n];
            if (c == 92) {
                c = 47;
            }
            if (c != 47 || prevC != 47) {
                if (c == 0) {
                    throw new InvalidPathException(this.zfs.getString(path), "Path: nul character not allowed");
                }
                to[m] = c;
                prevC = c;
                m++;
            }
        }
        if (m > 1 && to[m - 1] == 47) {
            m--;
        }
        return m == to.length ? to : Arrays.copyOf(to, m);
    }

    private byte[] normalize(String path) {
        int len = path.length();
        if (len == 0) {
            return new byte[0];
        }
        char prevC = 0;
        for (int i = 0; i < len; i++) {
            char c = path.charAt(i);
            if (c == '\\' || c == 0) {
                return normalize(path, i, len);
            }
            if (c == '/' && prevC == '/') {
                return normalize(path, i - 1, len);
            }
            prevC = c;
        }
        if (len > 1 && prevC == '/') {
            path = path.substring(0, len - 1);
        }
        return this.zfs.getBytes(path);
    }

    private byte[] normalize(String path, int off, int len) {
        StringBuilder to = new StringBuilder(len);
        to.append((CharSequence) path, 0, off);
        char prevC = 0;
        while (off < len) {
            off++;
            char c = path.charAt(off);
            if (c == '\\') {
                c = IOUtils.DIR_SEPARATOR_UNIX;
            }
            if (c != '/' || prevC != '/') {
                if (c == 0) {
                    throw new InvalidPathException(path, "Path: nul character not allowed");
                }
                to.append(c);
                prevC = c;
            }
        }
        int len2 = to.length();
        if (len2 > 1 && prevC == '/') {
            to.delete(len2 - 1, len2);
        }
        return this.zfs.getBytes(to.toString());
    }

    private byte[] getResolved() {
        if (this.path.length == 0) {
            return this.path;
        }
        for (int i = 0; i < this.path.length; i++) {
            byte c = this.path[i];
            if (c == 46) {
                return resolve0();
            }
        }
        return this.path;
    }

    private byte[] resolve0() {
        byte[] to = new byte[this.path.length];
        int nc = getNameCount();
        int[] lastM = new int[nc];
        int lastMOff = -1;
        int m = 0;
        int i = 0;
        while (i < nc) {
            int n = this.offsets[i];
            int len = i == this.offsets.length - 1 ? this.path.length - n : (this.offsets[i + 1] - n) - 1;
            if (len == 1 && this.path[n] == 46) {
                if (m == 0 && this.path[0] == 47) {
                    to[m] = 47;
                    m++;
                }
            } else if (len == 2 && this.path[n] == 46 && this.path[n + 1] == 46) {
                if (lastMOff < 0) {
                    if (this.path[0] == 47) {
                        if (m == 0) {
                            to[m] = 47;
                            m++;
                        }
                    } else {
                        if (m != 0 && to[m - 1] != 47) {
                            to[m] = 47;
                            m++;
                        }
                        while (true) {
                            int len2 = len - 1;
                            if (len > 0) {
                                to[m] = this.path[n];
                                len = len2;
                                m++;
                                n++;
                            }
                        }
                    }
                } else {
                    int lastMOff2 = lastMOff - 1;
                    int m2 = lastM[lastMOff];
                    m = m2;
                    lastMOff = lastMOff2;
                }
            } else {
                if ((m == 0 && this.path[0] == 47) || (m != 0 && to[m - 1] != 47)) {
                    to[m] = 47;
                    m++;
                }
                lastMOff++;
                lastM[lastMOff] = m;
                while (true) {
                    int len3 = len - 1;
                    if (len > 0) {
                        to[m] = this.path[n];
                        len = len3;
                        m++;
                        n++;
                    }
                }
            }
            i++;
        }
        if (m > 1 && to[m - 1] == 47) {
            m--;
        }
        return m == to.length ? to : Arrays.copyOf(to, m);
    }

    @Override // java.nio.file.Path
    public String toString() {
        return this.zfs.getString(this.path);
    }

    @Override // java.nio.file.Path
    public int hashCode() {
        int h = this.hashcode;
        if (h == 0) {
            int h2 = Arrays.hashCode(this.path);
            this.hashcode = h2;
            return h2;
        }
        return h;
    }

    @Override // java.nio.file.Path
    public boolean equals(Object obj) {
        return obj != null && (obj instanceof ZipPath) && this.zfs == ((ZipPath) obj).zfs && compareTo((Path) obj) == 0;
    }

    @Override // java.lang.Comparable
    public int compareTo(Path other) {
        ZipPath o = checkPath(other);
        int len1 = this.path.length;
        int len2 = o.path.length;
        int n = Math.min(len1, len2);
        byte[] v1 = this.path;
        byte[] v2 = o.path;
        for (int k = 0; k < n; k++) {
            int c1 = v1[k] & UByte.MAX_VALUE;
            int c2 = v2[k] & UByte.MAX_VALUE;
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return len1 - len2;
    }

    @Override // java.nio.file.Path, java.nio.file.Watchable
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) {
        if (watcher == null || events == null || modifiers == null) {
            throw new NullPointerException();
        }
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.file.Path, java.nio.file.Watchable
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) {
        return register(watcher, events, new WatchEvent.Modifier[0]);
    }

    @Override // java.nio.file.Path
    public final File toFile() {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.file.Path, java.lang.Iterable
    public Iterator<Path> iterator() {
        return new Iterator<Path>() { // from class: com.sun.nio.zipfs.ZipPath.1
            private int i = 0;

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.i < ZipPath.this.getNameCount();
            }

            @Override // java.util.Iterator
            public Path next() {
                if (this.i < ZipPath.this.getNameCount()) {
                    Path result = ZipPath.this.getName(this.i);
                    this.i++;
                    return result;
                }
                throw new NoSuchElementException();
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new ReadOnlyFileSystemException();
            }
        };
    }

    void createDirectory(FileAttribute<?>... attrs) throws IOException {
        this.zfs.createDirectory(getResolvedPath(), attrs);
    }

    InputStream newInputStream(OpenOption... options) throws IOException {
        if (options.length > 0) {
            for (OpenOption opt : options) {
                if (opt != StandardOpenOption.READ) {
                    throw new UnsupportedOperationException("'" + opt + "' not allowed");
                }
            }
        }
        return this.zfs.newInputStream(getResolvedPath());
    }

    DirectoryStream<Path> newDirectoryStream(DirectoryStream.Filter<? super Path> filter) throws IOException {
        return new ZipDirectoryStream(this, filter);
    }

    void delete() throws IOException {
        this.zfs.deleteFile(getResolvedPath(), true);
    }

    void deleteIfExists() throws IOException {
        this.zfs.deleteFile(getResolvedPath(), false);
    }

    ZipFileAttributes getAttributes() throws IOException {
        ZipFileAttributes zfas = this.zfs.getFileAttributes(getResolvedPath());
        if (zfas == null) {
            throw new NoSuchFileException(toString());
        }
        return zfas;
    }

    void setAttribute(String attribute, Object value, LinkOption... options) throws IOException {
        String type;
        String attr;
        int colonPos = attribute.indexOf(58);
        if (colonPos == -1) {
            type = "basic";
            attr = attribute;
        } else {
            type = attribute.substring(0, colonPos);
            attr = attribute.substring(colonPos + 1);
        }
        ZipFileAttributeView view = ZipFileAttributeView.get(this, type);
        if (view == null) {
            throw new UnsupportedOperationException("view <" + view + "> is not supported");
        }
        view.setAttribute(attr, value);
    }

    void setTimes(FileTime mtime, FileTime atime, FileTime ctime) throws IOException {
        this.zfs.setTimes(getResolvedPath(), mtime, atime, ctime);
    }

    Map<String, Object> readAttributes(String attributes, LinkOption... options) throws IOException {
        String view;
        String attrs;
        int colonPos = attributes.indexOf(58);
        if (colonPos == -1) {
            view = "basic";
            attrs = attributes;
        } else {
            view = attributes.substring(0, colonPos);
            attrs = attributes.substring(colonPos + 1);
        }
        ZipFileAttributeView zfv = ZipFileAttributeView.get(this, view);
        if (zfv == null) {
            throw new UnsupportedOperationException("view not supported");
        }
        return zfv.readAttributes(attrs);
    }

    FileStore getFileStore() throws IOException {
        if (exists()) {
            return this.zfs.getFileStore(this);
        }
        throw new NoSuchFileException(this.zfs.getString(this.path));
    }

    boolean isSameFile(Path other) throws IOException {
        if (equals(other)) {
            return true;
        }
        if (other == null || getFileSystem() != other.getFileSystem()) {
            return false;
        }
        checkAccess(new AccessMode[0]);
        ((ZipPath) other).checkAccess(new AccessMode[0]);
        return Arrays.equals(getResolvedPath(), ((ZipPath) other).getResolvedPath());
    }

    SeekableByteChannel newByteChannel(Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return this.zfs.newByteChannel(getResolvedPath(), options, attrs);
    }

    FileChannel newFileChannel(Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return this.zfs.newFileChannel(getResolvedPath(), options, attrs);
    }

    void checkAccess(AccessMode... modes) throws IOException {
        boolean w = false;
        boolean x = false;
        for (AccessMode mode : modes) {
            switch (AnonymousClass2.$SwitchMap$java$nio$file$AccessMode[mode.ordinal()]) {
                case 1:
                    break;
                case 2:
                    w = true;
                    break;
                case 3:
                    x = true;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        ZipFileAttributes attrs = this.zfs.getFileAttributes(getResolvedPath());
        if (attrs == null && (this.path.length != 1 || this.path[0] != 47)) {
            throw new NoSuchFileException(toString());
        }
        if (w && this.zfs.isReadOnly()) {
            throw new AccessDeniedException(toString());
        }
        if (x) {
            throw new AccessDeniedException(toString());
        }
    }

    /* JADX INFO: renamed from: com.sun.nio.zipfs.ZipPath$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$java$nio$file$AccessMode = new int[AccessMode.values().length];

        static {
            try {
                $SwitchMap$java$nio$file$AccessMode[AccessMode.READ.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$nio$file$AccessMode[AccessMode.WRITE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$nio$file$AccessMode[AccessMode.EXECUTE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    boolean exists() {
        if (this.path.length == 1 && this.path[0] == 47) {
            return true;
        }
        try {
            return this.zfs.exists(getResolvedPath());
        } catch (IOException e) {
            return false;
        }
    }

    OutputStream newOutputStream(OpenOption... options) throws IOException {
        return options.length == 0 ? this.zfs.newOutputStream(getResolvedPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE) : this.zfs.newOutputStream(getResolvedPath(), options);
    }

    void move(ZipPath target, CopyOption... options) throws IOException {
        if (Files.isSameFile(this.zfs.getZipFile(), target.zfs.getZipFile())) {
            this.zfs.copyFile(true, getResolvedPath(), target.getResolvedPath(), options);
        } else {
            copyToTarget(target, options);
            delete();
        }
    }

    void copy(ZipPath target, CopyOption... options) throws IOException {
        if (Files.isSameFile(this.zfs.getZipFile(), target.zfs.getZipFile())) {
            this.zfs.copyFile(false, getResolvedPath(), target.getResolvedPath(), options);
        } else {
            copyToTarget(target, options);
        }
    }

    private void copyToTarget(ZipPath target, CopyOption... options) throws IOException {
        boolean exists;
        boolean replaceExisting = false;
        boolean copyAttrs = false;
        for (CopyOption opt : options) {
            if (opt == StandardCopyOption.REPLACE_EXISTING) {
                replaceExisting = true;
            } else if (opt == StandardCopyOption.COPY_ATTRIBUTES) {
                copyAttrs = true;
            }
        }
        ZipFileAttributes zfas = getAttributes();
        if (replaceExisting) {
            try {
                target.deleteIfExists();
                exists = false;
            } catch (DirectoryNotEmptyException e) {
                exists = true;
            }
        } else {
            exists = target.exists();
        }
        if (exists) {
            throw new FileAlreadyExistsException(target.toString());
        }
        if (zfas.isDirectory()) {
            target.createDirectory(new FileAttribute[0]);
        } else {
            InputStream is = this.zfs.newInputStream(getResolvedPath());
            try {
                OutputStream os = target.newOutputStream(new OpenOption[0]);
                try {
                    byte[] buf = new byte[8192];
                    while (true) {
                        int n = is.read(buf);
                        if (n == -1) {
                            break;
                        } else {
                            os.write(buf, 0, n);
                        }
                    }
                    os.close();
                    is.close();
                } catch (Throwable th) {
                    os.close();
                    throw th;
                }
            } catch (Throwable th2) {
                is.close();
                throw th2;
            }
        }
        if (copyAttrs) {
            BasicFileAttributeView view = (BasicFileAttributeView) ZipFileAttributeView.get(target, BasicFileAttributeView.class);
            try {
                view.setTimes(zfas.lastModifiedTime(), zfas.lastAccessTime(), zfas.creationTime());
            } catch (IOException x) {
                try {
                    target.delete();
                } catch (IOException e2) {
                }
                throw x;
            }
        }
    }
}

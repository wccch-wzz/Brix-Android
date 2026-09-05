package com.github.junrar.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public final class RandomAccessInputStream extends InputStream {
    private static final int BLOCK_MASK = 511;
    private static final int BLOCK_SHIFT = 9;
    private static final int BLOCK_SIZE = 512;
    private final InputStream src;
    private long pointer = 0;
    private final Vector data = new Vector();
    private long length = 0;
    private boolean foundEOS = false;

    public RandomAccessInputStream(InputStream inputstream) {
        this.src = inputstream;
    }

    public int getFilePointer() throws IOException {
        return (int) this.pointer;
    }

    public long getLongFilePointer() throws IOException {
        return this.pointer;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        long l = this.pointer + 1;
        long l1 = readUntil(l);
        if (l1 >= l) {
            byte[] abyte0 = (byte[]) this.data.elementAt((int) (this.pointer >>> 9));
            long j = this.pointer;
            this.pointer = 1 + j;
            return abyte0[(int) (511 & j)] & UByte.MAX_VALUE;
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] bytes, int off, int len) throws IOException {
        if (bytes == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        long l = readUntil(this.pointer + ((long) len));
        if (l <= this.pointer) {
            return -1;
        }
        byte[] abyte1 = (byte[]) this.data.elementAt((int) (this.pointer >>> 9));
        int k = Math.min(len, 512 - ((int) (this.pointer & 511)));
        System.arraycopy(abyte1, (int) (this.pointer & 511), bytes, off, k);
        this.pointer += (long) k;
        return k;
    }

    public void readFully(byte[] bytes) throws IOException {
        readFully(bytes, bytes.length);
    }

    public void readFully(byte[] bytes, int len) throws IOException {
        int read = 0;
        do {
            int l = read(bytes, read, len - read);
            if (l >= 0) {
                read += l;
            } else {
                return;
            }
        } while (read < len);
    }

    private long readUntil(long l) throws IOException {
        if (l < this.length) {
            return l;
        }
        if (this.foundEOS) {
            return this.length;
        }
        int i = (int) (l >>> 9);
        int j = (int) (this.length >>> 9);
        for (int k = j; k <= i; k++) {
            byte[] abyte0 = new byte[512];
            this.data.addElement(abyte0);
            int i1 = 512;
            int j1 = 0;
            while (i1 > 0) {
                int k1 = this.src.read(abyte0, j1, i1);
                if (k1 == -1) {
                    this.foundEOS = true;
                    return this.length;
                }
                j1 += k1;
                i1 -= k1;
                this.length += (long) k1;
            }
        }
        return this.length;
    }

    public void seek(long loc) throws IOException {
        if (loc < 0) {
            this.pointer = 0L;
        } else {
            this.pointer = loc;
        }
    }

    public void seek(int loc) throws IOException {
        long lloc = ((long) loc) & 4294967295L;
        if (lloc < 0) {
            this.pointer = 0L;
        } else {
            this.pointer = lloc;
        }
    }

    public int readInt() throws IOException {
        int i = read();
        int j = read();
        int k = read();
        int l = read();
        if ((i | j | k | l) < 0) {
            throw new EOFException();
        }
        return (i << 24) + (j << 16) + (k << 8) + l;
    }

    public long readLong() throws IOException {
        return (((long) readInt()) << 32) + (((long) readInt()) & 4294967295L);
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public short readShort() throws IOException {
        int i = read();
        int j = read();
        if ((i | j) < 0) {
            throw new EOFException();
        }
        return (short) ((i << 8) + j);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.data.removeAllElements();
        this.src.close();
    }
}

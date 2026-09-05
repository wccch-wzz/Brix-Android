package com.github.junrar.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class SeekableReadOnlyInputStream implements SeekableReadOnlyByteChannel {
    private final RandomAccessInputStream is;

    public SeekableReadOnlyInputStream(InputStream is) {
        this.is = new RandomAccessInputStream(new BufferedInputStream(is));
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public long getPosition() throws IOException {
        return this.is.getLongFilePointer();
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public void setPosition(long pos) throws IOException {
        this.is.seek(pos);
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int read() throws IOException {
        return this.is.read();
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int read(byte[] buffer, int off, int count) throws IOException {
        return this.is.read(buffer, off, count);
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int readFully(byte[] buffer, int count) throws IOException {
        this.is.readFully(buffer, count);
        return count;
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public void close() throws IOException {
        this.is.close();
    }
}

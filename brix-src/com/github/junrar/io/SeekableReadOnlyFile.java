package com.github.junrar.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: classes.dex */
public class SeekableReadOnlyFile implements SeekableReadOnlyByteChannel {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final RandomAccessFile file;

    public SeekableReadOnlyFile(File file) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, "r");
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int readFully(byte[] buffer, int count) throws IOException {
        if (count < 0) {
            throw new AssertionError(count);
        }
        this.file.readFully(buffer, 0, count);
        return count;
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public void close() throws IOException {
        this.file.close();
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public long getPosition() throws IOException {
        return this.file.getFilePointer();
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public void setPosition(long pos) throws IOException {
        this.file.seek(pos);
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int read() throws IOException {
        return this.file.read();
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int read(byte[] buffer, int off, int count) throws IOException {
        return this.file.read(buffer, off, count);
    }
}

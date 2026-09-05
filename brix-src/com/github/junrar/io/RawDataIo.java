package com.github.junrar.io;

import java.io.IOException;
import java.util.LinkedList;
import javax.crypto.Cipher;

/* JADX INFO: loaded from: classes.dex */
public class RawDataIo implements SeekableReadOnlyByteChannel {
    private final SeekableReadOnlyByteChannel underlyingByteChannel;
    private Cipher cipher = null;
    private boolean isEncrypted = false;
    private final LinkedList<Byte> dataPool = new LinkedList<>();
    private final byte[] reused = new byte[1];

    public RawDataIo(SeekableReadOnlyByteChannel channel) {
        this.underlyingByteChannel = channel;
    }

    public Cipher getCipher() {
        return this.cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
        this.isEncrypted = true;
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public long getPosition() throws IOException {
        return this.underlyingByteChannel.getPosition();
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public void setPosition(long pos) throws IOException {
        this.underlyingByteChannel.setPosition(pos);
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int read() throws IOException {
        read(this.reused, 0, 1);
        return this.reused[0];
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int read(byte[] buffer, int off, int count) throws IOException {
        byte[] tmp = new byte[count];
        int size = readFully(tmp, count);
        System.arraycopy(tmp, 0, buffer, off, count);
        return size;
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public int readFully(byte[] buffer, int count) throws IOException {
        if (this.isEncrypted) {
            int remainingSize = this.dataPool.size();
            int toRead = count - remainingSize;
            int realRead = (((~toRead) + 1) & 15) + toRead;
            byte[] tmp = new byte[realRead];
            if (realRead > 0) {
                this.underlyingByteChannel.readFully(tmp, realRead);
                byte[] decrypted = this.cipher.update(tmp);
                for (byte b : decrypted) {
                    this.dataPool.add(Byte.valueOf(b));
                }
            }
            int realReadSize = 0;
            for (int i = 0; i < count && !this.dataPool.isEmpty(); i++) {
                buffer[i] = this.dataPool.poll().byteValue();
                realReadSize++;
            }
            return realReadSize;
        }
        return this.underlyingByteChannel.readFully(buffer, count);
    }

    @Override // com.github.junrar.io.SeekableReadOnlyByteChannel
    public void close() throws IOException {
        this.underlyingByteChannel.close();
    }
}

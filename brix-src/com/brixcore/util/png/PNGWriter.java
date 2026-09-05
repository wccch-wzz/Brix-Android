package com.brixcore.util.png;

import com.brixcore.util.png.image.ArgbImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/* JADX INFO: loaded from: classes12.dex */
public final class PNGWriter implements Closeable {
    private static final int COMPRESS_THRESHOLD = 20;
    public static final int DEFAULT_COMPRESS_LEVEL = -1;
    private static final byte[] PNG_FILE_HEADER = {-119, 80, 78, 71, 13, 10, 26, 10};
    private static final byte[] textSeparator = {0};
    private final int compressLevel;
    private final CRC32 crc32;
    private final Deflater deflater;
    private final OutputStream out;
    private final PNGType type;
    private final byte[] writeBuffer;

    public PNGWriter(OutputStream out) {
        this(out, PNGType.RGBA, -1);
    }

    public PNGWriter(OutputStream out, PNGType type) {
        this(out, type, -1);
    }

    public PNGWriter(OutputStream out, int compressLevel) {
        this(out, PNGType.RGBA, compressLevel);
    }

    public PNGWriter(OutputStream out, PNGType type, int compressLevel) {
        this.deflater = new Deflater();
        this.crc32 = new CRC32();
        this.writeBuffer = new byte[8];
        Objects.requireNonNull(type);
        Objects.requireNonNull(out);
        if (compressLevel != -1 && (compressLevel < 0 || compressLevel > 9)) {
            throw new IllegalArgumentException();
        }
        if (type != PNGType.RGB && type != PNGType.RGBA) {
            throw new UnsupportedOperationException("SimplePNG currently only supports RGB or RGBA images");
        }
        this.type = type;
        this.compressLevel = compressLevel;
        this.out = out;
        this.deflater.setLevel(compressLevel);
    }

    public PNGType getType() {
        return this.type;
    }

    public int getCompressLevel() {
        return this.compressLevel;
    }

    private void writeByte(int b) throws IOException {
        this.out.write(b);
        this.crc32.update(b);
    }

    private void writeShort(int s) throws IOException {
        this.writeBuffer[0] = (byte) (s >>> 8);
        this.writeBuffer[1] = (byte) (s >>> 0);
        writeBytes(this.writeBuffer, 0, 2);
    }

    private void writeInt(int value) throws IOException {
        this.writeBuffer[0] = (byte) (value >>> 24);
        this.writeBuffer[1] = (byte) (value >>> 16);
        this.writeBuffer[2] = (byte) (value >>> 8);
        this.writeBuffer[3] = (byte) (value >>> 0);
        writeBytes(this.writeBuffer, 0, 4);
    }

    private void writeLong(long value) throws IOException {
        this.writeBuffer[0] = (byte) (value >>> 56);
        this.writeBuffer[1] = (byte) (value >>> 48);
        this.writeBuffer[2] = (byte) (value >>> 40);
        this.writeBuffer[3] = (byte) (value >>> 32);
        this.writeBuffer[4] = (byte) (value >>> 24);
        this.writeBuffer[5] = (byte) (value >>> 16);
        this.writeBuffer[6] = (byte) (value >>> 8);
        this.writeBuffer[7] = (byte) (value >>> 0);
        writeBytes(this.writeBuffer, 0, 8);
    }

    private void writeBytes(byte[] bytes) throws IOException {
        writeBytes(bytes, 0, bytes.length);
    }

    private void writeBytes(byte[] bytes, int off, int len) throws IOException {
        this.out.write(bytes, off, len);
        this.crc32.update(bytes, off, len);
    }

    private void beginChunk(String header, int length) throws IOException {
        this.writeBuffer[0] = (byte) (length >>> 24);
        this.writeBuffer[1] = (byte) (length >>> 16);
        this.writeBuffer[2] = (byte) (length >>> 8);
        this.writeBuffer[3] = (byte) (length >>> 0);
        this.writeBuffer[4] = (byte) header.charAt(0);
        this.writeBuffer[5] = (byte) header.charAt(1);
        this.writeBuffer[6] = (byte) header.charAt(2);
        this.writeBuffer[7] = (byte) header.charAt(3);
        this.out.write(this.writeBuffer, 0, 8);
        this.crc32.reset();
        this.crc32.update(this.writeBuffer, 4, 4);
    }

    private void endChunk() throws IOException {
        int crc = (int) this.crc32.getValue();
        this.writeBuffer[0] = (byte) (crc >>> 24);
        this.writeBuffer[1] = (byte) (crc >>> 16);
        this.writeBuffer[2] = (byte) (crc >>> 8);
        this.writeBuffer[3] = (byte) (crc >>> 0);
        this.out.write(this.writeBuffer, 0, 4);
    }

    private void textChunk(String keyword, String text) throws IOException {
        byte[] separator;
        String chunkType;
        byte[] keywordBytes = keyword.getBytes(StandardCharsets.US_ASCII);
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        int textBytesLength = textBytes.length;
        boolean isAscii = text.length() == textBytesLength;
        boolean compress = this.compressLevel != 0 && textBytesLength >= 20;
        if (compress) {
            byte[] compressed = new byte[textBytesLength];
            this.deflater.reset();
            this.deflater.setInput(textBytes);
            this.deflater.finish();
            int len = this.deflater.deflate(compressed, 0, textBytesLength, 2);
            if (len < textBytesLength) {
                textBytes = compressed;
                textBytesLength = len;
            } else {
                compress = false;
                this.deflater.reset();
            }
        }
        if (isAscii) {
            if (compress) {
                chunkType = "zTXt";
                separator = new byte[]{0, 0};
            } else {
                separator = new byte[]{0};
                chunkType = "tEXt";
            }
        } else {
            separator = new byte[]{0, (byte) (compress ? 1 : 0), 0, 0, 0};
            chunkType = "iTXt";
        }
        beginChunk(chunkType, keywordBytes.length + separator.length + textBytesLength);
        writeBytes(keywordBytes);
        writeBytes(separator);
        writeBytes(textBytes, 0, textBytesLength);
        endChunk();
    }

    public void write(ArgbImage image) throws IOException {
        PNGMetadata metadata = image.getMetadata();
        int width = image.getWidth();
        int height = image.getHeight();
        this.out.write(PNG_FILE_HEADER);
        beginChunk("IHDR", 13);
        writeInt(width);
        writeInt(height);
        writeByte(8);
        writeByte(this.type.id);
        writeByte(0);
        writeByte(0);
        writeByte(0);
        endChunk();
        if (metadata != null) {
            for (Map.Entry<String, String> entry : metadata.texts.entrySet()) {
                textChunk(entry.getKey(), entry.getValue());
            }
        }
        int colorPerPixel = this.type.cpp;
        int bytesPerLine = (colorPerPixel * width) + 1;
        int rawOutputSize = bytesPerLine * height;
        byte[] lineBuffer = new byte[bytesPerLine];
        this.deflater.reset();
        OutputBuffer buffer = new OutputBuffer(this.compressLevel == 0 ? rawOutputSize + 12 : rawOutputSize / 2);
        DeflaterOutputStream dos = new DeflaterOutputStream(buffer, this.deflater);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                try {
                    int color = image.getArgb(x, y);
                    int off = (colorPerPixel * x) + 1;
                    lineBuffer[off + 0] = (byte) (color >>> 16);
                    lineBuffer[off + 1] = (byte) (color >>> 8);
                    lineBuffer[off + 2] = (byte) (color >>> 0);
                    if (colorPerPixel == 4) {
                        lineBuffer[off + 3] = (byte) (color >>> 24);
                    }
                } catch (Throwable th) {
                    try {
                        dos.close();
                        throw th;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        throw th;
                    }
                }
            }
            dos.write(lineBuffer);
        }
        dos.close();
        int len = buffer.size();
        beginChunk("IDAT", len);
        writeBytes(buffer.getBuffer(), 0, len);
        endChunk();
        beginChunk("IEND", 0);
        endChunk();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.deflater.end();
        this.out.close();
    }

    private static final class OutputBuffer extends ByteArrayOutputStream {
        public OutputBuffer() {
        }

        public OutputBuffer(int size) {
            super(size);
        }

        byte[] getBuffer() {
            return ((ByteArrayOutputStream) this).buf;
        }
    }
}

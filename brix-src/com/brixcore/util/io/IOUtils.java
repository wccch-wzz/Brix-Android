package com.brixcore.util.io;

import com.brixcore.util.platform.OperatingSystem;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang3.CharEncoding;
import org.glavo.chardet.DetectedCharset;
import org.glavo.chardet.UniversalDetector;

/* JADX INFO: loaded from: classes3.dex */
public final class IOUtils {
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    private IOUtils() {
    }

    public static byte[] readFullyWithoutClosing(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream(Math.max(stream.available(), 32));
        copyTo(stream, result);
        return result.toByteArray();
    }

    public static String readFullyAsStringWithClosing(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream(Math.max(stream.available(), 32));
        copyTo(stream, result);
        return result.toString(CharEncoding.UTF_8);
    }

    public static BufferedReader newBufferedReaderMaybeNativeEncoding(Path file) throws IOException {
        if (OperatingSystem.NATIVE_CHARSET == StandardCharsets.UTF_8) {
            return Files.newBufferedReader(file);
        }
        FileChannel channel = FileChannel.open(file, new OpenOption[0]);
        try {
            long oldPosition = channel.position();
            DetectedCharset detectedCharset = UniversalDetector.detectCharset(channel);
            Charset charset = (detectedCharset != null && detectedCharset.isSupported() && (detectedCharset.getCharset() == StandardCharsets.UTF_8 || detectedCharset.getCharset() == StandardCharsets.US_ASCII)) ? StandardCharsets.UTF_8 : OperatingSystem.NATIVE_CHARSET;
            channel.position(oldPosition);
            return new BufferedReader(new InputStreamReader(Channels.newInputStream(channel), charset));
        } catch (Throwable e) {
            closeQuietly(channel, e);
            throw e;
        }
    }

    public static byte[] readFully(InputStream stream) throws IOException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[16384];
            while (true) {
                int numRead = stream.read(data, 0, data.length);
                if (numRead == -1) {
                    break;
                }
                buffer.write(data, 0, numRead);
            }
            byte[] byteArray = buffer.toByteArray();
            if (stream != null) {
                stream.close();
            }
            return byteArray;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static String readFullyAsString(InputStream stream) throws IOException {
        return new String(readFully(stream), StandardCharsets.UTF_8);
    }

    public static String readFullyAsString(InputStream stream, Charset charset) throws IOException {
        return new String(readFully(stream), charset);
    }

    public static void skipNBytes(InputStream input, long n) throws IOException {
        while (n > 0) {
            long ns = input.skip(n);
            if (ns > 0 && ns <= n) {
                n -= ns;
            } else if (ns == 0) {
                if (input.read() == -1) {
                    throw new EOFException();
                }
                n--;
            } else {
                throw new IOException("Unexpected skip bytes. Expected: " + n + ", Actual: " + ns);
            }
        }
    }

    public static void copyTo(InputStream src, OutputStream dest) throws IOException {
        copyTo(src, dest, new byte[8192]);
    }

    public static void copyTo(InputStream src, OutputStream dest, byte[] buf) throws IOException {
        while (true) {
            int len = src.read(buf);
            if (len != -1) {
                dest.write(buf, 0, len);
            } else {
                return;
            }
        }
    }

    public static InputStream wrapFromGZip(InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                IOUtils$$ExternalSyntheticAutoCloseableDispatcher0.m(closeable);
            } catch (Throwable th) {
            }
        }
    }

    public static void closeQuietly(AutoCloseable closeable, Throwable exception) {
        if (closeable != null) {
            try {
                IOUtils$$ExternalSyntheticAutoCloseableDispatcher0.m(closeable);
            } catch (Throwable e) {
                exception.addSuppressed(e);
            }
        }
    }
}

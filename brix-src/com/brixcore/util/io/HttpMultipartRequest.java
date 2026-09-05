package com.brixcore.util.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.jsoup.helper.HttpConnection;

/* JADX INFO: loaded from: classes3.dex */
public final class HttpMultipartRequest implements Closeable {
    private static final byte[] ENDL = {13, 10};
    private final String boundary = "*****" + System.currentTimeMillis() + "*****";
    private final ByteArrayOutputStream stream;
    private final HttpURLConnection urlConnection;

    public HttpMultipartRequest(HttpURLConnection urlConnection) throws IOException {
        this.urlConnection = urlConnection;
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty(HttpConnection.CONTENT_TYPE, "multipart/form-data; boundary=" + this.boundary);
        this.stream = new ByteArrayOutputStream();
    }

    private void addLine(String content) throws IOException {
        this.stream.write(content.getBytes(StandardCharsets.UTF_8));
        this.stream.write(ENDL);
    }

    public HttpMultipartRequest file(String name, String filename, String contentType, InputStream inputStream) throws IOException {
        addLine("--" + this.boundary);
        addLine(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", name, filename));
        addLine("Content-Type: " + contentType);
        addLine("");
        IOUtils.copyTo(inputStream, this.stream);
        addLine("");
        return this;
    }

    public HttpMultipartRequest param(String name, String value) throws IOException {
        addLine("--" + this.boundary);
        addLine(String.format("Content-Disposition: form-data; name=\"%s\"", name));
        addLine("");
        addLine(value);
        return this;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        addLine("--" + this.boundary + "--");
        this.urlConnection.setRequestProperty("Content-Length", "" + this.stream.size());
        OutputStream os = this.urlConnection.getOutputStream();
        try {
            this.stream.writeTo(os);
            if (os != null) {
                os.close();
            }
        } catch (Throwable th) {
            if (os != null) {
                try {
                    os.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}

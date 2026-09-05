package com.brixcore.task;

import com.brixcore.util.io.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes7.dex */
public final class GetTask extends FetchTask<String> {
    private final Charset charset;

    public GetTask(URL url) {
        this(url, StandardCharsets.UTF_8);
    }

    public GetTask(URL url, Charset charset) {
        this(url, charset, 3);
    }

    public GetTask(URL url, Charset charset, int retry) {
        this((List<URL>) Collections.singletonList(url), charset, retry);
    }

    public GetTask(List<URL> url) {
        this(url, StandardCharsets.UTF_8, 3);
    }

    public GetTask(List<URL> urls, Charset charset, int retry) {
        super(urls, retry);
        this.charset = charset;
        setName(urls.get(0).toString());
    }

    @Override // com.brixcore.task.FetchTask
    protected FetchTask.EnumCheckETag shouldCheckETag() {
        return FetchTask.EnumCheckETag.CHECK_E_TAG;
    }

    @Override // com.brixcore.task.FetchTask
    protected void useCachedResult(Path cachedFile) throws IOException {
        setResult(FileUtils.readText(cachedFile));
    }

    @Override // com.brixcore.task.FetchTask
    protected FetchTask.Context getContext(final URLConnection conn, final boolean checkETag) {
        int length = conn.getContentLength();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(length <= 0 ? 8192 : length);
        return new FetchTask.Context() { // from class: com.brixcore.task.GetTask.1
            @Override // com.brixcore.task.FetchTask.Context
            public void write(byte[] buffer, int offset, int len) {
                baos.write(buffer, offset, len);
            }

            @Override // java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                if (isSuccess()) {
                    String result = baos.toString(GetTask.this.charset.name());
                    GetTask.this.setResult(result);
                    if (checkETag) {
                        GetTask.this.repository.cacheText(result, conn);
                    }
                }
            }
        };
    }
}

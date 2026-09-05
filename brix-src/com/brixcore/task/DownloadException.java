package com.brixcore.task;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/* JADX INFO: loaded from: classes7.dex */
public class DownloadException extends IOException {
    private final URL url;

    public DownloadException(URL url, Throwable cause) {
        super("Unable to download " + url + ", " + cause.getMessage(), (Throwable) Objects.requireNonNull(cause));
        this.url = url;
    }

    public URL getUrl() {
        return this.url;
    }
}

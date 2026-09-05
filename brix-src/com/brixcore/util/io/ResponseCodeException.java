package com.brixcore.util.io;

import java.io.IOException;
import java.net.URL;

/* JADX INFO: loaded from: classes3.dex */
public final class ResponseCodeException extends IOException {
    private final String data;
    private final int responseCode;
    private final URL url;

    public ResponseCodeException(URL url, int responseCode) {
        super("Unable to request url " + url + ", response code: " + responseCode);
        this.url = url;
        this.responseCode = responseCode;
        this.data = null;
    }

    public ResponseCodeException(URL url, int responseCode, Throwable cause) {
        super("Unable to request url " + url + ", response code: " + responseCode, cause);
        this.url = url;
        this.responseCode = responseCode;
        this.data = null;
    }

    public ResponseCodeException(URL url, int responseCode, String data) {
        super("Unable to request url " + url + ", response code: " + responseCode + ", data: " + data);
        this.url = url;
        this.responseCode = responseCode;
        this.data = data;
    }

    public URL getUrl() {
        return this.url;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getData() {
        return this.data;
    }
}

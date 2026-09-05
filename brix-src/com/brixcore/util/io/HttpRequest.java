package com.brixcore.util.io;

import com.brixcore.task.Schedulers;
import com.brixcore.util.Lang;
import com.brixcore.util.Pair;
import com.brixcore.util.function.ExceptionalBiConsumer;
import com.brixcore.util.function.ExceptionalSupplier;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.HttpConnection;

/* JADX INFO: loaded from: classes3.dex */
public abstract class HttpRequest {
    protected final Map<String, String> headers;
    protected boolean ignoreHttpCode;
    protected final String method;
    protected ExceptionalBiConsumer<URL, Integer, IOException> responseCodeTester;
    protected int retryTimes;
    protected final Set<Integer> toleratedHttpCodes;
    protected final String url;

    public interface Authorization {
        String getAccessToken();

        String getTokenType();
    }

    public abstract String getString() throws IOException;

    private HttpRequest(String url, String method) {
        this.headers = new HashMap();
        this.toleratedHttpCodes = new HashSet();
        this.retryTimes = 1;
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return this.url;
    }

    public HttpRequest accept(String contentType) {
        return header("Accept", contentType);
    }

    public HttpRequest authorization(String token) {
        return header("Authorization", token);
    }

    public HttpRequest authorization(String tokenType, String tokenString) {
        return authorization(tokenType + StringUtils.SPACE + tokenString);
    }

    public HttpRequest authorization(Authorization authorization) {
        return authorization(authorization.getTokenType(), authorization.getAccessToken());
    }

    public HttpRequest header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpRequest ignoreHttpCode() {
        this.ignoreHttpCode = true;
        return this;
    }

    public HttpRequest retry(int retryTimes) {
        if (retryTimes < 1) {
            throw new IllegalArgumentException("retryTimes >= 1");
        }
        this.retryTimes = retryTimes;
        return this;
    }

    public CompletableFuture<String> getStringAsync() {
        return CompletableFuture.supplyAsync(Lang.wrap(new ExceptionalSupplier() { // from class: com.brixcore.util.io.HttpRequest$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.getString();
            }
        }), Schedulers.io());
    }

    public <T> T getJson(Class<T> cls) throws JsonParseException, IOException {
        return (T) JsonUtils.fromNonNullJson(getString(), (Class) cls);
    }

    public <T> T getJson(TypeToken<T> typeToken) throws JsonParseException, IOException {
        return (T) JsonUtils.fromNonNullJson(getString(), typeToken);
    }

    public <T> T getJson(Type type) throws JsonParseException, IOException {
        return (T) JsonUtils.fromNonNullJson(getString(), type);
    }

    public <T> CompletableFuture<T> getJsonAsync(final Class<T> cls) {
        return (CompletableFuture<T>) getStringAsync().thenApplyAsync(new Function() { // from class: com.brixcore.util.io.HttpRequest$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return JsonUtils.fromNonNullJson((String) obj, cls);
            }
        });
    }

    public <T> CompletableFuture<T> getJsonAsync(final Type type) {
        return (CompletableFuture<T>) getStringAsync().thenApplyAsync(new Function() { // from class: com.brixcore.util.io.HttpRequest$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return JsonUtils.fromNonNullJson((String) obj, type);
            }
        });
    }

    public HttpRequest filter(ExceptionalBiConsumer<URL, Integer, IOException> responseCodeTester) {
        this.responseCodeTester = responseCodeTester;
        return this;
    }

    public HttpRequest ignoreHttpErrorCode(int code) {
        this.toleratedHttpCodes.add(Integer.valueOf(code));
        return this;
    }

    public HttpURLConnection createConnection() throws IOException {
        HttpURLConnection con = NetworkUtils.createHttpConnection(new URL(this.url));
        con.setRequestMethod(this.method);
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return con;
    }

    public static class HttpGetRequest extends HttpRequest {
        public HttpGetRequest(String url) {
            super(url, "GET");
        }

        @Override // com.brixcore.util.io.HttpRequest
        public String getString() throws IOException {
            return HttpRequest.getStringWithRetry(new ExceptionalSupplier() { // from class: com.brixcore.util.io.HttpRequest$HttpGetRequest$$ExternalSyntheticLambda0
                @Override // com.brixcore.util.function.ExceptionalSupplier
                public final Object get() {
                    return this.f$0.lambda$getString$0();
                }
            }, this.retryTimes);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getString$0() throws IOException {
            HttpURLConnection con = NetworkUtils.resolveConnection(createConnection());
            return IOUtils.readFullyAsString("gzip".equals(con.getContentEncoding()) ? IOUtils.wrapFromGZip(con.getInputStream()) : con.getInputStream());
        }
    }

    public static final class HttpPostRequest extends HttpRequest {
        private byte[] bytes;

        public HttpPostRequest(String url) {
            super(url, "POST");
        }

        public HttpPostRequest contentType(String contentType) {
            this.headers.put(HttpConnection.CONTENT_TYPE, contentType);
            return this;
        }

        public HttpPostRequest json(Object payload) throws JsonParseException {
            return string(payload instanceof String ? (String) payload : JsonUtils.GSON.toJson(payload), "application/json");
        }

        public HttpPostRequest form(Map<String, String> params) {
            return string(NetworkUtils.withQuery("", params), HttpConnection.FORM_URL_ENCODED);
        }

        @SafeVarargs
        public final HttpPostRequest form(Pair<String, String>... params) {
            return form(Lang.mapOf(params));
        }

        public HttpPostRequest string(String payload, String contentType) {
            this.bytes = payload.getBytes(StandardCharsets.UTF_8);
            header("Content-Length", "" + this.bytes.length);
            contentType(contentType + "; charset=utf-8");
            return this;
        }

        @Override // com.brixcore.util.io.HttpRequest
        public String getString() throws IOException {
            return HttpRequest.getStringWithRetry(new ExceptionalSupplier() { // from class: com.brixcore.util.io.HttpRequest$HttpPostRequest$$ExternalSyntheticLambda0
                @Override // com.brixcore.util.function.ExceptionalSupplier
                public final Object get() {
                    return this.f$0.lambda$getString$0();
                }
            }, this.retryTimes);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getString$0() throws Exception {
            HttpURLConnection con = createConnection();
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            try {
                os.write(this.bytes);
                if (os != null) {
                    os.close();
                }
                URL url = new URL(this.url);
                if (this.responseCodeTester != null) {
                    this.responseCodeTester.accept(url, Integer.valueOf(con.getResponseCode()));
                } else if (con.getResponseCode() / 100 != 2 && !this.ignoreHttpCode && !this.toleratedHttpCodes.contains(Integer.valueOf(con.getResponseCode()))) {
                    try {
                        throw new ResponseCodeException(url, con.getResponseCode(), NetworkUtils.readData(con));
                    } catch (IOException e) {
                        throw new ResponseCodeException(url, con.getResponseCode(), e);
                    }
                }
                return NetworkUtils.readData(con);
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

    public static HttpGetRequest GET(String url) {
        return new HttpGetRequest(url);
    }

    @SafeVarargs
    public static HttpGetRequest GET(String url, Pair<String, String>... query) {
        return GET(NetworkUtils.withQuery(url, Lang.mapOf(query)));
    }

    public static HttpPostRequest POST(String url) throws MalformedURLException {
        return new HttpPostRequest(url);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getStringWithRetry(ExceptionalSupplier<String, IOException> supplier, int retryTimes) throws IOException {
        Throwable exception = null;
        for (int i = 0; i < retryTimes; i++) {
            try {
                return supplier.get();
            } catch (Throwable e) {
                exception = e;
            }
        }
        if (exception != null) {
            if (exception instanceof IOException) {
                throw ((IOException) exception);
            }
            throw new IOException(exception);
        }
        throw new IOException("retry 0");
    }
}

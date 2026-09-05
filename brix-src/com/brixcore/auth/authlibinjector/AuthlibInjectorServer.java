package com.brixcore.auth.authlibinjector;

import com.brixcore.auth.yggdrasil.YggdrasilService;
import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.fakefx.ObservableHelper;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.io.IOUtils;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.JsonAdapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes14.dex */
@JsonAdapter(Deserializer.class)
public class AuthlibInjectorServer implements Observable {
    private static final Gson GSON = new GsonBuilder().create();
    private transient boolean metadataRefreshed;
    private String metadataResponse;
    private long metadataTimestamp;
    private transient String name;
    private transient boolean nonEmailLogin;
    private String url;
    private final transient YggdrasilService yggdrasilService;
    private transient Map<String, String> links = Collections.emptyMap();
    private final transient ObservableHelper helper = new ObservableHelper(this);

    public static AuthlibInjectorServer locateServer(String url) throws IOException {
        try {
            String url2 = NetworkUtils.addHttpsIfMissing(url);
            HttpURLConnection conn = (HttpURLConnection) new URL(url2).openConnection();
            conn.setRequestProperty("Accept-Language", Locale.getDefault().toLanguageTag());
            String ali = conn.getHeaderField("x-authlib-injector-api-location");
            if (ali != null) {
                URL absoluteAli = new URL(conn.getURL(), ali);
                if (!urlEqualsIgnoreSlash(url2, absoluteAli.toString())) {
                    conn.disconnect();
                    url2 = absoluteAli.toString();
                    conn = (HttpURLConnection) absoluteAli.openConnection();
                    conn.setRequestProperty("Accept-Language", Locale.getDefault().toLanguageTag());
                }
            }
            if (!url2.endsWith("/")) {
                url2 = url2 + "/";
            }
            try {
                AuthlibInjectorServer server = new AuthlibInjectorServer(url2);
                server.refreshMetadata(IOUtils.readFullyAsStringWithClosing(conn.getInputStream()));
                return server;
            } finally {
                conn.disconnect();
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }

    private static boolean urlEqualsIgnoreSlash(String a, String b) {
        if (!a.endsWith("/")) {
            a = a + "/";
        }
        if (!b.endsWith("/")) {
            b = b + "/";
        }
        return a.equals(b);
    }

    public AuthlibInjectorServer(String url) {
        this.url = url;
        this.yggdrasilService = new YggdrasilService(new AuthlibInjectorProvider(url));
    }

    public String getUrl() {
        return this.url;
    }

    public YggdrasilService getYggdrasilService() {
        return this.yggdrasilService;
    }

    public Optional<String> getMetadataResponse() {
        return Optional.ofNullable(this.metadataResponse);
    }

    public long getMetadataTimestamp() {
        return this.metadataTimestamp;
    }

    public String getName() {
        return (String) Optional.ofNullable(this.name).orElse(this.url);
    }

    public Map<String, String> getLinks() {
        return this.links;
    }

    public boolean isNonEmailLogin() {
        return this.nonEmailLogin;
    }

    public String fetchMetadataResponse() throws IOException {
        if (this.metadataResponse == null || !this.metadataRefreshed) {
            refreshMetadata();
        }
        return getMetadataResponse().get();
    }

    public void refreshMetadata() throws IOException {
        refreshMetadata(HttpRequest.GET(this.url).getString());
    }

    private void refreshMetadata(String text) throws IOException {
        long timestamp = System.currentTimeMillis();
        try {
            setMetadataResponse(text, timestamp);
            this.metadataRefreshed = true;
            Logging.LOG.info("authlib-injector server metadata refreshed: " + this.url);
            try {
                this.helper.invalidate();
            } catch (Throwable e) {
                Logging.LOG.info("refreshMetadata error: " + e);
            }
        } catch (JsonParseException e2) {
            throw new IOException("Malformed response\n" + text, e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMetadataResponse(String metadataResponse, long metadataTimestamp) throws JsonParseException {
        JsonObject response = (JsonObject) GSON.fromJson(metadataResponse, JsonObject.class);
        if (response == null) {
            throw new JsonParseException("Metadata response is empty");
        }
        synchronized (this) {
            this.metadataResponse = metadataResponse;
            this.metadataTimestamp = metadataTimestamp;
            Optional<JsonObject> metaObject = Lang.tryCast(response.get("meta"), JsonObject.class);
            this.name = (String) metaObject.flatMap(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Lang.tryCast(((JsonObject) obj).get("serverName"), JsonPrimitive.class).map(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda0
                        @Override // java.util.function.Function
                        public final Object apply(Object obj2) {
                            return ((JsonPrimitive) obj2).getAsString();
                        }
                    });
                }
            }).orElse(null);
            this.links = (Map) metaObject.flatMap(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Lang.tryCast(((JsonObject) obj).get("links"), JsonObject.class);
                }
            }).map(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return AuthlibInjectorServer.lambda$setMetadataResponse$4((JsonObject) obj);
                }
            }).orElse(Collections.emptyMap());
            this.nonEmailLogin = ((Boolean) metaObject.flatMap(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Lang.tryCast(((JsonObject) obj).get("feature.non_email_login"), JsonPrimitive.class);
                }
            }).map(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda6
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Boolean.valueOf(((JsonPrimitive) obj).getAsBoolean());
                }
            }).orElse(false)).booleanValue();
        }
    }

    static /* synthetic */ Map lambda$setMetadataResponse$4(JsonObject linksObject) {
        final Map<String, String> converted = new LinkedHashMap<>();
        linksObject.entrySet().forEach(new Consumer() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Map.Entry entry = (Map.Entry) obj;
                Lang.tryCast(entry.getValue(), JsonPrimitive.class).ifPresent(new Consumer() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorServer$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj2) {
                        map.put((String) entry.getKey(), ((JsonPrimitive) obj2).getAsString());
                    }
                });
            }
        });
        return converted;
    }

    public void invalidateMetadataCache() {
        this.metadataRefreshed = false;
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AuthlibInjectorServer)) {
            return false;
        }
        AuthlibInjectorServer another = (AuthlibInjectorServer) obj;
        return this.url.equals(another.url);
    }

    public String toString() {
        return this.name == null ? this.url : this.url + " (" + this.name + ")";
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper.addListener(listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper.removeListener(listener);
    }

    public static class Deserializer implements JsonDeserializer<AuthlibInjectorServer> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.gson.JsonDeserializer
        public AuthlibInjectorServer deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            AuthlibInjectorServer instance = new AuthlibInjectorServer(jsonObj.get("url").getAsString());
            if (jsonObj.has("name")) {
                instance.name = jsonObj.get("name").getAsString();
            }
            if (jsonObj.has("metadataResponse")) {
                try {
                    instance.setMetadataResponse(jsonObj.get("metadataResponse").getAsString(), jsonObj.get("metadataTimestamp").getAsLong());
                } catch (JsonParseException e) {
                    Logging.LOG.log(Level.WARNING, "Ignoring malformed metadata response cache: " + jsonObj.get("metadataResponse"), (Throwable) e);
                }
            }
            return instance;
        }
    }
}

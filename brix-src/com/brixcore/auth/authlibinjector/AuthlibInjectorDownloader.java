package com.brixcore.auth.authlibinjector;

import com.brixcore.download.DownloadProvider;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.util.Logging;
import com.brixcore.util.io.HttpRequest;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes14.dex */
public class AuthlibInjectorDownloader implements AuthlibInjectorArtifactProvider {
    private static final String LATEST_BUILD_URL = "https://authlib-injector.yushi.moe/artifact/latest.json";
    private final Path artifactLocation;
    private final Supplier<DownloadProvider> downloadProvider;
    private final AtomicBoolean updateChecked = new AtomicBoolean(false);

    public AuthlibInjectorDownloader(Path artifactLocation, Supplier<DownloadProvider> downloadProvider) {
        this.artifactLocation = artifactLocation;
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactProvider
    public AuthlibInjectorArtifactInfo getArtifactInfo() throws IOException {
        Optional<AuthlibInjectorArtifactInfo> cached = getArtifactInfoImmediately();
        if (cached.isPresent()) {
            return cached.get();
        }
        synchronized (this) {
            Optional<AuthlibInjectorArtifactInfo> local = getLocalArtifact();
            if (local.isPresent()) {
                return local.get();
            }
            Logging.LOG.info("No local authlib-injector found, downloading");
            this.updateChecked.set(true);
            update();
            return getLocalArtifact().orElseThrow(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorDownloader$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return AuthlibInjectorDownloader.lambda$getArtifactInfo$0();
                }
            });
        }
    }

    static /* synthetic */ IOException lambda$getArtifactInfo$0() {
        return new IOException("The downloaded authlib-inejector cannot be recognized");
    }

    @Override // com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactProvider
    public Optional<AuthlibInjectorArtifactInfo> getArtifactInfoImmediately() {
        return getLocalArtifact();
    }

    public void checkUpdate() throws IOException {
        if (this.updateChecked.compareAndSet(false, true)) {
            synchronized (this) {
                Logging.LOG.info("Checking update of authlib-injector");
                update();
            }
        }
    }

    private void update() throws IOException {
        AuthlibInjectorVersionInfo latest = getLatestArtifactInfo();
        Optional<AuthlibInjectorArtifactInfo> local = getLocalArtifact();
        if (local.isPresent() && local.get().getBuildNumber() >= latest.buildNumber) {
            return;
        }
        try {
            new FileDownloadTask(this.downloadProvider.get().injectURLWithCandidates(latest.downloadUrl), this.artifactLocation.toFile(), (FileDownloadTask.IntegrityCheck) Optional.ofNullable(latest.checksums.get("sha256")).map(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorDownloader$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return AuthlibInjectorDownloader.lambda$update$1((String) obj);
                }
            }).orElse(null)).run();
            Logging.LOG.info("Updated authlib-injector to " + latest.version);
        } catch (Exception e) {
            throw new IOException("Failed to download authlib-injector", e);
        }
    }

    static /* synthetic */ FileDownloadTask.IntegrityCheck lambda$update$1(String checksum) {
        return new FileDownloadTask.IntegrityCheck("SHA-256", checksum);
    }

    private AuthlibInjectorVersionInfo getLatestArtifactInfo() throws IOException {
        IOException exception = null;
        for (URL url : this.downloadProvider.get().injectURLWithCandidates(LATEST_BUILD_URL)) {
            try {
                return (AuthlibInjectorVersionInfo) HttpRequest.GET(url.toExternalForm()).getJson(AuthlibInjectorVersionInfo.class);
            } catch (JsonParseException | IOException e) {
                if (exception == null) {
                    exception = new IOException("Failed to fetch authlib-injector artifact info");
                }
                exception.addSuppressed(e);
            }
        }
        if (exception == null) {
            IOException exception2 = new IOException("No authlib-injector download providers available");
            throw exception2;
        }
        throw exception;
    }

    private Optional<AuthlibInjectorArtifactInfo> getLocalArtifact() {
        return parseArtifact(this.artifactLocation);
    }

    protected static Optional<AuthlibInjectorArtifactInfo> parseArtifact(Path path) {
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return Optional.empty();
        }
        try {
            return Optional.of(AuthlibInjectorArtifactInfo.from(path));
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Bad authlib-injector artifact", (Throwable) e);
            return Optional.empty();
        }
    }

    private static final class AuthlibInjectorVersionInfo {

        @SerializedName("build_number")
        public int buildNumber;

        @SerializedName("checksums")
        public Map<String, String> checksums;

        @SerializedName("download_url")
        public String downloadUrl;

        @SerializedName("version")
        public String version;

        private AuthlibInjectorVersionInfo() {
        }
    }
}

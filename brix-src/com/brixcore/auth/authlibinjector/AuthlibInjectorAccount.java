package com.brixcore.auth.authlibinjector;

import com.brixcore.auth.AuthInfo;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.CharacterSelector;
import com.brixcore.auth.NotLoggedInException;
import com.brixcore.auth.ServerDisconnectException;
import com.brixcore.auth.yggdrasil.CompleteGameProfile;
import com.brixcore.auth.yggdrasil.TextureType;
import com.brixcore.auth.yggdrasil.YggdrasilAccount;
import com.brixcore.auth.yggdrasil.YggdrasilSession;
import com.brixcore.game.Arguments;
import com.brixcore.game.LaunchOptions;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.function.ExceptionalSupplier;
import com.brixcore.util.io.NetworkUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes14.dex */
public class AuthlibInjectorAccount extends YggdrasilAccount {
    private AuthlibInjectorArtifactProvider downloader;
    private final AuthlibInjectorServer server;

    public AuthlibInjectorAccount(AuthlibInjectorServer server, AuthlibInjectorArtifactProvider downloader, String username, String password, CharacterSelector selector) throws AuthenticationException {
        super(server.getYggdrasilService(), username, password, selector);
        this.server = server;
        this.downloader = downloader;
    }

    public AuthlibInjectorAccount(AuthlibInjectorServer server, AuthlibInjectorArtifactProvider downloader, String username, YggdrasilSession session) {
        super(server.getYggdrasilService(), username, session);
        this.server = server;
        this.downloader = downloader;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ AuthInfo lambda$logIn$0() throws AuthenticationException {
        return super.logIn();
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public synchronized AuthInfo logIn() throws AuthenticationException {
        return inject(new ExceptionalSupplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorAccount$$ExternalSyntheticLambda2
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$logIn$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ AuthInfo lambda$logInWithPassword$1(String password) throws AuthenticationException {
        return super.logInWithPassword(password);
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.ClassicAccount
    public synchronized AuthInfo logInWithPassword(final String password) throws AuthenticationException {
        return inject(new ExceptionalSupplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorAccount$$ExternalSyntheticLambda3
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$logInWithPassword$1(password);
            }
        });
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public AuthInfo playOffline() throws AuthenticationException {
        AuthInfo auth = super.playOffline();
        Optional<AuthlibInjectorArtifactInfo> artifact = this.downloader.getArtifactInfoImmediately();
        Optional<String> prefetchedMeta = this.server.getMetadataResponse();
        if (artifact.isPresent() && prefetchedMeta.isPresent()) {
            return new AuthlibInjectorAuthInfo(auth, artifact.get(), this.server, prefetchedMeta.get());
        }
        throw new NotLoggedInException();
    }

    private AuthInfo inject(ExceptionalSupplier<AuthInfo, AuthenticationException> loginAction) throws Exception {
        CompletableFuture<String> prefetchedMetaTask = CompletableFuture.supplyAsync(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorAccount$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.lambda$inject$2();
            }
        });
        CompletableFuture<AuthlibInjectorArtifactInfo> artifactTask = CompletableFuture.supplyAsync(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorAccount$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.lambda$inject$3();
            }
        });
        AuthInfo auth = loginAction.get();
        try {
            String prefetchedMeta = prefetchedMetaTask.get();
            AuthlibInjectorArtifactInfo artifact = artifactTask.get();
            return new AuthlibInjectorAuthInfo(auth, artifact, this.server, prefetchedMeta);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthenticationException(e);
        } catch (ExecutionException e2) {
            if (e2.getCause() instanceof AuthenticationException) {
                throw ((AuthenticationException) e2.getCause());
            }
            throw new AuthenticationException(e2.getCause());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$inject$2() {
        try {
            return this.server.fetchMetadataResponse();
        } catch (IOException e) {
            throw new CompletionException(new ServerDisconnectException(e));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ AuthlibInjectorArtifactInfo lambda$inject$3() {
        try {
            return this.downloader.getArtifactInfo();
        } catch (IOException e) {
            throw new CompletionException(new AuthlibInjectorDownloadException(e));
        }
    }

    private static class AuthlibInjectorAuthInfo extends AuthInfo {
        private final AuthlibInjectorArtifactInfo artifact;
        private final String prefetchedMeta;
        private final AuthlibInjectorServer server;

        public AuthlibInjectorAuthInfo(AuthInfo authInfo, AuthlibInjectorArtifactInfo artifact, AuthlibInjectorServer server, String prefetchedMeta) {
            super(authInfo.getUsername(), authInfo.getUUID(), authInfo.getAccessToken(), authInfo.getUserType(), authInfo.getUserProperties());
            this.artifact = artifact;
            this.server = server;
            this.prefetchedMeta = prefetchedMeta;
        }

        @Override // com.brixcore.auth.AuthInfo
        public Arguments getLaunchArguments(LaunchOptions options) {
            return new Arguments().addJVMArguments("-javaagent:" + this.artifact.getLocation().toString() + NetworkUtils.NAME_VALUE_SEPARATOR + this.server.getUrl(), "-Dauthlibinjector.side=client", "-Dauthlibinjector.yggdrasil.prefetched=" + Base64.getEncoder().encodeToString(this.prefetchedMeta.getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public Map<Object, Object> toStorage() {
        Map<Object, Object> map = super.toStorage();
        map.put("serverBaseURL", this.server.getUrl());
        return map;
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public void clearCache() {
        super.clearCache();
        this.server.invalidateMetadataCache();
    }

    public AuthlibInjectorServer getServer() {
        return this.server;
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public String getIdentifier() {
        return this.server.getUrl() + ":" + super.getIdentifier();
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), Integer.valueOf(this.server.hashCode()));
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != AuthlibInjectorAccount.class) {
            return false;
        }
        AuthlibInjectorAccount another = (AuthlibInjectorAccount) obj;
        return isPortable() == another.isPortable() && this.characterUUID.equals(another.characterUUID) && this.server.equals(another.server);
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilAccount, com.brixcore.auth.Account
    public String toString() {
        return new ToStringBuilder(this).append("uuid", this.characterUUID).append("username", getUsername()).append("server", getServer().getUrl()).toString();
    }

    public static Set<TextureType> getUploadableTextures(CompleteGameProfile profile) {
        String prop = profile.getProperties().get("uploadableTextures");
        if (prop == null) {
            return Collections.emptySet();
        }
        Set<TextureType> result = EnumSet.noneOf(TextureType.class);
        for (String val : prop.split(",")) {
            try {
                TextureType parsed = TextureType.valueOf(val.toUpperCase(Locale.ROOT));
                result.add(parsed);
            } catch (IllegalArgumentException e) {
            }
        }
        return Collections.unmodifiableSet(result);
    }
}

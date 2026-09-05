package com.brixcore.auth.offline;

import com.brixcore.auth.Account;
import com.brixcore.auth.AuthInfo;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactInfo;
import com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactProvider;
import com.brixcore.auth.authlibinjector.AuthlibInjectorDownloadException;
import com.brixcore.auth.yggdrasil.TextureType;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.game.Arguments;
import com.brixcore.game.LaunchOptions;
import com.brixcore.util.Lang;
import com.brixcore.util.Pair;
import com.brixcore.util.StringUtils;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.gson.UUIDTypeAdapter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes14.dex */
public class OfflineAccount extends Account {
    private final AuthlibInjectorArtifactProvider downloader;
    private Skin skin;
    private final String username;
    private final UUID uuid;

    protected OfflineAccount(AuthlibInjectorArtifactProvider downloader, String username, UUID uuid, Skin skin) {
        this.downloader = (AuthlibInjectorArtifactProvider) Objects.requireNonNull(downloader);
        this.username = (String) Objects.requireNonNull(username);
        this.uuid = (UUID) Objects.requireNonNull(uuid);
        this.skin = skin;
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
    }

    public AuthlibInjectorArtifactProvider getDownloader() {
        return this.downloader;
    }

    @Override // com.brixcore.auth.Account
    public UUID getUUID() {
        return this.uuid;
    }

    @Override // com.brixcore.auth.Account
    public String getUsername() {
        return this.username;
    }

    @Override // com.brixcore.auth.Account
    public String getCharacter() {
        return this.username;
    }

    @Override // com.brixcore.auth.Account
    public String getIdentifier() {
        return this.username + ":" + this.username;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
        invalidate();
    }

    protected boolean loadAuthlibInjector(Skin skin) {
        return (skin == null || skin.type() == Skin.Type.DEFAULT) ? false : true;
    }

    @Override // com.brixcore.auth.Account
    public AuthInfo logIn() throws AuthenticationException {
        AuthInfo authInfo = new AuthInfo(this.username, this.uuid, UUIDTypeAdapter.fromUUID(UUID.randomUUID()), AuthInfo.USER_TYPE_MSA, "{}");
        if (!loadAuthlibInjector(this.skin)) {
            return authInfo;
        }
        CompletableFuture<AuthlibInjectorArtifactInfo> artifactTask = CompletableFuture.supplyAsync(new Supplier() { // from class: com.brixcore.auth.offline.OfflineAccount$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.lambda$logIn$0();
            }
        });
        try {
            AuthlibInjectorArtifactInfo artifact = artifactTask.get();
            try {
                return new OfflineAuthInfo(authInfo, artifact);
            } catch (Exception e) {
                throw new AuthenticationException(e);
            }
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            throw new AuthenticationException(e2);
        } catch (ExecutionException e3) {
            if (e3.getCause() instanceof AuthenticationException) {
                throw ((AuthenticationException) e3.getCause());
            }
            throw new AuthenticationException(e3.getCause());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ AuthlibInjectorArtifactInfo lambda$logIn$0() {
        try {
            return this.downloader.getArtifactInfo();
        } catch (IOException e) {
            throw new CompletionException(new AuthlibInjectorDownloadException(e));
        }
    }

    private class OfflineAuthInfo extends AuthInfo {
        private final AuthlibInjectorArtifactInfo artifact;
        private YggdrasilServer server;

        public OfflineAuthInfo(AuthInfo authInfo, AuthlibInjectorArtifactInfo artifact) {
            super(authInfo.getUsername(), authInfo.getUUID(), authInfo.getAccessToken(), AuthInfo.USER_TYPE_MSA, authInfo.getUserProperties());
            this.artifact = artifact;
        }

        @Override // com.brixcore.auth.AuthInfo
        public Arguments getLaunchArguments(LaunchOptions options) throws IOException {
            this.server = new YggdrasilServer(0);
            this.server.start();
            try {
                this.server.addCharacter(new YggdrasilServer.Character(OfflineAccount.this.uuid, OfflineAccount.this.username, OfflineAccount.this.skin != null ? OfflineAccount.this.skin.load().run() : null));
            } catch (IOException e) {
            } catch (Exception e2) {
                throw new IOException(e2);
            }
            return new Arguments().addJVMArguments("-javaagent:" + this.artifact.getLocation().toString() + "=http://localhost:" + this.server.getListeningPort(), "-Dauthlibinjector.side=client");
        }
    }

    @Override // com.brixcore.auth.Account
    public AuthInfo playOffline() throws AuthenticationException {
        return new AuthInfo(this.username, this.uuid, UUIDTypeAdapter.fromUUID(UUID.randomUUID()), AuthInfo.USER_TYPE_MSA, "{}");
    }

    @Override // com.brixcore.auth.Account
    public Map<Object, Object> toStorage() {
        Pair[] pairArr = new Pair[3];
        pairArr[0] = Pair.pair("uuid", UUIDTypeAdapter.fromUUID(this.uuid));
        pairArr[1] = Pair.pair("username", this.username);
        pairArr[2] = Pair.pair("skin", this.skin == null ? null : this.skin.toStorage());
        return Lang.mapOf(pairArr);
    }

    @Override // com.brixcore.auth.Account
    public ObjectBinding<Optional<Map<TextureType, com.brixcore.auth.yggdrasil.Texture>>> getTextures() {
        final Map<TextureType, com.brixcore.auth.yggdrasil.Texture> map = new HashMap<>();
        map.put(TextureType.SKIN, new com.brixcore.auth.yggdrasil.Texture("offline", null));
        return Bindings.createObjectBinding(new Callable() { // from class: com.brixcore.auth.offline.OfflineAccount$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return Optional.of(map);
            }
        }, new Observable[0]);
    }

    @Override // com.brixcore.auth.Account
    public String toString() {
        return new ToStringBuilder(this).append("username", this.username).append("uuid", this.uuid).toString();
    }

    @Override // com.brixcore.auth.Account
    public int hashCode() {
        return this.username.hashCode();
    }

    @Override // com.brixcore.auth.Account
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflineAccount)) {
            return false;
        }
        OfflineAccount another = (OfflineAccount) obj;
        return isPortable() == another.isPortable() && this.username.equals(another.username);
    }
}

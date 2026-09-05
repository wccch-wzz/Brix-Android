package com.brixcore.auth.authlibinjector;

import com.brixcore.auth.Account;
import com.brixcore.auth.AccountFactory;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.CharacterSelector;
import com.brixcore.auth.yggdrasil.CompleteGameProfile;
import com.brixcore.auth.yggdrasil.GameProfile;
import com.brixcore.auth.yggdrasil.YggdrasilSession;
import com.brixcore.util.Lang;
import com.brixcore.util.fakefx.ObservableOptionalCache;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes14.dex */
public class AuthlibInjectorAccountFactory extends AccountFactory<AuthlibInjectorAccount> {
    private final AuthlibInjectorArtifactProvider downloader;
    private final Function<String, AuthlibInjectorServer> serverLookup;

    @Override // com.brixcore.auth.AccountFactory
    public /* bridge */ /* synthetic */ Account fromStorage(Map map) {
        return fromStorage((Map<Object, Object>) map);
    }

    public AuthlibInjectorAccountFactory(AuthlibInjectorArtifactProvider downloader, Function<String, AuthlibInjectorServer> serverLookup) {
        this.downloader = downloader;
        this.serverLookup = serverLookup;
    }

    @Override // com.brixcore.auth.AccountFactory
    public AccountFactory.AccountLoginType getLoginType() {
        return AccountFactory.AccountLoginType.USERNAME_PASSWORD;
    }

    @Override // com.brixcore.auth.AccountFactory
    public AuthlibInjectorAccount create(CharacterSelector selector, String username, String password, AccountFactory.ProgressCallback progressCallback, Object additionalData) throws AuthenticationException {
        Objects.requireNonNull(selector);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        AuthlibInjectorServer server = (AuthlibInjectorServer) additionalData;
        return new AuthlibInjectorAccount(server, this.downloader, username, password, selector);
    }

    @Override // com.brixcore.auth.AccountFactory
    public AuthlibInjectorAccount fromStorage(Map<Object, Object> storage) {
        Objects.requireNonNull(storage);
        String apiRoot = (String) Lang.tryCast(storage.get("serverBaseURL"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorAccountFactory$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                return AuthlibInjectorAccountFactory.lambda$fromStorage$0();
            }
        });
        AuthlibInjectorServer server = this.serverLookup.apply(apiRoot);
        return fromStorage(storage, this.downloader, server);
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$0() {
        return new IllegalArgumentException("storage does not have API root.");
    }

    static AuthlibInjectorAccount fromStorage(Map<Object, Object> storage, AuthlibInjectorArtifactProvider downloader, final AuthlibInjectorServer server) {
        final YggdrasilSession session = YggdrasilSession.fromStorage(storage);
        String username = (String) Lang.tryCast(storage.get("username"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorAccountFactory$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return AuthlibInjectorAccountFactory.lambda$fromStorage$1();
            }
        });
        Lang.tryCast(storage.get("profileProperties"), Map.class).ifPresent(new Consumer() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorAccountFactory$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AuthlibInjectorAccountFactory.lambda$fromStorage$2(session, server, (Map) obj);
            }
        });
        return new AuthlibInjectorAccount(server, downloader, username, session);
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$1() {
        return new IllegalArgumentException("storage does not have username");
    }

    static /* synthetic */ void lambda$fromStorage$2(YggdrasilSession session, AuthlibInjectorServer server, Map it) {
        GameProfile selected = session.getSelectedProfile();
        ObservableOptionalCache<UUID, CompleteGameProfile, AuthenticationException> profileRepository = server.getYggdrasilService().getProfileRepository();
        profileRepository.put(selected.getId(), new CompleteGameProfile(selected, it));
        profileRepository.invalidate(selected.getId());
    }
}

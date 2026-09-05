package com.brixcore.auth.authlibinjector;

import com.brixcore.auth.Account;
import com.brixcore.auth.AccountFactory;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.CharacterSelector;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes14.dex */
public class BoundAuthlibInjectorAccountFactory extends AccountFactory<AuthlibInjectorAccount> {
    private final AuthlibInjectorArtifactProvider downloader;
    private final AuthlibInjectorServer server;

    @Override // com.brixcore.auth.AccountFactory
    public /* bridge */ /* synthetic */ Account fromStorage(Map map) {
        return fromStorage((Map<Object, Object>) map);
    }

    public BoundAuthlibInjectorAccountFactory(AuthlibInjectorArtifactProvider downloader, AuthlibInjectorServer server) {
        this.downloader = downloader;
        this.server = server;
    }

    @Override // com.brixcore.auth.AccountFactory
    public AccountFactory.AccountLoginType getLoginType() {
        return AccountFactory.AccountLoginType.USERNAME_PASSWORD;
    }

    public AuthlibInjectorServer getServer() {
        return this.server;
    }

    @Override // com.brixcore.auth.AccountFactory
    public AuthlibInjectorAccount create(CharacterSelector selector, String username, String password, AccountFactory.ProgressCallback progressCallback, Object additionalData) throws AuthenticationException {
        Objects.requireNonNull(selector);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        return new AuthlibInjectorAccount(this.server, this.downloader, username, password, selector);
    }

    @Override // com.brixcore.auth.AccountFactory
    public AuthlibInjectorAccount fromStorage(Map<Object, Object> storage) {
        return AuthlibInjectorAccountFactory.fromStorage(storage, this.downloader, this.server);
    }
}

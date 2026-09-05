package com.brixcore.auth.microsoft;

import com.brixcore.auth.Account;
import com.brixcore.auth.AccountFactory;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.CharacterSelector;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes5.dex */
public class MicrosoftAccountFactory extends AccountFactory<MicrosoftAccount> {
    private final MicrosoftService service;

    @Override // com.brixcore.auth.AccountFactory
    public /* bridge */ /* synthetic */ Account fromStorage(Map map) {
        return fromStorage((Map<Object, Object>) map);
    }

    public MicrosoftAccountFactory(MicrosoftService service) {
        this.service = service;
    }

    @Override // com.brixcore.auth.AccountFactory
    public AccountFactory.AccountLoginType getLoginType() {
        return AccountFactory.AccountLoginType.NONE;
    }

    @Override // com.brixcore.auth.AccountFactory
    public MicrosoftAccount create(CharacterSelector selector, String username, String password, AccountFactory.ProgressCallback progressCallback, Object additionalData) throws AuthenticationException {
        Objects.requireNonNull(selector);
        return new MicrosoftAccount(this.service, selector);
    }

    @Override // com.brixcore.auth.AccountFactory
    public MicrosoftAccount fromStorage(Map<Object, Object> storage) {
        Objects.requireNonNull(storage);
        MicrosoftSession session = MicrosoftSession.fromStorage(storage);
        return new MicrosoftAccount(this.service, session);
    }
}

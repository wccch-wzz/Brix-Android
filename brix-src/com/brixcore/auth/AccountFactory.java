package com.brixcore.auth;

import com.brixcore.auth.Account;
import java.util.Map;

/* JADX INFO: loaded from: classes8.dex */
public abstract class AccountFactory<T extends Account> {

    public interface ProgressCallback {
        void onProgressChanged(String str);
    }

    public abstract T create(CharacterSelector characterSelector, String str, String str2, ProgressCallback progressCallback, Object obj) throws AuthenticationException;

    public abstract T fromStorage(Map<Object, Object> map);

    public abstract AccountLoginType getLoginType();

    public enum AccountLoginType {
        NONE(false, false),
        USERNAME(true, false),
        USERNAME_PASSWORD(true, true);

        public final boolean requiresPassword;
        public final boolean requiresUsername;

        AccountLoginType(boolean requiresUsername, boolean requiresPassword) {
            this.requiresUsername = requiresUsername;
            this.requiresPassword = requiresPassword;
        }
    }
}

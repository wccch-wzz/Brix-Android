package com.brixcore.auth;

import java.util.UUID;

/* JADX INFO: loaded from: classes8.dex */
public abstract class OAuthAccount extends Account {
    public abstract AuthInfo logInWhenCredentialsExpired() throws AuthenticationException;

    public static class WrongAccountException extends AuthenticationException {
        private final UUID actual;
        private final UUID expected;

        public WrongAccountException(UUID expected, UUID actual) {
            super("Expected account " + expected + ", but found " + actual);
            this.expected = expected;
            this.actual = actual;
        }

        public UUID getExpected() {
            return this.expected;
        }

        public UUID getActual() {
            return this.actual;
        }
    }
}

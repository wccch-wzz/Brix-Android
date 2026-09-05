package com.brixcore.auth;

/* JADX INFO: loaded from: classes8.dex */
public class CredentialExpiredException extends AuthenticationException {
    public CredentialExpiredException() {
    }

    public CredentialExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialExpiredException(String message) {
        super(message);
    }

    public CredentialExpiredException(Throwable cause) {
        super(cause);
    }
}

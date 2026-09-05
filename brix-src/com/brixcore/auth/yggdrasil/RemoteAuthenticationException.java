package com.brixcore.auth.yggdrasil;

import com.brixcore.auth.AuthenticationException;

/* JADX INFO: loaded from: classes3.dex */
public class RemoteAuthenticationException extends AuthenticationException {
    private final String cause;
    private final String message;
    private final String name;

    public RemoteAuthenticationException(String name, String message, String cause) {
        super(buildMessage(name, message, cause));
        this.name = name;
        this.message = message;
        this.cause = cause;
    }

    public String getRemoteName() {
        return this.name;
    }

    public String getRemoteMessage() {
        return this.message;
    }

    public String getRemoteCause() {
        return this.cause;
    }

    private static String buildMessage(String name, String message, String cause) {
        StringBuilder builder = new StringBuilder(name);
        if (message != null) {
            builder.append(": ").append(message);
        }
        if (cause != null) {
            builder.append(": ").append(cause);
        }
        return builder.toString();
    }
}

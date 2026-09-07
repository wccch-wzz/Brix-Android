package com.brixcore.util;

import java.util.Objects;

/* JADX INFO: loaded from: classes11.dex */
public final class ServerAddress {
    private static final int UNKNOWN_PORT = -1;
    private final String host;
    private final int port;

    private static IllegalArgumentException illegalAddress(String address) {
        return new IllegalArgumentException("Invalid server address: " + address);
    }

    public static ServerAddress parse(String address) {
        Objects.requireNonNull(address);
        if (!address.startsWith("[")) {
            int colonPos = address.indexOf(58);
            if (colonPos >= 0) {
                if (colonPos == address.length() - 1) {
                    throw illegalAddress(address);
                }
                String host = address.substring(0, colonPos);
                try {
                    int port = Integer.parseInt(address.substring(colonPos + 1));
                    if (port < 0 || port > 65535) {
                        throw illegalAddress(address);
                    }
                    return new ServerAddress(host, port);
                } catch (NumberFormatException e) {
                    throw illegalAddress(address);
                }
            }
            return new ServerAddress(address);
        }
        int colonIndex = address.indexOf(58);
        int closeBracketIndex = address.lastIndexOf(93);
        if (colonIndex < 0 || closeBracketIndex < colonIndex) {
            throw illegalAddress(address);
        }
        String host2 = address.substring(1, closeBracketIndex);
        if (closeBracketIndex == address.length() - 1) {
            return new ServerAddress(host2);
        }
        if (address.length() < closeBracketIndex + 3 || address.charAt(closeBracketIndex + 1) != ':') {
            throw illegalAddress(address);
        }
        try {
            int port2 = Integer.parseInt(address.substring(closeBracketIndex + 2));
            if (port2 < 0 || port2 > 65535) {
                throw illegalAddress(address);
            }
            return new ServerAddress(host2, port2);
        } catch (NumberFormatException e2) {
            throw illegalAddress(address);
        }
    }

    public ServerAddress(String host) {
        this(host, -1);
    }

    public ServerAddress(String host, int port) {
        this.host = (String) Objects.requireNonNull(host);
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ServerAddress)) {
            return false;
        }
        ServerAddress that = (ServerAddress) o;
        return this.port == that.port && Objects.equals(this.host, that.host);
    }

    public int hashCode() {
        return Objects.hash(this.host, Integer.valueOf(this.port));
    }

    public String toString() {
        return String.format("ServerAddress[host='%s', port=%d]", this.host, Integer.valueOf(this.port));
    }
}

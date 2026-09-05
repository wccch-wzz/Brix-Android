package com.brixcore.auth.microsoft;

import com.brixcore.auth.AuthInfo;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.gson.UUIDTypeAdapter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes5.dex */
public class MicrosoftSession {
    private final String accessToken;
    private final long notAfter;
    private final GameProfile profile;
    private final String refreshToken;
    private final String tokenType;
    private final User user;

    public MicrosoftSession(String tokenType, String accessToken, long notAfter, String refreshToken, User user, GameProfile profile) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.notAfter = notAfter;
        this.refreshToken = refreshToken;
        this.user = user;
        this.profile = profile;
        if (accessToken != null) {
            Logging.registerAccessToken(accessToken);
        }
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public long getNotAfter() {
        return this.notAfter;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getAuthorization() {
        return String.format("%s %s", getTokenType(), getAccessToken());
    }

    public User getUser() {
        return this.user;
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public static MicrosoftSession fromStorage(Map<?, ?> storage) {
        UUID uuid = (UUID) Lang.tryCast(storage.get("uuid"), String.class).map(new Function() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return UUIDTypeAdapter.fromString((String) obj);
            }
        }).orElseThrow(new Supplier() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return MicrosoftSession.lambda$fromStorage$0();
            }
        });
        String name = (String) Lang.tryCast(storage.get("displayName"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                return MicrosoftSession.lambda$fromStorage$1();
            }
        });
        String tokenType = (String) Lang.tryCast(storage.get("tokenType"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda3
            @Override // java.util.function.Supplier
            public final Object get() {
                return MicrosoftSession.lambda$fromStorage$2();
            }
        });
        String accessToken = (String) Lang.tryCast(storage.get("accessToken"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda4
            @Override // java.util.function.Supplier
            public final Object get() {
                return MicrosoftSession.lambda$fromStorage$3();
            }
        });
        String refreshToken = (String) Lang.tryCast(storage.get("refreshToken"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda5
            @Override // java.util.function.Supplier
            public final Object get() {
                return MicrosoftSession.lambda$fromStorage$4();
            }
        });
        Long notAfter = (Long) Lang.tryCast(storage.get("notAfter"), Number.class).map(new Function() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Long.valueOf(((Number) obj).longValue());
            }
        }).orElse(0L);
        String userId = (String) Lang.tryCast(storage.get("userid"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.microsoft.MicrosoftSession$$ExternalSyntheticLambda7
            @Override // java.util.function.Supplier
            public final Object get() {
                return MicrosoftSession.lambda$fromStorage$5();
            }
        });
        return new MicrosoftSession(tokenType, accessToken, notAfter.longValue(), refreshToken, new User(userId), new GameProfile(uuid, name));
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$0() {
        return new IllegalArgumentException("uuid is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$1() {
        return new IllegalArgumentException("displayName is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$2() {
        return new IllegalArgumentException("tokenType is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$3() {
        return new IllegalArgumentException("accessToken is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$4() {
        return new IllegalArgumentException("refreshToken is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$5() {
        return new IllegalArgumentException("userid is missing");
    }

    public Map<Object, Object> toStorage() {
        Objects.requireNonNull(this.profile);
        Objects.requireNonNull(this.user);
        return Lang.mapOf(Pair.pair("uuid", UUIDTypeAdapter.fromUUID(this.profile.getId())), Pair.pair("displayName", this.profile.getName()), Pair.pair("tokenType", this.tokenType), Pair.pair("accessToken", this.accessToken), Pair.pair("refreshToken", this.refreshToken), Pair.pair("notAfter", Long.valueOf(this.notAfter)), Pair.pair("userid", this.user.id));
    }

    public AuthInfo toAuthInfo() {
        Objects.requireNonNull(this.profile);
        return new AuthInfo(this.profile.getName(), this.profile.getId(), this.accessToken, AuthInfo.USER_TYPE_MSA, "{}");
    }

    public static class User {
        private final String id;

        public User(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    public static class GameProfile {
        private final UUID id;
        private final String name;

        public GameProfile(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }
    }
}

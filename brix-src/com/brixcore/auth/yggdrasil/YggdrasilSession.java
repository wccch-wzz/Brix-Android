package com.brixcore.auth.yggdrasil;

import com.brixcore.auth.AuthInfo;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.gson.UUIDTypeAdapter;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes3.dex */
public class YggdrasilSession {
    private static final Gson GSON_PROPERTIES = new Gson();
    private final String accessToken;
    private final List<GameProfile> availableProfiles;
    private final String clientToken;
    private final GameProfile selectedProfile;
    private final Map<String, String> userProperties;

    public YggdrasilSession(String clientToken, String accessToken, GameProfile selectedProfile, List<GameProfile> availableProfiles, Map<String, String> userProperties) {
        this.clientToken = clientToken;
        this.accessToken = accessToken;
        this.selectedProfile = selectedProfile;
        this.availableProfiles = availableProfiles;
        this.userProperties = userProperties;
        if (accessToken != null) {
            Logging.registerAccessToken(accessToken);
        }
    }

    public String getClientToken() {
        return this.clientToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public GameProfile getSelectedProfile() {
        return this.selectedProfile;
    }

    public List<GameProfile> getAvailableProfiles() {
        return this.availableProfiles;
    }

    public Map<String, String> getUserProperties() {
        return this.userProperties;
    }

    public static YggdrasilSession fromStorage(Map<?, ?> storage) {
        Objects.requireNonNull(storage);
        UUID uuid = (UUID) Lang.tryCast(storage.get("uuid"), String.class).map(new Function() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return UUIDTypeAdapter.fromString((String) obj);
            }
        }).orElseThrow(new Supplier() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda5
            @Override // java.util.function.Supplier
            public final Object get() {
                return YggdrasilSession.lambda$fromStorage$0();
            }
        });
        String name = (String) Lang.tryCast(storage.get("displayName"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda6
            @Override // java.util.function.Supplier
            public final Object get() {
                return YggdrasilSession.lambda$fromStorage$1();
            }
        });
        String clientToken = (String) Lang.tryCast(storage.get("clientToken"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda7
            @Override // java.util.function.Supplier
            public final Object get() {
                return YggdrasilSession.lambda$fromStorage$2();
            }
        });
        String accessToken = (String) Lang.tryCast(storage.get("accessToken"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda8
            @Override // java.util.function.Supplier
            public final Object get() {
                return YggdrasilSession.lambda$fromStorage$3();
            }
        });
        Map<String, String> userProperties = (Map) Lang.tryCast(storage.get("userProperties"), Map.class).orElse(null);
        return new YggdrasilSession(clientToken, accessToken, new GameProfile(uuid, name), null, userProperties);
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$0() {
        return new IllegalArgumentException("uuid is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$1() {
        return new IllegalArgumentException("displayName is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$2() {
        return new IllegalArgumentException("clientToken is missing");
    }

    static /* synthetic */ IllegalArgumentException lambda$fromStorage$3() {
        return new IllegalArgumentException("accessToken is missing");
    }

    public Map<Object, Object> toStorage() {
        if (this.selectedProfile == null) {
            throw new IllegalStateException("No character is selected");
        }
        return Lang.mapOf(Pair.pair("clientToken", this.clientToken), Pair.pair("accessToken", this.accessToken), Pair.pair("uuid", UUIDTypeAdapter.fromUUID(this.selectedProfile.getId())), Pair.pair("displayName", this.selectedProfile.getName()), Pair.pair("userProperties", this.userProperties));
    }

    public AuthInfo toAuthInfo() {
        if (this.selectedProfile == null) {
            throw new IllegalStateException("No character is selected");
        }
        String name = this.selectedProfile.getName();
        UUID id = this.selectedProfile.getId();
        String str = this.accessToken;
        Optional map = Optional.ofNullable(this.userProperties).map(new Function() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return YggdrasilSession.lambda$toAuthInfo$5((Map) obj);
            }
        });
        final Gson gson = GSON_PROPERTIES;
        Objects.requireNonNull(gson);
        return new AuthInfo(name, id, str, AuthInfo.USER_TYPE_MSA, (String) map.map(new Function() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return gson.toJson((Map) obj);
            }
        }).orElse("{}"));
    }

    static /* synthetic */ Map lambda$toAuthInfo$5(Map properties) {
        return (Map) properties.entrySet().stream().collect(Collectors.toMap(new YggdrasilSession$$ExternalSyntheticLambda0(), new Function() { // from class: com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Collections.singleton((String) ((Map.Entry) obj).getValue());
            }
        }));
    }
}

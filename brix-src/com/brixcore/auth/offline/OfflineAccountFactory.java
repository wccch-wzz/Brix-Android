package com.brixcore.auth.offline;

import com.brixcore.auth.Account;
import com.brixcore.auth.AccountFactory;
import com.brixcore.auth.CharacterSelector;
import com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactProvider;
import com.brixcore.util.Lang;
import com.brixcore.util.gson.UUIDTypeAdapter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes14.dex */
public final class OfflineAccountFactory extends AccountFactory<OfflineAccount> {
    private final AuthlibInjectorArtifactProvider downloader;

    @Override // com.brixcore.auth.AccountFactory
    public /* bridge */ /* synthetic */ Account fromStorage(Map map) {
        return fromStorage((Map<Object, Object>) map);
    }

    public OfflineAccountFactory(AuthlibInjectorArtifactProvider downloader) {
        this.downloader = downloader;
    }

    @Override // com.brixcore.auth.AccountFactory
    public AccountFactory.AccountLoginType getLoginType() {
        return AccountFactory.AccountLoginType.USERNAME;
    }

    public OfflineAccount create(String username, UUID uuid) {
        return new OfflineAccount(this.downloader, username, uuid, null);
    }

    @Override // com.brixcore.auth.AccountFactory
    public OfflineAccount create(CharacterSelector selector, String username, String password, AccountFactory.ProgressCallback progressCallback, Object additionalData) {
        UUID uuid;
        Skin skin;
        if (additionalData != null) {
            AdditionalData data = (AdditionalData) additionalData;
            uuid = data.uuid == null ? getUUIDFromUserName(username) : data.uuid;
            skin = data.skin;
        } else {
            uuid = getUUIDFromUserName(username);
            skin = null;
        }
        return new OfflineAccount(this.downloader, username, uuid, skin);
    }

    @Override // com.brixcore.auth.AccountFactory
    public OfflineAccount fromStorage(Map<Object, Object> storage) {
        String username = (String) Lang.tryCast(storage.get("username"), String.class).orElseThrow(new Supplier() { // from class: com.brixcore.auth.offline.OfflineAccountFactory$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return OfflineAccountFactory.lambda$fromStorage$0();
            }
        });
        UUID uuid = (UUID) Lang.tryCast(storage.get("uuid"), String.class).map(new Function() { // from class: com.brixcore.auth.offline.OfflineAccountFactory$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return UUIDTypeAdapter.fromString((String) obj);
            }
        }).orElse(getUUIDFromUserName(username));
        Skin skin = Skin.fromStorage((Map) Lang.tryCast(storage.get("skin"), Map.class).orElse(null));
        return new OfflineAccount(this.downloader, username, uuid, skin);
    }

    static /* synthetic */ IllegalStateException lambda$fromStorage$0() {
        return new IllegalStateException("Offline account configuration malformed.");
    }

    public static UUID getUUIDFromUserName(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    public static class AdditionalData {
        private final Skin skin;
        private final UUID uuid;

        public AdditionalData(UUID uuid, Skin skin) {
            this.uuid = uuid;
            this.skin = skin;
        }
    }
}

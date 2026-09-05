package com.brixcore.auth.yggdrasil;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/* JADX INFO: loaded from: classes3.dex */
public class CompleteGameProfile extends GameProfile {

    @JsonAdapter(PropertyMapSerializer.class)
    private final Map<String, String> properties;

    public CompleteGameProfile(UUID id, String name, Map<String, String> properties) {
        super(id, name);
        this.properties = (Map) Objects.requireNonNull(properties);
    }

    public CompleteGameProfile(GameProfile profile, Map<String, String> properties) {
        this(profile.getId(), profile.getName(), properties);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Override // com.brixcore.auth.yggdrasil.GameProfile, com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        super.validate();
        if (this.properties == null) {
            throw new JsonParseException("Game profile properties cannot be null");
        }
    }
}

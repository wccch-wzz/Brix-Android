package com.brixcore.auth.yggdrasil;

import com.brixcore.util.gson.UUIDTypeAdapter;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import java.util.Objects;
import java.util.UUID;

/* JADX INFO: loaded from: classes3.dex */
public class GameProfile implements Validation {

    @JsonAdapter(UUIDTypeAdapter.class)
    private final UUID id;
    private final String name;

    public GameProfile(UUID id, String name) {
        this.id = (UUID) Objects.requireNonNull(id);
        this.name = (String) Objects.requireNonNull(name);
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        Validation.requireNonNull(this.id, "Game profile id cannot be null");
        Validation.requireNonNull(this.name, "Game profile name cannot be null");
    }
}

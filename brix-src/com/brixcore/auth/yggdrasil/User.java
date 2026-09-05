package com.brixcore.auth.yggdrasil;

import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public final class User implements Validation {
    private final String id;

    @JsonAdapter(PropertyMapSerializer.class)
    private final Map<String, String> properties;

    public User(String id) {
        this(id, null);
    }

    public User(String id, Map<String, String> properties) {
        this.id = id;
        this.properties = properties;
    }

    public String getId() {
        return this.id;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(this.id)) {
            throw new JsonParseException("User id cannot be empty.");
        }
    }
}

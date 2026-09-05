package com.brixcore.auth.yggdrasil;

import java.util.Map;
import java.util.UUID;
import org.antlr.v4.runtime.TokenStreamRewriter;

/* JADX INFO: loaded from: classes3.dex */
public enum TextureModel {
    STEVE(TokenStreamRewriter.DEFAULT_PROGRAM_NAME),
    ALEX("slim");

    public final String modelName;

    TextureModel(String modelName) {
        this.modelName = modelName;
    }

    public static TextureModel detectModelName(Map<String, String> metadata) {
        if (metadata != null && "slim".equals(metadata.get("model"))) {
            return ALEX;
        }
        return STEVE;
    }

    public static TextureModel detectUUID(UUID uuid) {
        return (uuid.hashCode() & 1) == 1 ? ALEX : STEVE;
    }
}

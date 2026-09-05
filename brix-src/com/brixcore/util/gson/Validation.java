package com.brixcore.util.gson;

import com.google.gson.JsonParseException;

/* JADX INFO: loaded from: classes8.dex */
public interface Validation {
    void validate() throws JsonParseException, TolerableValidationException;

    static void requireNonNull(Object object, String message) throws JsonParseException {
        if (object == null) {
            throw new JsonParseException(message);
        }
    }
}

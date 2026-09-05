package com.brixcore.util.gson.fakefx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/* JADX INFO: loaded from: classes14.dex */
public final class FxGson {
    private FxGson() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    public static Gson create() {
        return new FxGsonBuilder().create();
    }

    public static GsonBuilder coreBuilder() {
        return new FxGsonBuilder().builder();
    }

    public static GsonBuilder fullBuilder() {
        return new FxGsonBuilder().builder();
    }

    public static GsonBuilder addFxSupport(GsonBuilder builder) {
        return new FxGsonBuilder(builder).builder();
    }
}

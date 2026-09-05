package com.brixcore.util.gson.fakefx.properties.primitives;

/* JADX INFO: loaded from: classes16.dex */
public class NullPrimitiveException extends RuntimeException {
    public NullPrimitiveException(String pathInJson) {
        super("Illegal null value for a primitive type at path " + pathInJson);
    }
}

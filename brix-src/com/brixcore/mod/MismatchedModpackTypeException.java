package com.brixcore.mod;

/* JADX INFO: loaded from: classes2.dex */
public class MismatchedModpackTypeException extends Exception {
    private final String found;
    private final String required;

    public MismatchedModpackTypeException(String required, String found) {
        super("Required " + required + ", but found " + found);
        this.required = required;
        this.found = found;
    }

    public String getRequired() {
        return this.required;
    }

    public String getFound() {
        return this.found;
    }
}

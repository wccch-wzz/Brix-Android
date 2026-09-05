package com.brixcore.download;

/* JADX INFO: loaded from: classes14.dex */
public class VersionMismatchException extends Exception {
    private final String actual;
    private final String expect;

    public VersionMismatchException(String expect, String actual) {
        super("Mismatched game version requirement, library requires game to be " + expect + ", but actual is " + actual);
        this.expect = expect;
        this.actual = actual;
    }

    public String getExpect() {
        return this.expect;
    }

    public String getActual() {
        return this.actual;
    }
}

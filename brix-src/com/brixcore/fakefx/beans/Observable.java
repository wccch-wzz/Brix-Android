package com.brixcore.fakefx.beans;

/* JADX INFO: loaded from: classes15.dex */
public interface Observable {
    void addListener(InvalidationListener invalidationListener);

    void removeListener(InvalidationListener invalidationListener);
}

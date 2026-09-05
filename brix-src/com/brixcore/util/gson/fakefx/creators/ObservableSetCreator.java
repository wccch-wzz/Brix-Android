package com.brixcore.util.gson.fakefx.creators;

import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableSet;
import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;

/* JADX INFO: loaded from: classes8.dex */
public class ObservableSetCreator implements InstanceCreator<ObservableSet<?>> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.gson.InstanceCreator
    public ObservableSet<?> createInstance(Type type) {
        return FXCollections.observableSet(new Object[0]);
    }
}

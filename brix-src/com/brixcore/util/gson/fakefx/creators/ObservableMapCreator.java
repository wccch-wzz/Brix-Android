package com.brixcore.util.gson.fakefx.creators;

import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableMap;
import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;

/* JADX INFO: loaded from: classes8.dex */
public class ObservableMapCreator implements InstanceCreator<ObservableMap<?, ?>> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.gson.InstanceCreator
    public ObservableMap<?, ?> createInstance(Type type) {
        return FXCollections.observableHashMap();
    }
}

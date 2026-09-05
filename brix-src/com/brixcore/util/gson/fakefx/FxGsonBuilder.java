package com.brixcore.util.gson.fakefx;

import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.util.gson.fakefx.creators.ObservableListCreator;
import com.brixcore.util.gson.fakefx.creators.ObservableMapCreator;
import com.brixcore.util.gson.fakefx.creators.ObservableSetCreator;
import com.brixcore.util.gson.fakefx.factories.JavaFxPropertyTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/* JADX INFO: loaded from: classes14.dex */
public class FxGsonBuilder {
    private final GsonBuilder builder;
    private boolean includeExtras;
    private boolean strictPrimitives;
    private boolean strictProperties;

    public FxGsonBuilder() {
        this(new GsonBuilder());
    }

    public FxGsonBuilder(GsonBuilder sourceBuilder) {
        this.strictProperties = true;
        this.strictPrimitives = true;
        this.includeExtras = false;
        this.builder = sourceBuilder;
    }

    public GsonBuilder builder() {
        this.builder.serializeNulls().registerTypeAdapter(ObservableList.class, new ObservableListCreator()).registerTypeAdapter(ObservableSet.class, new ObservableSetCreator()).registerTypeAdapter(ObservableMap.class, new ObservableMapCreator()).registerTypeAdapterFactory(new JavaFxPropertyTypeAdapterFactory(this.strictProperties, this.strictPrimitives));
        return this.builder;
    }

    public Gson create() {
        return builder().create();
    }

    public FxGsonBuilder acceptNullProperties() {
        this.strictProperties = false;
        return this;
    }

    public FxGsonBuilder acceptNullPrimitives() {
        this.strictPrimitives = false;
        return this;
    }
}

package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.collections.ObservableMap;

/* JADX INFO: loaded from: classes4.dex */
public class SimpleMapProperty<K, V> extends MapPropertyBase<K, V> {
    private static final Object DEFAULT_BEAN = null;
    private static final String DEFAULT_NAME = "";
    private final Object bean;
    private final String name;

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
    public Object getBean() {
        return this.bean;
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
    public String getName() {
        return this.name;
    }

    public SimpleMapProperty() {
        this(DEFAULT_BEAN, "");
    }

    public SimpleMapProperty(ObservableMap<K, V> initialValue) {
        this(DEFAULT_BEAN, "", initialValue);
    }

    public SimpleMapProperty(Object bean, String name) {
        this.bean = bean;
        this.name = name == null ? "" : name;
    }

    public SimpleMapProperty(Object bean, String name, ObservableMap<K, V> initialValue) {
        super(initialValue);
        this.bean = bean;
        this.name = name == null ? "" : name;
    }
}

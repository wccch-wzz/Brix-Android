package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes4.dex */
public class SimpleListProperty<E> extends ListPropertyBase<E> {
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

    public SimpleListProperty() {
        this(DEFAULT_BEAN, "");
    }

    public SimpleListProperty(ObservableList<E> initialValue) {
        this(DEFAULT_BEAN, "", initialValue);
    }

    public SimpleListProperty(Object bean, String name) {
        this.bean = bean;
        this.name = name == null ? "" : name;
    }

    public SimpleListProperty(Object bean, String name, ObservableList<E> initialValue) {
        super(initialValue);
        this.bean = bean;
        this.name = name == null ? "" : name;
    }
}

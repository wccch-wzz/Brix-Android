package com.brixcore.fakefx.beans.property;

/* JADX INFO: loaded from: classes4.dex */
public class SimpleIntegerProperty extends IntegerPropertyBase {
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

    public SimpleIntegerProperty() {
        this(DEFAULT_BEAN, "");
    }

    public SimpleIntegerProperty(int initialValue) {
        this(DEFAULT_BEAN, "", initialValue);
    }

    public SimpleIntegerProperty(Object bean, String name) {
        this.bean = bean;
        this.name = name == null ? "" : name;
    }

    public SimpleIntegerProperty(Object bean, String name, int initialValue) {
        super(initialValue);
        this.bean = bean;
        this.name = name == null ? "" : name;
    }
}

package com.brixcore.fakefx.property.adapter;

import java.util.EventListener;

/* JADX INFO: loaded from: classes7.dex */
public interface VetoableChangeListener extends EventListener {
    void vetoableChange(PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException;
}

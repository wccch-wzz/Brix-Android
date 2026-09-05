package com.brixcore.fakefx.property.adapter;

/* JADX INFO: loaded from: classes7.dex */
public class PropertyVetoException extends Exception {
    private static final long serialVersionUID = 129596057694162164L;
    private PropertyChangeEvent evt;

    public PropertyVetoException(String mess, PropertyChangeEvent evt) {
        super(mess);
        this.evt = evt;
    }

    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.evt;
    }
}

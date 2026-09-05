package com.brixcore.util;

/* JADX INFO: loaded from: classes11.dex */
public final class ToStringBuilder {
    private boolean first = true;
    private final StringBuilder stringBuilder;

    public ToStringBuilder(Object object) {
        this.stringBuilder = new StringBuilder(object.getClass().getSimpleName()).append(" [");
    }

    public ToStringBuilder append(String name, Object content) {
        if (!this.first) {
            this.stringBuilder.append(", ");
        }
        this.first = false;
        this.stringBuilder.append(name).append('=').append(content);
        return this;
    }

    public String toString() {
        return this.stringBuilder.toString() + "]";
    }
}

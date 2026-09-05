package com.brixcore.event;

import com.brixcore.util.ToStringBuilder;

/* JADX INFO: loaded from: classes11.dex */
public class RemoveVersionEvent extends Event {
    private final String version;

    public RemoveVersionEvent(Object source, String version) {
        super(source);
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    @Override // com.brixcore.event.Event
    public boolean hasResult() {
        return true;
    }

    @Override // com.brixcore.event.Event
    public String toString() {
        return new ToStringBuilder(this).append("source", this.source).append("version", this.version).toString();
    }
}

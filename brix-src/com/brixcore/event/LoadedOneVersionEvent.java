package com.brixcore.event;

import com.brixcore.game.Version;
import com.brixcore.util.ToStringBuilder;

/* JADX INFO: loaded from: classes11.dex */
public final class LoadedOneVersionEvent extends Event {
    private final Version version;

    public LoadedOneVersionEvent(Object source, Version version) {
        super(source);
        this.version = version;
    }

    public Version getVersion() {
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

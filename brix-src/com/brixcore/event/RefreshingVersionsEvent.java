package com.brixcore.event;

/* JADX INFO: loaded from: classes11.dex */
public final class RefreshingVersionsEvent extends Event {
    public RefreshingVersionsEvent(Object source) {
        super(source);
    }

    @Override // com.brixcore.event.Event
    public boolean hasResult() {
        return true;
    }
}

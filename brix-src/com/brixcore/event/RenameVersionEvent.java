package com.brixcore.event;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import com.brixcore.util.ToStringBuilder;

/* JADX INFO: loaded from: classes11.dex */
public class RenameVersionEvent extends Event {
    private final String from;
    private final String to;

    public RenameVersionEvent(Object source, String from, String to) {
        super(source);
        this.from = from;
        this.to = to;
    }

    public String getFromVersion() {
        return this.from;
    }

    public String getToVersion() {
        return this.to;
    }

    @Override // com.brixcore.event.Event
    public boolean hasResult() {
        return true;
    }

    @Override // com.brixcore.event.Event
    public String toString() {
        return new ToStringBuilder(this).append("source", this.source).append(TypedValues.TransitionType.S_FROM, this.from).append(TypedValues.TransitionType.S_TO, this.to).toString();
    }
}

package com.brixcore.event;

import com.brixcore.util.ToStringBuilder;
import java.util.Objects;

/* JADX INFO: loaded from: classes11.dex */
public class Event {
    private boolean canceled;
    private Result result = Result.DEFAULT;
    protected final transient Object source;

    public enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }

    public Event(Object source) {
        Objects.requireNonNull(source);
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }

    public String toString() {
        return new ToStringBuilder(this).append("source", this.source).toString();
    }

    public final boolean isCanceled() {
        return this.canceled;
    }

    public final void setCanceled(boolean canceled) {
        if (!isCancelable()) {
            throw new UnsupportedOperationException("Attempted to cancel a non-cancelable event: " + getClass());
        }
        this.canceled = canceled;
    }

    public boolean isCancelable() {
        return false;
    }

    public boolean hasResult() {
        return false;
    }

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        if (!hasResult()) {
            throw new UnsupportedOperationException("Attempted to set result on a no result event: " + getClass() + " of type.");
        }
        this.result = result;
    }
}

package com.brixcore.event;

import com.brixcore.util.ToStringBuilder;
import java.io.File;

/* JADX INFO: loaded from: classes11.dex */
public final class GameJsonParseFailedEvent extends Event {
    private final File jsonFile;
    private final String version;

    public GameJsonParseFailedEvent(Object source, File jsonFile, String version) {
        super(source);
        this.version = version;
        this.jsonFile = jsonFile;
    }

    public File getJsonFile() {
        return this.jsonFile;
    }

    public String getVersion() {
        return this.version;
    }

    @Override // com.brixcore.event.Event
    public String toString() {
        return new ToStringBuilder(this).append("source", this.source).append("jsonFile", this.jsonFile).append("version", this.version).toString();
    }
}

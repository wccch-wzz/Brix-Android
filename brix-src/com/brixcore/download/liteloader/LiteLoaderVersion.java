package com.brixcore.download.liteloader;

import com.brixcore.game.Library;
import java.util.Collection;
import java.util.Collections;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderVersion {
    private final String file;
    private final int lastSuccessfulBuild;
    private final Collection<Library> libraries;
    private final String md5;
    private final String timestamp;
    private final String tweakClass;
    private final String version;

    public LiteLoaderVersion() {
        this("", "", "", "", "", 0, Collections.emptySet());
    }

    public LiteLoaderVersion(String tweakClass, String file, String version, String md5, String timestamp, int lastSuccessfulBuild, Collection<Library> libraries) {
        this.tweakClass = tweakClass;
        this.file = file;
        this.version = version;
        this.md5 = md5;
        this.timestamp = timestamp;
        this.lastSuccessfulBuild = lastSuccessfulBuild;
        this.libraries = libraries;
    }

    public String getTweakClass() {
        return this.tweakClass;
    }

    public String getFile() {
        return this.file;
    }

    public String getVersion() {
        return this.version;
    }

    public String getMd5() {
        return this.md5;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public int getLastSuccessfulBuild() {
        return this.lastSuccessfulBuild;
    }

    public Collection<Library> getLibraries() {
        return Collections.unmodifiableCollection(this.libraries);
    }
}

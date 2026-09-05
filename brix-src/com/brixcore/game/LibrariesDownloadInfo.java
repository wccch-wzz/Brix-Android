package com.brixcore.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public final class LibrariesDownloadInfo {
    private final LibraryDownloadInfo artifact;
    private final Map<String, LibraryDownloadInfo> classifiers;

    public LibrariesDownloadInfo(LibraryDownloadInfo artifact) {
        this(artifact, null);
    }

    public LibrariesDownloadInfo(LibraryDownloadInfo artifact, Map<String, LibraryDownloadInfo> classifiers) {
        this.artifact = artifact;
        this.classifiers = classifiers == null ? null : new HashMap(classifiers);
    }

    public LibraryDownloadInfo getArtifact() {
        return this.artifact;
    }

    public Map<String, LibraryDownloadInfo> getClassifiers() {
        return this.classifiers == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.classifiers);
    }
}

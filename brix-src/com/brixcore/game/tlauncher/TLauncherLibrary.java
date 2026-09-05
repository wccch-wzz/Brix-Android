package com.brixcore.game.tlauncher;

import com.brixcore.game.Artifact;
import com.brixcore.game.CompatibilityRule;
import com.brixcore.game.ExtractRules;
import com.brixcore.game.LibrariesDownloadInfo;
import com.brixcore.game.Library;
import com.brixcore.game.LibraryDownloadInfo;
import com.brixcore.util.platform.OperatingSystem;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes14.dex */
public class TLauncherLibrary {
    private final LibraryDownloadInfo artifact;
    private final List<String> checksums;

    @SerializedName("classifies")
    private final Map<String, LibraryDownloadInfo> classifiers;
    private final ExtractRules extract;

    @SerializedName("name")
    private final Artifact name;
    private final Map<OperatingSystem, String> natives;
    private final List<CompatibilityRule> rules;
    private final String url;

    public TLauncherLibrary(Artifact name, String url, LibraryDownloadInfo artifact, Map<String, LibraryDownloadInfo> classifiers, ExtractRules extract, Map<OperatingSystem, String> natives, List<CompatibilityRule> rules, List<String> checksums) {
        this.name = name;
        this.url = url;
        this.artifact = artifact;
        this.classifiers = classifiers;
        this.extract = extract;
        this.natives = natives;
        this.rules = rules;
        this.checksums = checksums;
    }

    public Library toLibrary() {
        return new Library(this.name, this.url, new LibrariesDownloadInfo(this.artifact, this.classifiers), this.checksums, this.extract, this.natives, this.rules, null, null);
    }
}

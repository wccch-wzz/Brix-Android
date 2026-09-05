package com.brixcore.mod.multimc;

import com.brixcore.game.Library;
import com.brixcore.util.Lang;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes10.dex */
public final class MultiMCInstancePatch {

    @SerializedName("+libraries")
    private final List<Library> _libraries;
    private final String fileId;

    @SerializedName("mcVersion")
    private final String gameVersion;

    @SerializedName("libraries")
    private final List<Library> libraries;
    private final String mainClass;
    private final String name;

    @SerializedName("+tweakers")
    private final List<String> tweakers;
    private final String version;

    public MultiMCInstancePatch() {
        this("", "", "", "", "", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public MultiMCInstancePatch(String name, String version, String gameVersion, String mainClass, String fileId, List<String> tweakers, List<Library> _libraries, List<Library> libraries) {
        this.name = name;
        this.version = version;
        this.gameVersion = gameVersion;
        this.mainClass = mainClass;
        this.fileId = fileId;
        this.tweakers = new ArrayList(tweakers);
        this._libraries = new ArrayList(_libraries);
        this.libraries = new ArrayList(libraries);
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getMainClass() {
        return this.mainClass;
    }

    public String getFileId() {
        return this.fileId;
    }

    public List<String> getTweakers() {
        return Collections.unmodifiableList(this.tweakers);
    }

    public List<Library> getLibraries() {
        return Lang.merge(this._libraries, this.libraries);
    }
}

package com.brixcore.game.tlauncher;

import com.brixcore.game.Arguments;
import com.brixcore.game.AssetIndexInfo;
import com.brixcore.game.CompatibilityRule;
import com.brixcore.game.DownloadInfo;
import com.brixcore.game.DownloadType;
import com.brixcore.game.GameJavaVersion;
import com.brixcore.game.LoggingInfo;
import com.brixcore.game.ReleaseType;
import com.brixcore.game.Version;
import com.brixcore.util.gson.JsonMap;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes14.dex */
public class TLauncherVersion implements Validation {
    private final Arguments arguments;
    private final AssetIndexInfo assetIndex;
    private final String assets;
    private final List<CompatibilityRule> compatibilityRules;
    private final Integer complianceLevel;
    private final JsonMap<DownloadType, DownloadInfo> downloads;
    private final String id;
    private final String inheritsFrom;
    private final String jar;
    private final GameJavaVersion javaVersion;
    private final List<TLauncherLibrary> libraries;
    private final JsonMap<DownloadType, LoggingInfo> logging;
    private final String mainClass;
    private final String minecraftArguments;
    private final Integer minimumLauncherVersion;
    private final Instant releaseTime;
    private final Instant time;
    private final Integer tlauncherVersion;
    private final ReleaseType type;

    public TLauncherVersion(String id, String minecraftArguments, Arguments arguments, String mainClass, String inheritsFrom, String jar, AssetIndexInfo assetIndex, String assets, Integer complianceLevel, GameJavaVersion javaVersion, List<TLauncherLibrary> libraries, List<CompatibilityRule> compatibilityRules, JsonMap<DownloadType, DownloadInfo> downloads, JsonMap<DownloadType, LoggingInfo> logging, ReleaseType type, Instant time, Instant releaseTime, Integer minimumLauncherVersion, Integer tlauncherVersion) {
        this.id = id;
        this.minecraftArguments = minecraftArguments;
        this.arguments = arguments;
        this.mainClass = mainClass;
        this.inheritsFrom = inheritsFrom;
        this.jar = jar;
        this.assetIndex = assetIndex;
        this.assets = assets;
        this.complianceLevel = complianceLevel;
        this.javaVersion = javaVersion;
        this.libraries = libraries;
        this.compatibilityRules = compatibilityRules;
        this.downloads = downloads;
        this.logging = logging;
        this.type = type;
        this.time = time;
        this.releaseTime = releaseTime;
        this.minimumLauncherVersion = minimumLauncherVersion;
        this.tlauncherVersion = tlauncherVersion;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        Validation.requireNonNull(this.tlauncherVersion, "Not TLauncher version json format");
    }

    public Version toVersion() {
        return new Version(false, this.id, null, null, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries == null ? null : (List) this.libraries.stream().map(new Function() { // from class: com.brixcore.game.tlauncher.TLauncherVersion$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((TLauncherLibrary) obj).toLibrary();
            }
        }).collect(Collectors.toList()), this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, null, null, null);
    }
}

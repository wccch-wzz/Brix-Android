package com.brixcore.game;

import com.brixcore.auth.AuthInfo;
import com.brixcore.util.Constants;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.gson.JsonMap;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes2.dex */
public class Version implements Comparable<Version>, Validation {
    private final Arguments arguments;
    private final AssetIndexInfo assetIndex;
    private final String assets;
    private final List<CompatibilityRule> compatibilityRules;
    private final Integer complianceLevel;
    private final JsonMap<DownloadType, DownloadInfo> downloads;
    private final Boolean hidden;
    private String id;
    private final String inheritsFrom;
    private final String jar;
    private final GameJavaVersion javaVersion;
    private final List<Library> libraries;
    private final JsonMap<DownloadType, LoggingInfo> logging;
    private final String mainClass;
    private final String minecraftArguments;
    private final Integer minimumLauncherVersion;
    private final List<Version> patches;
    private final Integer priority;
    private final Instant releaseTime;
    private final transient boolean resolved;
    private final Boolean root;
    private final Instant time;
    private final ReleaseType type;
    private final String version;

    public Version(String id) {
        this(false, id, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, true, null);
    }

    public Version(String id, String version, int priority, Arguments arguments, String mainClass, List<Library> libraries) {
        this(false, id, version, Integer.valueOf(priority), null, arguments, mainClass, null, null, null, null, null, null, libraries, null, null, null, null, null, null, null, null, null, null);
    }

    public Version(boolean resolved, String id, String version, Integer priority, String minecraftArguments, Arguments arguments, String mainClass, String inheritsFrom, String jar, AssetIndexInfo assetIndex, String assets, Integer complianceLevel, GameJavaVersion javaVersion, List<Library> libraries, List<CompatibilityRule> compatibilityRules, Map<DownloadType, DownloadInfo> downloads, Map<DownloadType, LoggingInfo> logging, ReleaseType type, Instant time, Instant releaseTime, Integer minimumLauncherVersion, Boolean hidden, Boolean root, List<Version> patches) {
        this.resolved = resolved;
        this.id = id;
        this.version = version;
        this.priority = priority;
        this.minecraftArguments = minecraftArguments;
        this.arguments = arguments;
        this.mainClass = mainClass;
        this.inheritsFrom = inheritsFrom;
        this.jar = jar;
        this.assetIndex = assetIndex;
        this.assets = assets;
        this.complianceLevel = complianceLevel;
        this.javaVersion = javaVersion;
        this.libraries = Lang.copyList(libraries);
        this.compatibilityRules = Lang.copyList(compatibilityRules);
        this.downloads = downloads == null ? null : new JsonMap<>(downloads);
        this.logging = logging == null ? null : new JsonMap<>(logging);
        this.type = type;
        this.time = time;
        this.releaseTime = releaseTime;
        this.minimumLauncherVersion = minimumLauncherVersion;
        this.hidden = hidden;
        this.root = root;
        this.patches = Lang.copyList(patches);
    }

    public Optional<String> getMinecraftArguments() {
        return Optional.ofNullable(this.minecraftArguments);
    }

    public Optional<Arguments> getArguments() {
        return Optional.ofNullable(this.arguments);
    }

    public String getMainClass() {
        return this.mainClass;
    }

    public Instant getTime() {
        return this.time;
    }

    public String getId() {
        return this.id;
    }

    public void _setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public int getPriority() {
        if (this.priority == null) {
            return Integer.MIN_VALUE;
        }
        return this.priority.intValue();
    }

    public ReleaseType getType() {
        return this.type == null ? ReleaseType.UNKNOWN : this.type;
    }

    public Instant getReleaseTime() {
        return this.releaseTime;
    }

    public String getJar() {
        return this.jar;
    }

    public String getInheritsFrom() {
        return this.inheritsFrom;
    }

    public int getMinimumLauncherVersion() {
        if (this.minimumLauncherVersion == null) {
            return 0;
        }
        return this.minimumLauncherVersion.intValue();
    }

    public Integer getComplianceLevel() {
        return this.complianceLevel;
    }

    public GameJavaVersion getJavaVersion() {
        return this.javaVersion;
    }

    public boolean isHidden() {
        if (this.hidden == null) {
            return false;
        }
        return this.hidden.booleanValue();
    }

    public boolean isRoot() {
        if (this.root == null) {
            return false;
        }
        return this.root.booleanValue();
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public boolean isResolvedPreservingPatches() {
        return this.inheritsFrom == null && !this.resolved;
    }

    public List<Version> getPatches() {
        return this.patches == null ? Collections.emptyList() : this.patches;
    }

    public Map<DownloadType, LoggingInfo> getLogging() {
        return this.logging == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.logging);
    }

    public List<Library> getLibraries() {
        return this.libraries == null ? Collections.emptyList() : Collections.unmodifiableList(this.libraries);
    }

    public List<CompatibilityRule> getCompatibilityRules() {
        return this.compatibilityRules == null ? Collections.emptyList() : Collections.unmodifiableList(this.compatibilityRules);
    }

    public Map<DownloadType, DownloadInfo> getDownloads() {
        return this.downloads == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.downloads);
    }

    public DownloadInfo getDownloadInfo() {
        DownloadInfo client = this.downloads == null ? null : this.downloads.get(DownloadType.CLIENT);
        String jarName = this.jar == null ? this.id : this.jar;
        if (client == null) {
            return new DownloadInfo(String.format("%s%s/%s.jar", Constants.DEFAULT_VERSION_DOWNLOAD_URL, jarName, jarName));
        }
        return client;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:35:0x0064  */
    public AssetIndexInfo getAssetIndex() {
        String hash;
        String assetsId = this.assets == null ? AuthInfo.USER_TYPE_LEGACY : this.assets;
        if (this.assetIndex == null) {
            switch (assetsId) {
                case "1.8":
                    hash = "f6ad102bcaa53b1a58358f16e376d548d44933ec";
                    break;
                case "14w31a":
                    hash = "10a2a0e75b03cfb5a7196abbdf43b54f7fa61deb";
                    break;
                case "14w25a":
                    hash = "32ff354a3be1c4dd83027111e6d79ee4d701d2c0";
                    break;
                case "1.7.4":
                    hash = "545510a60f526b9aa8a38f9c0bc7a74235d21675";
                    break;
                case "1.7.10":
                    hash = "1863782e33ce7b584fc45b037325a1964e095d3e";
                    break;
                case "1.7.3":
                    hash = "f6cf726f4747128d13887010c2cbc44ba83504d9";
                    break;
                case "pre-1.6":
                    hash = "3d8e55480977e32acd9844e545177e69a52f594b";
                    break;
                case "legacy":
                default:
                    assetsId = AuthInfo.USER_TYPE_LEGACY;
                    hash = "770572e819335b6c0a053f8378ad88eda189fc14";
                    break;
            }
            String url = Constants.DEFAULT_INDEX_URL + hash + "/" + assetsId + ".json";
            return new AssetIndexInfo(assetsId, url);
        }
        return this.assetIndex;
    }

    public boolean appliesToCurrentEnvironment() {
        return CompatibilityRule.appliesToCurrentEnvironment(this.compatibilityRules);
    }

    public Version resolve(VersionProvider provider) throws VersionNotFoundException {
        return isResolved() ? this : resolve(provider, new HashSet()).markAsResolved();
    }

    protected Version merge(Version parent, boolean isPatch) {
        String str = this.id;
        String str2 = this.minecraftArguments == null ? parent.minecraftArguments : this.minecraftArguments;
        Arguments argumentsMerge = Arguments.merge(parent.arguments, this.arguments);
        String str3 = this.mainClass == null ? parent.mainClass : this.mainClass;
        String str4 = this.jar == null ? parent.jar : this.jar;
        AssetIndexInfo assetIndexInfo = this.assetIndex == null ? parent.assetIndex : this.assetIndex;
        String str5 = this.assets == null ? parent.assets : this.assets;
        Integer num = this.complianceLevel;
        GameJavaVersion gameJavaVersion = this.javaVersion == null ? parent.javaVersion : this.javaVersion;
        List listMerge = Lang.merge(this.libraries, parent.libraries);
        List listMerge2 = Lang.merge(parent.compatibilityRules, this.compatibilityRules);
        JsonMap<DownloadType, DownloadInfo> jsonMap = this.downloads == null ? parent.downloads : this.downloads;
        JsonMap<DownloadType, LoggingInfo> jsonMap2 = this.logging == null ? parent.logging : this.logging;
        ReleaseType releaseType = this.type == null ? parent.type : this.type;
        Instant instant = this.time == null ? parent.time : this.time;
        Instant instant2 = this.releaseTime == null ? parent.releaseTime : this.releaseTime;
        Integer num2 = (Integer) Lang.merge(this.minimumLauncherVersion, parent.minimumLauncherVersion, new BinaryOperator() { // from class: com.brixcore.game.Version$$ExternalSyntheticLambda2
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Integer.valueOf(Math.max(((Integer) obj).intValue(), ((Integer) obj2).intValue()));
            }
        });
        Boolean bool = this.hidden;
        List<Version> listMerge3 = parent.patches;
        if (!isPatch) {
            listMerge3 = Lang.merge(Lang.merge(listMerge3, Collections.singleton(toPatch())), this.patches);
        }
        return new Version(true, str, null, null, str2, argumentsMerge, str3, null, str4, assetIndexInfo, str5, num, gameJavaVersion, listMerge, listMerge2, jsonMap, jsonMap2, releaseType, instant, instant2, num2, bool, true, listMerge3);
    }

    protected Version resolve(VersionProvider provider, Set<String> resolvedSoFar) throws VersionNotFoundException {
        Version thisVersion;
        Version thisVersion2;
        if (this.inheritsFrom == null) {
            if (isRoot()) {
                thisVersion2 = new Version(this.id).setPatches(this.patches);
            } else {
                thisVersion2 = this;
            }
            thisVersion = thisVersion2.setJar(this.jar == null ? this.id : this.jar);
        } else if (!resolvedSoFar.add(this.id)) {
            Logging.LOG.log(Level.WARNING, "Found circular dependency versions: " + resolvedSoFar);
            thisVersion = this.jar == null ? setJar(this.id) : this;
        } else {
            thisVersion = merge(provider.getVersion(this.inheritsFrom).resolve(provider, resolvedSoFar), false);
        }
        if (this.patches == null) {
            return thisVersion;
        }
        if (!this.patches.isEmpty()) {
            List<Version> sortedPatches = (List) this.patches.stream().sorted(Comparator.comparing(new Function() { // from class: com.brixcore.game.Version$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((Version) obj).getPriority());
                }
            })).collect(Collectors.toList());
            for (Version patch : sortedPatches) {
                thisVersion = patch.setJar(null).merge(thisVersion, true);
            }
        }
        return thisVersion.setId(this.id);
    }

    private Version toPatch() {
        return clearPatches().setHidden(true).setId("resolved." + getId());
    }

    public Version resolvePreservingPatches(VersionProvider provider) throws VersionNotFoundException {
        return resolvePreservingPatches(provider, new HashSet());
    }

    protected Version mergePreservingPatches(Version parent) {
        return parent.addPatch(toPatch()).addPatches(this.patches);
    }

    protected Version resolvePreservingPatches(VersionProvider provider, Set<String> resolvedSoFar) throws VersionNotFoundException {
        Version thisVersion = isRoot() ? this : new Version(this.id).addPatch(toPatch()).addPatches(getPatches());
        if (this.inheritsFrom != null) {
            if (!resolvedSoFar.add(this.id)) {
                Logging.LOG.log(Level.WARNING, "Found circular dependency versions: " + resolvedSoFar);
            } else {
                thisVersion = mergePreservingPatches(provider.getVersion(this.inheritsFrom).resolvePreservingPatches(provider, resolvedSoFar));
            }
        }
        return thisVersion.setId(this.id).setJar(resolve(provider).getJar());
    }

    private Version markAsResolved() {
        return new Version(true, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version markAsUnresolved() {
        return new Version(false, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    private Version setHidden(Boolean hidden) {
        return new Version(true, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, hidden, this.root, this.patches);
    }

    public Version setId(String id) {
        return new Version(this.resolved, id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setVersion(String version) {
        return new Version(this.resolved, this.id, version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setPriority(Integer priority) {
        return new Version(this.resolved, this.id, this.version, priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setMinecraftArguments(String minecraftArguments) {
        return new Version(this.resolved, this.id, this.version, this.priority, minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setArguments(Arguments arguments) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setMainClass(String mainClass) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setInheritsFrom(String inheritsFrom) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setJar(String jar) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setLibraries(List<Library> libraries) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setLogging(Map<DownloadType, LoggingInfo> logging) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches);
    }

    public Version setPatches(List<Version> patches) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, patches);
    }

    public Version addPatch(Version... additional) {
        return addPatches(Arrays.asList(additional));
    }

    public Version addPatches(List<Version> additional) {
        final Set<String> patchIds = additional == null ? Collections.emptySet() : (Set) additional.stream().map(new Function() { // from class: com.brixcore.game.Version$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Version) obj).getId();
            }
        }).collect(Collectors.toSet());
        List<Version> patches = Lang.merge(this.patches == null ? null : (Collection) this.patches.stream().filter(new Predicate() { // from class: com.brixcore.game.Version$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Version.lambda$addPatches$0(patchIds, (Version) obj);
            }
        }).collect(Collectors.toList()), additional);
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, patches);
    }

    static /* synthetic */ boolean lambda$addPatches$0(Set patchIds, Version patch) {
        return !patchIds.contains(patch.getId());
    }

    public Version clearPatches() {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, null);
    }

    public Version removePatchById(final String patchId) {
        return new Version(this.resolved, this.id, this.version, this.priority, this.minecraftArguments, this.arguments, this.mainClass, this.inheritsFrom, this.jar, this.assetIndex, this.assets, this.complianceLevel, this.javaVersion, this.libraries, this.compatibilityRules, this.downloads, this.logging, this.type, this.time, this.releaseTime, this.minimumLauncherVersion, this.hidden, this.root, this.patches == null ? null : (List) this.patches.stream().filter(new Predicate() { // from class: com.brixcore.game.Version$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Version.lambda$removePatchById$1(patchId, (Version) obj);
            }
        }).collect(Collectors.toList()));
    }

    static /* synthetic */ boolean lambda$removePatchById$1(String patchId, Version patch) {
        return !patchId.equals(patch.getId());
    }

    public boolean hasPatch(final String patchId) {
        return this.patches != null && this.patches.stream().anyMatch(new Predicate() { // from class: com.brixcore.game.Version$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return patchId.equals(((Version) obj).getId());
            }
        });
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof Version) && Objects.equals(this.id, ((Version) obj).id);
    }

    @Override // java.lang.Comparable
    public int compareTo(Version o) {
        return this.id.compareTo(o.id);
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(this.id)) {
            throw new JsonParseException("Version ID cannot be blank");
        }
        if (this.downloads != null) {
            for (Map.Entry<DownloadType, DownloadInfo> entry : this.downloads.entrySet()) {
                if (!(entry.getKey() instanceof DownloadType)) {
                    throw new JsonParseException("Version downloads key must be DownloadType");
                }
                if (!(entry.getValue() instanceof DownloadInfo)) {
                    throw new JsonParseException("Version downloads value must be DownloadInfo");
                }
            }
        }
        if (this.logging != null) {
            for (Map.Entry<DownloadType, LoggingInfo> entry2 : this.logging.entrySet()) {
                if (!(entry2.getKey() instanceof DownloadType)) {
                    throw new JsonParseException("Version logging key must be DownloadType");
                }
                if (!(entry2.getValue() instanceof LoggingInfo)) {
                    throw new JsonParseException("Version logging value must be LoggingInfo");
                }
            }
        }
    }
}

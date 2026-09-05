package com.brixcore.download;

import com.brixcore.download.cleanroom.CleanroomVersionList;
import com.brixcore.download.fabric.FabricAPIVersionList;
import com.brixcore.download.fabric.FabricVersionList;
import com.brixcore.download.forge.ForgeBMCLVersionList;
import com.brixcore.download.game.GameVersionList;
import com.brixcore.download.liteloader.LiteLoaderBMCLVersionList;
import com.brixcore.download.neoforge.NeoForgeBMCLVersionList;
import com.brixcore.download.optifine.OptiFineBMCLVersionList;
import com.brixcore.download.quilt.QuiltAPIVersionList;
import com.brixcore.download.quilt.QuiltVersionList;
import com.brixcore.util.Pair;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes14.dex */
public final class BMCLAPIDownloadProvider implements DownloadProvider {
    private final String apiRoot;
    private final ForgeBMCLVersionList forge;
    private final NeoForgeBMCLVersionList neoforge;
    private final OptiFineBMCLVersionList optifine;
    private final List<Pair<String, String>> replacement;
    private final GameVersionList game = new GameVersionList(this);
    private final FabricVersionList fabric = new FabricVersionList(this);
    private final FabricAPIVersionList fabricApi = new FabricAPIVersionList(this);
    private final CleanroomVersionList cleanroom = new CleanroomVersionList(this);
    private final LiteLoaderBMCLVersionList liteLoader = new LiteLoaderBMCLVersionList(this);
    private final QuiltVersionList quilt = new QuiltVersionList(this);
    private final QuiltAPIVersionList quiltApi = new QuiltAPIVersionList(this);

    public BMCLAPIDownloadProvider(String apiRoot) {
        this.apiRoot = apiRoot;
        this.forge = new ForgeBMCLVersionList(apiRoot);
        this.neoforge = new NeoForgeBMCLVersionList(apiRoot);
        this.optifine = new OptiFineBMCLVersionList(apiRoot);
        this.replacement = Arrays.asList(Pair.pair("https://bmclapi2.bangbang93.com", apiRoot), Pair.pair("https://launchermeta.mojang.com", apiRoot), Pair.pair("https://piston-meta.mojang.com", apiRoot), Pair.pair("https://piston-data.mojang.com", apiRoot), Pair.pair("https://launcher.mojang.com", apiRoot), Pair.pair("https://libraries.minecraft.net", apiRoot + "/libraries"), Pair.pair("http://files.minecraftforge.net/maven", apiRoot + "/maven"), Pair.pair("https://files.minecraftforge.net/maven", apiRoot + "/maven"), Pair.pair("https://maven.minecraftforge.net", apiRoot + "/maven"), Pair.pair("https://maven.neoforged.net/releases/", apiRoot + "/maven/"), Pair.pair("http://dl.liteloader.com/versions/versions.json", apiRoot + "/maven/com/mumfrey/liteloader/versions.json"), Pair.pair("http://dl.liteloader.com/versions", apiRoot + "/maven"), Pair.pair("https://meta.fabricmc.net", apiRoot + "/fabric-meta"), Pair.pair("https://maven.fabricmc.net", apiRoot + "/maven"), Pair.pair("https://authlib-injector.yushi.moe", apiRoot + "/mirrors/authlib-injector"), Pair.pair("https://repo1.maven.org/maven2", "https://mirrors.cloud.tencent.com/nexus/repository/maven-public"), Pair.pair("https://repo.maven.apache.org/maven2", "https://mirrors.cloud.tencent.com/nexus/repository/maven-public"), Pair.pair("https://hmcl.glavo.site/metadata/cleanroom", "https://alist.8mi.tech/d/mirror/HMCL-Metadata/Auto/cleanroom"), Pair.pair("https://zkitefly.github.io/unlisted-versions-of-minecraft", "https://alist.8mi.tech/d/mirror/unlisted-versions-of-minecraft/Auto"), Pair.pair("https://api.modrinth.com", "https://mod.mcimirror.top/modrinth"), Pair.pair("https://cdn.modrinth.com", "https://mod.mcimirror.top"), Pair.pair("https://api.curseforge.com", "https://mod.mcimirror.top/curseforge"), Pair.pair("https://edge.forgecdn.net", "https://mod.mcimirror.top"));
    }

    public String getApiRoot() {
        return this.apiRoot;
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getVersionListURL() {
        return this.apiRoot + "/mc/game/version_manifest.json";
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getAssetBaseURL() {
        return this.apiRoot + "/assets/";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:35:0x006f  */
    @Override // com.brixcore.download.DownloadProvider
    public VersionList<?> getVersionListById(String id) {
        switch (id) {
            case "game":
                return this.game;
            case "fabric":
                return this.fabric;
            case "fabric-api":
                return this.fabricApi;
            case "forge":
                return this.forge;
            case "cleanroom":
                return this.cleanroom;
            case "neoforge":
                return this.neoforge;
            case "liteloader":
                return this.liteLoader;
            case "optifine":
                return this.optifine;
            case "quilt":
                return this.quilt;
            case "quilt-api":
                return this.quiltApi;
            default:
                throw new IllegalArgumentException("Unrecognized version list id: " + id);
        }
    }

    @Override // com.brixcore.download.DownloadProvider
    public String injectURL(String baseURL) {
        for (Pair<String, String> pair : this.replacement) {
            if (baseURL.startsWith(pair.getKey())) {
                return pair.getValue() + baseURL.substring(pair.getKey().length());
            }
        }
        return baseURL;
    }

    @Override // com.brixcore.download.DownloadProvider
    public int getConcurrency() {
        return Math.max(Runtime.getRuntime().availableProcessors() * 2, 6);
    }
}

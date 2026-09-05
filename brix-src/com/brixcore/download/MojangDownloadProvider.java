package com.brixcore.download;

import com.brixcore.download.cleanroom.CleanroomVersionList;
import com.brixcore.download.fabric.FabricAPIVersionList;
import com.brixcore.download.fabric.FabricVersionList;
import com.brixcore.download.forge.ForgeVersionList;
import com.brixcore.download.game.GameVersionList;
import com.brixcore.download.liteloader.LiteLoaderVersionList;
import com.brixcore.download.neoforge.NeoForgeOfficialVersionList;
import com.brixcore.download.optifine.OptiFine302VersionList;
import com.brixcore.download.quilt.QuiltAPIVersionList;
import com.brixcore.download.quilt.QuiltVersionList;

/* JADX INFO: loaded from: classes14.dex */
public class MojangDownloadProvider implements DownloadProvider {
    private final GameVersionList game = new GameVersionList(this);
    private final FabricVersionList fabric = new FabricVersionList(this);
    private final FabricAPIVersionList fabricApi = new FabricAPIVersionList(this);
    private final ForgeVersionList forge = new ForgeVersionList(this);
    private final CleanroomVersionList cleanroom = new CleanroomVersionList(this);
    private final NeoForgeOfficialVersionList neoforge = new NeoForgeOfficialVersionList(this);
    private final LiteLoaderVersionList liteLoader = new LiteLoaderVersionList(this);
    private final OptiFine302VersionList optifine = new OptiFine302VersionList("https://hmcl-dev.github.io/metadata/optifine/");
    private final QuiltVersionList quilt = new QuiltVersionList(this);
    private final QuiltAPIVersionList quiltApi = new QuiltAPIVersionList(this);

    @Override // com.brixcore.download.DownloadProvider
    public String getVersionListURL() {
        return "https://piston-meta.mojang.com/mc/game/version_manifest.json";
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getAssetBaseURL() {
        return "https://resources.download.minecraft.net/";
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
        return baseURL;
    }

    @Override // com.brixcore.download.DownloadProvider
    public int getConcurrency() {
        return 6;
    }
}

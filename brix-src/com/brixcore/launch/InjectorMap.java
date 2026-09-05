package com.brixcore.launch;

import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.Version;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class InjectorMap {
    private final ArrayList<MapInfo> maps;

    public InjectorMap(ArrayList<MapInfo> maps) {
        this.maps = maps;
    }

    public ArrayList<MapInfo> getMaps() {
        return this.maps;
    }

    public static class MapInfo {
        private final Argument argument;
        private final String id;

        public MapInfo(String id, Argument argument) {
            this.id = id;
            this.argument = argument;
        }

        public String getId() {
            return this.id;
        }

        public Argument getArgument() {
            return this.argument;
        }
    }

    public static class Argument {
        private final String fabric;
        private final String forge;
        private final String neoforge;
        private final String vanilla;

        public Argument(String vanilla, String forge, String neoforge, String fabric) {
            this.vanilla = vanilla;
            this.forge = forge;
            this.neoforge = neoforge;
            this.fabric = fabric;
        }

        public String getVanilla() {
            return this.vanilla;
        }

        public String getForge() {
            return this.forge;
        }

        public String getNeoforge() {
            return this.neoforge;
        }

        public String getFabric() {
            return this.fabric;
        }

        public String getArgument(Version version, String gameVersion) {
            if (LibraryAnalyzer.analyze(version, gameVersion).has(LibraryAnalyzer.LibraryType.FORGE)) {
                return getForge();
            }
            if (LibraryAnalyzer.analyze(version, gameVersion).has(LibraryAnalyzer.LibraryType.NEO_FORGE)) {
                return getNeoforge();
            }
            if (LibraryAnalyzer.analyze(version, gameVersion).has(LibraryAnalyzer.LibraryType.FABRIC) || LibraryAnalyzer.analyze(version, gameVersion).has(LibraryAnalyzer.LibraryType.QUILT)) {
                return getFabric();
            }
            return getVanilla();
        }
    }
}

package com.brixcore.mod;

import com.brixcore.util.Lang;
import java.io.File;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface ModAdviser {
    public static final List<String> MODPACK_BLACK_LIST = Lang.immutableListOf("regex:(.*?)\\.log", "usernamecache.json", "usercache.json", "launcher_profiles.json", "launcher.pack.lzma", "launcher_accounts.json", "launcher_cef_log.txt", "launcher_log.txt", "launcher_msa_credentials.bin", "launcher_settings.json", "launcher_ui_state.json", "realms_persistence.json", "webcache2", "treatment_tags.json", "clientId.txt", "PCL.ini", "backup", "pack.json", "launcher.jar", "cache", "modpack.cfg", "manifest.json", "minecraftinstance.json", ".curseclient", ".fabric", ".mixin.out", "jars", "logs", "versions", "assets", "libraries", "crash-reports", "NVIDIA", "AMD", "screenshots", "natives", "native", "$native", "server-resource-packs", "downloads", "asm", "backups", "TCNodeTracker", "CustomDISkins", "data", "CustomSkinLoader/caches");
    public static final List<String> MODPACK_SUGGESTED_BLACK_LIST = Lang.immutableListOf("fonts", "saves", "servers.dat", "options.txt", "blueprints", "optionsof.txt", "journeymap", "optionsshaders.txt", "mods" + File.separator + "VoxelMods");

    public enum ModSuggestion {
        SUGGESTED,
        NORMAL,
        HIDDEN
    }

    ModSuggestion advise(String str, boolean z);

    static ModSuggestion suggestMod(String fileName, boolean isDirectory) {
        if (match(MODPACK_BLACK_LIST, fileName, isDirectory)) {
            return ModSuggestion.HIDDEN;
        }
        if (match(MODPACK_SUGGESTED_BLACK_LIST, fileName, isDirectory)) {
            return ModSuggestion.NORMAL;
        }
        return ModSuggestion.SUGGESTED;
    }

    static boolean match(List<String> l, String fileName, boolean isDirectory) {
        for (String s : l) {
            if (isDirectory) {
                if (fileName.startsWith(s + File.separator)) {
                    return true;
                }
            } else if (s.startsWith("regex:")) {
                if (fileName.matches(s.substring("regex:".length()))) {
                    return true;
                }
            } else if (fileName.equals(s)) {
                return true;
            }
        }
        return false;
    }
}

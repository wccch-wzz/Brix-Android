package com.brixcore.game;

import com.brixcore.util.Logging;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jenkinsci.constant_pool_scanner.ConstantPool;
import org.jenkinsci.constant_pool_scanner.ConstantPoolScanner;
import org.jenkinsci.constant_pool_scanner.ConstantType;
import org.jenkinsci.constant_pool_scanner.StringConstant;

/* JADX INFO: loaded from: classes2.dex */
public final class GameVersion {
    private GameVersion() {
    }

    private static Optional<String> getVersionFromJson(InputStream versionJson) {
        try {
            Map<?, ?> version = (Map) JsonUtils.fromNonNullJsonFully(versionJson, Map.class);
            String id = (String) version.get("id");
            if (id != null && id.contains(" / ")) {
                id = id.substring(0, id.indexOf(" / "));
            }
            return Optional.ofNullable(id);
        } catch (JsonParseException | IOException | ClassCastException e) {
            Logging.LOG.log(Level.WARNING, "Failed to parse version.json", (Throwable) e);
            return Optional.empty();
        }
    }

    private static Optional<String> getVersionOBrixassMinecraft(InputStream bytecode) throws IOException {
        ConstantPool pool = ConstantPoolScanner.parse(bytecode, ConstantType.STRING);
        return StreamSupport.stream(pool.list(StringConstant.class).spliterator(), false).map(new GameVersion$$ExternalSyntheticLambda0()).filter(new Predicate() { // from class: com.brixcore.game.GameVersion$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((String) obj).startsWith("Minecraft Minecraft ");
            }
        }).map(new Function() { // from class: com.brixcore.game.GameVersion$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((String) obj).substring("Minecraft Minecraft ".length());
            }
        }).findFirst();
    }

    private static Optional<String> getVersionFromClassMinecraftServer(InputStream bytecode) throws IOException {
        ConstantPool pool = ConstantPoolScanner.parse(bytecode, ConstantType.STRING);
        List<String> list = (List) StreamSupport.stream(pool.list(StringConstant.class).spliterator(), false).map(new GameVersion$$ExternalSyntheticLambda0()).collect(Collectors.toList());
        int idx = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith("Can't keep up!")) {
                idx = i;
                break;
            }
        }
        for (int i2 = idx - 1; i2 >= 0; i2--) {
            if (list.get(i2).matches(".*[0-9].*")) {
                return Optional.of(list.get(i2));
            }
        }
        return Optional.empty();
    }

    public static Optional<String> minecraftVersion(File file) {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return Optional.empty();
        }
        try {
            ZipFile gameJar = new ZipFile(file);
            try {
                ZipEntry versionJson = gameJar.getEntry("version.json");
                if (versionJson != null) {
                    Optional<String> result = getVersionFromJson(gameJar.getInputStream(versionJson));
                    if (result.isPresent()) {
                        gameJar.close();
                        return result;
                    }
                }
                ZipEntry minecraft = gameJar.getEntry("net/minecraft/client/Minecraft.class");
                if (minecraft != null) {
                    InputStream is = gameJar.getInputStream(minecraft);
                    try {
                        Optional<String> result2 = getVersionOBrixassMinecraft(is);
                        if (result2.isPresent()) {
                            String version = result2.get();
                            if (version.startsWith("Beta ")) {
                                result2 = Optional.of("b" + version.substring("Beta ".length()));
                            }
                            if (is != null) {
                                is.close();
                            }
                            gameJar.close();
                            return result2;
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (Throwable th) {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                    try {
                        gameJar.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                    throw th;
                }
                ZipEntry minecraftServer = gameJar.getEntry("net/minecraft/server/MinecraftServer.class");
                if (minecraftServer == null) {
                    Optional<String> optionalEmpty = Optional.empty();
                    gameJar.close();
                    return optionalEmpty;
                }
                InputStream is2 = gameJar.getInputStream(minecraftServer);
                try {
                    Optional<String> versionFromClassMinecraftServer = getVersionFromClassMinecraftServer(is2);
                    if (is2 != null) {
                        is2.close();
                    }
                    gameJar.close();
                    return versionFromClassMinecraftServer;
                } catch (Throwable th4) {
                    if (is2 != null) {
                        try {
                            is2.close();
                        } catch (Throwable th5) {
                            th4.addSuppressed(th5);
                        }
                    }
                    throw th4;
                }
            } catch (Throwable th6) {
                gameJar.close();
                throw th6;
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}

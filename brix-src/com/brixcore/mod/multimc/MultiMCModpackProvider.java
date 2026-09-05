package com.brixcore.mod.multimc;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.mod.MismatchedModpackTypeException;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.mod.ModpackUpdateTask;
import com.brixcore.task.Task;
import com.brixcore.util.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* JADX INFO: loaded from: classes10.dex */
public final class MultiMCModpackProvider implements ModpackProvider {
    public static final MultiMCModpackProvider INSTANCE = new MultiMCModpackProvider();

    @Override // com.brixcore.mod.ModpackProvider
    public String getName() {
        return "MultiMC";
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        return null;
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createUpdateTask(DefaultDependencyManager dependencyManager, String name, File zipFile, Modpack modpack) throws MismatchedModpackTypeException {
        if (modpack.getManifest() instanceof MultiMCInstanceConfiguration) {
            return new ModpackUpdateTask(dependencyManager.getGameRepository(), name, new MultiMCModpackInstallTask(dependencyManager, zipFile, modpack, (MultiMCInstanceConfiguration) modpack.getManifest(), name));
        }
        throw new MismatchedModpackTypeException(getName(), modpack.getManifest().getProvider().getName());
    }

    private static boolean testPath(Path root) {
        return Files.exists(root.resolve("instance.cfg"), new LinkOption[0]);
    }

    public static Path getRootPath(Path root) throws IOException {
        if (testPath(root)) {
            return root;
        }
        Stream<Path> stream = Files.list(root);
        try {
            Path candidate = stream.filter(new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackProvider$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Files.isDirectory((Path) obj, new LinkOption[0]);
                }
            }).findAny().orElseThrow(new Supplier() { // from class: com.brixcore.mod.multimc.MultiMCModpackProvider$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return MultiMCModpackProvider.lambda$getRootPath$1();
                }
            });
            if (!testPath(candidate)) {
                throw new IOException("Not a valid MultiMC modpack");
            }
            if (stream != null) {
                stream.close();
            }
            return candidate;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    static /* synthetic */ IOException lambda$getRootPath$1() {
        return new IOException("Not a valid MultiMC modpack");
    }

    private static String getRootEntryName(ZipFile file) throws IOException {
        if (file.getEntry("instance.cfg") != null) {
            return "";
        }
        Enumeration<ZipArchiveEntry> entries = file.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            String entryName = entry.getName();
            int idx = entryName.indexOf(47);
            if (idx >= 0 && entryName.length() == "instance.cfg".length() + idx + 1 && entryName.startsWith("instance.cfg", idx + 1)) {
                return entryName.substring(0, idx + 1);
            }
        }
        throw new IOException("Not a valid MultiMC modpack");
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Modpack readManifest(ZipFile modpackFile, Path modpackPath, Charset encoding) throws IOException {
        String rootEntryName = getRootEntryName(modpackFile);
        MultiMCManifest manifest = MultiMCManifest.readMultiMCModpackManifest(modpackFile, rootEntryName);
        String name = rootEntryName.isEmpty() ? FileUtils.getNameWithoutExtension(modpackPath) : rootEntryName.substring(0, rootEntryName.length() - 1);
        ZipArchiveEntry instanceEntry = modpackFile.getEntry(rootEntryName + "instance.cfg");
        if (instanceEntry == null) {
            throw new IOException("`instance.cfg` not found, " + modpackFile + " is not a valid MultiMC modpack.");
        }
        InputStream instanceStream = modpackFile.getInputStream(instanceEntry);
        try {
            final MultiMCInstanceConfiguration cfg = new MultiMCInstanceConfiguration(name, instanceStream, manifest);
            Modpack modpack = new Modpack(cfg.getName(), "", "", cfg.getGameVersion(), cfg.getNotes(), encoding, cfg) { // from class: com.brixcore.mod.multimc.MultiMCModpackProvider.1
                @Override // com.brixcore.mod.Modpack
                public Task<?> getInstallTask(DefaultDependencyManager dependencyManager, File zipFile, String name2) {
                    return new MultiMCModpackInstallTask(dependencyManager, zipFile, this, cfg, name2);
                }
            };
            if (instanceStream != null) {
                instanceStream.close();
            }
            return modpack;
        } catch (Throwable th) {
            if (instanceStream == null) {
                throw th;
            }
            try {
                instanceStream.close();
                throw th;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
                throw th;
            }
        }
    }
}

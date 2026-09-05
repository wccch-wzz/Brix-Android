package com.brixcore.mod;

import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.Unzipper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes2.dex */
public class ModpackInstallTask<T> extends Task<Void> {
    private final Predicate<String> callback;
    private final Charset charset;
    private final File dest;
    private final File modpackFile;
    private final List<ModpackConfiguration.FileInformation> overrides;
    private final List<String> subDirectories;

    public ModpackInstallTask(File modpackFile, File dest, Charset charset, List<String> subDirectories, Predicate<String> callback, ModpackConfiguration<T> oldConfiguration) {
        this.modpackFile = modpackFile;
        this.dest = dest;
        this.charset = charset;
        this.subDirectories = subDirectories;
        this.callback = callback;
        if (oldConfiguration == null) {
            this.overrides = Collections.emptyList();
        } else {
            this.overrides = oldConfiguration.getOverrides();
        }
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        final Set<String> entries = new HashSet<>();
        if (!FileUtils.makeDirectory(this.dest)) {
            throw new IOException("Unable to make directory " + this.dest);
        }
        final HashMap<String, ModpackConfiguration.FileInformation> files = new HashMap<>();
        for (ModpackConfiguration.FileInformation file : this.overrides) {
            files.put(file.getPath(), file);
        }
        for (String subDirectory : this.subDirectories) {
            new Unzipper(this.modpackFile, this.dest).setSubDirectory(subDirectory).setTerminateIfSubDirectoryNotExists().setReplaceExistentFile(true).setEncoding(this.charset).setFilter(new Unzipper.FileFilter() { // from class: com.brixcore.mod.ModpackInstallTask$$ExternalSyntheticLambda0
                @Override // com.brixcore.util.io.Unzipper.FileFilter
                public final boolean accept(Path path, boolean z, Path path2, String str) {
                    return this.f$0.lambda$execute$0(entries, files, path, z, path2, str);
                }
            }).unzip();
        }
        for (ModpackConfiguration.FileInformation file2 : this.overrides) {
            File original = new File(this.dest, file2.getPath());
            if (original.exists() && !entries.contains(file2.getPath())) {
                original.delete();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$execute$0(Set entries, HashMap files, Path destPath, boolean isDirectory, Path zipEntry, String entryPath) throws IOException {
        if (isDirectory) {
            return true;
        }
        if (!this.callback.test(entryPath)) {
            return false;
        }
        entries.add(entryPath);
        if (!files.containsKey(entryPath)) {
            return true;
        }
        if (!Files.exists(destPath, new LinkOption[0])) {
            return false;
        }
        String fileHash = DigestUtils.digestToString(CacheRepository.SHA1, destPath);
        String oldHash = ((ModpackConfiguration.FileInformation) files.get(entryPath)).getHash();
        return Objects.equals(oldHash, fileHash);
    }
}

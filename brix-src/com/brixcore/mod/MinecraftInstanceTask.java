package com.brixcore.mod;

import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes2.dex */
public final class MinecraftInstanceTask<T> extends Task<ModpackConfiguration<T>> {
    private final Charset encoding;
    private final File jsonFile;
    private final T manifest;
    private final String name;
    private final List<String> subDirectories;
    private final String type;
    private final String version;
    private final File zipFile;

    public MinecraftInstanceTask(File zipFile, Charset encoding, List<String> subDirectories, T manifest, ModpackProvider modpackProvider, String name, String version, File jsonFile) {
        this.zipFile = zipFile;
        this.encoding = encoding;
        this.subDirectories = (List) subDirectories.stream().map(new Function() { // from class: com.brixcore.mod.MinecraftInstanceTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return FileUtils.normalizePath((String) obj);
            }
        }).collect(Collectors.toList());
        this.manifest = manifest;
        this.jsonFile = jsonFile;
        this.type = modpackProvider.getName();
        this.name = name;
        this.version = version;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        final List<ModpackConfiguration.FileInformation> overrides = new ArrayList<>();
        FileSystem fs = CompressingUtils.readonly(this.zipFile.toPath()).setEncoding(this.encoding).build();
        try {
            for (String subDirectory : this.subDirectories) {
                final Path root = fs.getPath(subDirectory, new String[0]);
                if (Files.exists(root, new LinkOption[0])) {
                    Files.walkFileTree(root, new SimpleFileVisitor<Path>() { // from class: com.brixcore.mod.MinecraftInstanceTask.1
                        @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            String relativePath = root.relativize(file).normalize().toString().replace(File.separatorChar, IOUtils.DIR_SEPARATOR_UNIX);
                            overrides.add(new ModpackConfiguration.FileInformation(relativePath, DigestUtils.digestToString(CacheRepository.SHA1, file)));
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            }
            if (fs != null) {
                fs.close();
            }
            ModpackConfiguration modpackConfiguration = new ModpackConfiguration(this.manifest, this.type, this.name, this.version, overrides);
            FileUtils.writeText(this.jsonFile, JsonUtils.GSON.toJson(modpackConfiguration));
            setResult(modpackConfiguration);
        } catch (Throwable th) {
            if (fs == null) {
                throw th;
            }
            try {
                fs.close();
                throw th;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
                throw th;
            }
        }
    }
}

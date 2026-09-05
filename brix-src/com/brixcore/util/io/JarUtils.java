package com.brixcore.util.io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/* JADX INFO: loaded from: classes3.dex */
public final class JarUtils {
    private static final Optional<Path> THIS_JAR = Optional.ofNullable(JarUtils.class.getProtectionDomain().getCodeSource()).map(new Function() { // from class: com.brixcore.util.io.JarUtils$$ExternalSyntheticLambda0
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return JarUtils.lambda$static$0((CodeSource) obj);
        }
    }).filter(new Predicate() { // from class: com.brixcore.util.io.JarUtils$$ExternalSyntheticLambda1
        @Override // java.util.function.Predicate
        public final boolean test(Object obj) {
            return Files.isRegularFile((Path) obj, new LinkOption[0]);
        }
    });
    private static final Manifest manifest = (Manifest) THIS_JAR.flatMap(new Function() { // from class: com.brixcore.util.io.JarUtils$$ExternalSyntheticLambda2
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return JarUtils.getManifest((Path) obj);
        }
    }).orElseGet(new Supplier() { // from class: com.brixcore.util.io.JarUtils$$ExternalSyntheticLambda3
        @Override // java.util.function.Supplier
        public final Object get() {
            return JarUtils.$r8$lambda$2yh1NQLvPShq1k6FPQ_mbp416po();
        }
    });

    public static /* synthetic */ Manifest $r8$lambda$2yh1NQLvPShq1k6FPQ_mbp416po() {
        return new Manifest();
    }

    private JarUtils() {
    }

    static /* synthetic */ Path lambda$static$0(CodeSource codeSource) {
        try {
            return Paths.get(codeSource.getLocation().toURI());
        } catch (IllegalArgumentException | URISyntaxException | FileSystemNotFoundException e) {
            return null;
        }
    }

    public static Optional<Path> thisJar() {
        return THIS_JAR;
    }

    public static String getManifestAttribute(String name, String defaultValue) {
        String value = manifest.getMainAttributes().getValue(name);
        return value != null ? value : defaultValue;
    }

    public static Optional<Manifest> getManifest(Path jar) {
        try {
            JarFile file = new JarFile(jar.toFile());
            try {
                Optional<Manifest> optionalOfNullable = Optional.ofNullable(file.getManifest());
                file.close();
                return optionalOfNullable;
            } catch (Throwable th) {
                try {
                    file.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}

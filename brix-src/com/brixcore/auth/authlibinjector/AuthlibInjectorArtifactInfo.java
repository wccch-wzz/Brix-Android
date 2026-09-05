package com.brixcore.auth.authlibinjector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/* JADX INFO: loaded from: classes14.dex */
public class AuthlibInjectorArtifactInfo {
    private int buildNumber;
    private Path location;
    private String version;

    public static AuthlibInjectorArtifactInfo from(Path location) throws IOException {
        JarFile jarFile = new JarFile(location.toFile());
        try {
            Attributes attributes = jarFile.getManifest().getMainAttributes();
            String title = (String) Optional.ofNullable(attributes.getValue("Implementation-Title")).orElseThrow(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactInfo$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    return AuthlibInjectorArtifactInfo.lambda$from$0();
                }
            });
            if (!"authlib-injector".equals(title)) {
                throw new IOException("Bad Implementation-Title");
            }
            String version = (String) Optional.ofNullable(attributes.getValue("Implementation-Version")).orElseThrow(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactInfo$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return AuthlibInjectorArtifactInfo.lambda$from$1();
                }
            });
            try {
                int buildNumber = ((Integer) Optional.ofNullable(attributes.getValue("Build-Number")).map(new Function() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactInfo$$ExternalSyntheticLambda2
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return Integer.valueOf(Integer.parseInt((String) obj));
                    }
                }).orElseThrow(new Supplier() { // from class: com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactInfo$$ExternalSyntheticLambda3
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        return AuthlibInjectorArtifactInfo.lambda$from$2();
                    }
                })).intValue();
                AuthlibInjectorArtifactInfo authlibInjectorArtifactInfo = new AuthlibInjectorArtifactInfo(buildNumber, version, location.toAbsolutePath());
                jarFile.close();
                return authlibInjectorArtifactInfo;
            } catch (NumberFormatException e) {
                throw new IOException("Bad Build-Number", e);
            }
        } catch (Throwable th) {
            try {
                jarFile.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    static /* synthetic */ IOException lambda$from$0() {
        return new IOException("Missing Implementation-Title");
    }

    static /* synthetic */ IOException lambda$from$1() {
        return new IOException("Missing Implementation-Version");
    }

    static /* synthetic */ IOException lambda$from$2() {
        return new IOException("Missing Build-Number");
    }

    public AuthlibInjectorArtifactInfo(int buildNumber, String version, Path location) {
        this.buildNumber = buildNumber;
        this.version = version;
        this.location = location;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public String getVersion() {
        return this.version;
    }

    public Path getLocation() {
        return this.location;
    }

    public String toString() {
        return "authlib-injector [buildNumber=" + this.buildNumber + ", version=" + this.version + "]";
    }
}

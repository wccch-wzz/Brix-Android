package com.brixcore.auth.authlibinjector;

import com.brixcore.util.Logging;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes14.dex */
public class SimpleAuthlibInjectorArtifactProvider implements AuthlibInjectorArtifactProvider {
    private Path location;

    public SimpleAuthlibInjectorArtifactProvider(Path location) {
        this.location = location;
    }

    @Override // com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactProvider
    public AuthlibInjectorArtifactInfo getArtifactInfo() throws IOException {
        return AuthlibInjectorArtifactInfo.from(this.location);
    }

    @Override // com.brixcore.auth.authlibinjector.AuthlibInjectorArtifactProvider
    public Optional<AuthlibInjectorArtifactInfo> getArtifactInfoImmediately() {
        try {
            return Optional.of(getArtifactInfo());
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Bad authlib-injector artifact", (Throwable) e);
            return Optional.empty();
        }
    }
}

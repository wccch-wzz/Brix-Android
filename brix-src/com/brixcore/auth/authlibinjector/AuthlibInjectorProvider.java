package com.brixcore.auth.authlibinjector;

import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.yggdrasil.YggdrasilProvider;
import com.brixcore.util.gson.UUIDTypeAdapter;
import com.brixcore.util.io.NetworkUtils;
import java.net.URL;
import java.util.UUID;

/* JADX INFO: loaded from: classes14.dex */
public class AuthlibInjectorProvider implements YggdrasilProvider {
    private final String apiRoot;

    public AuthlibInjectorProvider(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilProvider
    public URL getAuthenticationURL() throws AuthenticationException {
        return NetworkUtils.toURL(this.apiRoot + "authserver/authenticate");
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilProvider
    public URL getRefreshmentURL() throws AuthenticationException {
        return NetworkUtils.toURL(this.apiRoot + "authserver/refresh");
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilProvider
    public URL getValidationURL() throws AuthenticationException {
        return NetworkUtils.toURL(this.apiRoot + "authserver/validate");
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilProvider
    public URL getInvalidationURL() throws AuthenticationException {
        return NetworkUtils.toURL(this.apiRoot + "authserver/invalidate");
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilProvider
    public URL getSkinUploadURL(UUID uuid) throws UnsupportedOperationException {
        return NetworkUtils.toURL(this.apiRoot + "api/user/profile/" + UUIDTypeAdapter.fromUUID(uuid) + "/skin");
    }

    @Override // com.brixcore.auth.yggdrasil.YggdrasilProvider
    public URL getProfilePropertiesURL(UUID uuid) throws AuthenticationException {
        return NetworkUtils.toURL(this.apiRoot + "sessionserver/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(uuid));
    }

    public String toString() {
        return this.apiRoot;
    }
}

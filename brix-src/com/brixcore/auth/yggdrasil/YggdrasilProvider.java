package com.brixcore.auth.yggdrasil;

import com.brixcore.auth.AuthenticationException;
import java.net.URL;
import java.util.UUID;

/* JADX INFO: loaded from: classes3.dex */
public interface YggdrasilProvider {
    URL getAuthenticationURL() throws AuthenticationException;

    URL getInvalidationURL() throws AuthenticationException;

    URL getProfilePropertiesURL(UUID uuid) throws AuthenticationException;

    URL getRefreshmentURL() throws AuthenticationException;

    URL getSkinUploadURL(UUID uuid) throws UnsupportedOperationException, AuthenticationException;

    URL getValidationURL() throws AuthenticationException;
}

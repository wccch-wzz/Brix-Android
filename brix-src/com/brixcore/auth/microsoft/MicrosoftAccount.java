package com.brixcore.auth.microsoft;

import com.brixcore.auth.AuthInfo;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.CharacterSelector;
import com.brixcore.auth.OAuthAccount;
import com.brixcore.auth.ServerResponseMalformedException;
import com.brixcore.auth.yggdrasil.CompleteGameProfile;
import com.brixcore.auth.yggdrasil.Texture;
import com.brixcore.auth.yggdrasil.TextureType;
import com.brixcore.auth.yggdrasil.YggdrasilService;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.util.Logging;
import com.brixcore.util.fakefx.BindingMapping;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes5.dex */
public class MicrosoftAccount extends OAuthAccount {
    private boolean authenticated;
    protected UUID characterUUID;
    protected final MicrosoftService service;
    private MicrosoftSession session;

    protected MicrosoftAccount(MicrosoftService service, MicrosoftSession session) {
        this.authenticated = false;
        this.service = (MicrosoftService) Objects.requireNonNull(service);
        this.session = (MicrosoftSession) Objects.requireNonNull(session);
        this.characterUUID = (UUID) Objects.requireNonNull(session.getProfile().getId());
    }

    protected MicrosoftAccount(MicrosoftService service, CharacterSelector characterSelector) throws AuthenticationException {
        this.authenticated = false;
        this.service = (MicrosoftService) Objects.requireNonNull(service);
        MicrosoftSession acquiredSession = service.authenticate();
        if (acquiredSession.getProfile() == null) {
            this.session = service.refresh(acquiredSession);
        } else {
            this.session = acquiredSession;
        }
        this.characterUUID = this.session.getProfile().getId();
        this.authenticated = true;
    }

    @Override // com.brixcore.auth.Account
    public String getUsername() {
        return "";
    }

    @Override // com.brixcore.auth.Account
    public String getCharacter() {
        return this.session.getProfile().getName();
    }

    @Override // com.brixcore.auth.Account
    public UUID getUUID() {
        return this.session.getProfile().getId();
    }

    @Override // com.brixcore.auth.Account
    public String getIdentifier() {
        return "microsoft:" + getUUID();
    }

    @Override // com.brixcore.auth.Account
    public AuthInfo logIn() throws AuthenticationException {
        if (!this.authenticated || System.currentTimeMillis() > this.session.getNotAfter()) {
            if (this.service.validate(this.session.getNotAfter(), this.session.getTokenType(), this.session.getAccessToken())) {
                this.authenticated = true;
            } else {
                MicrosoftSession acquiredSession = this.service.refresh(this.session);
                if (!Objects.equals(acquiredSession.getProfile().getId(), this.session.getProfile().getId())) {
                    throw new ServerResponseMalformedException("Selected profile changed");
                }
                this.session = acquiredSession;
                this.authenticated = true;
                invalidate();
            }
        }
        return this.session.toAuthInfo();
    }

    @Override // com.brixcore.auth.OAuthAccount
    public AuthInfo logInWhenCredentialsExpired() throws AuthenticationException {
        MicrosoftSession acquiredSession = this.service.authenticate();
        if (!Objects.equals(this.characterUUID, acquiredSession.getProfile().getId())) {
            throw new OAuthAccount.WrongAccountException(this.characterUUID, acquiredSession.getProfile().getId());
        }
        if (acquiredSession.getProfile() == null) {
            this.session = this.service.refresh(acquiredSession);
        } else {
            this.session = acquiredSession;
        }
        this.authenticated = true;
        invalidate();
        return this.session.toAuthInfo();
    }

    @Override // com.brixcore.auth.Account
    public AuthInfo playOffline() {
        return this.session.toAuthInfo();
    }

    @Override // com.brixcore.auth.Account
    public Map<Object, Object> toStorage() {
        return this.session.toStorage();
    }

    public MicrosoftService getService() {
        return this.service;
    }

    @Override // com.brixcore.auth.Account
    public ObjectBinding<Optional<Map<TextureType, Texture>>> getTextures() {
        return BindingMapping.of(this.service.getProfileRepository().binding(getUUID())).map(new Function() { // from class: com.brixcore.auth.microsoft.MicrosoftAccount$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Optional) obj).flatMap(new Function() { // from class: com.brixcore.auth.microsoft.MicrosoftAccount$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj2) {
                        return MicrosoftAccount.lambda$getTextures$0((CompleteGameProfile) obj2);
                    }
                });
            }
        });
    }

    static /* synthetic */ Optional lambda$getTextures$0(CompleteGameProfile it) {
        try {
            return YggdrasilService.getTextures(it);
        } catch (ServerResponseMalformedException e) {
            Logging.LOG.log(Level.WARNING, "Failed to parse texture payload", (Throwable) e);
            return Optional.empty();
        }
    }

    public void uploadSkin(String model, Path file) throws AuthenticationException {
        Objects.requireNonNull(model);
        Objects.requireNonNull(file);
        logIn();
        MinecraftSkinService.uploadSkin(this.session.getAccessToken(), model, file);
        clearCache();
    }

    public void resetSkin() throws AuthenticationException {
        logIn();
        MinecraftSkinService.resetSkin(this.session.getAccessToken());
        clearCache();
    }

    public void showCape(String capeId) throws AuthenticationException {
        Objects.requireNonNull(capeId);
        logIn();
        MinecraftSkinService.showCape(this.session.getAccessToken(), capeId);
        clearCache();
    }

    public void hideCape() throws AuthenticationException {
        logIn();
        MinecraftSkinService.hideCape(this.session.getAccessToken());
        clearCache();
    }

    public Optional<MicrosoftService.MinecraftProfileResponse> getProfile() throws AuthenticationException {
        logIn();
        return this.service.getCompleteProfile(this.session.getAuthorization());
    }

    @Override // com.brixcore.auth.Account
    public void clearCache() {
        this.authenticated = false;
        this.service.getProfileRepository().invalidate(this.characterUUID);
    }

    @Override // com.brixcore.auth.Account
    public String toString() {
        return "MicrosoftAccount[uuid=" + this.characterUUID + ", name=" + getCharacter() + "]";
    }

    @Override // com.brixcore.auth.Account
    public int hashCode() {
        return this.characterUUID.hashCode();
    }

    @Override // com.brixcore.auth.Account
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MicrosoftAccount that = (MicrosoftAccount) o;
        if (isPortable() == that.isPortable() && this.characterUUID.equals(that.characterUUID)) {
            return true;
        }
        return false;
    }
}

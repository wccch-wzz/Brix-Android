package com.brixcore.auth.yggdrasil;

import com.brixcore.auth.AuthInfo;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.CharacterDeletedException;
import com.brixcore.auth.CharacterSelector;
import com.brixcore.auth.ClassicAccount;
import com.brixcore.auth.CredentialExpiredException;
import com.brixcore.auth.NoCharacterException;
import com.brixcore.auth.ServerResponseMalformedException;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.util.Logging;
import com.brixcore.util.fakefx.BindingMapping;
import com.brixcore.util.gson.UUIDTypeAdapter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes3.dex */
public abstract class YggdrasilAccount extends ClassicAccount {
    private boolean authenticated;
    protected final UUID characterUUID;
    private ObjectBinding<Optional<CompleteGameProfile>> profilePropertiesBinding;
    protected final YggdrasilService service;
    private YggdrasilSession session;
    protected final String username;

    protected YggdrasilAccount(YggdrasilService service, String username, YggdrasilSession session) {
        this.authenticated = false;
        this.service = (YggdrasilService) Objects.requireNonNull(service);
        this.username = (String) Objects.requireNonNull(username);
        this.characterUUID = (UUID) Objects.requireNonNull(session.getSelectedProfile().getId());
        this.session = (YggdrasilSession) Objects.requireNonNull(session);
        addProfilePropertiesListener();
    }

    protected YggdrasilAccount(YggdrasilService service, String username, String password, CharacterSelector selector) throws AuthenticationException {
        this.authenticated = false;
        this.service = (YggdrasilService) Objects.requireNonNull(service);
        this.username = (String) Objects.requireNonNull(username);
        YggdrasilSession acquiredSession = service.authenticate(username, password, randomClientToken());
        if (acquiredSession.getSelectedProfile() == null) {
            if (acquiredSession.getAvailableProfiles() == null || acquiredSession.getAvailableProfiles().isEmpty()) {
                throw new NoCharacterException();
            }
            GameProfile characterToSelect = selector.select(service, acquiredSession.getAvailableProfiles());
            this.session = service.refresh(acquiredSession.getAccessToken(), acquiredSession.getClientToken(), characterToSelect);
        } else {
            this.session = acquiredSession;
        }
        this.characterUUID = this.session.getSelectedProfile().getId();
        this.authenticated = true;
        addProfilePropertiesListener();
    }

    private void addProfilePropertiesListener() {
        this.profilePropertiesBinding = this.service.getProfileRepository().binding(this.characterUUID, true);
        this.profilePropertiesBinding.addListener(new ChangeListener() { // from class: com.brixcore.auth.yggdrasil.YggdrasilAccount$$ExternalSyntheticLambda2
            @Override // com.brixcore.fakefx.beans.value.ChangeListener
            public final void changed(ObservableValue observableValue, Object obj, Object obj2) {
                this.f$0.lambda$addProfilePropertiesListener$0(observableValue, (Optional) obj, (Optional) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addProfilePropertiesListener$0(ObservableValue a, Optional b, Optional c) {
        invalidate();
    }

    @Override // com.brixcore.auth.Account
    public String getUsername() {
        return this.username;
    }

    @Override // com.brixcore.auth.Account
    public String getCharacter() {
        return this.session.getSelectedProfile().getName();
    }

    @Override // com.brixcore.auth.Account
    public UUID getUUID() {
        return this.session.getSelectedProfile().getId();
    }

    @Override // com.brixcore.auth.Account
    public String getIdentifier() {
        return getUsername() + ":" + getUUID();
    }

    @Override // com.brixcore.auth.Account
    public synchronized AuthInfo logIn() throws AuthenticationException {
        if (!this.authenticated) {
            if (this.service.validate(this.session.getAccessToken(), this.session.getClientToken())) {
                this.authenticated = true;
            } else {
                try {
                    YggdrasilSession acquiredSession = this.service.refresh(this.session.getAccessToken(), this.session.getClientToken(), null);
                    if (acquiredSession.getSelectedProfile() == null || !acquiredSession.getSelectedProfile().getId().equals(this.characterUUID)) {
                        throw new ServerResponseMalformedException("Selected profile changed");
                    }
                    this.session = acquiredSession;
                    this.authenticated = true;
                    invalidate();
                } catch (RemoteAuthenticationException e) {
                    if ("ForbiddenOperationException".equals(e.getRemoteName())) {
                        throw new CredentialExpiredException(e);
                    }
                    throw e;
                }
            }
        }
        return this.session.toAuthInfo();
    }

    @Override // com.brixcore.auth.ClassicAccount
    public synchronized AuthInfo logInWithPassword(String password) throws AuthenticationException {
        YggdrasilSession acquiredSession = this.service.authenticate(this.username, password, randomClientToken());
        if (acquiredSession.getSelectedProfile() == null) {
            if (acquiredSession.getAvailableProfiles() == null || acquiredSession.getAvailableProfiles().isEmpty()) {
                throw new CharacterDeletedException();
            }
            GameProfile characterToSelect = acquiredSession.getAvailableProfiles().stream().filter(new Predicate() { // from class: com.brixcore.auth.yggdrasil.YggdrasilAccount$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return this.f$0.lambda$logInWithPassword$1((GameProfile) obj);
                }
            }).findFirst().orElseThrow(new Supplier() { // from class: com.brixcore.auth.yggdrasil.YggdrasilAccount$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return new CharacterDeletedException();
                }
            });
            this.session = this.service.refresh(acquiredSession.getAccessToken(), acquiredSession.getClientToken(), characterToSelect);
        } else {
            if (!acquiredSession.getSelectedProfile().getId().equals(this.characterUUID)) {
                throw new CharacterDeletedException();
            }
            this.session = acquiredSession;
        }
        this.authenticated = true;
        invalidate();
        return this.session.toAuthInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$logInWithPassword$1(GameProfile charatcer) {
        return charatcer.getId().equals(this.characterUUID);
    }

    @Override // com.brixcore.auth.Account
    public AuthInfo playOffline() throws AuthenticationException {
        return this.session.toAuthInfo();
    }

    @Override // com.brixcore.auth.Account
    public Map<Object, Object> toStorage() {
        final Map<Object, Object> storage = new HashMap<>();
        storage.put("username", this.username);
        storage.putAll(this.session.toStorage());
        this.service.getProfileRepository().getImmediately(this.characterUUID).ifPresent(new Consumer() { // from class: com.brixcore.auth.yggdrasil.YggdrasilAccount$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                storage.put("profileProperties", ((CompleteGameProfile) obj).getProperties());
            }
        });
        return storage;
    }

    public YggdrasilService getYggdrasilService() {
        return this.service;
    }

    @Override // com.brixcore.auth.Account
    public void clearCache() {
        this.authenticated = false;
        this.service.getProfileRepository().invalidate(this.characterUUID);
    }

    @Override // com.brixcore.auth.Account
    public ObjectBinding<Optional<Map<TextureType, Texture>>> getTextures() {
        return BindingMapping.of(this.service.getProfileRepository().binding(getUUID())).map(new Function() { // from class: com.brixcore.auth.yggdrasil.YggdrasilAccount$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Optional) obj).flatMap(new Function() { // from class: com.brixcore.auth.yggdrasil.YggdrasilAccount$$ExternalSyntheticLambda5
                    @Override // java.util.function.Function
                    public final Object apply(Object obj2) {
                        return YggdrasilAccount.lambda$getTextures$3((CompleteGameProfile) obj2);
                    }
                });
            }
        });
    }

    static /* synthetic */ Optional lambda$getTextures$3(CompleteGameProfile it) {
        try {
            return YggdrasilService.getTextures(it);
        } catch (ServerResponseMalformedException e) {
            Logging.LOG.log(Level.WARNING, "Failed to parse texture payload", (Throwable) e);
            return Optional.empty();
        }
    }

    public void uploadSkin(String model, Path file) throws UnsupportedOperationException, AuthenticationException {
        this.service.uploadSkin(this.characterUUID, this.session.getAccessToken(), model, file);
    }

    private static String randomClientToken() {
        return UUIDTypeAdapter.fromUUID(UUID.randomUUID());
    }

    @Override // com.brixcore.auth.Account
    public String toString() {
        return "YggdrasilAccount[uuid=" + this.characterUUID + ", username=" + this.username + "]";
    }

    @Override // com.brixcore.auth.Account
    public int hashCode() {
        return this.characterUUID.hashCode();
    }

    @Override // com.brixcore.auth.Account
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != YggdrasilAccount.class) {
            return false;
        }
        YggdrasilAccount another = (YggdrasilAccount) obj;
        if (isPortable() == another.isPortable() && this.characterUUID.equals(another.characterUUID)) {
            return true;
        }
        return false;
    }
}

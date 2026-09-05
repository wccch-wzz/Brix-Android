package com.brixcore.auth;

import com.brixcore.auth.yggdrasil.Texture;
import com.brixcore.auth.yggdrasil.TextureType;
import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.fakefx.beans.property.BooleanProperty;
import com.brixcore.fakefx.beans.property.SimpleBooleanProperty;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.fakefx.ObservableHelper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes8.dex */
public abstract class Account implements Observable {
    private final BooleanProperty portable = new SimpleBooleanProperty(false);
    private final ObservableHelper helper = new ObservableHelper(this);

    public abstract String getCharacter();

    public abstract String getIdentifier();

    public abstract UUID getUUID();

    public abstract String getUsername();

    public abstract AuthInfo logIn() throws AuthenticationException;

    public abstract AuthInfo playOffline() throws AuthenticationException;

    public abstract Map<Object, Object> toStorage();

    public void clearCache() {
    }

    public BooleanProperty portableProperty() {
        return this.portable;
    }

    public boolean isPortable() {
        return this.portable.get();
    }

    public void setPortable(boolean value) {
        this.portable.set(value);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper.addListener(listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper.removeListener(listener);
    }

    protected void invalidate() {
        this.helper.invalidate();
    }

    public ObjectBinding<Optional<Map<TextureType, Texture>>> getTextures() {
        return Bindings.createObjectBinding(new Callable() { // from class: com.brixcore.auth.Account$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return Optional.empty();
            }
        }, new Observable[0]);
    }

    public int hashCode() {
        return Objects.hash(this.portable);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }
        Account another = (Account) obj;
        return isPortable() == another.isPortable();
    }

    public String toString() {
        return new ToStringBuilder(this).append("username", getUsername()).append("character", getCharacter()).append("uuid", getUUID()).append("portable", Boolean.valueOf(isPortable())).toString();
    }
}

package com.brixcore.mod.modinfo;

import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: EmbeddedJarMetadata.kt */
/* JADX INFO: loaded from: classes10.dex */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\u001f\u0010\u0013\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u0014\u001a\u00020\u00052\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016HÖ\u0083\u0004J\n\u0010\u0017\u001a\u00020\u0018HÖ\u0081\u0004J\n\u0010\u0019\u001a\u00020\u0003HÖ\u0081\u0004R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0004\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u001a"}, d2 = {"Lcom/brixcore/mod/modinfo/EmbeddedJarMetadata;", "Lcom/brixcore/util/gson/Validation;", "path", "", "isObfuscated", "", "<init>", "(Ljava/lang/String;Z)V", "getPath", "()Ljava/lang/String;", "setPath", "(Ljava/lang/String;)V", "()Z", "setObfuscated", "(Z)V", "validate", "", "component1", "component2", "copy", "equals", "other", "", "hashCode", "", "toString", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final /* data */ class EmbeddedJarMetadata implements Validation {
    private boolean isObfuscated;
    private String path;

    /* JADX WARN: Multi-variable type inference failed */
    public EmbeddedJarMetadata() {
        this(null, false, 3, 0 == true ? 1 : 0);
    }

    public static /* synthetic */ EmbeddedJarMetadata copy$default(EmbeddedJarMetadata embeddedJarMetadata, String str, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            str = embeddedJarMetadata.path;
        }
        if ((i & 2) != 0) {
            z = embeddedJarMetadata.isObfuscated;
        }
        return embeddedJarMetadata.copy(str, z);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getPath() {
        return this.path;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final boolean getIsObfuscated() {
        return this.isObfuscated;
    }

    public final EmbeddedJarMetadata copy(String path, boolean isObfuscated) {
        return new EmbeddedJarMetadata(path, isObfuscated);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmbeddedJarMetadata)) {
            return false;
        }
        EmbeddedJarMetadata embeddedJarMetadata = (EmbeddedJarMetadata) other;
        return Intrinsics.areEqual(this.path, embeddedJarMetadata.path) && this.isObfuscated == embeddedJarMetadata.isObfuscated;
    }

    public int hashCode() {
        return ((this.path == null ? 0 : this.path.hashCode()) * 31) + Boolean.hashCode(this.isObfuscated);
    }

    public String toString() {
        return "EmbeddedJarMetadata(path=" + this.path + ", isObfuscated=" + this.isObfuscated + ")";
    }

    public EmbeddedJarMetadata(String path, boolean isObfuscated) {
        this.path = path;
        this.isObfuscated = isObfuscated;
    }

    public /* synthetic */ EmbeddedJarMetadata(String str, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : str, (i & 2) != 0 ? false : z);
    }

    public final String getPath() {
        return this.path;
    }

    public final void setPath(String str) {
        this.path = str;
    }

    public final boolean isObfuscated() {
        return this.isObfuscated;
    }

    public final void setObfuscated(boolean z) {
        this.isObfuscated = z;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        Validation.requireNonNull(this.path, "path");
    }
}

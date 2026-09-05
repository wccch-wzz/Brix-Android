package com.brixcore.mod.modinfo;

import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: JarInJarMetadata.kt */
/* JADX INFO: loaded from: classes10.dex */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\b\u0010\n\u001a\u00020\u000bH\u0016J\u0011\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003HÆ\u0003J\u001b\u0010\r\u001a\u00020\u00002\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003HÆ\u0001J\u0014\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011HÖ\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004J\n\u0010\u0014\u001a\u00020\u0015HÖ\u0081\u0004R\"\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\u0006¨\u0006\u0016"}, d2 = {"Lcom/brixcore/mod/modinfo/JarInJarMetadata;", "Lcom/brixcore/util/gson/Validation;", "jars", "", "Lcom/brixcore/mod/modinfo/EmbeddedJarMetadata;", "<init>", "(Ljava/util/List;)V", "getJars", "()Ljava/util/List;", "setJars", "validate", "", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final /* data */ class JarInJarMetadata implements Validation {
    private List<EmbeddedJarMetadata> jars;

    /* JADX WARN: Multi-variable type inference failed */
    public JarInJarMetadata() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ JarInJarMetadata copy$default(JarInJarMetadata jarInJarMetadata, List list, int i, Object obj) {
        if ((i & 1) != 0) {
            list = jarInJarMetadata.jars;
        }
        return jarInJarMetadata.copy(list);
    }

    public final List<EmbeddedJarMetadata> component1() {
        return this.jars;
    }

    public final JarInJarMetadata copy(List<EmbeddedJarMetadata> jars) {
        return new JarInJarMetadata(jars);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return (other instanceof JarInJarMetadata) && Intrinsics.areEqual(this.jars, ((JarInJarMetadata) other).jars);
    }

    public int hashCode() {
        if (this.jars == null) {
            return 0;
        }
        return this.jars.hashCode();
    }

    public String toString() {
        return "JarInJarMetadata(jars=" + this.jars + ")";
    }

    public JarInJarMetadata(List<EmbeddedJarMetadata> list) {
        this.jars = list;
    }

    public /* synthetic */ JarInJarMetadata(List list, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : list);
    }

    public final List<EmbeddedJarMetadata> getJars() {
        return this.jars;
    }

    public final void setJars(List<EmbeddedJarMetadata> list) {
        this.jars = list;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        Validation.requireNonNull(this.jars, "jars");
        List<EmbeddedJarMetadata> list = this.jars;
        Intrinsics.checkNotNull(list);
        for (EmbeddedJarMetadata jar : list) {
            jar.validate();
        }
    }
}

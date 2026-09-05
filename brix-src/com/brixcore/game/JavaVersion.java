package com.brixcore.game;

import com.brixcore.utils.BrixPath;
import com.mio.JavaManager;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: JavaVersion.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\r\u001a\u00020\u00052\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0014\u0010\u0012\u001a\u00020\u00032\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004J\n\u0010\u0014\u001a\u00020\u0011H\u0096\u0080\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000b¨\u0006\u0016"}, d2 = {"Lcom/brixcore/game/JavaVersion;", "", "isAuto", "", "versionName", "", "name", "<init>", "(ZLjava/lang/String;Ljava/lang/String;)V", "()Z", "getVersionName", "()Ljava/lang/String;", "getName", "getJavaPath", "version", "Lcom/brixcore/game/Version;", "getVersion", "", "equals", "other", "hashCode", "Companion", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class JavaVersion {
    public static final JavaVersion JAVA_AUTO = new JavaVersion(true, "1.8", "Auto");
    public static final int JAVA_VERSION_17 = 17;
    public static final int JAVA_VERSION_21 = 21;
    public static final int JAVA_VERSION_25 = 25;
    public static final int JAVA_VERSION_8 = 8;
    private final boolean isAuto;
    private final String name;
    private final String versionName;

    public JavaVersion(boolean isAuto, String versionName, String name) {
        Intrinsics.checkNotNullParameter(versionName, "versionName");
        Intrinsics.checkNotNullParameter(name, "name");
        this.isAuto = isAuto;
        this.versionName = versionName;
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public final String getVersionName() {
        return this.versionName;
    }

    /* JADX INFO: renamed from: isAuto, reason: from getter */
    public final boolean getIsAuto() {
        return this.isAuto;
    }

    public final String getJavaPath(Version version) {
        JavaVersion javaVersion = this.isAuto ? JavaManager.getSuitableJavaVersion(version) : this;
        return BrixPath.JAVA_PATH + "/" + javaVersion.name;
    }

    public final int getVersion() {
        List split = StringsKt.split$default((CharSequence) this.versionName, new String[]{"."}, false, 0, 6, (Object) null);
        if (Intrinsics.areEqual(split.get(0), "1")) {
            return Integer.parseInt((String) split.get(1));
        }
        return Integer.parseInt((String) split.get(0));
    }

    public boolean equals(Object other) {
        if (other instanceof JavaVersion) {
            return Intrinsics.areEqual(this.name, ((JavaVersion) other).name);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

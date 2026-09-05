package com.mio;

import com.brixcore.game.GameJavaVersion;
import com.brixcore.game.JavaVersion;
import com.brixcore.game.Version;
import com.brixcore.util.io.FileUtils;
import com.brixcore.utils.BrixPath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchResult;
import kotlin.text.Regex;

/* JADX INFO: compiled from: JavaManager.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\r\u001a\u00020\u000eH\u0007J\u0010\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0011H\u0007J\u000e\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014J\u0010\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0011H\u0007J\u0012\u0010\u0016\u001a\u00020\u00072\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0007J\u0010\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u0019H\u0007J\u0017\u0010\u001a\u001a\u00020\u00072\b\u0010\u0017\u001a\u0004\u0018\u00010\u0019H\u0002¢\u0006\u0002\u0010\u001bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\"\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t8FX\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\n\u0010\u0003\u001a\u0004\b\u000b\u0010\f¨\u0006\u001c"}, d2 = {"Lcom/mio/JavaManager;", "", "<init>", "()V", "isInit", "", "NO_JAVA_FOUND", "Lcom/brixcore/game/JavaVersion;", "javaList", "", "getJavaList$annotations", "getJavaList", "()Ljava/util/List;", "init", "", "remove", "name", "", "addToJavaVersion", "javaDir", "Ljava/io/File;", "getJavaFromVersionName", "getSuitableJavaVersion", "version", "Lcom/brixcore/game/Version;", "", "findExactOrNextGreater", "(Ljava/lang/Integer;)Lcom/brixcore/game/JavaVersion;", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class JavaManager {
    private static boolean isInit;
    public static final JavaManager INSTANCE = new JavaManager();
    public static final JavaVersion NO_JAVA_FOUND = new JavaVersion(false, "-1", "None");
    private static final List<JavaVersion> javaList = new ArrayList();

    @JvmStatic
    public static /* synthetic */ void getJavaList$annotations() {
    }

    private JavaManager() {
    }

    public static final List<JavaVersion> getJavaList() {
        if (!isInit) {
            init();
        }
        return javaList;
    }

    @JvmStatic
    public static final void init() {
        isInit = true;
        getJavaList().add(new JavaVersion(true, "1.8", "Auto"));
        File[] fileArrListFiles = new File(BrixPath.JAVA_PATH).listFiles();
        if (fileArrListFiles == null) {
            return;
        }
        for (File file : fileArrListFiles) {
            JavaManager javaManager = INSTANCE;
            Intrinsics.checkNotNull(file);
            javaManager.addToJavaVersion(file);
        }
    }

    @JvmStatic
    public static final void remove(final String name) throws IOException {
        Intrinsics.checkNotNullParameter(name, "name");
        File it = new File(BrixPath.JAVA_PATH, name);
        if (it.exists()) {
            FileUtils.deleteDirectory(it);
        }
        List<JavaVersion> javaList2 = getJavaList();
        final Function1 function1 = new Function1() { // from class: com.mio.JavaManager$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(JavaManager.remove$lambda$1(name, (JavaVersion) obj));
            }
        };
        javaList2.removeIf(new Predicate() { // from class: com.mio.JavaManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return JavaManager.remove$lambda$2(function1, obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean remove$lambda$1(String $name, JavaVersion it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return Intrinsics.areEqual(it.getName(), $name);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean remove$lambda$2(Function1 $tmp0, Object p0) {
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    public final boolean addToJavaVersion(final File javaDir) {
        MatchResult match;
        String version;
        Intrinsics.checkNotNullParameter(javaDir, "javaDir");
        if (!javaDir.isDirectory() || !FilesKt.resolve(javaDir, "release").exists() || (match = Regex.find$default(new Regex("JAVA_VERSION=\"([^\"]+)\""), FilesKt.readText$default(FilesKt.resolve(javaDir, "release"), null, 1, null), 0, 2, null)) == null || (version = match.getGroupValues().get(1)) == null) {
            return false;
        }
        List<JavaVersion> javaList2 = getJavaList();
        final Function1 function1 = new Function1() { // from class: com.mio.JavaManager$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(JavaManager.addToJavaVersion$lambda$1(javaDir, (JavaVersion) obj));
            }
        };
        javaList2.removeIf(new Predicate() { // from class: com.mio.JavaManager$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return JavaManager.addToJavaVersion$lambda$2(function1, obj);
            }
        });
        List<JavaVersion> javaList3 = getJavaList();
        String name = javaDir.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        javaList3.add(new JavaVersion(false, version, name));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean addToJavaVersion$lambda$1(File $javaDir, JavaVersion it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return Intrinsics.areEqual(it.getName(), $javaDir.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean addToJavaVersion$lambda$2(Function1 $tmp0, Object p0) {
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    @JvmStatic
    public static final JavaVersion getJavaFromVersionName(String name) {
        Object next;
        JavaVersion it;
        Intrinsics.checkNotNullParameter(name, "name");
        Iterator<T> it2 = getJavaList().iterator();
        do {
            if (!it2.hasNext()) {
                next = null;
                break;
            }
            next = it2.next();
            it = (JavaVersion) next;
        } while (!Intrinsics.areEqual(it.getName(), name));
        JavaVersion javaVersion = (JavaVersion) next;
        return javaVersion == null ? (JavaVersion) CollectionsKt.first((List) getJavaList()) : javaVersion;
    }

    @JvmStatic
    public static final JavaVersion getSuitableJavaVersion(Version version) {
        GameJavaVersion javaVersion;
        return INSTANCE.findExactOrNextGreater((version == null || (javaVersion = version.getJavaVersion()) == null) ? null : Integer.valueOf(javaVersion.getMajorVersion()));
    }

    @JvmStatic
    public static final JavaVersion getSuitableJavaVersion(int version) {
        return INSTANCE.findExactOrNextGreater(Integer.valueOf(version));
    }

    private final JavaVersion findExactOrNextGreater(Integer version) {
        JavaVersion javaVersion;
        if (version == null) {
            return getJavaFromVersionName("jre8");
        }
        JavaVersion exact = null;
        JavaVersion closestGreater = null;
        Iterable $this$filter$iv = getJavaList();
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            JavaVersion it = (JavaVersion) element$iv$iv;
            if (!Intrinsics.areEqual(it.getName(), "Auto")) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        for (JavaVersion java : (List) destination$iv$iv) {
            int version2 = java.getVersion();
            if (version != null && version2 == version.intValue()) {
                exact = java;
                break;
            }
            if (java.getVersion() > version.intValue()) {
                if (closestGreater == null || java.getVersion() < closestGreater.getVersion()) {
                    javaVersion = java;
                } else {
                    javaVersion = closestGreater;
                }
                closestGreater = javaVersion;
            }
        }
        if (exact == null) {
            return closestGreater == null ? NO_JAVA_FOUND : closestGreater;
        }
        return exact;
    }
}

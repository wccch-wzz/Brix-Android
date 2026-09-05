package com.brixcore.data;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Renderer.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000e\b\u0086\b\u0018\u0000 /2\u00020\u0001:\u0001/Bk\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u000e\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t\u0012\u000e\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003¢\u0006\u0004\b\u000e\u0010\u000fJ\u0006\u0010\u001c\u001a\u00020\u0003J\u000e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u000b\u001a\u00020\u0003J\u0014\u0010\u001f\u001a\u00020\u001e2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004J\n\u0010!\u001a\u00020\"H\u0096\u0080\u0004J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\u0003HÆ\u0003J\t\u0010'\u001a\u00020\u0003HÆ\u0003J\u0011\u0010(\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\tHÆ\u0003J\u0011\u0010)\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\tHÆ\u0003J\t\u0010*\u001a\u00020\u0003HÆ\u0003J\t\u0010+\u001a\u00020\u0003HÆ\u0003J\t\u0010,\u001a\u00020\u0003HÆ\u0003J}\u0010-\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\u0010\b\u0002\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t2\u0010\b\u0002\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t2\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u0003HÆ\u0001J\n\u0010.\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0019\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0019\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011R\u0011\u0010\f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0011R\u0011\u0010\r\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0011¨\u00060"}, d2 = {"Lcom/brixcore/data/Renderer;", "", "name", "", "des", "glName", "eglName", "path", "boatEnv", "", "pojavEnv", "id", "minMCver", "maxMCver", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getName", "()Ljava/lang/String;", "getDes", "getGlName", "getEglName", "getPath", "getBoatEnv", "()Ljava/util/List;", "getPojavEnv", "getId", "getMinMCver", "getMaxMCver", "getGLPath", "isEqual", "", "equals", "other", "hashCode", "", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "copy", "toString", "Companion", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final /* data */ class Renderer {
    public static final String ID_FREEDRENO = "8d427e6c-9d22-2d19-db0c-3b9ac2c1543f";
    public static final String ID_GL4ES = "f7e985d8-6d4c-f63c-d9f1-06074dab823a";
    public static final String ID_NGGL4ES = "e7b90ed6-e518-4d4e-93dc-5c7133cd5b31";
    public static final String ID_VGPU = "0fb718e4-64e3-83d4-a974-8204ea1d9f9f";
    public static final String ID_VIRGL = "417a7a93-d9b4-98b9-ec6e-1ea400259c1f";
    public static final String ID_ZINK = "18d93f17-ff53-a319-fa61-58709a77bf87";
    private final List<String> boatEnv;
    private final String des;
    private final String eglName;
    private final String glName;
    private final String id;
    private final String maxMCver;
    private final String minMCver;
    private final String name;
    private final String path;
    private final List<String> pojavEnv;

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ Renderer copy$default(Renderer renderer, String str, String str2, String str3, String str4, String str5, List list, List list2, String str6, String str7, String str8, int i, Object obj) {
        if ((i & 1) != 0) {
            str = renderer.name;
        }
        if ((i & 2) != 0) {
            str2 = renderer.des;
        }
        if ((i & 4) != 0) {
            str3 = renderer.glName;
        }
        if ((i & 8) != 0) {
            str4 = renderer.eglName;
        }
        if ((i & 16) != 0) {
            str5 = renderer.path;
        }
        if ((i & 32) != 0) {
            list = renderer.boatEnv;
        }
        if ((i & 64) != 0) {
            list2 = renderer.pojavEnv;
        }
        if ((i & 128) != 0) {
            str6 = renderer.id;
        }
        if ((i & 256) != 0) {
            str7 = renderer.minMCver;
        }
        if ((i & 512) != 0) {
            str8 = renderer.maxMCver;
        }
        String str9 = str7;
        String str10 = str8;
        List list3 = list2;
        String str11 = str6;
        String str12 = str5;
        List list4 = list;
        return renderer.copy(str, str2, str3, str4, str12, list4, list3, str11, str9, str10);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getName() {
        return this.name;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final String getMaxMCver() {
        return this.maxMCver;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getDes() {
        return this.des;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getGlName() {
        return this.glName;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getEglName() {
        return this.eglName;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String getPath() {
        return this.path;
    }

    public final List<String> component6() {
        return this.boatEnv;
    }

    public final List<String> component7() {
        return this.pojavEnv;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final String getId() {
        return this.id;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final String getMinMCver() {
        return this.minMCver;
    }

    public final Renderer copy(String name, String des, String glName, String eglName, String path, List<String> boatEnv, List<String> pojavEnv, String id, String minMCver, String maxMCver) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(des, "des");
        Intrinsics.checkNotNullParameter(glName, "glName");
        Intrinsics.checkNotNullParameter(eglName, "eglName");
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(minMCver, "minMCver");
        Intrinsics.checkNotNullParameter(maxMCver, "maxMCver");
        return new Renderer(name, des, glName, eglName, path, boatEnv, pojavEnv, id, minMCver, maxMCver);
    }

    public String toString() {
        return "Renderer(name=" + this.name + ", des=" + this.des + ", glName=" + this.glName + ", eglName=" + this.eglName + ", path=" + this.path + ", boatEnv=" + this.boatEnv + ", pojavEnv=" + this.pojavEnv + ", id=" + this.id + ", minMCver=" + this.minMCver + ", maxMCver=" + this.maxMCver + ")";
    }

    public Renderer(String name, String des, String glName, String eglName, String path, List<String> list, List<String> list2, String id, String minMCver, String maxMCver) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(des, "des");
        Intrinsics.checkNotNullParameter(glName, "glName");
        Intrinsics.checkNotNullParameter(eglName, "eglName");
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(minMCver, "minMCver");
        Intrinsics.checkNotNullParameter(maxMCver, "maxMCver");
        this.name = name;
        this.des = des;
        this.glName = glName;
        this.eglName = eglName;
        this.path = path;
        this.boatEnv = list;
        this.pojavEnv = list2;
        this.id = id;
        this.minMCver = minMCver;
        this.maxMCver = maxMCver;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public /* synthetic */ Renderer(String str, String str2, String str3, String str4, String str5, List list, List list2, String str6, String str7, String str8, int i, DefaultConstructorMarker defaultConstructorMarker) {
        String str9;
        str7 = (i & 256) != 0 ? "" : str7;
        if ((i & 512) == 0) {
            str9 = str8;
        } else {
            str9 = "";
        }
        this(str, str2, str3, str4, str5, list, list2, str6, str7, str9);
    }

    public final String getName() {
        return this.name;
    }

    public final String getDes() {
        return this.des;
    }

    public final String getGlName() {
        return this.glName;
    }

    public final String getEglName() {
        return this.eglName;
    }

    public final String getPath() {
        return this.path;
    }

    public final List<String> getBoatEnv() {
        return this.boatEnv;
    }

    public final List<String> getPojavEnv() {
        return this.pojavEnv;
    }

    public final String getId() {
        return this.id;
    }

    public final String getMinMCver() {
        return this.minMCver;
    }

    public final String getMaxMCver() {
        return this.maxMCver;
    }

    public final String getGLPath() {
        if (this.path.length() == 0) {
            return this.glName;
        }
        return this.path + "/" + this.glName;
    }

    public final boolean isEqual(String id) {
        Intrinsics.checkNotNullParameter(id, "id");
        return Intrinsics.areEqual(this.id, id);
    }

    public boolean equals(Object other) {
        if (other instanceof Renderer) {
            return Intrinsics.areEqual(this.id, ((Renderer) other).id);
        }
        return false;
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}

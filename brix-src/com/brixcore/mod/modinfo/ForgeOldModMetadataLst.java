package com.brixcore.mod.modinfo;

import com.android.tools.r8.RecordTag;
import com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticRecord1;
import com.brixcore.mod.curse.CurseAddon$LatestFileHash$$ExternalSyntheticRecord0;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes10.dex */
public final class ForgeOldModMetadataLst extends RecordTag {
    private final List<ForgeOldModMetadata> modList;
    private final int modListVersion;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof ForgeOldModMetadataLst)) {
            return false;
        }
        ForgeOldModMetadataLst forgeOldModMetadataLst = (ForgeOldModMetadataLst) obj;
        return this.modListVersion == forgeOldModMetadataLst.modListVersion && Objects.equals(this.modList, forgeOldModMetadataLst.modList);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{Integer.valueOf(this.modListVersion), this.modList};
    }

    public ForgeOldModMetadataLst(int modListVersion, List<ForgeOldModMetadata> modList) {
        this.modListVersion = modListVersion;
        this.modList = modList;
    }

    public final boolean equals(Object o) {
        return $record$equals(o);
    }

    public final int hashCode() {
        return CurseAddon$LatestFileHash$$ExternalSyntheticRecord0.m(this.modListVersion, this.modList);
    }

    public List<ForgeOldModMetadata> modList() {
        return this.modList;
    }

    public int modListVersion() {
        return this.modListVersion;
    }

    public final String toString() {
        return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ForgeOldModMetadataLst.class, "modListVersion;modList");
    }
}

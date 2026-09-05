package com.brixcore.mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes2.dex */
public final /* synthetic */ class ModManager$$ExternalSyntheticBackport1 {
    public static /* synthetic */ List m(Collection collection) {
        ArrayList arrayList = new ArrayList(collection.size());
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            arrayList.add(Objects.requireNonNull(it.next()));
        }
        return Collections.unmodifiableList(arrayList);
    }
}

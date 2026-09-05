package com.brixcore.download;

import java.util.function.Predicate;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes14.dex */
public final /* synthetic */ class LibraryAnalyzer$$ExternalSyntheticLambda8 implements Predicate {
    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((LibraryAnalyzer.LibraryType) obj).isModLoader();
    }
}

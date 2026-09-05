package com.brixcore.fakefx.collections;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class TrackableObservableList<T> extends ObservableListWrapper<T> {
    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: onChanged, reason: merged with bridge method [inline-methods] */
    public abstract void lambda$new$0(ListChangeListener.Change<T> change);

    public TrackableObservableList(List<T> list) {
        super(list);
    }

    public TrackableObservableList() {
        super(new ArrayList());
        addListener(new ListChangeListener() { // from class: com.brixcore.fakefx.collections.TrackableObservableList$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.collections.ListChangeListener
            public final void onChanged(ListChangeListener.Change change) {
                this.f$0.lambda$new$0(change);
            }
        });
    }
}

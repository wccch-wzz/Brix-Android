package com.brixcore.fakefx.collections.transformation;

import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableListBase;
import com.brixcore.fakefx.collections.WeakListChangeListener;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public abstract class TransformationList<E, F> extends ObservableListBase<E> implements ObservableList<E> {
    private ObservableList<? extends F> source;
    private ListChangeListener<F> sourceListener;

    public abstract int getSourceIndex(int i);

    public abstract int getViewIndex(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: sourceChanged, reason: merged with bridge method [inline-methods] */
    public abstract void lambda$getListener$0(ListChangeListener.Change<? extends F> change);

    protected TransformationList(ObservableList<? extends F> source) {
        if (source == null) {
            throw new NullPointerException();
        }
        this.source = source;
        source.addListener(new WeakListChangeListener(getListener()));
    }

    public final ObservableList<? extends F> getSource() {
        return this.source;
    }

    public final boolean isInTransformationChain(ObservableList<?> list) {
        if (this.source == list) {
            return true;
        }
        List<?> currentSource = this.source;
        while (currentSource instanceof TransformationList) {
            currentSource = ((TransformationList) currentSource).source;
            if (currentSource == list) {
                return true;
            }
        }
        return false;
    }

    private ListChangeListener<F> getListener() {
        if (this.sourceListener == null) {
            this.sourceListener = new ListChangeListener() { // from class: com.brixcore.fakefx.collections.transformation.TransformationList$$ExternalSyntheticLambda0
                @Override // com.brixcore.fakefx.collections.ListChangeListener
                public final void onChanged(ListChangeListener.Change change) {
                    this.f$0.lambda$getListener$0(change);
                }
            };
        }
        return this.sourceListener;
    }

    public final int getSourceIndexFor(ObservableList<?> list, int index) {
        if (!isInTransformationChain(list)) {
            throw new IllegalArgumentException("Provided list is not in the transformation chain of thistransformation list");
        }
        List<?> currentSource = this.source;
        int idx = getSourceIndex(index);
        while (currentSource != list && (currentSource instanceof TransformationList)) {
            TransformationList tSource = (TransformationList) currentSource;
            idx = tSource.getSourceIndex(idx);
            currentSource = tSource.source;
        }
        return idx;
    }
}

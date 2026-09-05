package com.brixcore.util.fakefx;

import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.WeakListChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes15.dex */
public final class MappedObservableList {
    private MappedObservableList() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class MappedObservableListUpdater<T, U> implements ListChangeListener<T> {
        private List<U> buffer;
        private Function<T, U> mapper;
        private ObservableList<T> origin;
        private ObservableList<U> target;

        MappedObservableListUpdater(ObservableList<T> origin, ObservableList<U> target, Function<T, U> mapper) {
            this.origin = origin;
            this.target = target;
            this.mapper = mapper;
            this.buffer = new ArrayList(target);
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener
        public void onChanged(ListChangeListener.Change<? extends T> change) {
            IdentityHashMap identityHashMap = new IdentityHashMap();
            while (change.next()) {
                int from = change.getFrom();
                int to = change.getTo();
                if (change.wasPermutated()) {
                    Object[] objArr = new Object[to - from];
                    for (int i = 0; i < objArr.length; i++) {
                        objArr[i] = this.buffer.get(from + i);
                    }
                    for (int i2 = from; i2 < to; i2++) {
                        this.buffer.set(change.getPermutation(i2), (U) objArr[i2 - from]);
                    }
                } else {
                    if (change.wasRemoved()) {
                        List<? extends T> removed = change.getRemoved();
                        List<U> listSubList = this.buffer.subList(from, removed.size() + from);
                        for (int i3 = 0; i3 < listSubList.size(); i3++) {
                            pushCache(identityHashMap, removed.get(i3), listSubList.get(i3));
                        }
                        listSubList.clear();
                    }
                    if (change.wasAdded()) {
                        Object[] objArr2 = new Object[to - from];
                        for (int i4 = 0; i4 < objArr2.length; i4++) {
                            objArr2[i4] = map(identityHashMap, this.origin.get(from + i4));
                        }
                        this.buffer.addAll(from, Arrays.asList(objArr2));
                    }
                }
            }
            this.target.setAll(this.buffer);
        }

        static /* synthetic */ LinkedList lambda$pushCache$0(Object any) {
            return new LinkedList();
        }

        private void pushCache(Map<T, LinkedList<U>> cache, T key, U value) {
            cache.computeIfAbsent(key, new Function() { // from class: com.brixcore.util.fakefx.MappedObservableList$MappedObservableListUpdater$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return MappedObservableList.MappedObservableListUpdater.lambda$pushCache$0(obj);
                }
            }).push(value);
        }

        private U map(Map<T, LinkedList<U>> cache, T key) {
            LinkedList<U> stack = cache.get(key);
            if (stack != null && !stack.isEmpty()) {
                return stack.pop();
            }
            return this.mapper.apply(key);
        }
    }

    public static <T, U> ObservableList<U> create(ObservableList<T> origin, Function<T, U> mapper) {
        ObservableList<U> target = (ObservableList) origin.stream().map(mapper).collect(Collectors.toCollection(new Supplier() { // from class: com.brixcore.util.fakefx.MappedObservableList$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return FXCollections.observableArrayList();
            }
        }));
        ListChangeListener<T> listener = new MappedObservableListUpdater<>(origin, target, mapper);
        target.addListener(new ReferenceHolder(listener));
        origin.addListener(new WeakListChangeListener(listener));
        return FXCollections.unmodifiableObservableList(target);
    }
}

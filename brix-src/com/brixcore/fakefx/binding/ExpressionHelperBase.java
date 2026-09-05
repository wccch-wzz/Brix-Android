package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.WeakListener;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes6.dex */
public class ExpressionHelperBase {
    static /* synthetic */ boolean lambda$trim$0(Object t) {
        return (t instanceof WeakListener) && ((WeakListener) t).wasGarbageCollected();
    }

    protected static int trim(int size, Object[] listeners) {
        Predicate<Object> p = new Predicate() { // from class: com.brixcore.fakefx.binding.ExpressionHelperBase$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ExpressionHelperBase.lambda$trim$0(obj);
            }
        };
        int index = 0;
        while (index < size && !p.test(listeners[index])) {
            index++;
        }
        if (index < size) {
            for (int src = index + 1; src < size; src++) {
                if (!p.test(listeners[src])) {
                    listeners[index] = listeners[src];
                    index++;
                }
            }
            size = index;
            while (index < size) {
                listeners[index] = null;
                index++;
            }
        }
        return size;
    }
}

package com.brixcore.util.fakefx;

import com.brixcore.util.function.ExceptionalFunction;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes15.dex */
public final /* synthetic */ class SafeStringConverter$$ExternalSyntheticLambda1 implements ExceptionalFunction {
    @Override // com.brixcore.util.function.ExceptionalFunction
    public final Object apply(Object obj) {
        return Double.valueOf(Double.parseDouble((String) obj));
    }
}

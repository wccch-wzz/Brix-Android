package com.brixcore.fakefx.property;

import com.brixcore.fakefx.reflect.MethodUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/* JADX INFO: loaded from: classes15.dex */
public class MethodHelper {
    private static final boolean logAccessErrors = ((Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.property.MethodHelper$$ExternalSyntheticLambda0
        @Override // java.security.PrivilegedAction
        public final Object run() {
            return Boolean.valueOf(Boolean.getBoolean("sun.reflect.debugModuleAccessChecks"));
        }
    })).booleanValue();

    public static Object invoke(Method m, Object obj, Object[] params) throws IllegalAccessException, InvocationTargetException {
        m.getDeclaringClass();
        return MethodUtil.invoke(m, obj, params);
    }

    private MethodHelper() {
    }
}

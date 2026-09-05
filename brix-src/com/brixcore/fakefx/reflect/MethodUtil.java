package com.brixcore.fakefx.reflect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public final class MethodUtil extends SecureClassLoader {
    private static final String MISC_PKG = "com.sun.javafx.reflect.";
    private static final String TRAMPOLINE = "com.sun.javafx.reflect.Trampoline";
    private static final Method bounce = getTrampoline();

    private MethodUtil() {
    }

    public static Method getMethod(Class<?> cls, String name, Class<?>[] args) throws NoSuchMethodException {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getMethod(name, args);
    }

    public static Method[] getMethods(Class<?> cls) {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getMethods();
    }

    static Method[] getPublicMethods(Class<?> cls) {
        if (System.getSecurityManager() == null) {
            return cls.getMethods();
        }
        Map<Signature, Method> sigs = new HashMap<>();
        while (cls != null) {
            boolean done = getInternalPublicMethods(cls, sigs);
            if (done) {
                break;
            }
            getInterfaceMethods(cls, sigs);
            cls = cls.getSuperclass();
        }
        return (Method[]) sigs.values().toArray(new Method[sigs.size()]);
    }

    private static void getInterfaceMethods(Class<?> cls, Map<Signature, Method> sigs) {
        Class<?>[] intfs = cls.getInterfaces();
        for (Class<?> intf : intfs) {
            boolean done = getInternalPublicMethods(intf, sigs);
            if (!done) {
                getInterfaceMethods(intf, sigs);
            }
        }
    }

    private static boolean getInternalPublicMethods(Class<?> cls, Map<Signature, Method> sigs) {
        try {
            if (!Modifier.isPublic(cls.getModifiers()) || !ReflectUtil.isPackageAccessible(cls)) {
                return false;
            }
            Method[] methods = cls.getMethods();
            boolean done = true;
            for (Method method : methods) {
                Class<?> dc = method.getDeclaringClass();
                if (!Modifier.isPublic(dc.getModifiers())) {
                    done = false;
                    break;
                }
            }
            if (done) {
                for (Method method2 : methods) {
                    addMethod(sigs, method2);
                }
            } else {
                for (int i = 0; i < methods.length; i++) {
                    Class<?> dc2 = methods[i].getDeclaringClass();
                    if (cls.equals(dc2)) {
                        addMethod(sigs, methods[i]);
                    }
                }
            }
            return done;
        } catch (SecurityException e) {
            return false;
        }
    }

    private static void addMethod(Map<Signature, Method> sigs, Method method) {
        Signature signature = new Signature(method);
        if (!sigs.containsKey(signature)) {
            sigs.put(signature, method);
        } else if (!method.getDeclaringClass().isInterface()) {
            Method old = sigs.get(signature);
            if (old.getDeclaringClass().isInterface()) {
                sigs.put(signature, method);
            }
        }
    }

    private static class Signature {
        private final Class<?>[] argClasses;
        private final int hashCode;
        private final String methodName;

        Signature(Method m) {
            this.methodName = m.getName();
            this.argClasses = m.getParameterTypes();
            this.hashCode = this.methodName.hashCode() + Arrays.hashCode(this.argClasses);
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o2) {
            if (this == o2) {
                return true;
            }
            Signature that = (Signature) o2;
            if (!this.methodName.equals(that.methodName) || this.argClasses.length != that.argClasses.length) {
                return false;
            }
            for (int i = 0; i < this.argClasses.length; i++) {
                if (this.argClasses[i] != that.argClasses[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    public static Object invoke(Method m, Object obj, Object[] params) throws IllegalAccessException, InvocationTargetException {
        try {
            return bounce.invoke(null, m, obj, params);
        } catch (IllegalAccessException iae) {
            throw new Error("Unexpected invocation error", iae);
        } catch (InvocationTargetException ie) {
            Throwable t = ie.getCause();
            if (t instanceof InvocationTargetException) {
                throw ((InvocationTargetException) t);
            }
            if (t instanceof IllegalAccessException) {
                throw ((IllegalAccessException) t);
            }
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            }
            if (t instanceof Error) {
                throw ((Error) t);
            }
            throw new Error("Unexpected invocation error", t);
        }
    }

    private static Method getTrampoline() {
        try {
            return (Method) AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() { // from class: com.brixcore.fakefx.reflect.MethodUtil.1
                @Override // java.security.PrivilegedExceptionAction
                public Method run() throws Exception {
                    Class<?> t = MethodUtil.getTrampolineClass();
                    Class<?>[] types = {Method.class, Object.class, Object[].class};
                    Method b = t.getDeclaredMethod("invoke", types);
                    b.setAccessible(true);
                    return b;
                }
            });
        } catch (Exception e) {
            throw new InternalError("bouncer cannot be found", e);
        }
    }

    private Class<?> defineClass(String name, byte[] b) throws IOException {
        CodeSource cs = new CodeSource((URL) null, (Certificate[]) null);
        if (!name.equals(TRAMPOLINE)) {
            throw new IOException("MethodUtil: bad name " + name);
        }
        return defineClass(name, b, 0, b.length, cs);
    }

    @Override // java.security.SecureClassLoader
    protected PermissionCollection getPermissions(CodeSource codesource) {
        PermissionCollection perms = super.getPermissions(codesource);
        perms.add(new AllPermission());
        return perms;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Class<?> getTrampolineClass() {
        try {
            return Class.forName(TRAMPOLINE, true, new MethodUtil());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

package com.brixcore.fakefx.reflect;

import java.lang.reflect.Proxy;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes2.dex */
public final class ReflectUtil {
    public static final String PROXY_PACKAGE = "com.sun.proxy";

    private ReflectUtil() {
    }

    public static void checkPackageAccess(Class<?> clazz) {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            privateCheckPackageAccess(s, clazz);
        }
    }

    private static void privateCheckPackageAccess(SecurityManager s, Class<?> clazz) {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        String pkg = ((Package) Objects.requireNonNull(clazz.getPackage())).getName();
        if (pkg != null && !pkg.isEmpty()) {
            s.checkPackageAccess(pkg);
        }
        if (isNonPublicProxyClass(clazz)) {
            privateCheckProxyPackageAccess(s, clazz);
        }
    }

    public static void checkPackageAccess(String name) {
        int b;
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            String cname = name.replace(IOUtils.DIR_SEPARATOR_UNIX, '.');
            if (cname.startsWith("[") && (b = cname.lastIndexOf(91) + 2) > 1 && b < cname.length()) {
                cname = cname.substring(b);
            }
            int i = cname.lastIndexOf(46);
            if (i != -1) {
                s.checkPackageAccess(cname.substring(0, i));
            }
        }
    }

    public static boolean isPackageAccessible(Class<?> clazz) {
        try {
            checkPackageAccess(clazz);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    private static void privateCheckProxyPackageAccess(SecurityManager s, Class<?> clazz) {
        if (Proxy.isProxyClass(clazz)) {
            for (Class<?> intf : clazz.getInterfaces()) {
                privateCheckPackageAccess(s, intf);
            }
        }
    }

    public static boolean isNonPublicProxyClass(Class<?> cls) {
        if (!Proxy.isProxyClass(cls)) {
            return false;
        }
        String pkg = ((Package) Objects.requireNonNull(cls.getPackage())).getName();
        return pkg == null || !pkg.startsWith(PROXY_PACKAGE);
    }
}

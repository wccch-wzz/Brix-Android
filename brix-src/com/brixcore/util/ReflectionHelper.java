package com.brixcore.util;

import java.util.function.Predicate;

/* JADX INFO: loaded from: classes11.dex */
public final class ReflectionHelper {
    private ReflectionHelper() {
    }

    public static StackTraceElement getCaller(Predicate<String> packageFilter) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = elements[2];
        for (int i = 3; i < elements.length; i++) {
            if (packageFilter.test(StringUtils.substringBeforeLast(elements[i].getClassName(), '.')) && !caller.getClassName().equals(elements[i].getClassName())) {
                return elements[i];
            }
        }
        return caller;
    }
}

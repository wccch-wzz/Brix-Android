package com.brixcore.fakefx.util;

import com.brixcore.fakefx.beans.NamedArg;
import java.io.Serializable;

/* JADX INFO: loaded from: classes2.dex */
public class Duration implements Comparable<Duration>, Serializable {
    private final double millis;
    public static final Duration ZERO = new Duration(0.0d);
    public static final Duration ONE = new Duration(1.0d);
    public static final Duration INDEFINITE = new Duration(Double.POSITIVE_INFINITY);
    public static final Duration UNKNOWN = new Duration(Double.NaN);

    public static Duration valueOf(String time) {
        int index = -1;
        for (int i = 0; i < time.length(); i++) {
            char c = time.charAt(i);
            if (!Character.isDigit(c) && c != '.' && c != '-') {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("The time parameter must have a suffix of [ms|s|m|h]");
        }
        double value = Double.parseDouble(time.substring(0, index));
        String suffix = time.substring(index);
        if ("ms".equals(suffix)) {
            return millis(value);
        }
        if ("s".equals(suffix)) {
            return seconds(value);
        }
        if ("m".equals(suffix)) {
            return minutes(value);
        }
        if ("h".equals(suffix)) {
            return hours(value);
        }
        throw new IllegalArgumentException("The time parameter must have a suffix of [ms|s|m|h]");
    }

    public static Duration millis(double ms) {
        if (ms == 0.0d) {
            return ZERO;
        }
        if (ms == 1.0d) {
            return ONE;
        }
        if (ms == Double.POSITIVE_INFINITY) {
            return INDEFINITE;
        }
        if (Double.isNaN(ms)) {
            return UNKNOWN;
        }
        return new Duration(ms);
    }

    public static Duration seconds(double s) {
        if (s == 0.0d) {
            return ZERO;
        }
        if (s == Double.POSITIVE_INFINITY) {
            return INDEFINITE;
        }
        if (Double.isNaN(s)) {
            return UNKNOWN;
        }
        return new Duration(1000.0d * s);
    }

    public static Duration minutes(double m) {
        if (m == 0.0d) {
            return ZERO;
        }
        if (m == Double.POSITIVE_INFINITY) {
            return INDEFINITE;
        }
        if (Double.isNaN(m)) {
            return UNKNOWN;
        }
        return new Duration(60000.0d * m);
    }

    public static Duration hours(double h) {
        if (h == 0.0d) {
            return ZERO;
        }
        if (h == Double.POSITIVE_INFINITY) {
            return INDEFINITE;
        }
        if (Double.isNaN(h)) {
            return UNKNOWN;
        }
        return new Duration(3600000.0d * h);
    }

    public Duration(@NamedArg("millis") double millis) {
        this.millis = millis;
    }

    public double toMillis() {
        return this.millis;
    }

    public double toSeconds() {
        return this.millis / 1000.0d;
    }

    public double toMinutes() {
        return this.millis / 60000.0d;
    }

    public double toHours() {
        return this.millis / 3600000.0d;
    }

    public Duration add(Duration other) {
        return millis(this.millis + other.millis);
    }

    public Duration subtract(Duration other) {
        return millis(this.millis - other.millis);
    }

    @Deprecated
    public Duration multiply(Duration other) {
        return millis(this.millis * other.millis);
    }

    public Duration multiply(double n) {
        return millis(this.millis * n);
    }

    public Duration divide(double n) {
        return millis(this.millis / n);
    }

    @Deprecated
    public Duration divide(Duration other) {
        return millis(this.millis / other.millis);
    }

    public Duration negate() {
        return millis(-this.millis);
    }

    public boolean isIndefinite() {
        return this.millis == Double.POSITIVE_INFINITY;
    }

    public boolean isUnknown() {
        return Double.isNaN(this.millis);
    }

    public boolean lessThan(Duration other) {
        return this.millis < other.millis;
    }

    public boolean lessThanOrEqualTo(Duration other) {
        return this.millis <= other.millis;
    }

    public boolean greaterThan(Duration other) {
        return this.millis > other.millis;
    }

    public boolean greaterThanOrEqualTo(Duration other) {
        return this.millis >= other.millis;
    }

    public String toString() {
        if (isIndefinite()) {
            return "INDEFINITE";
        }
        return isUnknown() ? "UNKNOWN" : this.millis + " ms";
    }

    @Override // java.lang.Comparable
    public int compareTo(Duration d) {
        return Double.compare(this.millis, d.millis);
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof Duration) && this.millis == ((Duration) obj).millis);
    }

    public int hashCode() {
        long bits = Double.doubleToLongBits(this.millis);
        return (int) ((bits >>> 32) ^ bits);
    }
}

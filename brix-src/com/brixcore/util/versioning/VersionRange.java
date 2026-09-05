package com.brixcore.util.versioning;

import java.lang.Comparable;
import java.util.Objects;

/* JADX INFO: loaded from: classes11.dex */
public final class VersionRange<T extends Comparable<T>> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final T maximum;
    private final T minimum;
    private static final VersionRange<?> EMPTY = new VersionRange<>(null, null);
    private static final VersionRange<?> ALL = new VersionRange<>(null, null);

    public static <T extends Comparable<T>> VersionRange<T> empty() {
        return (VersionRange<T>) EMPTY;
    }

    public static <T extends Comparable<T>> VersionRange<T> all() {
        return (VersionRange<T>) ALL;
    }

    public static <T extends Comparable<T>> VersionRange<T> between(T minimum, T maximum) {
        if (minimum.compareTo(maximum) > 0) {
            throw new AssertionError();
        }
        return new VersionRange<>(minimum, maximum);
    }

    public static <T extends Comparable<T>> VersionRange<T> atLeast(T minimum) {
        if (minimum == null) {
            throw new AssertionError();
        }
        return new VersionRange<>(minimum, null);
    }

    public static <T extends Comparable<T>> VersionRange<T> atMost(T maximum) {
        if (maximum == null) {
            throw new AssertionError();
        }
        return new VersionRange<>(null, maximum);
    }

    private VersionRange(T minimum, T maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public T getMinimum() {
        return this.minimum;
    }

    public T getMaximum() {
        return this.maximum;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isAll() {
        return !isEmpty() && this.minimum == null && this.maximum == null;
    }

    public boolean contains(T versionNumber) {
        if (versionNumber == null || isEmpty()) {
            return false;
        }
        if (isAll()) {
            return true;
        }
        if (this.minimum == null || this.minimum.compareTo(versionNumber) <= 0) {
            return this.maximum == null || this.maximum.compareTo(versionNumber) >= 0;
        }
        return false;
    }

    public boolean isOverlappedBy(VersionRange<T> that) {
        if (isEmpty() || that.isEmpty()) {
            return false;
        }
        if (isAll() || that.isAll()) {
            return true;
        }
        if (this.minimum == null) {
            return that.minimum == null || that.minimum.compareTo(this.maximum) <= 0;
        }
        if (this.maximum == null) {
            return that.maximum == null || that.maximum.compareTo(this.minimum) >= 0;
        }
        return that.contains(this.minimum) || that.contains(this.maximum) || (that.minimum != null && contains(that.minimum));
    }

    public VersionRange<T> intersectionWith(VersionRange<T> that) {
        T newMinimum;
        T newMaximum;
        if (isAll()) {
            return that;
        }
        if (that.isAll()) {
            return this;
        }
        if (!isOverlappedBy(that)) {
            return empty();
        }
        if (this.minimum == null) {
            newMinimum = that.minimum;
        } else {
            T newMinimum2 = that.minimum;
            if (newMinimum2 == null) {
                newMinimum = this.minimum;
            } else {
                T newMinimum3 = this.minimum;
                newMinimum = newMinimum3.compareTo(that.minimum) >= 0 ? this.minimum : that.minimum;
            }
        }
        if (this.maximum == null) {
            newMaximum = that.maximum;
        } else {
            T newMaximum2 = that.maximum;
            if (newMaximum2 == null) {
                newMaximum = this.maximum;
            } else {
                T newMaximum3 = this.maximum;
                newMaximum = newMaximum3.compareTo(that.maximum) <= 0 ? this.maximum : that.maximum;
            }
        }
        return new VersionRange<>(newMinimum, newMaximum);
    }

    public int hashCode() {
        if (isEmpty()) {
            return 1121763849;
        }
        if (isAll()) {
            return -475303149;
        }
        return Objects.hash(this.minimum) ^ Objects.hash(this.maximum);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VersionRange)) {
            return false;
        }
        VersionRange<T> that = (VersionRange) obj;
        return isEmpty() == that.isEmpty() && isAll() == that.isAll() && Objects.equals(this.minimum, that.minimum) && Objects.equals(this.maximum, that.maximum);
    }

    public String toString() {
        if (isEmpty()) {
            return "EMPTY";
        }
        if (isAll()) {
            return "ALL";
        }
        if (this.minimum == null) {
            return "At most " + this.maximum;
        }
        if (this.maximum == null) {
            return "At least " + this.minimum;
        }
        return "[" + this.minimum + ".." + this.maximum + "]";
    }
}

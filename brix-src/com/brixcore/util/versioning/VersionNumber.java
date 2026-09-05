package com.brixcore.util.versioning;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

/* JADX INFO: loaded from: classes11.dex */
public final class VersionNumber implements Comparable<VersionNumber> {
    private static final int MAX_LONGITEM_LENGTH = 18;
    public static final VersionNumber ZERO = asVersion("0");
    private final String canonical;
    private final ListItem items;
    private final String value;

    private interface Item {
        public static final int BIGINTEGER_ITEM = 1;
        public static final int LIST_ITEM = 3;
        public static final int LONG_ITEM = 0;
        public static final int STRING_ITEM = 2;

        void appendTo(StringBuilder sb);

        int compareTo(Item item);

        int getType();

        boolean isNull();
    }

    public static VersionNumber asVersion(String version) {
        Objects.requireNonNull(version);
        return new VersionNumber(version);
    }

    public static int compare(String version1, String version2) {
        return asVersion(version1).compareTo(asVersion(version2));
    }

    public static String normalize(String str) {
        return new VersionNumber(str).getCanonical();
    }

    public static boolean isIntVersionNumber(String version) {
        int endIndex;
        if (version.isEmpty()) {
            return false;
        }
        int idx = 0;
        boolean cont = true;
        do {
            int dotIndex = version.indexOf(46, idx);
            if (dotIndex == idx || dotIndex == version.length() - 1) {
                return false;
            }
            if (dotIndex < 0) {
                cont = false;
                endIndex = version.length();
            } else {
                endIndex = dotIndex;
            }
            if (endIndex - idx > 9) {
                return false;
            }
            for (int i = idx; i < endIndex; i++) {
                char ch = version.charAt(i);
                if (ch < '0' || ch > '9') {
                    return false;
                }
            }
            idx = endIndex + 1;
        } while (cont);
        return true;
    }

    public static VersionRange<VersionNumber> between(String minimum, String maximum) {
        return VersionRange.between(asVersion(minimum), asVersion(maximum));
    }

    public static VersionRange<VersionNumber> atLeast(String minimum) {
        return VersionRange.atLeast(asVersion(minimum));
    }

    public static VersionRange<VersionNumber> atMost(String maximum) {
        return VersionRange.atMost(asVersion(maximum));
    }

    private static final class LongItem implements Item {
        public static final LongItem ZERO = new LongItem(0);
        private final long value;

        LongItem(long value) {
            this.value = value;
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int getType() {
            return 0;
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public boolean isNull() {
            return this.value == 0;
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int compareTo(Item item) {
            if (item == null) {
                return this.value == 0 ? 0 : 1;
            }
            switch (item.getType()) {
                case 0:
                    long itemValue = ((LongItem) item).value;
                    return Long.compare(this.value, itemValue);
                case 1:
                    return -1;
                case 2:
                    return 1;
                case 3:
                    return 1;
                default:
                    throw new AssertionError("invalid item: " + item.getClass());
            }
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public void appendTo(StringBuilder buffer) {
            buffer.append(this.value);
        }

        public String toString() {
            return Long.toString(this.value);
        }
    }

    private static final class BigIntegerItem implements Item {
        private final BigInteger value;

        BigIntegerItem(String str) {
            this.value = new BigInteger(str);
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int getType() {
            return 1;
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public boolean isNull() {
            return false;
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int compareTo(Item item) {
            if (item == null) {
                return 1;
            }
            switch (item.getType()) {
                case 0:
                    return 1;
                case 1:
                    return this.value.compareTo(((BigIntegerItem) item).value);
                case 2:
                    return 1;
                case 3:
                    return 1;
                default:
                    throw new AssertionError("invalid item: " + item.getClass());
            }
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public void appendTo(StringBuilder buffer) {
            buffer.append(this.value);
        }

        public String toString() {
            return this.value.toString();
        }
    }

    private static final class StringItem implements Item {
        private final boolean pre;
        private final String value;

        StringItem(String value) {
            this.value = value;
            String lower = value.trim().toLowerCase(Locale.ROOT);
            this.pre = lower.startsWith("alpha") || lower.startsWith("beta") || lower.startsWith("pre") || lower.startsWith("rc") || lower.startsWith("experimental");
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int getType() {
            return 2;
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public boolean isNull() {
            return this.value.isEmpty();
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int compareTo(Item item) {
            if (item == null) {
                return this.pre ? -1 : 1;
            }
            switch (item.getType()) {
                case 0:
                case 1:
                    return -1;
                case 2:
                    return this.value.compareTo(((StringItem) item).value);
                case 3:
                    return -1;
                default:
                    throw new AssertionError("invalid item: " + item.getClass());
            }
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public void appendTo(StringBuilder buffer) {
            buffer.append(this.value);
        }

        public String toString() {
            return this.value;
        }
    }

    private static final class ListItem extends ArrayList<Item> implements Item {
        private final Character separator;

        ListItem() {
            this.separator = null;
        }

        ListItem(char separator) {
            this.separator = Character.valueOf(separator);
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int getType() {
            return 3;
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public boolean isNull() {
            return size() == 0;
        }

        void normalize() {
            for (int i = size() - 1; i >= 0; i--) {
                Item lastItem = get(i);
                if (lastItem.isNull()) {
                    remove(i);
                } else if (!(lastItem instanceof ListItem)) {
                    return;
                }
            }
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public int compareTo(Item item) {
            int result;
            if (item == null) {
                if (size() == 0) {
                    return 0;
                }
                Item first = get(0);
                return first.compareTo(null);
            }
            switch (item.getType()) {
                case 0:
                case 1:
                    return -1;
                case 2:
                    return 1;
                case 3:
                    Iterator<Item> left = iterator();
                    Iterator<Item> right = ((ListItem) item).iterator();
                    do {
                        if (!left.hasNext() && !right.hasNext()) {
                            return 0;
                        }
                        Item l = left.hasNext() ? left.next() : null;
                        Item r = right.hasNext() ? right.next() : null;
                        if (l == null) {
                            result = r == null ? 0 : r.compareTo(l) * (-1);
                        } else {
                            result = l.compareTo(r);
                        }
                    } while (result == 0);
                    return result;
                default:
                    throw new AssertionError("invalid item: " + item.getClass());
            }
        }

        @Override // com.brixcore.util.versioning.VersionNumber.Item
        public void appendTo(StringBuilder buffer) {
            if (this.separator != null) {
                buffer.append(this.separator.charValue());
            }
            int initLength = buffer.length();
            for (Item item : this) {
                if (buffer.length() > initLength && !(item instanceof ListItem)) {
                    buffer.append('.');
                }
                item.appendTo(buffer);
            }
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            appendTo(buffer);
            return buffer.toString();
        }
    }

    private VersionNumber(String version) {
        this.value = version;
        ListItem list = new ListItem();
        this.items = list;
        Deque<Item> stack = new ArrayDeque<>();
        stack.push(list);
        boolean isDigit = false;
        int startIndex = 0;
        for (int i = 0; i < version.length(); i++) {
            char c = version.charAt(i);
            if (c == '.') {
                if (i == startIndex) {
                    list.add(LongItem.ZERO);
                } else {
                    list.add(parseItem(version.substring(startIndex, i)));
                }
                startIndex = i + 1;
            } else if ("!\"#$%&'()*+,-/:;<=>?@[\\]^_`{|}~".indexOf(c) != -1) {
                if (i == startIndex) {
                    list.add(LongItem.ZERO);
                } else {
                    list.add(parseItem(version.substring(startIndex, i)));
                }
                startIndex = i + 1;
                ListItem list2 = new ListItem(c);
                list.add(list2);
                stack.push(list2);
                list = list2;
            } else if (c >= '0' && c <= '9') {
                if (!isDigit && i > startIndex) {
                    list.add(parseItem(version.substring(startIndex, i)));
                    startIndex = i;
                    ListItem list3 = new ListItem();
                    list.add(list3);
                    stack.push(list3);
                    list = list3;
                }
                isDigit = true;
            } else {
                if (isDigit && i > startIndex) {
                    list.add(parseItem(version.substring(startIndex, i)));
                    startIndex = i;
                    ListItem list4 = new ListItem();
                    list.add(list4);
                    stack.push(list4);
                    list = list4;
                }
                isDigit = false;
            }
        }
        int i2 = version.length();
        if (i2 > startIndex) {
            list.add(parseItem(version.substring(startIndex)));
        }
        while (!stack.isEmpty()) {
            ((ListItem) stack.pop()).normalize();
        }
        this.canonical = this.items.toString();
    }

    private VersionNumber(String version, ListItem items) {
        this.value = version;
        this.items = items;
        this.canonical = version;
    }

    private static Item parseItem(String buf) {
        int numberLength = 0;
        boolean leadingZero = true;
        for (int i = 0; i < buf.length(); i++) {
            char ch = buf.charAt(i);
            if (ch >= '0' && ch <= '9') {
                if (ch != '0') {
                    leadingZero = false;
                }
                if (!leadingZero) {
                    numberLength++;
                }
            } else {
                return new StringItem(buf);
            }
        }
        if (numberLength == 0) {
            return LongItem.ZERO;
        }
        if (numberLength <= 18) {
            return new LongItem(Long.parseLong(buf));
        }
        return new BigIntegerItem(buf);
    }

    public int compareTo(String o) {
        return compareTo(asVersion(o));
    }

    @Override // java.lang.Comparable
    public int compareTo(VersionNumber o) {
        return this.items.compareTo(o.items);
    }

    public String toString() {
        return this.value;
    }

    public String getCanonical() {
        return this.canonical;
    }

    public boolean equals(Object o) {
        return (o instanceof VersionNumber) && this.canonical.equals(((VersionNumber) o).canonical);
    }

    public int hashCode() {
        return this.canonical.hashCode();
    }
}

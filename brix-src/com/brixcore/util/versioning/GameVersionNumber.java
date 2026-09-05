package com.brixcore.util.versioning;

import com.brixcore.util.Logging;
import com.brixcore.util.ToStringBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glavo.chardet.prober.CharsetProber;

/* JADX INFO: loaded from: classes11.dex */
public abstract class GameVersionNumber implements Comparable<GameVersionNumber> {
    final String normalized;
    final String value;

    enum Type {
        PRE_CLASSIC,
        CLASSIC,
        INDEV,
        INFDEV,
        ALPHA,
        BETA,
        NEW
    }

    abstract int compareToImpl(GameVersionNumber gameVersionNumber);

    abstract Type getType();

    public static String[] getDefaultGameVersions() {
        return Versions.DEFAULT_GAME_VERSIONS;
    }

    public static GameVersionNumber asGameVersion(String version) {
        GameVersionNumber versionNumber = Versions.SPECIALS.get(version);
        if (versionNumber != null) {
            return versionNumber;
        }
        try {
            if (!version.isEmpty()) {
                char ch = version.charAt(0);
                switch (ch) {
                    case CharsetProber.ASCII_A /* 97 */:
                    case 'b':
                    case 'c':
                    case 'i':
                    case 'r':
                        return Old.parse(version);
                    default:
                        if (version.equals("0.0")) {
                            return Release.ZERO;
                        }
                        if (version.length() >= 6 && version.charAt(2) == 'w') {
                            return LegacySnapshot.parse(version);
                        }
                        return Release.parse(version);
                }
            }
        } catch (Throwable th) {
        }
        return new Special(version, version);
    }

    public static GameVersionNumber asGameVersion(Optional<String> version) {
        return version.isPresent() ? asGameVersion(version.get()) : unknown();
    }

    public static GameVersionNumber unknown() {
        return Release.ZERO;
    }

    public static int compare(String version1, String version2) {
        return asGameVersion(version1).compareTo(asGameVersion(version2));
    }

    public static VersionRange<GameVersionNumber> between(String minimum, String maximum) {
        return VersionRange.between(asGameVersion(minimum), asGameVersion(maximum));
    }

    public static VersionRange<GameVersionNumber> atLeast(String minimum) {
        return VersionRange.atLeast(asGameVersion(minimum));
    }

    public static VersionRange<GameVersionNumber> atMost(String maximum) {
        return VersionRange.atMost(asGameVersion(maximum));
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public static boolean isKnown(String version) {
        boolean z;
        GameVersionNumber gameVersionNumberAsGameVersion = asGameVersion(version);
        if (gameVersionNumberAsGameVersion instanceof Special) {
            Special special = (Special) gameVersionNumberAsGameVersion;
            if (special.prev == null) {
                z = true;
            } else {
                z = false;
            }
        } else {
            z = false;
        }
        return !z;
    }

    GameVersionNumber(String value, String normalized) {
        this.value = value;
        this.normalized = normalized;
    }

    public boolean isAprilFools() {
        if (this instanceof Special) {
            String normalizedVersion = toNormalizedString();
            return !(normalizedVersion.startsWith("1.") || normalizedVersion.equals("13w12~")) || normalizedVersion.equals("1.RV-Pre1");
        }
        if (!(this instanceof LegacySnapshot)) {
            return false;
        }
        LegacySnapshot snapshot = (LegacySnapshot) this;
        return snapshot.intValue == LegacySnapshot.toInt(15, 14, 'a', false);
    }

    public int compareTo(String other) {
        return compareTo(asGameVersion(other));
    }

    @Override // java.lang.Comparable
    public int compareTo(GameVersionNumber other) {
        if (getType() != other.getType()) {
            return Integer.compare(getType().ordinal(), other.getType().ordinal());
        }
        return compareToImpl(other);
    }

    public boolean isAtLeast(String releaseVersion, String snapshotVersion) {
        return isAtLeast(releaseVersion, snapshotVersion, false);
    }

    public boolean isAtLeast(String releaseVersion, String snapshotVersion, boolean strictReleaseVersion) {
        Release other;
        if (!(this instanceof Release)) {
            return compareTo((GameVersionNumber) LegacySnapshot.parse(snapshotVersion)) >= 0;
        }
        Release self = (Release) this;
        if (strictReleaseVersion) {
            other = Release.parse(releaseVersion);
        } else {
            other = Release.parseSimple(releaseVersion);
        }
        return self.compareToRelease(other) >= 0;
    }

    public String toNormalizedString() {
        return this.normalized;
    }

    public String toString() {
        return this.value;
    }

    protected ToStringBuilder buildDebugString() {
        return new ToStringBuilder(this).append("value", this.value).append("normalized", this.normalized).append("type", getType());
    }

    public final String toDebugString() {
        return buildDebugString().toString();
    }

    public static GameVersionNumber getReleaseOfSnapshot(GameVersionNumber gameVersion) {
        if (gameVersion instanceof Release) {
            Release release = (Release) gameVersion;
            if (release.getEaType() == Release.ReleaseType.GA) {
                return null;
            }
            if (release.getPatch() > 0) {
                return asGameVersion(release.getMajor() + "." + release.getMinor() + "." + release.getPatch());
            }
            return asGameVersion(release.getMajor() + "." + release.getMinor());
        }
        if (gameVersion instanceof LegacySnapshot) {
            LegacySnapshot snapshot = (LegacySnapshot) gameVersion;
            String[] defaultVersions = Versions.DEFAULT_GAME_VERSIONS;
            for (int i = defaultVersions.length - 1; i >= 0; i--) {
                Release gaRelease = (Release) asGameVersion(defaultVersions[i]);
                if (gaRelease.compareToSnapshot(snapshot) > 0) {
                    return gaRelease;
                }
            }
        }
        return null;
    }

    public static final class Old extends GameVersionNumber {
        final Type type;
        final VersionNumber versionNumber;

        @Override // com.brixcore.util.versioning.GameVersionNumber, java.lang.Comparable
        public /* bridge */ /* synthetic */ int compareTo(GameVersionNumber gameVersionNumber) {
            return super.compareTo(gameVersionNumber);
        }

        static Old parse(String value) {
            Type type;
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Empty old version number");
            }
            int prefixLength = 1;
            switch (value.charAt(0)) {
                case CharsetProber.ASCII_A /* 97 */:
                    type = Type.ALPHA;
                    break;
                case 'b':
                    type = Type.BETA;
                    break;
                case 'c':
                    type = Type.CLASSIC;
                    break;
                case 'i':
                    if (value.startsWith("inf-")) {
                        type = Type.INFDEV;
                        prefixLength = "inf-".length();
                    } else if (value.startsWith("in-")) {
                        type = Type.INDEV;
                        prefixLength = "in-".length();
                    } else {
                        throw new IllegalArgumentException(value);
                    }
                    break;
                case 'r':
                    if (!value.startsWith("rd-")) {
                        throw new IllegalArgumentException(value);
                    }
                    type = Type.PRE_CLASSIC;
                    prefixLength = "rd-".length();
                    break;
                    break;
                default:
                    throw new IllegalArgumentException(value);
            }
            if (value.length() < prefixLength + 1 || !Character.isDigit(value.charAt(prefixLength))) {
                throw new IllegalArgumentException(value);
            }
            return new Old(value, type, VersionNumber.asVersion(value.substring(prefixLength)));
        }

        private Old(String value, Type type, VersionNumber versionNumber) {
            super(value, value);
            this.type = type;
            this.versionNumber = versionNumber;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        Type getType() {
            return this.type;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        int compareToImpl(GameVersionNumber other) {
            return this.versionNumber.compareTo(((Old) other).versionNumber);
        }

        public int hashCode() {
            return Objects.hash(this.type, this.versionNumber);
        }

        public boolean equals(Object o) {
            if (o instanceof Old) {
                Old that = (Old) o;
                if (this.type == that.type && this.versionNumber.equals(that.versionNumber)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static final class Release extends GameVersionNumber {
        private static final int MINIMUM_YEAR_MAJOR_VERSION = 26;
        private final Additional additional;
        private final ReleaseType eaType;
        private final VersionNumber eaVersion;
        private final int major;
        private final int minor;
        private final int patch;
        static final Release ZERO = new Release("0.0", "0.0", 0, 0, 0, ReleaseType.UNKNOWN, VersionNumber.ZERO, Additional.NONE);
        private static final Pattern VERSION_PATTERN = Pattern.compile("(?<prefix>(?<major>1|[1-9]\\d+)\\.(?<minor>\\d+)(\\.(?<patch>[0-9]+))?)(?<suffix>.*)");

        @Override // com.brixcore.util.versioning.GameVersionNumber, java.lang.Comparable
        public /* bridge */ /* synthetic */ int compareTo(GameVersionNumber gameVersionNumber) {
            return super.compareTo(gameVersionNumber);
        }

        public enum ReleaseType {
            UNKNOWN(""),
            SNAPSHOT("-snapshot-"),
            PRE_RELEASE("-pre", "-pre-"),
            RELEASE_CANDIDATE("-rc", "-rc-"),
            GA("");

            private final String legacyInfix;
            private final String newInfix;

            ReleaseType(String infix) {
                this.legacyInfix = infix;
                this.newInfix = infix;
            }

            ReleaseType(String legacyInfix, String newInfix) {
                this.legacyInfix = legacyInfix;
                this.newInfix = newInfix;
            }
        }

        public enum Additional {
            NONE(""),
            UNOBFUSCATED("_unobfuscated");

            private final String suffix;

            Additional(String suffix) {
                this.suffix = suffix;
            }
        }

        static Release parse(String value) {
            String suffix;
            Additional additional;
            VersionNumber eaVersion;
            boolean needNormalize;
            ReleaseType releaseType;
            String normalized;
            Matcher matcher = VERSION_PATTERN.matcher(value);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(value);
            }
            int major = Integer.parseInt(matcher.group("major"));
            boolean isLegacyRelease = true;
            if (major != 1 && major < 26) {
                throw new IllegalArgumentException(value);
            }
            int minor = Integer.parseInt(matcher.group("minor"));
            String patchString = matcher.group("patch");
            int patch = patchString != null ? Integer.parseInt(patchString) : 0;
            String suffix2 = matcher.group("suffix");
            if (major != 1) {
                isLegacyRelease = false;
            }
            Additional additional2 = Additional.NONE;
            boolean needNormalize2 = false;
            if (suffix2.endsWith("_unobfuscated")) {
                String suffix3 = suffix2.substring(0, suffix2.length() - "_unobfuscated".length());
                Additional additional3 = Additional.UNOBFUSCATED;
                suffix = suffix3;
                additional = additional3;
            } else if (!suffix2.endsWith(" Unobfuscated")) {
                suffix = suffix2;
                additional = additional2;
            } else {
                needNormalize2 = true;
                String suffix4 = suffix2.substring(0, suffix2.length() - " Unobfuscated".length());
                Additional additional4 = Additional.UNOBFUSCATED;
                suffix = suffix4;
                additional = additional4;
            }
            if (suffix.isEmpty()) {
                ReleaseType releaseType2 = ReleaseType.GA;
                eaVersion = VersionNumber.ZERO;
                needNormalize = needNormalize2;
                releaseType = releaseType2;
            } else if (suffix.startsWith("-snapshot-")) {
                ReleaseType releaseType3 = ReleaseType.SNAPSHOT;
                eaVersion = VersionNumber.asVersion(suffix.substring("-snapshot-".length()));
                needNormalize = needNormalize2;
                releaseType = releaseType3;
            } else if (suffix.startsWith(" Snapshot ")) {
                ReleaseType releaseType4 = ReleaseType.SNAPSHOT;
                eaVersion = VersionNumber.asVersion(suffix.substring(" Snapshot ".length()));
                needNormalize = true;
                releaseType = releaseType4;
            } else if (suffix.startsWith("-pre-")) {
                if (isLegacyRelease) {
                    needNormalize2 = true;
                }
                ReleaseType releaseType5 = ReleaseType.PRE_RELEASE;
                eaVersion = VersionNumber.asVersion(suffix.substring("-pre-".length()));
                needNormalize = needNormalize2;
                releaseType = releaseType5;
            } else if (suffix.startsWith("-pre")) {
                if (!isLegacyRelease) {
                    needNormalize2 = true;
                }
                ReleaseType releaseType6 = ReleaseType.PRE_RELEASE;
                eaVersion = VersionNumber.asVersion(suffix.substring("-pre".length()));
                needNormalize = needNormalize2;
                releaseType = releaseType6;
            } else if (suffix.startsWith(" Pre-Release ")) {
                ReleaseType releaseType7 = ReleaseType.PRE_RELEASE;
                eaVersion = VersionNumber.asVersion(suffix.substring(" Pre-Release ".length()));
                needNormalize = true;
                releaseType = releaseType7;
            } else if (suffix.startsWith(" Pre-release ")) {
                ReleaseType releaseType8 = ReleaseType.PRE_RELEASE;
                eaVersion = VersionNumber.asVersion(suffix.substring(" Pre-release ".length()));
                needNormalize = true;
                releaseType = releaseType8;
            } else if (suffix.startsWith("-rc-")) {
                if (isLegacyRelease) {
                    needNormalize2 = true;
                }
                ReleaseType releaseType9 = ReleaseType.RELEASE_CANDIDATE;
                eaVersion = VersionNumber.asVersion(suffix.substring("-rc-".length()));
                needNormalize = needNormalize2;
                releaseType = releaseType9;
            } else if (suffix.startsWith("-rc")) {
                if (!isLegacyRelease) {
                    needNormalize2 = true;
                }
                ReleaseType releaseType10 = ReleaseType.RELEASE_CANDIDATE;
                eaVersion = VersionNumber.asVersion(suffix.substring("-rc".length()));
                needNormalize = needNormalize2;
                releaseType = releaseType10;
            } else if (suffix.startsWith(" Release Candidate ")) {
                ReleaseType releaseType11 = ReleaseType.RELEASE_CANDIDATE;
                eaVersion = VersionNumber.asVersion(suffix.substring(" Release Candidate ".length()));
                needNormalize = true;
                releaseType = releaseType11;
            } else {
                throw new IllegalArgumentException(value);
            }
            if (needNormalize) {
                StringBuilder builder = new StringBuilder(value.length());
                builder.append(matcher.group("prefix"));
                if (releaseType != ReleaseType.GA) {
                    builder.append(isLegacyRelease ? releaseType.legacyInfix : releaseType.newInfix);
                    builder.append(eaVersion);
                }
                builder.append(additional.suffix);
                String normalized2 = builder.toString();
                normalized = normalized2;
            } else {
                normalized = value;
            }
            return new Release(value, normalized, major, minor, patch, releaseType, eaVersion, additional);
        }

        static Release parseSimple(String value) {
            String value2;
            int patch;
            int majorLength = getNumberLength(value, 0);
            if (majorLength != 0 && value.length() >= majorLength + 2 && value.charAt(majorLength) == '.') {
                int major = Integer.parseInt(value.subSequence(0, majorLength).toString(), 10);
                if (major != 1 && major < 26) {
                    throw new IllegalArgumentException(value);
                }
                int minorOffset = majorLength + 1;
                int minorLength = getNumberLength(value, minorOffset);
                if (minorLength != 0) {
                    try {
                        int minor = Integer.parseInt(value.subSequence(minorOffset, minorOffset + minorLength).toString(), 10);
                        if (minorOffset + minorLength < value.length()) {
                            int patchOffset = minorOffset + minorLength + 1;
                            try {
                                if (patchOffset < value.length() && value.charAt(patchOffset - 1) == '.') {
                                    int patch2 = Integer.parseInt(value.subSequence(patchOffset, value.length()).toString(), 10);
                                    patch = patch2;
                                } else {
                                    throw new IllegalArgumentException(value);
                                }
                            } catch (NumberFormatException e) {
                                value2 = value;
                                throw new IllegalArgumentException(value2);
                            }
                        } else {
                            patch = 0;
                        }
                        value2 = value;
                        try {
                            return new Release(value2, value, major, minor, patch, ReleaseType.UNKNOWN, VersionNumber.ZERO, Additional.NONE);
                        } catch (NumberFormatException e2) {
                            throw new IllegalArgumentException(value2);
                        }
                    } catch (NumberFormatException e3) {
                        value2 = value;
                    }
                } else {
                    throw new IllegalArgumentException(value);
                }
            } else {
                throw new IllegalArgumentException(value);
            }
        }

        private static int getNumberLength(String value, int offset) {
            char ch;
            int current = offset;
            while (current < value.length() && (ch = value.charAt(current)) >= '0' && ch <= '9') {
                current++;
            }
            return current - offset;
        }

        Release(String value, String normalized, int major, int minor, int patch, ReleaseType eaType, VersionNumber eaVersion, Additional additional) {
            super(value, normalized);
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.eaType = eaType;
            this.eaVersion = eaVersion;
            this.additional = additional;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        Type getType() {
            return Type.NEW;
        }

        int compareToRelease(Release other) {
            int c = Integer.compare(this.major, other.major);
            if (c != 0) {
                return c;
            }
            int c2 = Integer.compare(this.minor, other.minor);
            if (c2 != 0) {
                return c2;
            }
            int c3 = Integer.compare(this.patch, other.patch);
            if (c3 != 0) {
                return c3;
            }
            int c4 = this.eaType.compareTo(other.eaType);
            if (c4 != 0) {
                return c4;
            }
            int c5 = this.eaVersion.compareTo(other.eaVersion);
            if (c5 != 0) {
                return c5;
            }
            return this.additional.compareTo(other.additional);
        }

        int compareToSnapshot(LegacySnapshot other) {
            if (this.major == 0) {
                return -1;
            }
            if (this.major != 1) {
                return 1;
            }
            int idx = Arrays.binarySearch(Versions.SNAPSHOT_INTS, other.intValue);
            if (idx >= 0) {
                return compareToRelease(Versions.SNAPSHOT_PREV[idx]) <= 0 ? -1 : 1;
            }
            int idx2 = -(idx + 1);
            return (idx2 != Versions.SNAPSHOT_INTS.length && compareToRelease(Versions.SNAPSHOT_PREV[idx2]) > 0) ? 1 : -1;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        int compareToImpl(GameVersionNumber other) {
            if (other instanceof Release) {
                Release release = (Release) other;
                return compareToRelease(release);
            }
            if (other instanceof LegacySnapshot) {
                LegacySnapshot snapshot = (LegacySnapshot) other;
                return compareToSnapshot(snapshot);
            }
            if (other instanceof Special) {
                Special special = (Special) other;
                return -special.compareToReleaseOrSnapshot(this);
            }
            throw new AssertionError(other.getClass());
        }

        public int getMajor() {
            return this.major;
        }

        public int getMinor() {
            return this.minor;
        }

        public int getPatch() {
            return this.patch;
        }

        public ReleaseType getEaType() {
            return this.eaType;
        }

        public VersionNumber getEaVersion() {
            return this.eaVersion;
        }

        public Additional getAdditional() {
            return this.additional;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.major), Integer.valueOf(this.minor), Integer.valueOf(this.patch), this.eaType, this.eaVersion, this.additional);
        }

        public boolean equals(Object o) {
            if (o instanceof Release) {
                Release that = (Release) o;
                if (this.major == that.major && this.minor == that.minor && this.patch == that.patch && this.eaType == that.eaType && this.eaVersion.equals(that.eaVersion) && this.additional == that.additional) {
                    return true;
                }
            }
            return false;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        protected ToStringBuilder buildDebugString() {
            return super.buildDebugString().append("major", Integer.valueOf(this.major)).append("minor", Integer.valueOf(this.minor)).append("patch", Integer.valueOf(this.patch)).append("eaType", this.eaType).append("eaVersion", this.eaVersion).append("additional", this.additional);
        }
    }

    public static final class LegacySnapshot extends GameVersionNumber {
        final int intValue;

        @Override // com.brixcore.util.versioning.GameVersionNumber, java.lang.Comparable
        public /* bridge */ /* synthetic */ int compareTo(GameVersionNumber gameVersionNumber) {
            return super.compareTo(gameVersionNumber);
        }

        static LegacySnapshot parse(String value) {
            int prefixLength;
            boolean unobfuscated;
            String normalized;
            if (value.length() < 6 || value.charAt(2) != 'w') {
                throw new IllegalArgumentException(value);
            }
            if (value.endsWith("_unobfuscated")) {
                prefixLength = value.length() - "_unobfuscated".length();
                unobfuscated = true;
                normalized = value;
            } else if (value.endsWith(" Unobfuscated")) {
                prefixLength = value.length() - " Unobfuscated".length();
                normalized = value.substring(0, prefixLength) + "_unobfuscated";
                unobfuscated = true;
            } else {
                prefixLength = value.length();
                unobfuscated = false;
                normalized = value;
            }
            if (prefixLength != 6) {
                throw new IllegalArgumentException(value);
            }
            try {
                int year = Integer.parseInt(value.subSequence(0, 2).toString(), 10);
                int week = Integer.parseInt(value.subSequence(3, 5).toString(), 10);
                if (year >= 26) {
                    throw new IllegalArgumentException(value);
                }
                char suffix = value.charAt(5);
                if (suffix < 'a' || suffix > 'z') {
                    throw new IllegalArgumentException(value);
                }
                return new LegacySnapshot(value, normalized, year, week, suffix, unobfuscated);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(value);
            }
        }

        static int toInt(int i, int i2, char c, boolean z) {
            return ((((i << 24) | (i2 << 16)) | (c << '\b')) | (z ? 1 : 0)) == true ? 1 : 0;
        }

        LegacySnapshot(String value, String normalized, int year, int week, char suffix, boolean unobfuscated) {
            super(value, normalized);
            this.intValue = toInt(year, week, suffix, unobfuscated);
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        Type getType() {
            return Type.NEW;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        int compareToImpl(GameVersionNumber other) {
            if (other instanceof Release) {
                Release otherRelease = (Release) other;
                return -otherRelease.compareToSnapshot(this);
            }
            if (other instanceof LegacySnapshot) {
                LegacySnapshot otherSnapshot = (LegacySnapshot) other;
                return Integer.compare(this.intValue, otherSnapshot.intValue);
            }
            if (other instanceof Special) {
                Special otherSpecial = (Special) other;
                return -otherSpecial.compareToReleaseOrSnapshot(this);
            }
            throw new AssertionError(other.getClass());
        }

        public int getYear() {
            return (this.intValue >> 24) & 255;
        }

        public int getWeek() {
            return (this.intValue >> 16) & 255;
        }

        public char getSuffix() {
            return (char) ((this.intValue >> 8) & 255);
        }

        public boolean isUnobfuscated() {
            return (this.intValue & 1) != 0;
        }

        public int hashCode() {
            return this.intValue;
        }

        public boolean equals(Object o) {
            if (o instanceof LegacySnapshot) {
                LegacySnapshot that = (LegacySnapshot) o;
                if (this.intValue == that.intValue) {
                    return true;
                }
            }
            return false;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        protected ToStringBuilder buildDebugString() {
            return super.buildDebugString().append("year", Integer.valueOf(getYear())).append("week", Integer.valueOf(getWeek())).append("suffix", Character.valueOf(getSuffix())).append("unobfuscated", Boolean.valueOf(isUnobfuscated()));
        }
    }

    public static final class Special extends GameVersionNumber {
        private GameVersionNumber prev;
        private VersionNumber versionNumber;

        @Override // com.brixcore.util.versioning.GameVersionNumber, java.lang.Comparable
        public /* bridge */ /* synthetic */ int compareTo(GameVersionNumber gameVersionNumber) {
            return super.compareTo(gameVersionNumber);
        }

        Special(String value, String normalized) {
            super(value, normalized);
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        Type getType() {
            return Type.NEW;
        }

        boolean isUnknown() {
            return this.prev == null;
        }

        VersionNumber asVersionNumber() {
            if (this.versionNumber != null) {
                return this.versionNumber;
            }
            VersionNumber versionNumberAsVersion = VersionNumber.asVersion(this.normalized);
            this.versionNumber = versionNumberAsVersion;
            return versionNumberAsVersion;
        }

        GameVersionNumber getPrevNormalVersion() {
            GameVersionNumber v = this.prev;
            while (v instanceof Special) {
                Special special = (Special) v;
                v = special.prev;
            }
            if (v == null) {
                throw new AssertionError("version: " + this.value);
            }
            return v;
        }

        int compareToReleaseOrSnapshot(GameVersionNumber other) {
            return (!isUnknown() && getPrevNormalVersion().compareTo(other) < 0) ? -1 : 1;
        }

        int compareToSpecial(Special other) {
            if (isUnknown()) {
                if (other.isUnknown()) {
                    return asVersionNumber().compareTo(other.asVersionNumber());
                }
                return 1;
            }
            if (other.isUnknown()) {
                return -1;
            }
            if (this.normalized.equals(other.normalized)) {
                return 0;
            }
            int c = getPrevNormalVersion().compareTo(other.getPrevNormalVersion());
            if (c != 0) {
                return c;
            }
            GameVersionNumber v = this.prev;
            while (v instanceof Special) {
                Special special = (Special) v;
                if (v == other) {
                    return 1;
                }
                v = special.prev;
            }
            return -1;
        }

        @Override // com.brixcore.util.versioning.GameVersionNumber
        int compareToImpl(GameVersionNumber o) {
            if ((o instanceof Release) || (o instanceof LegacySnapshot)) {
                return compareToReleaseOrSnapshot(o);
            }
            if (o instanceof Special) {
                Special special = (Special) o;
                return compareToSpecial(special);
            }
            throw new AssertionError(o.getClass());
        }

        public boolean equals(Object o) {
            if (o instanceof Special) {
                Special that = (Special) o;
                if (this.normalized.equals(that.normalized)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.normalized.hashCode();
        }
    }

    static final class Versions {
        static final String[] DEFAULT_GAME_VERSIONS;
        static final int[] SNAPSHOT_INTS;
        static final Release[] SNAPSHOT_PREV;
        static final HashMap<String, Special> SPECIALS = new HashMap<>();

        Versions() {
        }

        static {
            ArrayDeque<String> defaultGameVersions = new ArrayDeque<>(64);
            List<LegacySnapshot> snapshots = new ArrayList<>(1024);
            List<Release> snapshotPrev = new ArrayList<>(1024);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(GameVersionNumber.class.getResourceAsStream("/assets/game/versions.txt"), StandardCharsets.US_ASCII));
                Release currentRelease = null;
                GameVersionNumber prev = null;
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line != null) {
                            if (!line.isEmpty()) {
                                GameVersionNumber version = GameVersionNumber.asGameVersion(line);
                                if (currentRelease == null) {
                                    currentRelease = (Release) version;
                                }
                                if (version instanceof LegacySnapshot) {
                                    LegacySnapshot snapshot = (LegacySnapshot) version;
                                    snapshots.add(snapshot);
                                    snapshotPrev.add(currentRelease);
                                } else if (version instanceof Release) {
                                    Release release = (Release) version;
                                    currentRelease = release;
                                    if (currentRelease.eaType == Release.ReleaseType.GA && currentRelease.additional == Release.Additional.NONE) {
                                        defaultGameVersions.addFirst(currentRelease.value);
                                    }
                                } else if (version instanceof Special) {
                                    Special special = (Special) version;
                                    special.prev = prev;
                                    SPECIALS.put(special.value, special);
                                } else {
                                    throw new AssertionError("version: " + version);
                                }
                                prev = version;
                            }
                        } else {
                            reader.close();
                            try {
                                BufferedReader reader2 = new BufferedReader(new InputStreamReader(GameVersionNumber.class.getResourceAsStream("/assets/game/version-alias.csv"), StandardCharsets.US_ASCII));
                                while (true) {
                                    try {
                                        String line2 = reader2.readLine();
                                        if (line2 == null) {
                                            break;
                                        }
                                        if (!line2.isEmpty()) {
                                            String[] parts = line2.split(",");
                                            if (parts.length < 2) {
                                                Logging.LOG.warning("Invalid line: " + line2);
                                            } else {
                                                String normalized = parts[0];
                                                Special normalizedVersion = SPECIALS.get(normalized);
                                                if (normalizedVersion == null) {
                                                    Logging.LOG.warning("Unknown special version: " + normalized);
                                                } else {
                                                    for (int i = 1; i < parts.length; i++) {
                                                        String version2 = parts[i];
                                                        Special versionNumber = new Special(version2, normalized);
                                                        versionNumber.prev = normalizedVersion.prev;
                                                        SPECIALS.put(version2, versionNumber);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Throwable th) {
                                        try {
                                            reader2.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                        throw th;
                                    }
                                }
                                reader2.close();
                                DEFAULT_GAME_VERSIONS = (String[]) defaultGameVersions.toArray(new String[0]);
                                SNAPSHOT_INTS = new int[snapshots.size()];
                                for (int i2 = 0; i2 < snapshots.size(); i2++) {
                                    SNAPSHOT_INTS[i2] = snapshots.get(i2).intValue;
                                }
                                SNAPSHOT_PREV = (Release[]) snapshotPrev.toArray(new Release[SNAPSHOT_INTS.length]);
                                return;
                            } catch (IOException e) {
                                throw new AssertionError(e);
                            }
                        }
                    } catch (Throwable th3) {
                        try {
                            reader.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                        throw th3;
                    }
                    throw new AssertionError(e);
                }
            } catch (IOException e2) {
                throw new AssertionError(e2);
            }
        }
    }
}

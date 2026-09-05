package com.brixcore.mod;

import com.android.tools.r8.RecordTag;
import com.brixcore.download.DownloadProvider;
import com.brixcore.util.io.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class LocalAddonFile {
    public abstract AddonUpdate checkUpdates(DownloadProvider downloadProvider, String str, RemoteMod.Type type) throws IOException;

    public abstract void delete() throws IOException;

    public abstract Path getFile();

    public abstract String getFileName();

    public abstract boolean keepOldFiles();

    public abstract void markDisabled() throws IOException;

    public abstract void setOld(boolean z) throws IOException;

    protected LocalAddonFile() {
    }

    public boolean isDisabled() {
        return FileUtils.getName(getFile()).endsWith(".disabled");
    }

    public static final class AddonUpdate extends RecordTag {
        private final RemoteMod.Version currentVersion;
        private final LocalAddonFile localAddonFile;
        private final RemoteMod.Version targetVersion;
        private final boolean useRemoteFileName;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof AddonUpdate)) {
                return false;
            }
            AddonUpdate addonUpdate = (AddonUpdate) obj;
            return this.useRemoteFileName == addonUpdate.useRemoteFileName && Objects.equals(this.localAddonFile, addonUpdate.localAddonFile) && Objects.equals(this.currentVersion, addonUpdate.currentVersion) && Objects.equals(this.targetVersion, addonUpdate.targetVersion);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.localAddonFile, this.currentVersion, this.targetVersion, Boolean.valueOf(this.useRemoteFileName)};
        }

        public AddonUpdate(LocalAddonFile localAddonFile, RemoteMod.Version currentVersion, RemoteMod.Version targetVersion, boolean useRemoteFileName) {
            this.localAddonFile = localAddonFile;
            this.currentVersion = currentVersion;
            this.targetVersion = targetVersion;
            this.useRemoteFileName = useRemoteFileName;
        }

        public RemoteMod.Version currentVersion() {
            return this.currentVersion;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return LocalAddonFile$AddonUpdate$$ExternalSyntheticRecord1.m(this.useRemoteFileName, this.localAddonFile, this.currentVersion, this.targetVersion);
        }

        public LocalAddonFile localAddonFile() {
            return this.localAddonFile;
        }

        public RemoteMod.Version targetVersion() {
            return this.targetVersion;
        }

        public final String toString() {
            return LocalAddonFile$AddonUpdate$$ExternalSyntheticRecord0.m($record$getFieldsAsObjects(), AddonUpdate.class, "localAddonFile;currentVersion;targetVersion;useRemoteFileName");
        }

        public boolean useRemoteFileName() {
            return this.useRemoteFileName;
        }
    }

    public static final class Description extends RecordTag {
        private final List<Part> parts;

        private /* synthetic */ boolean $record$equals(Object obj) {
            return (obj instanceof Description) && Objects.equals(this.parts, ((Description) obj).parts);
        }

        public Description(List<Part> parts) {
            this.parts = parts;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return Objects.hashCode(this.parts);
        }

        public List<Part> parts() {
            return this.parts;
        }

        public Description(String text) {
            this(new ArrayList());
            this.parts.add(new Part(text, "black"));
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Part part : this.parts) {
                builder.append(part.text);
            }
            return builder.toString();
        }

        public String toStringSingleLine() {
            return (String) Stream.of((Object[]) toString().split(StringUtils.LF)).map(new Function() { // from class: com.brixcore.mod.LocalAddonFile$Description$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((String) obj).trim();
                }
            }).filter(new Predicate() { // from class: com.brixcore.mod.LocalAddonFile$Description$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return com.brixcore.util.StringUtils.isNotBlank((String) obj);
                }
            }).collect(Collectors.joining(" | "));
        }

        public static final class Part extends RecordTag {
            private final String color;
            private final String text;

            private /* synthetic */ boolean $record$equals(Object obj) {
                if (!(obj instanceof Part)) {
                    return false;
                }
                Part part = (Part) obj;
                return Objects.equals(this.text, part.text) && Objects.equals(this.color, part.color);
            }

            private /* synthetic */ Object[] $record$getFieldsAsObjects() {
                return new Object[]{this.text, this.color};
            }

            public String color() {
                return this.color;
            }

            public final boolean equals(Object o) {
                return $record$equals(o);
            }

            public final int hashCode() {
                return LocalAddonFile$Description$Part$$ExternalSyntheticRecord0.m(this.text, this.color);
            }

            public String text() {
                return this.text;
            }

            public final String toString() {
                return LocalAddonFile$AddonUpdate$$ExternalSyntheticRecord0.m($record$getFieldsAsObjects(), Part.class, "text;color");
            }

            public Part(String text) {
                this(text, "");
            }

            public Part(String text, String color) {
                this.text = (String) Objects.requireNonNull(text);
                this.color = (String) Objects.requireNonNull(color);
            }
        }
    }
}

package com.brixcore.game;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes2.dex */
@JsonAdapter(Serializer.class)
public final class Artifact {
    private final String classifier;
    private final String descriptor;
    private final String extension;
    private final String fileName;
    private final String group;
    private final String name;
    private final String path;
    private final String version;

    public Artifact(String group, String name, String version) {
        this(group, name, version, null);
    }

    public Artifact(String group, String name, String version, String classifier) {
        this(group, name, version, classifier, null);
    }

    public Artifact(String group, String name, String version, String classifier, String extension) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.classifier = classifier;
        this.extension = extension == null ? "jar" : extension;
        String fileName = this.name + "-" + this.version;
        this.fileName = (classifier != null ? fileName + "-" + this.classifier : fileName) + "." + this.extension;
        this.path = String.format("%s/%s/%s/%s", this.group.replace('.', IOUtils.DIR_SEPARATOR_UNIX), this.name, this.version, this.fileName);
        String descriptor = String.format("%s:%s:%s", group, name, version);
        descriptor = classifier != null ? descriptor + ":" + classifier : descriptor;
        this.descriptor = "jar".equals(this.extension) ? descriptor : descriptor + "@" + this.extension;
    }

    public static Artifact fromDescriptor(String descriptor) {
        String ext;
        String[] arr = descriptor.split(":", 4);
        if (arr.length != 3 && arr.length != 4) {
            throw new IllegalArgumentException("Artifact name is malformed");
        }
        int last = arr.length - 1;
        String[] splitted = arr[last].split("@");
        if (splitted.length == 2) {
            arr[last] = splitted[0];
            String ext2 = splitted[1];
            ext = ext2;
        } else if (splitted.length <= 2) {
            ext = null;
        } else {
            throw new IllegalArgumentException("Artifact name is malformed");
        }
        return new Artifact(arr[0].replace(IOUtils.DIR_SEPARATOR_WINDOWS, IOUtils.DIR_SEPARATOR_UNIX), arr[1], arr[2], arr.length >= 4 ? arr[3] : null, ext);
    }

    public String getGroup() {
        return this.group;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getClassifier() {
        return this.classifier;
    }

    public Artifact setClassifier(String classifier) {
        return new Artifact(this.group, this.name, this.version, classifier, this.extension);
    }

    public String getExtension() {
        return this.extension;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getPath() {
        return this.path;
    }

    public Path getPath(Path root) {
        return root.resolve(this.path);
    }

    public String toString() {
        return this.descriptor;
    }

    public static class Serializer implements JsonDeserializer<Artifact>, JsonSerializer<Artifact> {
        @Override // com.google.gson.JsonSerializer
        public JsonElement serialize(Artifact src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.gson.JsonDeserializer
        public Artifact deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return Artifact.fromDescriptor(json.getAsJsonPrimitive().getAsString());
            }
            return null;
        }
    }
}

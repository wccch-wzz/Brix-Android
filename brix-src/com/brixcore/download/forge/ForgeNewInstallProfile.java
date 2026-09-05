package com.brixcore.download.forge;

import com.brixcore.auth.yggdrasil.YggdrasilSession$$ExternalSyntheticLambda0;
import com.brixcore.game.Artifact;
import com.brixcore.game.Library;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes3.dex */
public class ForgeNewInstallProfile implements Validation {
    private final Map<String, Datum> data;
    private final String json;
    private final List<Library> libraries;
    private final String minecraft;
    private final Artifact path;
    private final List<Processor> processors;
    private final int spec;
    private final String version;

    public ForgeNewInstallProfile(int spec, String minecraft, String json, String version, Artifact path, List<Library> libraries, List<Processor> processors, Map<String, Datum> data) {
        this.spec = spec;
        this.minecraft = minecraft;
        this.json = json;
        this.version = version;
        this.path = path;
        this.libraries = libraries;
        this.processors = processors;
        this.data = data;
    }

    public int getSpec() {
        return this.spec;
    }

    public String getMinecraft() {
        return this.minecraft;
    }

    public String getJson() {
        return this.json;
    }

    public String getVersion() {
        return this.version;
    }

    public Optional<Artifact> getPath() {
        return Optional.ofNullable(this.path);
    }

    public List<Library> getLibraries() {
        return this.libraries == null ? Collections.emptyList() : this.libraries;
    }

    public List<Processor> getProcessors() {
        return this.processors == null ? Collections.emptyList() : (List) this.processors.stream().filter(new Predicate() { // from class: com.brixcore.download.forge.ForgeNewInstallProfile$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((ForgeNewInstallProfile.Processor) obj).isSide("client");
            }
        }).collect(Collectors.toList());
    }

    public Map<String, String> getData() {
        if (this.data == null) {
            return new HashMap();
        }
        return (Map) this.data.entrySet().stream().collect(Collectors.toMap(new YggdrasilSession$$ExternalSyntheticLambda0(), new Function() { // from class: com.brixcore.download.forge.ForgeNewInstallProfile$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ForgeNewInstallProfile.Datum) ((Map.Entry) obj).getValue()).getClient();
            }
        }));
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        if (this.minecraft == null || this.json == null || this.version == null) {
            throw new JsonParseException("ForgeNewInstallProfile is malformed");
        }
    }

    public static class Processor implements Validation {
        private final List<String> args;
        private final List<Artifact> classpath;
        private final Artifact jar;
        private final Map<String, String> outputs;
        private final List<String> sides;

        public Processor(List<String> sides, Artifact jar, List<Artifact> classpath, List<String> args, Map<String, String> outputs) {
            this.sides = sides;
            this.jar = jar;
            this.classpath = classpath;
            this.args = args;
            this.outputs = outputs;
        }

        public boolean isSide(String side) {
            return this.sides == null || this.sides.contains(side);
        }

        public Artifact getJar() {
            return this.jar;
        }

        public List<Artifact> getClasspath() {
            return this.classpath == null ? Collections.emptyList() : this.classpath;
        }

        public List<String> getArgs() {
            return this.args == null ? Collections.emptyList() : this.args;
        }

        public Map<String, String> getOutputs() {
            return this.outputs == null ? Collections.emptyMap() : this.outputs;
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            if (this.jar == null) {
                throw new JsonParseException("Processor::jar cannot be null");
            }
        }
    }

    public static class Datum {
        private final String client;

        public Datum(String client) {
            this.client = client;
        }

        public String getClient() {
            return this.client;
        }
    }
}

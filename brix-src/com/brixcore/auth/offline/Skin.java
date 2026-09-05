package com.brixcore.auth.offline;

import com.android.tools.r8.RecordTag;
import com.brixcore.auth.yggdrasil.TextureModel;
import com.brixcore.task.Task;
import com.brixcore.util.Lang;
import com.brixcore.util.Pair;
import com.brixcore.util.io.FileUtils;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.antlr.v4.runtime.TokenStreamRewriter;

/* JADX INFO: loaded from: classes14.dex */
public final class Skin extends RecordTag {
    private static Function<Type, InputStream> defaultSkinLoader = new Function() { // from class: com.brixcore.auth.offline.Skin$$ExternalSyntheticLambda6
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Skin.lambda$static$0((Skin.Type) obj);
        }
    };
    private final String localCapePath;
    private final String localSkinPath;
    private final TextureModel textureModel;
    private final Type type;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof Skin)) {
            return false;
        }
        Skin skin = (Skin) obj;
        return Objects.equals(this.type, skin.type) && Objects.equals(this.textureModel, skin.textureModel) && Objects.equals(this.localSkinPath, skin.localSkinPath) && Objects.equals(this.localCapePath, skin.localCapePath);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.type, this.textureModel, this.localSkinPath, this.localCapePath};
    }

    public Skin(Type type, TextureModel textureModel, String localSkinPath, String localCapePath) {
        this.type = type;
        this.textureModel = textureModel;
        this.localSkinPath = localSkinPath;
        this.localCapePath = localCapePath;
    }

    public final boolean equals(Object o) {
        return $record$equals(o);
    }

    public final int hashCode() {
        return Skin$$ExternalSyntheticRecord0.m(this.type, this.textureModel, this.localSkinPath, this.localCapePath);
    }

    public String localCapePath() {
        return this.localCapePath;
    }

    public String localSkinPath() {
        return this.localSkinPath;
    }

    public final String toString() {
        return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Skin.class, "type;textureModel;localSkinPath;localCapePath");
    }

    public Type type() {
        return this.type;
    }

    public enum Type {
        DEFAULT,
        ALEX,
        STEVE,
        LOCAL_FILE,
        YGGDRASIL_API;

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code duplicated, block: B:20:0x003a  */
        public static Type fromStorage(String type) {
            switch (type) {
                case "default":
                    return DEFAULT;
                case "alex":
                    return ALEX;
                case "steve":
                    return STEVE;
                case "local_file":
                    return LOCAL_FILE;
                case "yggdrasil_api":
                    return YGGDRASIL_API;
                default:
                    return null;
            }
        }
    }

    static /* synthetic */ InputStream lambda$static$0(Type type) {
        switch (type) {
            case ALEX:
                return Skin.class.getResourceAsStream("/assets/img/alex.png");
            default:
                return Skin.class.getResourceAsStream("/assets/img/steve.png");
        }
    }

    public static void registerDefaultSkinLoader(Function<Type, InputStream> defaultSkinLoader0) {
        defaultSkinLoader = defaultSkinLoader0;
    }

    public TextureModel textureModel() {
        return this.textureModel == null ? TextureModel.STEVE : this.textureModel;
    }

    public Task<LoadedSkin> load() {
        switch (this.type) {
            case DEFAULT:
                return Task.supplyAsync(new Callable() { // from class: com.brixcore.auth.offline.Skin$$ExternalSyntheticLambda2
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return Skin.lambda$load$1();
                    }
                });
            case ALEX:
            case STEVE:
                if (defaultSkinLoader == null) {
                    return Task.supplyAsync(new Callable() { // from class: com.brixcore.auth.offline.Skin$$ExternalSyntheticLambda3
                        @Override // java.util.concurrent.Callable
                        public final Object call() {
                            return Skin.lambda$load$2();
                        }
                    });
                }
                final TextureModel model = this.type == Type.ALEX ? TextureModel.ALEX : TextureModel.STEVE;
                return Task.supplyAsync(new Callable() { // from class: com.brixcore.auth.offline.Skin$$ExternalSyntheticLambda4
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return this.f$0.lambda$load$3(model);
                    }
                });
            case LOCAL_FILE:
                return Task.supplyAsync(new Callable() { // from class: com.brixcore.auth.offline.Skin$$ExternalSyntheticLambda5
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return this.f$0.lambda$load$4();
                    }
                });
            default:
                throw new UnsupportedOperationException();
        }
    }

    static /* synthetic */ LoadedSkin lambda$load$1() throws Exception {
        return null;
    }

    static /* synthetic */ LoadedSkin lambda$load$2() throws Exception {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ LoadedSkin lambda$load$3(TextureModel model) throws Exception {
        return new LoadedSkin(model, Texture.loadTexture(defaultSkinLoader.apply(this.type)), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ LoadedSkin lambda$load$4() throws Exception {
        Texture skin = null;
        Optional<Path> skinPath = FileUtils.tryGetPath(this.localSkinPath, new String[0]);
        Optional<Path> capePath = FileUtils.tryGetPath(this.localCapePath, new String[0]);
        if (skinPath.isPresent()) {
            skin = Texture.loadTexture(Files.newInputStream(skinPath.get(), new OpenOption[0]));
        }
        Texture cape = capePath.isPresent() ? Texture.loadTexture(Files.newInputStream(capePath.get(), new OpenOption[0])) : null;
        return new LoadedSkin(textureModel(), skin, cape);
    }

    public Map<?, ?> toStorage() {
        return Lang.mapOf(Pair.pair("type", this.type.name().toLowerCase(Locale.ROOT)), Pair.pair("textureModel", textureModel().modelName), Pair.pair("localSkinPath", this.localSkinPath), Pair.pair("localCapePath", this.localCapePath));
    }

    public static Skin fromStorage(Map<?, ?> storage) {
        TextureModel model;
        if (storage == null) {
            return null;
        }
        Type type = (Type) Lang.tryCast(storage.get("type"), String.class).flatMap(new Function() { // from class: com.brixcore.auth.offline.Skin$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Optional.ofNullable(Skin.Type.fromStorage((String) obj));
            }
        }).orElse(Type.DEFAULT);
        String textureModel = (String) Lang.tryCast(storage.get("textureModel"), String.class).orElse(TokenStreamRewriter.DEFAULT_PROGRAM_NAME);
        String localSkinPath = (String) Lang.tryCast(storage.get("localSkinPath"), String.class).orElse(null);
        String localCapePath = (String) Lang.tryCast(storage.get("localCapePath"), String.class).orElse(null);
        if (!TokenStreamRewriter.DEFAULT_PROGRAM_NAME.equals(textureModel) && "slim".equals(textureModel)) {
            model = TextureModel.ALEX;
        } else {
            model = TextureModel.STEVE;
        }
        return new Skin(type, model, localSkinPath, localCapePath);
    }

    public static final class LoadedSkin extends RecordTag {
        private final Texture cape;
        private final TextureModel model;
        private final Texture skin;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof LoadedSkin)) {
                return false;
            }
            LoadedSkin loadedSkin = (LoadedSkin) obj;
            return Objects.equals(this.model, loadedSkin.model) && Objects.equals(this.skin, loadedSkin.skin) && Objects.equals(this.cape, loadedSkin.cape);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.model, this.skin, this.cape};
        }

        public LoadedSkin(TextureModel model, Texture skin, Texture cape) {
            this.model = model;
            this.skin = skin;
            this.cape = cape;
        }

        public Texture cape() {
            return this.cape;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return Skin$LoadedSkin$$ExternalSyntheticRecord0.m(this.model, this.skin, this.cape);
        }

        public TextureModel model() {
            return this.model;
        }

        public Texture skin() {
            return this.skin;
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), LoadedSkin.class, "model;skin;cape");
        }
    }
}

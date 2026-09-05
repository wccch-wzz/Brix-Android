package com.brixcore.game;

import com.brixcore.util.Lang;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes2.dex */
public final class Arguments {
    public static final List<Argument> DEFAULT_GAME_ARGUMENTS;
    public static final List<Argument> DEFAULT_JVM_ARGUMENTS;

    @SerializedName("game")
    private final List<Argument> game;

    @SerializedName("jvm")
    private final List<Argument> jvm;

    public Arguments() {
        this(null, null);
    }

    public Arguments(List<Argument> game, List<Argument> jvm) {
        this.game = game;
        this.jvm = jvm;
    }

    public List<Argument> getGame() {
        if (this.game == null) {
            return null;
        }
        return Collections.unmodifiableList(this.game);
    }

    public Arguments withGame(List<Argument> game) {
        return new Arguments(game, this.jvm);
    }

    public List<Argument> getJvm() {
        if (this.jvm == null) {
            return null;
        }
        return Collections.unmodifiableList(this.jvm);
    }

    public Arguments withJvm(List<Argument> jvm) {
        return new Arguments(this.game, jvm);
    }

    public Arguments addGameArguments(String... gameArguments) {
        return addGameArguments(Arrays.asList(gameArguments));
    }

    public Arguments addGameArguments(List<String> gameArguments) {
        List<Argument> list = (List) gameArguments.stream().map(new Arguments$$ExternalSyntheticLambda2()).collect(Collectors.toList());
        return new Arguments(Lang.merge(getGame(), list), getJvm());
    }

    public Arguments addJVMArguments(String... jvmArguments) {
        return addJVMArguments(Arrays.asList(jvmArguments));
    }

    public Arguments addJVMArguments(List<String> jvmArguments) {
        List<Argument> list = (List) jvmArguments.stream().map(new Arguments$$ExternalSyntheticLambda2()).collect(Collectors.toList());
        return new Arguments(getGame(), Lang.merge(getJvm(), list));
    }

    public static Arguments merge(Arguments a, Arguments b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new Arguments((a.game == null && b.game == null) ? null : Lang.merge(a.game, b.game), (a.jvm == null && b.jvm == null) ? null : Lang.merge(a.jvm, b.jvm));
    }

    public static List<String> parseStringArguments(List<String> arguments, final Map<String, String> keys) {
        return (List) arguments.stream().filter(new Predicate() { // from class: com.brixcore.game.Arguments$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((String) obj);
            }
        }).flatMap(new Function() { // from class: com.brixcore.game.Arguments$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return new StringArgument((String) obj).toString(keys, Collections.emptyMap()).stream();
            }
        }).collect(Collectors.toList());
    }

    public static List<String> parseArguments(List<Argument> arguments, Map<String, String> keys) {
        return parseArguments(arguments, keys, Collections.emptyMap());
    }

    public static List<String> parseArguments(List<Argument> arguments, final Map<String, String> keys, final Map<String, Boolean> features) {
        return (List) arguments.stream().filter(new Predicate() { // from class: com.brixcore.game.Arguments$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Argument) obj);
            }
        }).flatMap(new Function() { // from class: com.brixcore.game.Arguments$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Argument) obj).toString(keys, features).stream();
            }
        }).collect(Collectors.toList());
    }

    static {
        List<Argument> jvm = new ArrayList<>(5);
        jvm.add(new StringArgument("-Djava.library.path=${natives_directory}"));
        jvm.add(new StringArgument("-Dminecraft.launcher.brand=${launcher_name}"));
        jvm.add(new StringArgument("-Dminecraft.launcher.version=${launcher_version}"));
        jvm.add(new StringArgument("-cp"));
        jvm.add(new StringArgument("${classpath}"));
        DEFAULT_JVM_ARGUMENTS = Collections.unmodifiableList(jvm);
        List<Argument> game = new ArrayList<>(1);
        game.add(new RuledArgument(Collections.singletonList(new CompatibilityRule(CompatibilityRule.Action.ALLOW, null, Collections.singletonMap("has_custom_resolution", true))), Arrays.asList("--width", "${resolution_width}", "--height", "${resolution_height}")));
        DEFAULT_GAME_ARGUMENTS = Collections.unmodifiableList(game);
    }
}

package com.brixcore.game;

import com.brixcore.util.StringUtils;
import com.brixcore.util.platform.CommandBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes2.dex */
public final class VersionLibraryBuilder {
    private final List<Argument> game;
    private final List<Argument> jvm;
    private boolean jvmChanged = false;
    private final List<Library> libraries;
    private final List<String> mcArgs;
    private final boolean useMcArgs;
    private final Version version;

    /* JADX INFO: renamed from: $r8$lambda$ZpmJ_VNnAu-jaI1Md1zDJiayPjU, reason: not valid java name */
    public static /* synthetic */ ArrayList m263$r8$lambda$ZpmJ_VNnAujaI1Md1zDJiayPjU(Collection collection) {
        return new ArrayList(collection);
    }

    /* JADX INFO: renamed from: $r8$lambda$wBZ5N9SAmxJLRX-vx8hIUeo_fgc, reason: not valid java name */
    public static /* synthetic */ ArrayList m265$r8$lambda$wBZ5N9SAmxJLRXvx8hIUeo_fgc() {
        return new ArrayList();
    }

    public VersionLibraryBuilder(Version version) {
        this.version = version;
        this.libraries = new ArrayList(version.getLibraries());
        this.mcArgs = (List) version.getMinecraftArguments().map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return StringUtils.tokenize((String) obj);
            }
        }).map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return VersionLibraryBuilder.m263$r8$lambda$ZpmJ_VNnAujaI1Md1zDJiayPjU((List) obj);
            }
        }).orElse(null);
        this.game = (List) version.getArguments().map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Arguments) obj).getGame();
            }
        }).map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return VersionLibraryBuilder.m263$r8$lambda$ZpmJ_VNnAujaI1Md1zDJiayPjU((List) obj);
            }
        }).orElseGet(new Supplier() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda3
            @Override // java.util.function.Supplier
            public final Object get() {
                return VersionLibraryBuilder.m265$r8$lambda$wBZ5N9SAmxJLRXvx8hIUeo_fgc();
            }
        });
        this.jvm = new ArrayList((Collection) version.getArguments().map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Arguments) obj).getJvm();
            }
        }).orElse(Arguments.DEFAULT_JVM_ARGUMENTS));
        this.useMcArgs = this.mcArgs != null;
    }

    public Version build() {
        Version ret;
        Version ret2 = this.version;
        if (this.useMcArgs) {
            this.mcArgs.addAll((Collection) this.game.stream().map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda6
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((Argument) obj).toString(new HashMap(), new HashMap());
                }
            }).flatMap(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda7
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((List) obj).stream();
                }
            }).collect(Collectors.toList()));
            ret = ret2.setArguments(null).setMinecraftArguments(new CommandBuilder().addAllWithoutParsing(this.mcArgs).toString());
        } else {
            ret = ret2.setArguments((Arguments) ret2.getArguments().map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda8
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return this.f$0.lambda$build$1((Arguments) obj);
                }
            }).map(new Function() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda9
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return this.f$0.lambda$build$2((Arguments) obj);
                }
            }).orElse(new Arguments(this.game, this.jvmChanged ? this.jvm : null)));
        }
        return ret.setLibraries(this.libraries);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Arguments lambda$build$1(Arguments args) {
        return args.withGame(this.game);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Arguments lambda$build$2(Arguments args) {
        return this.jvmChanged ? args.withJvm(this.jvm) : args;
    }

    public boolean hasTweakClass(final String tweakClass) {
        return (this.useMcArgs && this.mcArgs.contains(tweakClass)) || this.game.stream().anyMatch(new Predicate() { // from class: com.brixcore.game.VersionLibraryBuilder$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((Argument) obj).toString().equals(tweakClass);
            }
        });
    }

    public void removeTweakClass(String target) {
        replaceTweakClass(target, null, false);
    }

    public void replaceTweakClass(String target, String replacement) {
        replaceTweakClass(target, replacement, true);
    }

    public void addTweakClass(String target, String replacement) {
        replaceTweakClass(target, replacement, false);
    }

    public void replaceTweakClass(String target, String replacement, boolean inPlace) {
        replaceTweakClass(target, replacement, inPlace, false);
    }

    public void replaceTweakClass(String target, String replacement, boolean inPlace, boolean reserve) {
        if (replacement == null && inPlace) {
            throw new IllegalArgumentException("Replacement cannot be null in replace mode");
        }
        boolean replaced = false;
        if (this.useMcArgs) {
            int i = 0;
            while (i + 1 < this.mcArgs.size()) {
                String arg0Str = this.mcArgs.get(i);
                String arg1Str = this.mcArgs.get(i + 1);
                if (arg0Str.equals("--tweakClass") && arg1Str.equals(target)) {
                    if (!replaced && inPlace) {
                        this.mcArgs.set(i + 1, replacement);
                        replaced = true;
                    } else {
                        this.mcArgs.remove(i);
                        this.mcArgs.remove(i);
                        i--;
                    }
                }
                i++;
            }
        }
        int i2 = 0;
        while (i2 + 1 < this.game.size()) {
            Argument arg0 = this.game.get(i2);
            Argument arg1 = this.game.get(i2 + 1);
            if ((arg0 instanceof StringArgument) && (arg1 instanceof StringArgument)) {
                String arg0Str2 = arg0.toString();
                String arg1Str2 = arg1.toString();
                if (arg0Str2.equals("--tweakClass") && arg1Str2.equals(target)) {
                    if (!replaced && inPlace) {
                        this.game.set(i2 + 1, new StringArgument(replacement));
                        replaced = true;
                    } else {
                        this.game.remove(i2);
                        this.game.remove(i2);
                        i2--;
                    }
                }
            }
            i2++;
        }
        if (!replaced && replacement != null) {
            if (reserve) {
                if (this.useMcArgs) {
                    this.mcArgs.add(0, replacement);
                    this.mcArgs.add(0, "--tweakClass");
                    return;
                } else {
                    this.game.add(0, new StringArgument(replacement));
                    this.game.add(0, new StringArgument("--tweakClass"));
                    return;
                }
            }
            this.game.add(new StringArgument("--tweakClass"));
            this.game.add(new StringArgument(replacement));
        }
    }

    public List<Argument> getMutableJvmArguments() {
        this.jvmChanged = true;
        return this.jvm;
    }

    public void addGameArgument(String... args) {
        for (String arg : args) {
            this.game.add(new StringArgument(arg));
        }
    }

    public void addJvmArgument(String... args) {
        this.jvmChanged = true;
        for (String arg : args) {
            this.jvm.add(new StringArgument(arg));
        }
    }

    public void addLibrary(Library library) {
        this.libraries.add(library);
    }
}

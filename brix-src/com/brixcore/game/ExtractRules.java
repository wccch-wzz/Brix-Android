package com.brixcore.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes2.dex */
public final class ExtractRules {
    public static final ExtractRules EMPTY = new ExtractRules();
    private final List<String> exclude;

    public ExtractRules() {
        this.exclude = Collections.emptyList();
    }

    public ExtractRules(List<String> exclude) {
        this.exclude = new ArrayList(exclude);
    }

    public List<String> getExclude() {
        return Collections.unmodifiableList(this.exclude);
    }

    public boolean shouldExtract(final String path) {
        Stream<String> stream = this.exclude.stream();
        Objects.requireNonNull(path);
        return stream.noneMatch(new Predicate() { // from class: com.brixcore.game.ExtractRules$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return path.startsWith((String) obj);
            }
        });
    }
}

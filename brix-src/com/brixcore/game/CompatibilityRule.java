package com.brixcore.game;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/* JADX INFO: loaded from: classes2.dex */
public final class CompatibilityRule {
    private final Action action;
    private final Map<String, Boolean> features;
    private final OSRestriction os;

    public enum Action {
        ALLOW,
        DISALLOW
    }

    public CompatibilityRule() {
        this(Action.ALLOW, null);
    }

    public CompatibilityRule(Action action, OSRestriction os) {
        this(action, os, null);
    }

    public CompatibilityRule(Action action, OSRestriction os, Map<String, Boolean> features) {
        this.action = action;
        this.os = os;
        this.features = features;
    }

    public Optional<Action> getAppliedAction(Map<String, Boolean> supportedFeatures) {
        if (this.os != null && !this.os.allow()) {
            return Optional.empty();
        }
        if (this.features != null) {
            for (Map.Entry<String, Boolean> entry : this.features.entrySet()) {
                if (!Objects.equals(supportedFeatures.get(entry.getKey()), entry.getValue())) {
                    return Optional.empty();
                }
            }
        }
        return Optional.ofNullable(this.action);
    }

    public static boolean appliesToCurrentEnvironment(Collection<CompatibilityRule> rules) {
        return appliesToCurrentEnvironment(rules, Collections.emptyMap());
    }

    public static boolean appliesToCurrentEnvironment(Collection<CompatibilityRule> rules, Map<String, Boolean> features) {
        if (rules == null || rules.isEmpty()) {
            return true;
        }
        Action action = Action.DISALLOW;
        for (CompatibilityRule rule : rules) {
            Optional<Action> thisAction = rule.getAppliedAction(features);
            if (thisAction.isPresent()) {
                Action action2 = thisAction.get();
                action = action2;
            }
        }
        return action == Action.ALLOW;
    }

    public static boolean equals(Collection<CompatibilityRule> rules1, Collection<CompatibilityRule> rules2) {
        return Objects.hashCode(rules1) == Objects.hashCode(rules2);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompatibilityRule that = (CompatibilityRule) o;
        if (this.action == that.action && Objects.equals(this.os, that.os) && Objects.equals(this.features, that.features)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.action, this.os, this.features);
    }
}

package com.brixcore.game;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes2.dex */
@JsonAdapter(Serializer.class)
public class RuledArgument implements Argument {
    private final List<CompatibilityRule> rules;
    private final List<String> value;

    public RuledArgument() {
        this(null, null);
    }

    public RuledArgument(List<CompatibilityRule> rules, List<String> args) {
        this.rules = rules;
        this.value = args;
    }

    public List<CompatibilityRule> getRules() {
        return Collections.unmodifiableList(this.rules);
    }

    public List<String> getValue() {
        return Collections.unmodifiableList(this.value);
    }

    public Object clone() {
        return new RuledArgument(this.rules == null ? null : new ArrayList(this.rules), this.value != null ? new ArrayList(this.value) : null);
    }

    @Override // com.brixcore.game.Argument
    public List<String> toString(final Map<String, String> keys, final Map<String, Boolean> features) {
        if (CompatibilityRule.appliesToCurrentEnvironment(this.rules, features) && this.value != null) {
            return (List) this.value.stream().filter(new Predicate() { // from class: com.brixcore.game.RuledArgument$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((String) obj);
                }
            }).map(new Arguments$$ExternalSyntheticLambda2()).map(new Function() { // from class: com.brixcore.game.RuledArgument$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((StringArgument) obj).toString(keys, features).get(0);
                }
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static class Serializer implements JsonSerializer<RuledArgument>, JsonDeserializer<RuledArgument> {
        @Override // com.google.gson.JsonSerializer
        public JsonElement serialize(RuledArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("rules", context.serialize(src.rules));
            obj.add("value", context.serialize(src.value));
            return obj;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.gson.JsonDeserializer
        public RuledArgument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonElement valuesElement;
            List<String> values;
            JsonObject obj = json.getAsJsonObject();
            List<CompatibilityRule> rules = (List) context.deserialize(obj.get("rules"), new TypeToken<List<CompatibilityRule>>() { // from class: com.brixcore.game.RuledArgument.Serializer.1
            }.getType());
            if (obj.has("values")) {
                valuesElement = obj.get("values");
            } else if (obj.has("value")) {
                valuesElement = obj.get("value");
            } else {
                throw new JsonParseException("RuledArguments instance does not have either value or values member.");
            }
            if (valuesElement.isJsonPrimitive()) {
                values = Collections.singletonList(valuesElement.getAsString());
            } else {
                values = (List) context.deserialize(valuesElement, new TypeToken<List<String>>() { // from class: com.brixcore.game.RuledArgument.Serializer.2
                }.getType());
            }
            return new RuledArgument(rules, values);
        }
    }
}

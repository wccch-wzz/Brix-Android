package com.brixcore.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/* JADX INFO: loaded from: classes8.dex */
public final class InstantTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    public static final InstantTypeAdapter INSTANCE = new InstantTypeAdapter();
    private static final DateTimeFormatter EN_US_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withLocale(Locale.US).withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter ISO_DATE_TIME = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE_TIME).optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd().optionalStart().appendOffset("+HHMM", "+0000").optionalEnd().optionalStart().appendOffset("+HH", "Z").optionalEnd().optionalStart().appendOffsetId().optionalEnd().toFormatter();

    private InstantTypeAdapter() {
    }

    @Override // com.google.gson.JsonSerializer
    public JsonElement serialize(Instant t, Type type, JsonSerializationContext jsc) {
        return new JsonPrimitive(serializeToString(t, ZoneId.systemDefault()));
    }

    @Override // com.google.gson.JsonDeserializer
    public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The instant should be a string value");
        }
        Instant time = deserializeToInstant(json.getAsString());
        if (type == Instant.class) {
            return time;
        }
        throw new IllegalArgumentException(getClass() + " cannot be deserialized to " + type);
    }

    /* JADX WARN: Type inference failed for: r3v6, types: [java.time.ZonedDateTime] */
    public static Instant deserializeToInstant(String string) {
        try {
            return ZonedDateTime.parse(string, EN_US_FORMAT).toInstant();
        } catch (DateTimeParseException e) {
            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(string, ISO_DATE_TIME);
                return zonedDateTime.toInstant();
            } catch (DateTimeParseException e2) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
                } catch (DateTimeParseException e3) {
                    try {
                        string = string.replaceFirst("\\+([0-9]):", "+0$1:");
                        ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(string, ISO_DATE_TIME);
                        return zonedDateTime2.toInstant();
                    } catch (DateTimeParseException e4) {
                        throw new JsonParseException("Invalid instant: " + string, e2);
                    }
                }
            }
        }
    }

    public static String serializeToString(Instant instant, ZoneId zone) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.ofInstant(instant, zone).truncatedTo(ChronoUnit.SECONDS));
    }
}

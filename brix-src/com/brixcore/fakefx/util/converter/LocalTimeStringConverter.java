package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public class LocalTimeStringConverter extends StringConverter<LocalTime> {
    LocalDateTimeStringConverter.LdtConverter<LocalTime> ldtConverter;

    public LocalTimeStringConverter() {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, null, null, null, null, null, null);
    }

    public LocalTimeStringConverter(FormatStyle timeStyle) {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, null, null, null, timeStyle, null, null);
    }

    public LocalTimeStringConverter(FormatStyle timeStyle, Locale locale) {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, null, null, null, timeStyle, locale, null);
    }

    public LocalTimeStringConverter(DateTimeFormatter formatter, DateTimeFormatter parser) {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, formatter, parser, null, null, null, null);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public LocalTime fromString(String value) {
        return (LocalTime) this.ldtConverter.fromString(value);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(LocalTime value) {
        return this.ldtConverter.toString(value);
    }
}

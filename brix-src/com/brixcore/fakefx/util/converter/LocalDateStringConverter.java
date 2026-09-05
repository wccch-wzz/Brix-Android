package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public class LocalDateStringConverter extends StringConverter<LocalDate> {
    LocalDateTimeStringConverter.LdtConverter<LocalDate> ldtConverter;

    public LocalDateStringConverter() {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, null, null, null, null, null, null);
    }

    public LocalDateStringConverter(FormatStyle dateStyle) {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, null, null, dateStyle, null, null, null);
    }

    public LocalDateStringConverter(DateTimeFormatter formatter, DateTimeFormatter parser) {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, formatter, parser, null, null, null, null);
    }

    public LocalDateStringConverter(FormatStyle dateStyle, Locale locale, Chronology chronology) {
        this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, null, null, dateStyle, null, locale, chronology);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public LocalDate fromString(String value) {
        return (LocalDate) this.ldtConverter.fromString(value);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(LocalDate value) {
        return this.ldtConverter.toString(value);
    }
}

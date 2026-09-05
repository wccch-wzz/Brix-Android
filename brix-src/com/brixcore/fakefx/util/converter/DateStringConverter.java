package com.brixcore.fakefx.util.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public class DateStringConverter extends DateTimeStringConverter {
    public DateStringConverter() {
        this(null, null, null, 2);
    }

    public DateStringConverter(int dateStyle) {
        this(null, null, null, dateStyle);
    }

    public DateStringConverter(Locale locale) {
        this(locale, null, null, 2);
    }

    public DateStringConverter(Locale locale, int dateStyle) {
        this(locale, null, null, dateStyle);
    }

    public DateStringConverter(String pattern) {
        this(null, pattern, null, 2);
    }

    public DateStringConverter(Locale locale, String pattern) {
        this(locale, pattern, null, 2);
    }

    public DateStringConverter(DateFormat dateFormat) {
        this(null, null, dateFormat, 2);
    }

    private DateStringConverter(Locale locale, String pattern, DateFormat dateFormat, int dateStyle) {
        super(locale, pattern, dateFormat, dateStyle, 2);
    }

    @Override // com.brixcore.fakefx.util.converter.DateTimeStringConverter
    protected DateFormat getDateFormat() {
        DateFormat df;
        if (this.dateFormat != null) {
            return this.dateFormat;
        }
        if (this.pattern != null) {
            df = new SimpleDateFormat(this.pattern, this.locale);
        } else {
            df = DateFormat.getDateInstance(this.dateStyle, this.locale);
        }
        df.setLenient(false);
        return df;
    }
}

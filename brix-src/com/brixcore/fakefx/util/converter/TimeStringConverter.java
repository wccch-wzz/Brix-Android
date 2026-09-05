package com.brixcore.fakefx.util.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public class TimeStringConverter extends DateTimeStringConverter {
    public TimeStringConverter() {
        this(null, null, null, 2);
    }

    public TimeStringConverter(int timeStyle) {
        this(null, null, null, timeStyle);
    }

    public TimeStringConverter(Locale locale) {
        this(locale, null, null, 2);
    }

    public TimeStringConverter(Locale locale, int timeStyle) {
        this(locale, null, null, timeStyle);
    }

    public TimeStringConverter(String pattern) {
        this(null, pattern, null, 2);
    }

    public TimeStringConverter(Locale locale, String pattern) {
        this(locale, pattern, null, 2);
    }

    public TimeStringConverter(DateFormat dateFormat) {
        this(null, null, dateFormat, 2);
    }

    private TimeStringConverter(Locale locale, String pattern, DateFormat dateFormat, int timeStyle) {
        super(locale, pattern, dateFormat, 2, timeStyle);
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
            df = DateFormat.getTimeInstance(this.timeStyle, this.locale);
        }
        df.setLenient(false);
        return df;
    }
}

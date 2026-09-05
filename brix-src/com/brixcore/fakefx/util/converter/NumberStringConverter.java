package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public class NumberStringConverter extends StringConverter<Number> {
    final Locale locale;
    final NumberFormat numberFormat;
    final String pattern;

    public NumberStringConverter() {
        this(Locale.getDefault());
    }

    public NumberStringConverter(Locale locale) {
        this(locale, null);
    }

    public NumberStringConverter(String pattern) {
        this(Locale.getDefault(), pattern);
    }

    public NumberStringConverter(Locale locale, String pattern) {
        this(locale, pattern, null);
    }

    public NumberStringConverter(NumberFormat numberFormat) {
        this(null, null, numberFormat);
    }

    NumberStringConverter(Locale locale, String pattern, NumberFormat numberFormat) {
        this.locale = locale;
        this.pattern = pattern;
        this.numberFormat = numberFormat;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.util.StringConverter
    public Number fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            String value2 = value.trim();
            if (value2.length() < 1) {
                return null;
            }
            NumberFormat parser = getNumberFormat();
            return parser.parse(value2);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(Number value) {
        if (value == null) {
            return "";
        }
        NumberFormat formatter = getNumberFormat();
        return formatter.format(value);
    }

    protected NumberFormat getNumberFormat() {
        Locale _locale = this.locale == null ? Locale.getDefault() : this.locale;
        if (this.numberFormat != null) {
            return this.numberFormat;
        }
        if (this.pattern != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(_locale);
            return new DecimalFormat(this.pattern, symbols);
        }
        return NumberFormat.getNumberInstance(_locale);
    }
}

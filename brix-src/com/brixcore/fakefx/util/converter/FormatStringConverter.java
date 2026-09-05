package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.util.StringConverter;
import java.text.Format;
import java.text.ParsePosition;

/* JADX INFO: loaded from: classes5.dex */
public class FormatStringConverter<T> extends StringConverter<T> {
    final Format format;

    public FormatStringConverter(@NamedArg("format") Format format) {
        this.format = format;
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public T fromString(String str) {
        if (str == null) {
            return null;
        }
        String strTrim = str.trim();
        if (strTrim.length() < 1) {
            return null;
        }
        Format format = getFormat();
        ParsePosition parsePosition = new ParsePosition(0);
        T t = (T) format.parseObject(strTrim, parsePosition);
        if (parsePosition.getIndex() != strTrim.length()) {
            throw new RuntimeException("Parsed string not according to the format");
        }
        return t;
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(T value) {
        if (value == null) {
            return "";
        }
        Format _format = getFormat();
        return _format.format(value);
    }

    protected Format getFormat() {
        return this.format;
    }
}

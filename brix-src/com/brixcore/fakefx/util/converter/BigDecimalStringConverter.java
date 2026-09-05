package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;
import java.math.BigDecimal;

/* JADX INFO: loaded from: classes5.dex */
public class BigDecimalStringConverter extends StringConverter<BigDecimal> {
    @Override // com.brixcore.fakefx.util.StringConverter
    public BigDecimal fromString(String value) {
        if (value == null) {
            return null;
        }
        String value2 = value.trim();
        if (value2.length() < 1) {
            return null;
        }
        return new BigDecimal(value2);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}

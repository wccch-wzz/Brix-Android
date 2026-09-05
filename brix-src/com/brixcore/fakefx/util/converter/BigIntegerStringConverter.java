package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;
import java.math.BigInteger;

/* JADX INFO: loaded from: classes5.dex */
public class BigIntegerStringConverter extends StringConverter<BigInteger> {
    @Override // com.brixcore.fakefx.util.StringConverter
    public BigInteger fromString(String value) {
        if (value == null) {
            return null;
        }
        String value2 = value.trim();
        if (value2.length() < 1) {
            return null;
        }
        return new BigInteger(value2);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(BigInteger value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}

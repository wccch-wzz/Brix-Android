package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import java.util.Locale;

/* JADX INFO: loaded from: classes16.dex */
public interface NumberExpression extends ObservableNumberValue {
    NumberBinding add(double d);

    NumberBinding add(float f);

    NumberBinding add(int i);

    NumberBinding add(long j);

    NumberBinding add(ObservableNumberValue observableNumberValue);

    StringBinding asString();

    StringBinding asString(String str);

    StringBinding asString(Locale locale, String str);

    NumberBinding divide(double d);

    NumberBinding divide(float f);

    NumberBinding divide(int i);

    NumberBinding divide(long j);

    NumberBinding divide(ObservableNumberValue observableNumberValue);

    BooleanBinding greaterThan(double d);

    BooleanBinding greaterThan(float f);

    BooleanBinding greaterThan(int i);

    BooleanBinding greaterThan(long j);

    BooleanBinding greaterThan(ObservableNumberValue observableNumberValue);

    BooleanBinding greaterThanOrEqualTo(double d);

    BooleanBinding greaterThanOrEqualTo(float f);

    BooleanBinding greaterThanOrEqualTo(int i);

    BooleanBinding greaterThanOrEqualTo(long j);

    BooleanBinding greaterThanOrEqualTo(ObservableNumberValue observableNumberValue);

    BooleanBinding isEqualTo(double d, double d2);

    BooleanBinding isEqualTo(float f, double d);

    BooleanBinding isEqualTo(int i);

    BooleanBinding isEqualTo(int i, double d);

    BooleanBinding isEqualTo(long j);

    BooleanBinding isEqualTo(long j, double d);

    BooleanBinding isEqualTo(ObservableNumberValue observableNumberValue);

    BooleanBinding isEqualTo(ObservableNumberValue observableNumberValue, double d);

    BooleanBinding isNotEqualTo(double d, double d2);

    BooleanBinding isNotEqualTo(float f, double d);

    BooleanBinding isNotEqualTo(int i);

    BooleanBinding isNotEqualTo(int i, double d);

    BooleanBinding isNotEqualTo(long j);

    BooleanBinding isNotEqualTo(long j, double d);

    BooleanBinding isNotEqualTo(ObservableNumberValue observableNumberValue);

    BooleanBinding isNotEqualTo(ObservableNumberValue observableNumberValue, double d);

    BooleanBinding lessThan(double d);

    BooleanBinding lessThan(float f);

    BooleanBinding lessThan(int i);

    BooleanBinding lessThan(long j);

    BooleanBinding lessThan(ObservableNumberValue observableNumberValue);

    BooleanBinding lessThanOrEqualTo(double d);

    BooleanBinding lessThanOrEqualTo(float f);

    BooleanBinding lessThanOrEqualTo(int i);

    BooleanBinding lessThanOrEqualTo(long j);

    BooleanBinding lessThanOrEqualTo(ObservableNumberValue observableNumberValue);

    NumberBinding multiply(double d);

    NumberBinding multiply(float f);

    NumberBinding multiply(int i);

    NumberBinding multiply(long j);

    NumberBinding multiply(ObservableNumberValue observableNumberValue);

    NumberBinding negate();

    NumberBinding subtract(double d);

    NumberBinding subtract(float f);

    NumberBinding subtract(int i);

    NumberBinding subtract(long j);

    NumberBinding subtract(ObservableNumberValue observableNumberValue);
}

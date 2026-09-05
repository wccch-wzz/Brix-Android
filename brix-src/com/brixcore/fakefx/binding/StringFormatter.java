package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.binding.StringBinding;
import com.brixcore.fakefx.beans.binding.StringExpression;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes6.dex */
public abstract class StringFormatter extends StringBinding {
    /* JADX INFO: Access modifiers changed from: private */
    public static Object extractValue(Object obj) {
        return obj instanceof ObservableValue ? ((ObservableValue) obj).getValue2() : obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object[] extractValues(Object[] objs) {
        int n = objs.length;
        Object[] values = new Object[n];
        for (int i = 0; i < n; i++) {
            values[i] = extractValue(objs[i]);
        }
        return values;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ObservableValue<?>[] extractDependencies(Object... args) {
        List<ObservableValue<?>> dependencies = new ArrayList<>();
        for (Object obj : args) {
            if (obj instanceof ObservableValue) {
                dependencies.add((ObservableValue) obj);
            }
        }
        return (ObservableValue[]) dependencies.toArray(new ObservableValue[dependencies.size()]);
    }

    public static StringExpression convert(final ObservableValue<?> observableValue) {
        if (observableValue == null) {
            throw new NullPointerException("ObservableValue must be specified");
        }
        if (observableValue instanceof StringExpression) {
            return (StringExpression) observableValue;
        }
        return new StringBinding() { // from class: com.brixcore.fakefx.binding.StringFormatter.1
            {
                super.bind(observableValue);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(observableValue);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                Object value = observableValue.getValue2();
                return value == null ? "null" : value.toString();
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<ObservableValue<?>> getDependencies() {
                return FXCollections.singletonObservableList(observableValue);
            }
        };
    }

    public static StringExpression concat(final Object... args) {
        if (args == null || args.length == 0) {
            return StringConstant.valueOf("");
        }
        if (args.length == 1) {
            Object cur = args[0];
            return cur instanceof ObservableValue ? convert((ObservableValue) cur) : StringConstant.valueOf(cur.toString());
        }
        if (extractDependencies(args).length == 0) {
            StringBuilder builder = new StringBuilder();
            for (Object obj : args) {
                builder.append(obj);
            }
            return StringConstant.valueOf(builder.toString());
        }
        return new StringFormatter() { // from class: com.brixcore.fakefx.binding.StringFormatter.2
            {
                super.bind(StringFormatter.extractDependencies(args));
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(StringFormatter.extractDependencies(args));
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                StringBuilder builder2 = new StringBuilder();
                for (Object obj2 : args) {
                    builder2.append(StringFormatter.extractValue(obj2));
                }
                return builder2.toString();
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<ObservableValue<?>> getDependencies() {
                return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(StringFormatter.extractDependencies(args)));
            }
        };
    }

    public static StringExpression format(final Locale locale, final String format, final Object... args) {
        if (format == null) {
            throw new NullPointerException("Format cannot be null.");
        }
        if (extractDependencies(args).length == 0) {
            return StringConstant.valueOf(String.format(locale, format, args));
        }
        StringFormatter formatter = new StringFormatter() { // from class: com.brixcore.fakefx.binding.StringFormatter.3
            {
                super.bind(StringFormatter.extractDependencies(args));
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(StringFormatter.extractDependencies(args));
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                Object[] values = StringFormatter.extractValues(args);
                return String.format(locale, format, values);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<ObservableValue<?>> getDependencies() {
                return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(StringFormatter.extractDependencies(args)));
            }
        };
        formatter.get();
        return formatter;
    }

    public static StringExpression format(final String format, final Object... args) {
        if (format == null) {
            throw new NullPointerException("Format cannot be null.");
        }
        if (extractDependencies(args).length == 0) {
            return StringConstant.valueOf(String.format(format, args));
        }
        StringFormatter formatter = new StringFormatter() { // from class: com.brixcore.fakefx.binding.StringFormatter.4
            {
                super.bind(StringFormatter.extractDependencies(args));
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(StringFormatter.extractDependencies(args));
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                Object[] values = StringFormatter.extractValues(args);
                return String.format(format, values);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<ObservableValue<?>> getDependencies() {
                return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(StringFormatter.extractDependencies(args)));
            }
        };
        formatter.get();
        return formatter;
    }
}

package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.property.Property;
import com.brixcore.fakefx.beans.value.ObservableBooleanValue;
import com.brixcore.fakefx.beans.value.ObservableDoubleValue;
import com.brixcore.fakefx.beans.value.ObservableFloatValue;
import com.brixcore.fakefx.beans.value.ObservableIntegerValue;
import com.brixcore.fakefx.beans.value.ObservableLongValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.beans.value.ObservableObjectValue;
import com.brixcore.fakefx.beans.value.ObservableStringValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.BidirectionalBinding;
import com.brixcore.fakefx.binding.BidirectionalContentBinding;
import com.brixcore.fakefx.binding.ContentBinding;
import com.brixcore.fakefx.binding.DoubleConstant;
import com.brixcore.fakefx.binding.FloatConstant;
import com.brixcore.fakefx.binding.IntegerConstant;
import com.brixcore.fakefx.binding.LongConstant;
import com.brixcore.fakefx.binding.ObjectConstant;
import com.brixcore.fakefx.binding.SelectBinding;
import com.brixcore.fakefx.binding.StringConstant;
import com.brixcore.fakefx.binding.StringFormatter;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ImmutableObservableList;
import com.brixcore.fakefx.collections.ObservableArray;
import com.brixcore.fakefx.collections.ObservableFloatArray;
import com.brixcore.fakefx.collections.ObservableIntegerArray;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.fakefx.util.StringConverter;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes16.dex */
public final class Bindings {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    private Bindings() {
    }

    public static BooleanBinding createBooleanBinding(final Callable<Boolean> func, final Observable... dependencies) {
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.1
            {
                bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                try {
                    return ((Boolean) func.call()).booleanValue();
                } catch (Exception e) {
                    return false;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies == null || dependencies.length == 0) {
                    return FXCollections.emptyObservableList();
                }
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static DoubleBinding createDoubleBinding(final Callable<Double> func, final Observable... dependencies) {
        return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.2
            {
                bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
            protected double computeValue() {
                try {
                    return ((Double) func.call()).doubleValue();
                } catch (Exception e) {
                    return 0.0d;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies == null || dependencies.length == 0) {
                    return FXCollections.emptyObservableList();
                }
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static FloatBinding createFloatBinding(final Callable<Float> func, final Observable... dependencies) {
        return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.3
            {
                bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding
            protected float computeValue() {
                try {
                    return ((Float) func.call()).floatValue();
                } catch (Exception e) {
                    return 0.0f;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies == null || dependencies.length == 0) {
                    return FXCollections.emptyObservableList();
                }
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static IntegerBinding createIntegerBinding(final Callable<Integer> func, final Observable... dependencies) {
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.4
            {
                bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                try {
                    return ((Integer) func.call()).intValue();
                } catch (Exception e) {
                    return 0;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies == null || dependencies.length == 0) {
                    return FXCollections.emptyObservableList();
                }
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static LongBinding createLongBinding(final Callable<Long> func, final Observable... dependencies) {
        return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.5
            {
                bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding
            protected long computeValue() {
                try {
                    return ((Long) func.call()).longValue();
                } catch (Exception e) {
                    return 0L;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies == null || dependencies.length == 0) {
                    return FXCollections.emptyObservableList();
                }
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static <T> ObjectBinding<T> createObjectBinding(final Callable<T> func, final Observable... dependencies) {
        return new ObjectBinding<T>() { // from class: com.brixcore.fakefx.beans.binding.Bindings.6
            {
                bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            protected T computeValue() {
                try {
                    return (T) func.call();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies == null || dependencies.length == 0) {
                    return FXCollections.emptyObservableList();
                }
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static StringBinding createStringBinding(final Callable<String> func, final Observable... dependencies) {
        return new StringBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.7
            {
                bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                try {
                    return (String) func.call();
                } catch (Exception e) {
                    return "";
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies == null || dependencies.length == 0) {
                    return FXCollections.emptyObservableList();
                }
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static <T> ObjectBinding<T> select(ObservableValue<?> root, String... steps) {
        return new SelectBinding.AsObject(root, steps);
    }

    public static DoubleBinding selectDouble(ObservableValue<?> root, String... steps) {
        return new SelectBinding.AsDouble(root, steps);
    }

    public static FloatBinding selectFloat(ObservableValue<?> root, String... steps) {
        return new SelectBinding.AsFloat(root, steps);
    }

    public static IntegerBinding selectInteger(ObservableValue<?> root, String... steps) {
        return new SelectBinding.AsInteger(root, steps);
    }

    public static LongBinding selectLong(ObservableValue<?> root, String... steps) {
        return new SelectBinding.AsLong(root, steps);
    }

    public static BooleanBinding selectBoolean(ObservableValue<?> root, String... steps) {
        return new SelectBinding.AsBoolean(root, steps);
    }

    public static StringBinding selectString(ObservableValue<?> root, String... steps) {
        return new SelectBinding.AsString(root, steps);
    }

    public static <T> ObjectBinding<T> select(Object root, String... steps) {
        return new SelectBinding.AsObject(root, steps);
    }

    public static DoubleBinding selectDouble(Object root, String... steps) {
        return new SelectBinding.AsDouble(root, steps);
    }

    public static FloatBinding selectFloat(Object root, String... steps) {
        return new SelectBinding.AsFloat(root, steps);
    }

    public static IntegerBinding selectInteger(Object root, String... steps) {
        return new SelectBinding.AsInteger(root, steps);
    }

    public static LongBinding selectLong(Object root, String... steps) {
        return new SelectBinding.AsLong(root, steps);
    }

    public static BooleanBinding selectBoolean(Object root, String... steps) {
        return new SelectBinding.AsBoolean(root, steps);
    }

    public static StringBinding selectString(Object root, String... steps) {
        return new SelectBinding.AsString(root, steps);
    }

    public static When when(ObservableBooleanValue condition) {
        return new When(condition);
    }

    public static <T> void bindBidirectional(Property<T> property1, Property<T> property2) {
        BidirectionalBinding.bind(property1, property2);
    }

    public static <T> void unbindBidirectional(Property<T> property1, Property<T> property2) {
        BidirectionalBinding.unbind((Property) property1, (Property) property2);
    }

    public static void unbindBidirectional(Object property1, Object property2) {
        BidirectionalBinding.unbind(property1, property2);
    }

    public static void bindBidirectional(Property<String> stringProperty, Property<?> otherProperty, Format format) {
        BidirectionalBinding.bind(stringProperty, otherProperty, format);
    }

    public static <T> void bindBidirectional(Property<String> stringProperty, Property<T> otherProperty, StringConverter<T> converter) {
        BidirectionalBinding.bind(stringProperty, otherProperty, converter);
    }

    public static <E> void bindContentBidirectional(ObservableList<E> list1, ObservableList<E> list2) {
        BidirectionalContentBinding.bind(list1, list2);
    }

    public static <E> void bindContentBidirectional(ObservableSet<E> set1, ObservableSet<E> set2) {
        BidirectionalContentBinding.bind(set1, set2);
    }

    public static <K, V> void bindContentBidirectional(ObservableMap<K, V> map1, ObservableMap<K, V> map2) {
        BidirectionalContentBinding.bind(map1, map2);
    }

    public static void unbindContentBidirectional(Object obj1, Object obj2) {
        BidirectionalContentBinding.unbind(obj1, obj2);
    }

    public static <E> void bindContent(List<E> list1, ObservableList<? extends E> list2) {
        ContentBinding.bind(list1, list2);
    }

    public static <E> void bindContent(Set<E> set1, ObservableSet<? extends E> set2) {
        ContentBinding.bind(set1, set2);
    }

    public static <K, V> void bindContent(Map<K, V> map1, ObservableMap<? extends K, ? extends V> map2) {
        ContentBinding.bind(map1, map2);
    }

    public static void unbindContent(Object obj1, Object obj2) {
        ContentBinding.unbind(obj1, obj2);
    }

    public static NumberBinding negate(final ObservableNumberValue value) {
        if (value == null) {
            throw new NullPointerException("Operand cannot be null.");
        }
        if (value instanceof ObservableDoubleValue) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.8
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return -value.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        if (value instanceof ObservableFloatValue) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.9
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return -value.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        if (value instanceof ObservableLongValue) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.10
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    return -value.longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.11
            {
                super.bind(value);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(value);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return -value.intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    private static NumberBinding add(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.12
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return op1.doubleValue() + op2.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.13
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return op1.floatValue() + op2.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.14
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    return op1.longValue() + op2.longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.15
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op1.intValue() + op2.intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static NumberBinding add(ObservableNumberValue op1, ObservableNumberValue op2) {
        return add(op1, op2, op1, op2);
    }

    public static DoubleBinding add(ObservableNumberValue op1, double op2) {
        return (DoubleBinding) add(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static DoubleBinding add(double op1, ObservableNumberValue op2) {
        return (DoubleBinding) add(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding add(ObservableNumberValue op1, float op2) {
        return add(op1, FloatConstant.valueOf(op2), op1);
    }

    public static NumberBinding add(float op1, ObservableNumberValue op2) {
        return add(FloatConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding add(ObservableNumberValue op1, long op2) {
        return add(op1, LongConstant.valueOf(op2), op1);
    }

    public static NumberBinding add(long op1, ObservableNumberValue op2) {
        return add(LongConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding add(ObservableNumberValue op1, int op2) {
        return add(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static NumberBinding add(int op1, ObservableNumberValue op2) {
        return add(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static NumberBinding subtract(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.16
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return op1.doubleValue() - op2.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.17
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return op1.floatValue() - op2.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.18
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    return op1.longValue() - op2.longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.19
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op1.intValue() - op2.intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static NumberBinding subtract(ObservableNumberValue op1, ObservableNumberValue op2) {
        return subtract(op1, op2, op1, op2);
    }

    public static DoubleBinding subtract(ObservableNumberValue op1, double op2) {
        return (DoubleBinding) subtract(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static DoubleBinding subtract(double op1, ObservableNumberValue op2) {
        return (DoubleBinding) subtract(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding subtract(ObservableNumberValue op1, float op2) {
        return subtract(op1, FloatConstant.valueOf(op2), op1);
    }

    public static NumberBinding subtract(float op1, ObservableNumberValue op2) {
        return subtract(FloatConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding subtract(ObservableNumberValue op1, long op2) {
        return subtract(op1, LongConstant.valueOf(op2), op1);
    }

    public static NumberBinding subtract(long op1, ObservableNumberValue op2) {
        return subtract(LongConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding subtract(ObservableNumberValue op1, int op2) {
        return subtract(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static NumberBinding subtract(int op1, ObservableNumberValue op2) {
        return subtract(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static NumberBinding multiply(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.20
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return op1.doubleValue() * op2.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.21
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return op1.floatValue() * op2.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.22
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    return op1.longValue() * op2.longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.23
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op1.intValue() * op2.intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static NumberBinding multiply(ObservableNumberValue op1, ObservableNumberValue op2) {
        return multiply(op1, op2, op1, op2);
    }

    public static DoubleBinding multiply(ObservableNumberValue op1, double op2) {
        return (DoubleBinding) multiply(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static DoubleBinding multiply(double op1, ObservableNumberValue op2) {
        return (DoubleBinding) multiply(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding multiply(ObservableNumberValue op1, float op2) {
        return multiply(op1, FloatConstant.valueOf(op2), op1);
    }

    public static NumberBinding multiply(float op1, ObservableNumberValue op2) {
        return multiply(FloatConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding multiply(ObservableNumberValue op1, long op2) {
        return multiply(op1, LongConstant.valueOf(op2), op1);
    }

    public static NumberBinding multiply(long op1, ObservableNumberValue op2) {
        return multiply(LongConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding multiply(ObservableNumberValue op1, int op2) {
        return multiply(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static NumberBinding multiply(int op1, ObservableNumberValue op2) {
        return multiply(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static NumberBinding divide(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.24
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return op1.doubleValue() / op2.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.25
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return op1.floatValue() / op2.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.26
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    return op1.longValue() / op2.longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.27
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op1.intValue() / op2.intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static NumberBinding divide(ObservableNumberValue op1, ObservableNumberValue op2) {
        return divide(op1, op2, op1, op2);
    }

    public static DoubleBinding divide(ObservableNumberValue op1, double op2) {
        return (DoubleBinding) divide(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static DoubleBinding divide(double op1, ObservableNumberValue op2) {
        return (DoubleBinding) divide(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding divide(ObservableNumberValue op1, float op2) {
        return divide(op1, FloatConstant.valueOf(op2), op1);
    }

    public static NumberBinding divide(float op1, ObservableNumberValue op2) {
        return divide(FloatConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding divide(ObservableNumberValue op1, long op2) {
        return divide(op1, LongConstant.valueOf(op2), op1);
    }

    public static NumberBinding divide(long op1, ObservableNumberValue op2) {
        return divide(LongConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding divide(ObservableNumberValue op1, int op2) {
        return divide(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static NumberBinding divide(int op1, ObservableNumberValue op2) {
        return divide(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding equal(final ObservableNumberValue op1, final ObservableNumberValue op2, final double epsilon, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.28
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return Math.abs(op1.doubleValue() - op2.doubleValue()) <= epsilon;
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.29
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return ((double) Math.abs(op1.floatValue() - op2.floatValue())) <= epsilon;
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.30
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return ((double) Math.abs(op1.longValue() - op2.longValue())) <= epsilon;
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.31
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return ((double) Math.abs(op1.intValue() - op2.intValue())) <= epsilon;
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding equal(ObservableNumberValue op1, ObservableNumberValue op2, double epsilon) {
        return equal(op1, op2, epsilon, op1, op2);
    }

    public static BooleanBinding equal(ObservableNumberValue op1, ObservableNumberValue op2) {
        return equal(op1, op2, 0.0d, op1, op2);
    }

    public static BooleanBinding equal(ObservableNumberValue op1, double op2, double epsilon) {
        return equal(op1, DoubleConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding equal(double op1, ObservableNumberValue op2, double epsilon) {
        return equal(DoubleConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding equal(ObservableNumberValue op1, float op2, double epsilon) {
        return equal(op1, FloatConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding equal(float op1, ObservableNumberValue op2, double epsilon) {
        return equal(FloatConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding equal(ObservableNumberValue op1, long op2, double epsilon) {
        return equal(op1, LongConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding equal(ObservableNumberValue op1, long op2) {
        return equal(op1, LongConstant.valueOf(op2), 0.0d, op1);
    }

    public static BooleanBinding equal(long op1, ObservableNumberValue op2, double epsilon) {
        return equal(LongConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding equal(long op1, ObservableNumberValue op2) {
        return equal(LongConstant.valueOf(op1), op2, 0.0d, op2);
    }

    public static BooleanBinding equal(ObservableNumberValue op1, int op2, double epsilon) {
        return equal(op1, IntegerConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding equal(ObservableNumberValue op1, int op2) {
        return equal(op1, IntegerConstant.valueOf(op2), 0.0d, op1);
    }

    public static BooleanBinding equal(int op1, ObservableNumberValue op2, double epsilon) {
        return equal(IntegerConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding equal(int op1, ObservableNumberValue op2) {
        return equal(IntegerConstant.valueOf(op1), op2, 0.0d, op2);
    }

    private static BooleanBinding notEqual(final ObservableNumberValue op1, final ObservableNumberValue op2, final double epsilon, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.32
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return Math.abs(op1.doubleValue() - op2.doubleValue()) > epsilon;
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.33
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return ((double) Math.abs(op1.floatValue() - op2.floatValue())) > epsilon;
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.34
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return ((double) Math.abs(op1.longValue() - op2.longValue())) > epsilon;
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.35
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return ((double) Math.abs(op1.intValue() - op2.intValue())) > epsilon;
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, ObservableNumberValue op2, double epsilon) {
        return notEqual(op1, op2, epsilon, op1, op2);
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, ObservableNumberValue op2) {
        return notEqual(op1, op2, 0.0d, op1, op2);
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, double op2, double epsilon) {
        return notEqual(op1, DoubleConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding notEqual(double op1, ObservableNumberValue op2, double epsilon) {
        return notEqual(DoubleConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, float op2, double epsilon) {
        return notEqual(op1, FloatConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding notEqual(float op1, ObservableNumberValue op2, double epsilon) {
        return notEqual(FloatConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, long op2, double epsilon) {
        return notEqual(op1, LongConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, long op2) {
        return notEqual(op1, LongConstant.valueOf(op2), 0.0d, op1);
    }

    public static BooleanBinding notEqual(long op1, ObservableNumberValue op2, double epsilon) {
        return notEqual(LongConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding notEqual(long op1, ObservableNumberValue op2) {
        return notEqual(LongConstant.valueOf(op1), op2, 0.0d, op2);
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, int op2, double epsilon) {
        return notEqual(op1, IntegerConstant.valueOf(op2), epsilon, op1);
    }

    public static BooleanBinding notEqual(ObservableNumberValue op1, int op2) {
        return notEqual(op1, IntegerConstant.valueOf(op2), 0.0d, op1);
    }

    public static BooleanBinding notEqual(int op1, ObservableNumberValue op2, double epsilon) {
        return notEqual(IntegerConstant.valueOf(op1), op2, epsilon, op2);
    }

    public static BooleanBinding notEqual(int op1, ObservableNumberValue op2) {
        return notEqual(IntegerConstant.valueOf(op1), op2, 0.0d, op2);
    }

    private static BooleanBinding greaterThan(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.36
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return op1.doubleValue() > op2.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.37
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return op1.floatValue() > op2.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.38
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return op1.longValue() > op2.longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.39
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op1.intValue() > op2.intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding greaterThan(ObservableNumberValue op1, ObservableNumberValue op2) {
        return greaterThan(op1, op2, op1, op2);
    }

    public static BooleanBinding greaterThan(ObservableNumberValue op1, double op2) {
        return greaterThan(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThan(double op1, ObservableNumberValue op2) {
        return greaterThan(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding greaterThan(ObservableNumberValue op1, float op2) {
        return greaterThan(op1, FloatConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThan(float op1, ObservableNumberValue op2) {
        return greaterThan(FloatConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding greaterThan(ObservableNumberValue op1, long op2) {
        return greaterThan(op1, LongConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThan(long op1, ObservableNumberValue op2) {
        return greaterThan(LongConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding greaterThan(ObservableNumberValue op1, int op2) {
        return greaterThan(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThan(int op1, ObservableNumberValue op2) {
        return greaterThan(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding lessThan(ObservableNumberValue op1, ObservableNumberValue op2, Observable... dependencies) {
        return greaterThan(op2, op1, dependencies);
    }

    public static BooleanBinding lessThan(ObservableNumberValue op1, ObservableNumberValue op2) {
        return lessThan(op1, op2, op1, op2);
    }

    public static BooleanBinding lessThan(ObservableNumberValue op1, double op2) {
        return lessThan(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThan(double op1, ObservableNumberValue op2) {
        return lessThan(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding lessThan(ObservableNumberValue op1, float op2) {
        return lessThan(op1, FloatConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThan(float op1, ObservableNumberValue op2) {
        return lessThan(FloatConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding lessThan(ObservableNumberValue op1, long op2) {
        return lessThan(op1, LongConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThan(long op1, ObservableNumberValue op2) {
        return lessThan(LongConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding lessThan(ObservableNumberValue op1, int op2) {
        return lessThan(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThan(int op1, ObservableNumberValue op2) {
        return lessThan(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding greaterThanOrEqual(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.40
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return op1.doubleValue() >= op2.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.41
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return op1.floatValue() >= op2.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.42
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return op1.longValue() >= op2.longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.43
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op1.intValue() >= op2.intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding greaterThanOrEqual(ObservableNumberValue op1, ObservableNumberValue op2) {
        return greaterThanOrEqual(op1, op2, op1, op2);
    }

    public static BooleanBinding greaterThanOrEqual(ObservableNumberValue op1, double op2) {
        return greaterThanOrEqual(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThanOrEqual(double op1, ObservableNumberValue op2) {
        return greaterThanOrEqual(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding greaterThanOrEqual(ObservableNumberValue op1, float op2) {
        return greaterThanOrEqual(op1, FloatConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThanOrEqual(float op1, ObservableNumberValue op2) {
        return greaterThanOrEqual(FloatConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding greaterThanOrEqual(ObservableNumberValue op1, long op2) {
        return greaterThanOrEqual(op1, LongConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThanOrEqual(long op1, ObservableNumberValue op2) {
        return greaterThanOrEqual(LongConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding greaterThanOrEqual(ObservableNumberValue op1, int op2) {
        return greaterThanOrEqual(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThanOrEqual(int op1, ObservableNumberValue op2) {
        return greaterThanOrEqual(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding lessThanOrEqual(ObservableNumberValue op1, ObservableNumberValue op2, Observable... dependencies) {
        return greaterThanOrEqual(op2, op1, dependencies);
    }

    public static BooleanBinding lessThanOrEqual(ObservableNumberValue op1, ObservableNumberValue op2) {
        return lessThanOrEqual(op1, op2, op1, op2);
    }

    public static BooleanBinding lessThanOrEqual(ObservableNumberValue op1, double op2) {
        return lessThanOrEqual(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThanOrEqual(double op1, ObservableNumberValue op2) {
        return lessThanOrEqual(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding lessThanOrEqual(ObservableNumberValue op1, float op2) {
        return lessThanOrEqual(op1, FloatConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThanOrEqual(float op1, ObservableNumberValue op2) {
        return lessThanOrEqual(FloatConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding lessThanOrEqual(ObservableNumberValue op1, long op2) {
        return lessThanOrEqual(op1, LongConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThanOrEqual(long op1, ObservableNumberValue op2) {
        return lessThanOrEqual(LongConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding lessThanOrEqual(ObservableNumberValue op1, int op2) {
        return lessThanOrEqual(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThanOrEqual(int op1, ObservableNumberValue op2) {
        return lessThanOrEqual(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static NumberBinding min(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.44
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return Math.min(op1.doubleValue(), op2.doubleValue());
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.45
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return Math.min(op1.floatValue(), op2.floatValue());
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.46
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    return Math.min(op1.longValue(), op2.longValue());
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.47
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return Math.min(op1.intValue(), op2.intValue());
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static NumberBinding min(ObservableNumberValue op1, ObservableNumberValue op2) {
        return min(op1, op2, op1, op2);
    }

    public static DoubleBinding min(ObservableNumberValue op1, double op2) {
        return (DoubleBinding) min(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static DoubleBinding min(double op1, ObservableNumberValue op2) {
        return (DoubleBinding) min(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding min(ObservableNumberValue op1, float op2) {
        return min(op1, FloatConstant.valueOf(op2), op1);
    }

    public static NumberBinding min(float op1, ObservableNumberValue op2) {
        return min(FloatConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding min(ObservableNumberValue op1, long op2) {
        return min(op1, LongConstant.valueOf(op2), op1);
    }

    public static NumberBinding min(long op1, ObservableNumberValue op2) {
        return min(LongConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding min(ObservableNumberValue op1, int op2) {
        return min(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static NumberBinding min(int op1, ObservableNumberValue op2) {
        return min(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static NumberBinding max(final ObservableNumberValue op1, final ObservableNumberValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        if ((op1 instanceof ObservableDoubleValue) || (op2 instanceof ObservableDoubleValue)) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.48
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return Math.max(op1.doubleValue(), op2.doubleValue());
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableFloatValue) || (op2 instanceof ObservableFloatValue)) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.49
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return Math.max(op1.floatValue(), op2.floatValue());
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        if ((op1 instanceof ObservableLongValue) || (op2 instanceof ObservableLongValue)) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.50
                {
                    super.bind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(dependencies);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    return Math.max(op1.longValue(), op2.longValue());
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    if (dependencies.length == 1) {
                        return FXCollections.singletonObservableList(dependencies[0]);
                    }
                    return new ImmutableObservableList(dependencies);
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.51
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return Math.max(op1.intValue(), op2.intValue());
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static NumberBinding max(ObservableNumberValue op1, ObservableNumberValue op2) {
        return max(op1, op2, op1, op2);
    }

    public static DoubleBinding max(ObservableNumberValue op1, double op2) {
        return (DoubleBinding) max(op1, DoubleConstant.valueOf(op2), op1);
    }

    public static DoubleBinding max(double op1, ObservableNumberValue op2) {
        return (DoubleBinding) max(DoubleConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding max(ObservableNumberValue op1, float op2) {
        return max(op1, FloatConstant.valueOf(op2), op1);
    }

    public static NumberBinding max(float op1, ObservableNumberValue op2) {
        return max(FloatConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding max(ObservableNumberValue op1, long op2) {
        return max(op1, LongConstant.valueOf(op2), op1);
    }

    public static NumberBinding max(long op1, ObservableNumberValue op2) {
        return max(LongConstant.valueOf(op1), op2, op2);
    }

    public static NumberBinding max(ObservableNumberValue op1, int op2) {
        return max(op1, IntegerConstant.valueOf(op2), op1);
    }

    public static NumberBinding max(int op1, ObservableNumberValue op2) {
        return max(IntegerConstant.valueOf(op1), op2, op2);
    }

    private static class BooleanAndBinding extends BooleanBinding {
        private final InvalidationListener observer = new ShortCircuitAndInvalidator(this);
        private final ObservableBooleanValue op1;
        private final ObservableBooleanValue op2;

        public BooleanAndBinding(ObservableBooleanValue op1, ObservableBooleanValue op2) {
            this.op1 = op1;
            this.op2 = op2;
            op1.addListener(this.observer);
            op2.addListener(this.observer);
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.op1.removeListener(this.observer);
            this.op2.removeListener(this.observer);
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
        protected boolean computeValue() {
            return this.op1.get() && this.op2.get();
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<?> getDependencies() {
            return new ImmutableObservableList(this.op1, this.op2);
        }
    }

    private static class ShortCircuitAndInvalidator implements InvalidationListener {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final WeakReference<BooleanAndBinding> ref;

        private ShortCircuitAndInvalidator(BooleanAndBinding binding) {
            if (binding == null) {
                throw new AssertionError();
            }
            this.ref = new WeakReference<>(binding);
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            BooleanAndBinding binding = this.ref.get();
            if (binding == null) {
                observable.removeListener(this);
            } else if (binding.op1.equals(observable) || (binding.isValid() && binding.op1.get())) {
                binding.invalidate();
            }
        }
    }

    public static BooleanBinding and(ObservableBooleanValue op1, ObservableBooleanValue op2) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new BooleanAndBinding(op1, op2);
    }

    private static class BooleanOrBinding extends BooleanBinding {
        private final InvalidationListener observer = new ShortCircuitOrInvalidator(this);
        private final ObservableBooleanValue op1;
        private final ObservableBooleanValue op2;

        public BooleanOrBinding(ObservableBooleanValue op1, ObservableBooleanValue op2) {
            this.op1 = op1;
            this.op2 = op2;
            op1.addListener(this.observer);
            op2.addListener(this.observer);
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.op1.removeListener(this.observer);
            this.op2.removeListener(this.observer);
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
        protected boolean computeValue() {
            return this.op1.get() || this.op2.get();
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<?> getDependencies() {
            return new ImmutableObservableList(this.op1, this.op2);
        }
    }

    private static class ShortCircuitOrInvalidator implements InvalidationListener {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final WeakReference<BooleanOrBinding> ref;

        private ShortCircuitOrInvalidator(BooleanOrBinding binding) {
            if (binding == null) {
                throw new AssertionError();
            }
            this.ref = new WeakReference<>(binding);
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            BooleanOrBinding binding = this.ref.get();
            if (binding == null) {
                observable.removeListener(this);
            } else if (binding.op1.equals(observable) || (binding.isValid() && !binding.op1.get())) {
                binding.invalidate();
            }
        }
    }

    public static BooleanBinding or(ObservableBooleanValue op1, ObservableBooleanValue op2) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new BooleanOrBinding(op1, op2);
    }

    public static BooleanBinding not(final ObservableBooleanValue op) {
        if (op == null) {
            throw new NullPointerException("Operand cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.52
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return !op.get();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static BooleanBinding equal(final ObservableBooleanValue op1, final ObservableBooleanValue op2) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.53
            {
                super.bind(op1, op2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op1, op2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op1.get() == op2.get();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op1, op2);
            }
        };
    }

    public static BooleanBinding notEqual(final ObservableBooleanValue op1, final ObservableBooleanValue op2) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.54
            {
                super.bind(op1, op2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op1, op2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op1.get() != op2.get();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op1, op2);
            }
        };
    }

    public static StringExpression convert(ObservableValue<?> observableValue) {
        return StringFormatter.convert(observableValue);
    }

    public static StringExpression concat(Object... args) {
        return StringFormatter.concat(args);
    }

    public static StringExpression format(String format, Object... args) {
        return StringFormatter.format(format, args);
    }

    public static StringExpression format(Locale locale, String format, Object... args) {
        return StringFormatter.format(locale, format, args);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getStringSafe(String value) {
        return value == null ? "" : value;
    }

    private static BooleanBinding equal(final ObservableStringValue op1, final ObservableStringValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.55
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                String s1 = Bindings.getStringSafe(op1.get());
                String s2 = Bindings.getStringSafe(op2.get());
                return s1.equals(s2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding equal(ObservableStringValue op1, ObservableStringValue op2) {
        return equal(op1, op2, op1, op2);
    }

    public static BooleanBinding equal(ObservableStringValue op1, String op2) {
        return equal(op1, (ObservableStringValue) StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding equal(String op1, ObservableStringValue op2) {
        return equal((ObservableStringValue) StringConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding notEqual(final ObservableStringValue op1, final ObservableStringValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.56
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                String s1 = Bindings.getStringSafe(op1.get());
                String s2 = Bindings.getStringSafe(op2.get());
                return !s1.equals(s2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding notEqual(ObservableStringValue op1, ObservableStringValue op2) {
        return notEqual(op1, op2, op1, op2);
    }

    public static BooleanBinding notEqual(ObservableStringValue op1, String op2) {
        return notEqual(op1, (ObservableStringValue) StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding notEqual(String op1, ObservableStringValue op2) {
        return notEqual((ObservableStringValue) StringConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding equalIgnoreCase(final ObservableStringValue op1, final ObservableStringValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.57
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                String s1 = Bindings.getStringSafe(op1.get());
                String s2 = Bindings.getStringSafe(op2.get());
                return s1.equalsIgnoreCase(s2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding equalIgnoreCase(ObservableStringValue op1, ObservableStringValue op2) {
        return equalIgnoreCase(op1, op2, op1, op2);
    }

    public static BooleanBinding equalIgnoreCase(ObservableStringValue op1, String op2) {
        return equalIgnoreCase(op1, StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding equalIgnoreCase(String op1, ObservableStringValue op2) {
        return equalIgnoreCase(StringConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding notEqualIgnoreCase(final ObservableStringValue op1, final ObservableStringValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.58
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                String s1 = Bindings.getStringSafe(op1.get());
                String s2 = Bindings.getStringSafe(op2.get());
                return !s1.equalsIgnoreCase(s2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding notEqualIgnoreCase(ObservableStringValue op1, ObservableStringValue op2) {
        return notEqualIgnoreCase(op1, op2, op1, op2);
    }

    public static BooleanBinding notEqualIgnoreCase(ObservableStringValue op1, String op2) {
        return notEqualIgnoreCase(op1, StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding notEqualIgnoreCase(String op1, ObservableStringValue op2) {
        return notEqualIgnoreCase(StringConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding greaterThan(final ObservableStringValue op1, final ObservableStringValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.59
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                String s1 = Bindings.getStringSafe(op1.get());
                String s2 = Bindings.getStringSafe(op2.get());
                return s1.compareTo(s2) > 0;
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding greaterThan(ObservableStringValue op1, ObservableStringValue op2) {
        return greaterThan(op1, op2, op1, op2);
    }

    public static BooleanBinding greaterThan(ObservableStringValue op1, String op2) {
        return greaterThan(op1, StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThan(String op1, ObservableStringValue op2) {
        return greaterThan(StringConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding lessThan(ObservableStringValue op1, ObservableStringValue op2, Observable... dependencies) {
        return greaterThan(op2, op1, dependencies);
    }

    public static BooleanBinding lessThan(ObservableStringValue op1, ObservableStringValue op2) {
        return lessThan(op1, op2, op1, op2);
    }

    public static BooleanBinding lessThan(ObservableStringValue op1, String op2) {
        return lessThan(op1, StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThan(String op1, ObservableStringValue op2) {
        return lessThan(StringConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding greaterThanOrEqual(final ObservableStringValue op1, final ObservableStringValue op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.60
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                String s1 = Bindings.getStringSafe(op1.get());
                String s2 = Bindings.getStringSafe(op2.get());
                return s1.compareTo(s2) >= 0;
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding greaterThanOrEqual(ObservableStringValue op1, ObservableStringValue op2) {
        return greaterThanOrEqual(op1, op2, op1, op2);
    }

    public static BooleanBinding greaterThanOrEqual(ObservableStringValue op1, String op2) {
        return greaterThanOrEqual(op1, StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding greaterThanOrEqual(String op1, ObservableStringValue op2) {
        return greaterThanOrEqual(StringConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding lessThanOrEqual(ObservableStringValue op1, ObservableStringValue op2, Observable... dependencies) {
        return greaterThanOrEqual(op2, op1, dependencies);
    }

    public static BooleanBinding lessThanOrEqual(ObservableStringValue op1, ObservableStringValue op2) {
        return lessThanOrEqual(op1, op2, op1, op2);
    }

    public static BooleanBinding lessThanOrEqual(ObservableStringValue op1, String op2) {
        return lessThanOrEqual(op1, StringConstant.valueOf(op2), op1);
    }

    public static BooleanBinding lessThanOrEqual(String op1, ObservableStringValue op2) {
        return lessThanOrEqual(StringConstant.valueOf(op1), op2, op2);
    }

    public static IntegerBinding length(final ObservableStringValue op) {
        if (op == null) {
            throw new NullPointerException("Operand cannot be null");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.61
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return Bindings.getStringSafe(op.get()).length();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static BooleanBinding isEmpty(final ObservableStringValue op) {
        if (op == null) {
            throw new NullPointerException("Operand cannot be null");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.62
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return Bindings.getStringSafe(op.get()).isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static BooleanBinding isNotEmpty(final ObservableStringValue op) {
        if (op == null) {
            throw new NullPointerException("Operand cannot be null");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.63
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return !Bindings.getStringSafe(op.get()).isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    private static BooleanBinding equal(final ObservableObjectValue<?> op1, final ObservableObjectValue<?> op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.64
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                Object obj1 = op1.get();
                Object obj2 = op2.get();
                if (obj1 == null) {
                    return obj2 == null;
                }
                return obj1.equals(obj2);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding equal(ObservableObjectValue<?> op1, ObservableObjectValue<?> op2) {
        return equal(op1, op2, op1, op2);
    }

    public static BooleanBinding equal(ObservableObjectValue<?> op1, Object op2) {
        return equal(op1, ObjectConstant.valueOf(op2), op1);
    }

    public static BooleanBinding equal(Object op1, ObservableObjectValue<?> op2) {
        return equal(ObjectConstant.valueOf(op1), op2, op2);
    }

    private static BooleanBinding notEqual(final ObservableObjectValue<?> op1, final ObservableObjectValue<?> op2, final Observable... dependencies) {
        if (op1 == null || op2 == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        if (dependencies == null || dependencies.length <= 0) {
            throw new AssertionError();
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.65
            {
                super.bind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(dependencies);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                Object obj1 = op1.get();
                Object obj2 = op2.get();
                if (obj1 == null) {
                    if (obj2 != null) {
                        return true;
                    }
                } else if (!obj1.equals(obj2)) {
                    return true;
                }
                return false;
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                if (dependencies.length == 1) {
                    return FXCollections.singletonObservableList(dependencies[0]);
                }
                return new ImmutableObservableList(dependencies);
            }
        };
    }

    public static BooleanBinding notEqual(ObservableObjectValue<?> op1, ObservableObjectValue<?> op2) {
        return notEqual(op1, op2, op1, op2);
    }

    public static BooleanBinding notEqual(ObservableObjectValue<?> op1, Object op2) {
        return notEqual(op1, ObjectConstant.valueOf(op2), op1);
    }

    public static BooleanBinding notEqual(Object op1, ObservableObjectValue<?> op2) {
        return notEqual(ObjectConstant.valueOf(op1), op2, op2);
    }

    public static BooleanBinding isNull(final ObservableObjectValue<?> op) {
        if (op == null) {
            throw new NullPointerException("Operand cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.66
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op.get() == null;
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static BooleanBinding isNotNull(final ObservableObjectValue<?> op) {
        if (op == null) {
            throw new NullPointerException("Operand cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.67
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op.get() != null;
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <E> IntegerBinding size(final ObservableList<E> op) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.68
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op.size();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <E> BooleanBinding isEmpty(final ObservableList<E> op) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.69
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op.isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <E> BooleanBinding isNotEmpty(final ObservableList<E> op) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.70
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return !op.isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <E> ObjectBinding<E> valueAt(final ObservableList<E> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new ObjectBinding<E>() { // from class: com.brixcore.fakefx.beans.binding.Bindings.71
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            protected E computeValue() {
                try {
                    return op.get(index);
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <E> ObjectBinding<E> valueAt(ObservableList<E> op, ObservableIntegerValue index) {
        return valueAt((ObservableList) op, (ObservableNumberValue) index);
    }

    public static <E> ObjectBinding<E> valueAt(final ObservableList<E> op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new ObjectBinding<E>() { // from class: com.brixcore.fakefx.beans.binding.Bindings.72
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            protected E computeValue() {
                try {
                    return op.get(index.intValue());
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static BooleanBinding booleanValueAt(final ObservableList<Boolean> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.73
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                try {
                    Boolean value = (Boolean) op.get(index);
                    if (value != null) {
                        return value.booleanValue();
                    }
                    return false;
                } catch (IndexOutOfBoundsException e) {
                    return false;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static BooleanBinding booleanValueAt(ObservableList<Boolean> op, ObservableIntegerValue index) {
        return booleanValueAt(op, (ObservableNumberValue) index);
    }

    public static BooleanBinding booleanValueAt(final ObservableList<Boolean> op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.74
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                try {
                    Boolean value = (Boolean) op.get(index.intValue());
                    if (value != null) {
                        return value.booleanValue();
                    }
                    return false;
                } catch (IndexOutOfBoundsException e) {
                    return false;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static DoubleBinding doubleValueAt(final ObservableList<? extends Number> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.75
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
            protected double computeValue() {
                try {
                    Number value = (Number) op.get(index);
                    if (value != null) {
                        return value.doubleValue();
                    }
                    return 0.0d;
                } catch (IndexOutOfBoundsException e) {
                    return 0.0d;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static DoubleBinding doubleValueAt(ObservableList<? extends Number> op, ObservableIntegerValue index) {
        return doubleValueAt(op, (ObservableNumberValue) index);
    }

    public static DoubleBinding doubleValueAt(final ObservableList<? extends Number> op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.76
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
            protected double computeValue() {
                try {
                    Number value = (Number) op.get(index.intValue());
                    if (value != null) {
                        return value.doubleValue();
                    }
                    return 0.0d;
                } catch (IndexOutOfBoundsException e) {
                    return 0.0d;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static FloatBinding floatValueAt(final ObservableList<? extends Number> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.77
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.FloatBinding
            protected float computeValue() {
                try {
                    Number value = (Number) op.get(index);
                    if (value != null) {
                        return value.floatValue();
                    }
                    return 0.0f;
                } catch (IndexOutOfBoundsException e) {
                    return 0.0f;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static FloatBinding floatValueAt(ObservableList<? extends Number> op, ObservableIntegerValue index) {
        return floatValueAt(op, (ObservableNumberValue) index);
    }

    public static FloatBinding floatValueAt(final ObservableList<? extends Number> op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.78
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.FloatBinding
            protected float computeValue() {
                try {
                    Number value = (Number) op.get(index.intValue());
                    if (value != null) {
                        return value.floatValue();
                    }
                    return 0.0f;
                } catch (IndexOutOfBoundsException e) {
                    return 0.0f;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static IntegerBinding integerValueAt(final ObservableList<? extends Number> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.79
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                try {
                    Number value = (Number) op.get(index);
                    if (value != null) {
                        return value.intValue();
                    }
                    return 0;
                } catch (IndexOutOfBoundsException e) {
                    return 0;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static IntegerBinding integerValueAt(ObservableList<? extends Number> op, ObservableIntegerValue index) {
        return integerValueAt(op, (ObservableNumberValue) index);
    }

    public static IntegerBinding integerValueAt(final ObservableList<? extends Number> op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.80
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                try {
                    Number value = (Number) op.get(index.intValue());
                    if (value != null) {
                        return value.intValue();
                    }
                    return 0;
                } catch (IndexOutOfBoundsException e) {
                    return 0;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static LongBinding longValueAt(final ObservableList<? extends Number> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.81
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.LongBinding
            protected long computeValue() {
                try {
                    Number value = (Number) op.get(index);
                    if (value != null) {
                        return value.longValue();
                    }
                    return 0L;
                } catch (IndexOutOfBoundsException e) {
                    return 0L;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static LongBinding longValueAt(ObservableList<? extends Number> op, ObservableIntegerValue index) {
        return longValueAt(op, (ObservableNumberValue) index);
    }

    public static LongBinding longValueAt(final ObservableList<? extends Number> op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.82
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.LongBinding
            protected long computeValue() {
                try {
                    Number value = (Number) op.get(index.intValue());
                    if (value != null) {
                        return value.longValue();
                    }
                    return 0L;
                } catch (IndexOutOfBoundsException e) {
                    return 0L;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static StringBinding stringValueAt(final ObservableList<String> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new StringBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.83
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                try {
                    return (String) op.get(index);
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static StringBinding stringValueAt(ObservableList<String> op, ObservableIntegerValue index) {
        return stringValueAt(op, (ObservableNumberValue) index);
    }

    public static StringBinding stringValueAt(final ObservableList<String> op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new StringBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.84
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                try {
                    return (String) op.get(index.intValue());
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static <E> IntegerBinding size(final ObservableSet<E> op) {
        if (op == null) {
            throw new NullPointerException("Set cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.85
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op.size();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <E> BooleanBinding isEmpty(final ObservableSet<E> op) {
        if (op == null) {
            throw new NullPointerException("Set cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.86
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op.isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <E> BooleanBinding isNotEmpty(final ObservableSet<E> op) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.87
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return !op.isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static IntegerBinding size(final ObservableArray op) {
        if (op == null) {
            throw new NullPointerException("Array cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.88
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op.size();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static FloatBinding floatValueAt(final ObservableFloatArray op, final int index) {
        if (op == null) {
            throw new NullPointerException("Array cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.89
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding
            protected float computeValue() {
                try {
                    return op.get(index);
                } catch (IndexOutOfBoundsException e) {
                    return 0.0f;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static FloatBinding floatValueAt(ObservableFloatArray op, ObservableIntegerValue index) {
        return floatValueAt(op, (ObservableNumberValue) index);
    }

    public static FloatBinding floatValueAt(final ObservableFloatArray op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.90
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding
            protected float computeValue() {
                try {
                    return op.get(index.intValue());
                } catch (IndexOutOfBoundsException e) {
                    return 0.0f;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static IntegerBinding integerValueAt(final ObservableIntegerArray op, final int index) {
        if (op == null) {
            throw new NullPointerException("Array cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.91
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                try {
                    return op.get(index);
                } catch (IndexOutOfBoundsException e) {
                    return 0;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static IntegerBinding integerValueAt(ObservableIntegerArray op, ObservableIntegerValue index) {
        return integerValueAt(op, (ObservableNumberValue) index);
    }

    public static IntegerBinding integerValueAt(final ObservableIntegerArray op, final ObservableNumberValue index) {
        if (op == null || index == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.92
            {
                super.bind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, index);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                try {
                    return op.get(index.intValue());
                } catch (IndexOutOfBoundsException e) {
                    return 0;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, index);
            }
        };
    }

    public static <K, V> IntegerBinding size(final ObservableMap<K, V> op) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.93
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                return op.size();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K, V> BooleanBinding isEmpty(final ObservableMap<K, V> op) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.94
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return op.isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K, V> BooleanBinding isNotEmpty(final ObservableMap<K, V> op) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.95
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                return !op.isEmpty();
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K, V> ObjectBinding<V> valueAt(final ObservableMap<K, V> op, final K key) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new ObjectBinding<V>() { // from class: com.brixcore.fakefx.beans.binding.Bindings.96
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            protected V computeValue() {
                try {
                    return op.get(key);
                } catch (ClassCastException | NullPointerException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K, V> ObjectBinding<V> valueAt(final ObservableMap<K, V> op, final ObservableValue<? extends K> key) {
        if (op == null || key == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new ObjectBinding<V>() { // from class: com.brixcore.fakefx.beans.binding.Bindings.97
            {
                super.bind(op, key);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            protected V computeValue() {
                try {
                    return op.get(key.getValue2());
                } catch (ClassCastException | NullPointerException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, key);
            }
        };
    }

    public static <K> BooleanBinding booleanValueAt(final ObservableMap<K, Boolean> op, final K key) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.98
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                try {
                    Boolean value = (Boolean) op.get(key);
                    if (value != null) {
                        return value.booleanValue();
                    }
                    return false;
                } catch (ClassCastException e) {
                    return false;
                } catch (NullPointerException e2) {
                    return false;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K> BooleanBinding booleanValueAt(final ObservableMap<K, Boolean> op, final ObservableValue<? extends K> key) {
        if (op == null || key == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.99
            {
                super.bind(op, key);
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, key);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
            protected boolean computeValue() {
                try {
                    Boolean value = (Boolean) op.get(key.getValue2());
                    if (value != null) {
                        return value.booleanValue();
                    }
                    return false;
                } catch (ClassCastException e) {
                    return false;
                } catch (NullPointerException e2) {
                    return false;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, key);
            }
        };
    }

    public static <K> DoubleBinding doubleValueAt(final ObservableMap<K, ? extends Number> op, final K key) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.100
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
            protected double computeValue() {
                try {
                    Number value = (Number) op.get(key);
                    if (value != null) {
                        return value.doubleValue();
                    }
                    return 0.0d;
                } catch (ClassCastException e) {
                    return 0.0d;
                } catch (NullPointerException e2) {
                    return 0.0d;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K> DoubleBinding doubleValueAt(final ObservableMap<K, ? extends Number> op, final ObservableValue<? extends K> key) {
        if (op == null || key == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.101
            {
                super.bind(op, key);
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, key);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
            protected double computeValue() {
                try {
                    Number value = (Number) op.get(key.getValue2());
                    if (value != null) {
                        return value.doubleValue();
                    }
                    return 0.0d;
                } catch (ClassCastException e) {
                    return 0.0d;
                } catch (NullPointerException e2) {
                    return 0.0d;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, key);
            }
        };
    }

    public static <K> FloatBinding floatValueAt(final ObservableMap<K, ? extends Number> op, final K key) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.102
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.FloatBinding
            protected float computeValue() {
                try {
                    Number value = (Number) op.get(key);
                    if (value != null) {
                        return value.floatValue();
                    }
                    return 0.0f;
                } catch (ClassCastException e) {
                    return 0.0f;
                } catch (NullPointerException e2) {
                    return 0.0f;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K> FloatBinding floatValueAt(final ObservableMap<K, ? extends Number> op, final ObservableValue<? extends K> key) {
        if (op == null || key == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.103
            {
                super.bind(op, key);
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, key);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.FloatBinding
            protected float computeValue() {
                try {
                    Number value = (Number) op.get(key.getValue2());
                    if (value != null) {
                        return value.floatValue();
                    }
                    return 0.0f;
                } catch (ClassCastException e) {
                    return 0.0f;
                } catch (NullPointerException e2) {
                    return 0.0f;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, key);
            }
        };
    }

    public static <K> IntegerBinding integerValueAt(final ObservableMap<K, ? extends Number> op, final K key) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.104
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                try {
                    Number value = (Number) op.get(key);
                    if (value != null) {
                        return value.intValue();
                    }
                    return 0;
                } catch (ClassCastException e) {
                    return 0;
                } catch (NullPointerException e2) {
                    return 0;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K> IntegerBinding integerValueAt(final ObservableMap<K, ? extends Number> op, final ObservableValue<? extends K> key) {
        if (op == null || key == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.105
            {
                super.bind(op, key);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, key);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                try {
                    Number value = (Number) op.get(key.getValue2());
                    if (value != null) {
                        return value.intValue();
                    }
                    return 0;
                } catch (ClassCastException e) {
                    return 0;
                } catch (NullPointerException e2) {
                    return 0;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, key);
            }
        };
    }

    public static <K> LongBinding longValueAt(final ObservableMap<K, ? extends Number> op, final K key) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.106
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.LongBinding
            protected long computeValue() {
                try {
                    Number value = (Number) op.get(key);
                    if (value != null) {
                        return value.longValue();
                    }
                    return 0L;
                } catch (ClassCastException e) {
                    return 0L;
                } catch (NullPointerException e2) {
                    return 0L;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K> LongBinding longValueAt(final ObservableMap<K, ? extends Number> op, final ObservableValue<? extends K> key) {
        if (op == null || key == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.107
            {
                super.bind(op, key);
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, key);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.LongBinding
            protected long computeValue() {
                try {
                    Number value = (Number) op.get(key.getValue2());
                    if (value != null) {
                        return value.longValue();
                    }
                    return 0L;
                } catch (ClassCastException e) {
                    return 0L;
                } catch (NullPointerException e2) {
                    return 0L;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, key);
            }
        };
    }

    public static <K> StringBinding stringValueAt(final ObservableMap<K, String> op, final K key) {
        if (op == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        return new StringBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.108
            {
                super.bind(op);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                try {
                    return (String) op.get(key);
                } catch (ClassCastException | NullPointerException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }

    public static <K> StringBinding stringValueAt(final ObservableMap<K, String> op, final ObservableValue<? extends K> key) {
        if (op == null || key == null) {
            throw new NullPointerException("Operands cannot be null.");
        }
        return new StringBinding() { // from class: com.brixcore.fakefx.beans.binding.Bindings.109
            {
                super.bind(op, key);
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                super.unbind(op, key);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.fakefx.beans.binding.StringBinding
            protected String computeValue() {
                try {
                    return (String) op.get(key.getValue2());
                } catch (ClassCastException | NullPointerException e) {
                    return null;
                }
            }

            @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<?> getDependencies() {
                return new ImmutableObservableList(op, key);
            }
        };
    }
}

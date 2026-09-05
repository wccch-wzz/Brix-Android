package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.beans.property.BooleanProperty;
import com.brixcore.fakefx.beans.property.DoubleProperty;
import com.brixcore.fakefx.beans.property.FloatProperty;
import com.brixcore.fakefx.beans.property.IntegerProperty;
import com.brixcore.fakefx.beans.property.LongProperty;
import com.brixcore.fakefx.beans.property.Property;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.util.StringConverter;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.ParseException;
import java.util.Objects;

/* JADX INFO: loaded from: classes6.dex */
public abstract class BidirectionalBinding implements InvalidationListener, WeakListener {
    private final int cachedHashCode;

    protected abstract Object getProperty1();

    protected abstract Object getProperty2();

    private static void checkParameters(Object property1, Object property2) {
        Objects.requireNonNull(property1, "Both properties must be specified.");
        Objects.requireNonNull(property2, "Both properties must be specified.");
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind property to itself");
        }
    }

    public static <T> BidirectionalBinding bind(Property<T> property1, Property<T> property2) {
        BidirectionalBinding binding;
        checkParameters(property1, property2);
        if ((property1 instanceof DoubleProperty) && (property2 instanceof DoubleProperty)) {
            binding = new BidirectionalDoubleBinding((DoubleProperty) property1, (DoubleProperty) property2);
        } else if ((property1 instanceof FloatProperty) && (property2 instanceof FloatProperty)) {
            binding = new BidirectionalFloatBinding((FloatProperty) property1, (FloatProperty) property2);
        } else if ((property1 instanceof IntegerProperty) && (property2 instanceof IntegerProperty)) {
            binding = new BidirectionalIntegerBinding((IntegerProperty) property1, (IntegerProperty) property2);
        } else if ((property1 instanceof LongProperty) && (property2 instanceof LongProperty)) {
            binding = new BidirectionalLongBinding((LongProperty) property1, (LongProperty) property2);
        } else if ((property1 instanceof BooleanProperty) && (property2 instanceof BooleanProperty)) {
            binding = new BidirectionalBooleanBinding((BooleanProperty) property1, (BooleanProperty) property2);
        } else {
            binding = new TypedGenericBidirectionalBinding(property1, property2);
        }
        property1.setValue(property2.getValue2());
        property1.getValue2();
        property1.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    public static Object bind(Property<String> stringProperty, Property<?> otherProperty, Format format) {
        checkParameters(stringProperty, otherProperty);
        Objects.requireNonNull(format, "Format cannot be null");
        StringFormatBidirectionalBinding binding = new StringFormatBidirectionalBinding(stringProperty, otherProperty, format);
        stringProperty.setValue(format.format(otherProperty.getValue2()));
        stringProperty.getValue2();
        stringProperty.addListener(binding);
        otherProperty.addListener(binding);
        return binding;
    }

    public static <T> Object bind(Property<String> stringProperty, Property<T> otherProperty, StringConverter<T> converter) {
        checkParameters(stringProperty, otherProperty);
        Objects.requireNonNull(converter, "Converter cannot be null");
        StringConverterBidirectionalBinding binding = new StringConverterBidirectionalBinding(stringProperty, otherProperty, converter);
        stringProperty.setValue(converter.toString(otherProperty.getValue2()));
        stringProperty.getValue2();
        stringProperty.addListener(binding);
        otherProperty.addListener(binding);
        return binding;
    }

    public static <T> void unbind(Property<T> property1, Property<T> property2) {
        checkParameters(property1, property2);
        BidirectionalBinding binding = new UntypedGenericBidirectionalBinding(property1, property2);
        property1.removeListener(binding);
        property2.removeListener(binding);
    }

    public static void unbind(Object property1, Object property2) {
        checkParameters(property1, property2);
        BidirectionalBinding binding = new UntypedGenericBidirectionalBinding(property1, property2);
        if (property1 instanceof ObservableValue) {
            ((ObservableValue) property1).removeListener(binding);
        }
        if (property2 instanceof ObservableValue) {
            ((ObservableValue) property2).removeListener(binding);
        }
    }

    public static BidirectionalBinding bindNumber(Property<Integer> property1, IntegerProperty property2) {
        return bindNumber(property1, (Property<Number>) property2);
    }

    public static BidirectionalBinding bindNumber(Property<Long> property1, LongProperty property2) {
        return bindNumber(property1, (Property<Number>) property2);
    }

    public static BidirectionalBinding bindNumber(Property<Float> property1, FloatProperty property2) {
        return bindNumber(property1, (Property<Number>) property2);
    }

    public static BidirectionalBinding bindNumber(Property<Double> property1, DoubleProperty property2) {
        return bindNumber(property1, (Property<Number>) property2);
    }

    public static BidirectionalBinding bindNumber(IntegerProperty property1, Property<Integer> property2) {
        return bindNumberObject(property1, property2);
    }

    public static BidirectionalBinding bindNumber(LongProperty property1, Property<Long> property2) {
        return bindNumberObject(property1, property2);
    }

    public static BidirectionalBinding bindNumber(FloatProperty property1, Property<Float> property2) {
        return bindNumberObject(property1, property2);
    }

    public static BidirectionalBinding bindNumber(DoubleProperty property1, Property<Double> property2) {
        return bindNumberObject(property1, property2);
    }

    private static <T extends Number> BidirectionalBinding bindNumberObject(Property<Number> property1, Property<T> property2) {
        checkParameters(property1, property2);
        BidirectionalBinding binding = new TypedNumberBidirectionalBinding(property2, property1);
        property1.setValue(property2.getValue2());
        property1.getValue2();
        property1.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T extends Number> BidirectionalBinding bindNumber(Property<T> property, Property<Number> property2) {
        checkParameters(property, property2);
        BidirectionalBinding binding = new TypedNumberBidirectionalBinding(property, property2);
        property.setValue(property2.getValue2());
        property.getValue2();
        property.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    private BidirectionalBinding(Object property1, Object property2) {
        this.cachedHashCode = property1.hashCode() * property2.hashCode();
    }

    public int hashCode() {
        return this.cachedHashCode;
    }

    @Override // com.brixcore.fakefx.beans.WeakListener
    public boolean wasGarbageCollected() {
        return getProperty1() == null || getProperty2() == null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        Object propertyA1 = getProperty1();
        Object propertyA2 = getProperty2();
        if (propertyA1 != null && propertyA2 != null && (obj instanceof BidirectionalBinding)) {
            BidirectionalBinding otherBinding = (BidirectionalBinding) obj;
            Object propertyB1 = otherBinding.getProperty1();
            Object propertyB2 = otherBinding.getProperty2();
            if (propertyB1 == null || propertyB2 == null) {
                return false;
            }
            if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                return true;
            }
            if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
                return true;
            }
        }
        return false;
    }

    private static class BidirectionalBooleanBinding extends BidirectionalBinding {
        private boolean oldValue;
        private final WeakReference<BooleanProperty> propertyRef1;
        private final WeakReference<BooleanProperty> propertyRef2;
        private boolean updating;

        private BidirectionalBooleanBinding(BooleanProperty property1, BooleanProperty property2) {
            super(property1, property2);
            this.oldValue = property1.get();
            this.propertyRef1 = new WeakReference<>(property1);
            this.propertyRef2 = new WeakReference<>(property2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Boolean> getProperty1() {
            return this.propertyRef1.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Boolean> getProperty2() {
            return this.propertyRef2.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable sourceProperty) {
            if (!this.updating) {
                BooleanProperty property1 = this.propertyRef1.get();
                BooleanProperty property2 = this.propertyRef2.get();
                if (property1 == null || property2 == null) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    try {
                        this.updating = true;
                        if (property1 == sourceProperty) {
                            boolean newValue = property1.get();
                            property2.set(newValue);
                            property2.get();
                            this.oldValue = newValue;
                        } else {
                            boolean newValue2 = property2.get();
                            property1.set(newValue2);
                            property1.get();
                            this.oldValue = newValue2;
                        }
                        this.updating = false;
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(this.oldValue);
                                property1.get();
                            } else {
                                property2.set(this.oldValue);
                                property2.get();
                            }
                            throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind((Property) property1, (Property) property2);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties " + property1 + " and " + property2, e2);
                        }
                    }
                } catch (Throwable th) {
                    this.updating = false;
                    throw th;
                }
            }
        }
    }

    private static class BidirectionalDoubleBinding extends BidirectionalBinding {
        private double oldValue;
        private final WeakReference<DoubleProperty> propertyRef1;
        private final WeakReference<DoubleProperty> propertyRef2;
        private boolean updating;

        private BidirectionalDoubleBinding(DoubleProperty property1, DoubleProperty property2) {
            super(property1, property2);
            this.updating = false;
            this.oldValue = property1.get();
            this.propertyRef1 = new WeakReference<>(property1);
            this.propertyRef2 = new WeakReference<>(property2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty1() {
            return this.propertyRef1.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty2() {
            return this.propertyRef2.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable sourceProperty) {
            if (!this.updating) {
                DoubleProperty property1 = this.propertyRef1.get();
                DoubleProperty property2 = this.propertyRef2.get();
                if (property1 == null || property2 == null) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    try {
                        this.updating = true;
                        if (property1 == sourceProperty) {
                            double newValue = property1.get();
                            property2.set(newValue);
                            property2.get();
                            this.oldValue = newValue;
                        } else {
                            double newValue2 = property2.get();
                            property1.set(newValue2);
                            property1.get();
                            this.oldValue = newValue2;
                        }
                        this.updating = false;
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(this.oldValue);
                                property1.get();
                            } else {
                                property2.set(this.oldValue);
                                property2.get();
                            }
                            throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind((Property) property1, (Property) property2);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties " + property1 + " and " + property2, e2);
                        }
                    }
                } catch (Throwable th) {
                    this.updating = false;
                    throw th;
                }
            }
        }
    }

    private static class BidirectionalFloatBinding extends BidirectionalBinding {
        private float oldValue;
        private final WeakReference<FloatProperty> propertyRef1;
        private final WeakReference<FloatProperty> propertyRef2;
        private boolean updating;

        private BidirectionalFloatBinding(FloatProperty property1, FloatProperty property2) {
            super(property1, property2);
            this.updating = false;
            this.oldValue = property1.get();
            this.propertyRef1 = new WeakReference<>(property1);
            this.propertyRef2 = new WeakReference<>(property2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty1() {
            return this.propertyRef1.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty2() {
            return this.propertyRef2.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable sourceProperty) {
            if (!this.updating) {
                FloatProperty property1 = this.propertyRef1.get();
                FloatProperty property2 = this.propertyRef2.get();
                if (property1 == null || property2 == null) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    try {
                        this.updating = true;
                        if (property1 == sourceProperty) {
                            float newValue = property1.get();
                            property2.set(newValue);
                            property2.get();
                            this.oldValue = newValue;
                        } else {
                            float newValue2 = property2.get();
                            property1.set(newValue2);
                            property1.get();
                            this.oldValue = newValue2;
                        }
                        this.updating = false;
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(this.oldValue);
                                property1.get();
                            } else {
                                property2.set(this.oldValue);
                                property2.get();
                            }
                            throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind((Property) property1, (Property) property2);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties " + property1 + " and " + property2, e2);
                        }
                    }
                } catch (Throwable th) {
                    this.updating = false;
                    throw th;
                }
            }
        }
    }

    private static class BidirectionalIntegerBinding extends BidirectionalBinding {
        private int oldValue;
        private final WeakReference<IntegerProperty> propertyRef1;
        private final WeakReference<IntegerProperty> propertyRef2;
        private boolean updating;

        private BidirectionalIntegerBinding(IntegerProperty property1, IntegerProperty property2) {
            super(property1, property2);
            this.updating = false;
            this.oldValue = property1.get();
            this.propertyRef1 = new WeakReference<>(property1);
            this.propertyRef2 = new WeakReference<>(property2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty1() {
            return this.propertyRef1.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty2() {
            return this.propertyRef2.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable sourceProperty) {
            if (!this.updating) {
                IntegerProperty property1 = this.propertyRef1.get();
                IntegerProperty property2 = this.propertyRef2.get();
                if (property1 == null || property2 == null) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    try {
                        this.updating = true;
                        if (property1 == sourceProperty) {
                            int newValue = property1.get();
                            property2.set(newValue);
                            property2.get();
                            this.oldValue = newValue;
                        } else {
                            int newValue2 = property2.get();
                            property1.set(newValue2);
                            property1.get();
                            this.oldValue = newValue2;
                        }
                        this.updating = false;
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(this.oldValue);
                                property1.get();
                            } else {
                                property2.set(this.oldValue);
                                property2.get();
                            }
                            throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind((Property) property1, (Property) property2);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties " + property1 + " and " + property2, e2);
                        }
                    }
                } catch (Throwable th) {
                    this.updating = false;
                    throw th;
                }
            }
        }
    }

    private static class BidirectionalLongBinding extends BidirectionalBinding {
        private long oldValue;
        private final WeakReference<LongProperty> propertyRef1;
        private final WeakReference<LongProperty> propertyRef2;
        private boolean updating;

        private BidirectionalLongBinding(LongProperty property1, LongProperty property2) {
            super(property1, property2);
            this.updating = false;
            this.oldValue = property1.get();
            this.propertyRef1 = new WeakReference<>(property1);
            this.propertyRef2 = new WeakReference<>(property2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty1() {
            return this.propertyRef1.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty2() {
            return this.propertyRef2.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable sourceProperty) {
            if (!this.updating) {
                LongProperty property1 = this.propertyRef1.get();
                LongProperty property2 = this.propertyRef2.get();
                if (property1 == null || property2 == null) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    try {
                        this.updating = true;
                        if (property1 == sourceProperty) {
                            long newValue = property1.get();
                            property2.set(newValue);
                            property2.get();
                            this.oldValue = newValue;
                        } else {
                            long newValue2 = property2.get();
                            property1.set(newValue2);
                            property1.get();
                            this.oldValue = newValue2;
                        }
                        this.updating = false;
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(this.oldValue);
                                property1.get();
                            } else {
                                property2.set(this.oldValue);
                                property2.get();
                            }
                            throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind((Property) property1, (Property) property2);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties " + property1 + " and " + property2, e2);
                        }
                    }
                } catch (Throwable th) {
                    this.updating = false;
                    throw th;
                }
            }
        }
    }

    private static class TypedGenericBidirectionalBinding<T> extends BidirectionalBinding {
        private T oldValue;
        private final WeakReference<Property<T>> propertyRef1;
        private final WeakReference<Property<T>> propertyRef2;
        private boolean updating;

        private TypedGenericBidirectionalBinding(Property<T> property1, Property<T> property2) {
            super(property1, property2);
            this.updating = false;
            this.oldValue = property1.getValue2();
            this.propertyRef1 = new WeakReference<>(property1);
            this.propertyRef2 = new WeakReference<>(property2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<T> getProperty1() {
            return this.propertyRef1.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<T> getProperty2() {
            return this.propertyRef2.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            if (!this.updating) {
                Property<T> property = this.propertyRef1.get();
                Property<T> property2 = this.propertyRef2.get();
                if (property == null || property2 == null) {
                    if (property != null) {
                        property.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    try {
                        this.updating = true;
                        if (property == observable) {
                            T value = property.getValue2();
                            property2.setValue(value);
                            property2.getValue2();
                            this.oldValue = value;
                        } else {
                            T value2 = property2.getValue2();
                            property.setValue(value2);
                            property.getValue2();
                            this.oldValue = value2;
                        }
                        this.updating = false;
                    } catch (RuntimeException e) {
                        try {
                            if (property == observable) {
                                property.setValue(this.oldValue);
                                property.getValue2();
                            } else {
                                property2.setValue(this.oldValue);
                                property2.getValue2();
                            }
                            throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind((Property) property, (Property) property2);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties " + property + " and " + property2, e2);
                        }
                    }
                } catch (Throwable th) {
                    this.updating = false;
                    throw th;
                }
            }
        }
    }

    private static class TypedNumberBidirectionalBinding<T extends Number> extends BidirectionalBinding {
        private T oldValue;
        private final WeakReference<Property<T>> propertyRef1;
        private final WeakReference<Property<Number>> propertyRef2;
        private boolean updating;

        private TypedNumberBidirectionalBinding(Property<T> property1, Property<Number> property2) {
            super(property1, property2);
            this.updating = false;
            this.oldValue = property1.getValue2();
            this.propertyRef1 = new WeakReference<>(property1);
            this.propertyRef2 = new WeakReference<>(property2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<T> getProperty1() {
            return this.propertyRef1.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        public Property<Number> getProperty2() {
            return this.propertyRef2.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable sourceProperty) {
            if (!this.updating) {
                Property<T> property = this.propertyRef1.get();
                Property property2 = this.propertyRef2.get();
                if (property == null || property2 == null) {
                    if (property != null) {
                        property.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    try {
                        this.updating = true;
                        if (property == sourceProperty) {
                            T value = property.getValue2();
                            property2.setValue(value);
                            property2.getValue2();
                            this.oldValue = value;
                        } else {
                            T t = (T) property2.getValue2();
                            property.setValue(t);
                            property.getValue2();
                            this.oldValue = t;
                        }
                        this.updating = false;
                    } catch (RuntimeException e) {
                        try {
                            if (property == sourceProperty) {
                                property.setValue(this.oldValue);
                                property.getValue2();
                            } else {
                                property2.setValue(this.oldValue);
                                property2.getValue2();
                            }
                            throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind((Object) property, (Object) property2);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties " + property + " and " + property2, e2);
                        }
                    }
                } catch (Throwable th) {
                    this.updating = false;
                    throw th;
                }
            }
        }
    }

    private static class UntypedGenericBidirectionalBinding extends BidirectionalBinding {
        private final Object property1;
        private final Object property2;

        public UntypedGenericBidirectionalBinding(Object property1, Object property2) {
            super(property1, property2);
            this.property1 = property1;
            this.property2 = property2;
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        protected Object getProperty1() {
            return this.property1;
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        protected Object getProperty2() {
            return this.property2;
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable sourceProperty) {
            throw new RuntimeException("Should not reach here");
        }
    }

    public static abstract class StringConversionBidirectionalBinding<T> extends BidirectionalBinding {
        private final WeakReference<Property<T>> otherPropertyRef;
        private final WeakReference<Property<String>> stringPropertyRef;
        private boolean updating;

        protected abstract T fromString(String str) throws ParseException;

        protected abstract String toString(T t);

        public StringConversionBidirectionalBinding(Property<String> stringProperty, Property<T> otherProperty) {
            super(stringProperty, otherProperty);
            this.stringPropertyRef = new WeakReference<>(stringProperty);
            this.otherPropertyRef = new WeakReference<>(otherProperty);
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        protected Object getProperty1() {
            return this.stringPropertyRef.get();
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding
        protected Object getProperty2() {
            return this.otherPropertyRef.get();
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            if (!this.updating) {
                Property<String> property = this.stringPropertyRef.get();
                Property property2 = (Property<T>) this.otherPropertyRef.get();
                if (property == null || property2 == null) {
                    if (property != null) {
                        property.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    this.updating = true;
                    if (property == observable) {
                        try {
                            property2.setValue(fromString(property.getValue2()));
                            property2.getValue2();
                        } catch (Exception e) {
                            property2.setValue(null);
                            property2.getValue2();
                        }
                    } else {
                        try {
                            property.setValue(toString(property2.getValue2()));
                            property.getValue2();
                        } catch (Exception e2) {
                            property.setValue("");
                            property.getValue2();
                        }
                    }
                } finally {
                    this.updating = false;
                }
            }
        }
    }

    private static class StringFormatBidirectionalBinding extends StringConversionBidirectionalBinding {
        private final Format format;

        public StringFormatBidirectionalBinding(Property<String> stringProperty, Property<?> otherProperty, Format format) {
            super(stringProperty, otherProperty);
            this.format = format;
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding.StringConversionBidirectionalBinding
        protected String toString(Object value) {
            return this.format.format(value);
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding.StringConversionBidirectionalBinding
        protected Object fromString(String value) throws ParseException {
            return this.format.parseObject(value);
        }
    }

    private static class StringConverterBidirectionalBinding<T> extends StringConversionBidirectionalBinding<T> {
        private final StringConverter<T> converter;

        public StringConverterBidirectionalBinding(Property<String> stringProperty, Property<T> otherProperty, StringConverter<T> converter) {
            super(stringProperty, otherProperty);
            this.converter = converter;
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding.StringConversionBidirectionalBinding
        protected String toString(T value) {
            return this.converter.toString(value);
        }

        @Override // com.brixcore.fakefx.binding.BidirectionalBinding.StringConversionBidirectionalBinding
        protected T fromString(String value) throws ParseException {
            return this.converter.fromString(value);
        }
    }
}

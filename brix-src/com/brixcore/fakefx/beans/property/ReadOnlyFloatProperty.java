package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakInvalidationListener;
import com.brixcore.fakefx.beans.binding.FloatExpression;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlyFloatProperty extends FloatExpression implements ReadOnlyProperty<Number> {
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("ReadOnlyFloatProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static <T extends Number> ReadOnlyFloatProperty readOnlyFloatProperty(ReadOnlyProperty<T> property) {
        if (property != null) {
            return property instanceof ReadOnlyFloatProperty ? (ReadOnlyFloatProperty) property : new AnonymousClass1(property);
        }
        throw new NullPointerException("Property cannot be null");
    }

    /* JADX INFO: renamed from: com.brixcore.fakefx.beans.property.ReadOnlyFloatProperty$1, reason: invalid class name */
    class AnonymousClass1 extends ReadOnlyFloatPropertyBase {
        final /* synthetic */ ReadOnlyProperty val$property;
        private boolean valid = true;
        private final InvalidationListener listener = new InvalidationListener() { // from class: com.brixcore.fakefx.beans.property.ReadOnlyFloatProperty$1$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.beans.InvalidationListener
            public final void invalidated(Observable observable) {
                this.f$0.lambda$$0(observable);
            }
        };

        AnonymousClass1(ReadOnlyProperty readOnlyProperty) {
            this.val$property = readOnlyProperty;
            this.val$property.addListener(new WeakInvalidationListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$$0(Observable observable) {
            if (this.valid) {
                this.valid = false;
                fireValueChangedEvent();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.brixcore.fakefx.beans.value.ObservableFloatValue
        public float get() {
            this.valid = true;
            Number number = (Number) this.val$property.getValue2();
            if (number == null) {
                return 0.0f;
            }
            return number.floatValue();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return null;
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return this.val$property.getName();
        }
    }

    /* JADX INFO: renamed from: com.brixcore.fakefx.beans.property.ReadOnlyFloatProperty$2, reason: invalid class name */
    class AnonymousClass2 extends ReadOnlyObjectPropertyBase<Float> {
        private boolean valid = true;
        private final InvalidationListener listener = new InvalidationListener() { // from class: com.brixcore.fakefx.beans.property.ReadOnlyFloatProperty$2$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.beans.InvalidationListener
            public final void invalidated(Observable observable) {
                this.f$0.lambda$$0(observable);
            }
        };

        AnonymousClass2() {
            ReadOnlyFloatProperty.this.addListener(new WeakInvalidationListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$$0(Observable observable) {
            if (this.valid) {
                this.valid = false;
                fireValueChangedEvent();
            }
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return null;
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlyFloatProperty.this.getName();
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
        public Float get() {
            this.valid = true;
            return ReadOnlyFloatProperty.this.getValue2();
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.FloatExpression
    public ReadOnlyObjectProperty<Float> asObject() {
        return new AnonymousClass2();
    }
}

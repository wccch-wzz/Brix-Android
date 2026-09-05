package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakInvalidationListener;
import com.brixcore.fakefx.beans.binding.IntegerExpression;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlyIntegerProperty extends IntegerExpression implements ReadOnlyProperty<Number> {
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("ReadOnlyIntegerProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static <T extends Number> ReadOnlyIntegerProperty readOnlyIntegerProperty(ReadOnlyProperty<T> property) {
        if (property != null) {
            return property instanceof ReadOnlyIntegerProperty ? (ReadOnlyIntegerProperty) property : new AnonymousClass1(property);
        }
        throw new NullPointerException("Property cannot be null");
    }

    /* JADX INFO: renamed from: com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty$1, reason: invalid class name */
    class AnonymousClass1 extends ReadOnlyIntegerPropertyBase {
        final /* synthetic */ ReadOnlyProperty val$property;
        private boolean valid = true;
        private final InvalidationListener listener = new InvalidationListener() { // from class: com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty$1$$ExternalSyntheticLambda0
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
        @Override // com.brixcore.fakefx.beans.value.ObservableIntegerValue
        public int get() {
            this.valid = true;
            Number number = (Number) this.val$property.getValue2();
            if (number == null) {
                return 0;
            }
            return number.intValue();
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

    /* JADX INFO: renamed from: com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty$2, reason: invalid class name */
    class AnonymousClass2 extends ReadOnlyObjectPropertyBase<Integer> {
        private boolean valid = true;
        private final InvalidationListener listener = new InvalidationListener() { // from class: com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty$2$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.beans.InvalidationListener
            public final void invalidated(Observable observable) {
                this.f$0.lambda$$0(observable);
            }
        };

        AnonymousClass2() {
            ReadOnlyIntegerProperty.this.addListener(new WeakInvalidationListener(this.listener));
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
            return ReadOnlyIntegerProperty.this.getName();
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
        public Integer get() {
            this.valid = true;
            return ReadOnlyIntegerProperty.this.getValue2();
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.IntegerExpression
    public ReadOnlyObjectProperty<Integer> asObject() {
        return new AnonymousClass2();
    }
}

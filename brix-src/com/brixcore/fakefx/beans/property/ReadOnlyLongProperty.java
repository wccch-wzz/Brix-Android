package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakInvalidationListener;
import com.brixcore.fakefx.beans.binding.LongExpression;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlyLongProperty extends LongExpression implements ReadOnlyProperty<Number> {
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("ReadOnlyLongProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static <T extends Number> ReadOnlyLongProperty readOnlyLongProperty(ReadOnlyProperty<T> property) {
        if (property != null) {
            return property instanceof ReadOnlyLongProperty ? (ReadOnlyLongProperty) property : new AnonymousClass1(property);
        }
        throw new NullPointerException("Property cannot be null");
    }

    /* JADX INFO: renamed from: com.brixcore.fakefx.beans.property.ReadOnlyLongProperty$1, reason: invalid class name */
    class AnonymousClass1 extends ReadOnlyLongPropertyBase {
        final /* synthetic */ ReadOnlyProperty val$property;
        private boolean valid = true;
        private final InvalidationListener listener = new InvalidationListener() { // from class: com.brixcore.fakefx.beans.property.ReadOnlyLongProperty$1$$ExternalSyntheticLambda0
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
        @Override // com.brixcore.fakefx.beans.value.ObservableLongValue
        public long get() {
            this.valid = true;
            Number number = (Number) this.val$property.getValue2();
            if (number == null) {
                return 0L;
            }
            return number.longValue();
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

    /* JADX INFO: renamed from: com.brixcore.fakefx.beans.property.ReadOnlyLongProperty$2, reason: invalid class name */
    class AnonymousClass2 extends ReadOnlyObjectPropertyBase<Long> {
        private boolean valid = true;
        private final InvalidationListener listener = new InvalidationListener() { // from class: com.brixcore.fakefx.beans.property.ReadOnlyLongProperty$2$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.beans.InvalidationListener
            public final void invalidated(Observable observable) {
                this.f$0.lambda$$0(observable);
            }
        };

        AnonymousClass2() {
            ReadOnlyLongProperty.this.addListener(new WeakInvalidationListener(this.listener));
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
            return ReadOnlyLongProperty.this.getName();
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
        public Long get() {
            this.valid = true;
            return ReadOnlyLongProperty.this.getValue2();
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.LongExpression
    public ReadOnlyObjectProperty<Long> asObject() {
        return new AnonymousClass2();
    }
}

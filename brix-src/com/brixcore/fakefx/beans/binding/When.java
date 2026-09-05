package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.value.ObservableBooleanValue;
import com.brixcore.fakefx.beans.value.ObservableDoubleValue;
import com.brixcore.fakefx.beans.value.ObservableFloatValue;
import com.brixcore.fakefx.beans.value.ObservableLongValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.beans.value.ObservableObjectValue;
import com.brixcore.fakefx.beans.value.ObservableStringValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.DoubleConstant;
import com.brixcore.fakefx.binding.FloatConstant;
import com.brixcore.fakefx.binding.IntegerConstant;
import com.brixcore.fakefx.binding.LongConstant;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes16.dex */
public class When {
    private final ObservableBooleanValue condition;

    public When(@NamedArg("condition") ObservableBooleanValue condition) {
        if (condition == null) {
            throw new NullPointerException("Condition must be specified.");
        }
        this.condition = condition;
    }

    private static class WhenListener implements InvalidationListener {
        private final ObservableBooleanValue condition;
        private final ObservableValue<?> otherwiseValue;
        private final WeakReference<Binding<?>> ref;
        private final ObservableValue<?> thenValue;

        private WhenListener(Binding<?> binding, ObservableBooleanValue condition, ObservableValue<?> thenValue, ObservableValue<?> otherwiseValue) {
            this.ref = new WeakReference<>(binding);
            this.condition = condition;
            this.thenValue = thenValue;
            this.otherwiseValue = otherwiseValue;
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            Binding<?> binding = this.ref.get();
            if (binding == null) {
                this.condition.removeListener(this);
                if (this.thenValue != null) {
                    this.thenValue.removeListener(this);
                }
                if (this.otherwiseValue != null) {
                    this.otherwiseValue.removeListener(this);
                    return;
                }
                return;
            }
            if (this.condition.equals(observable) || (binding.isValid() && this.condition.get() == observable.equals(this.thenValue))) {
                binding.invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static NumberBinding createNumberCondition(final ObservableBooleanValue condition, final ObservableNumberValue thenValue, final ObservableNumberValue otherwiseValue) {
        if ((thenValue instanceof ObservableDoubleValue) || (otherwiseValue instanceof ObservableDoubleValue)) {
            return new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.When.1
                final InvalidationListener observer;

                {
                    this.observer = new WhenListener(this, condition, thenValue, otherwiseValue);
                    condition.addListener(this.observer);
                    thenValue.addListener(this.observer);
                    otherwiseValue.addListener(this.observer);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    condition.removeListener(this.observer);
                    thenValue.removeListener(this.observer);
                    otherwiseValue.removeListener(this.observer);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    boolean conditionValue = condition.get();
                    return (conditionValue ? thenValue : otherwiseValue).doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableValue<?>> getDependencies() {
                    return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(condition, thenValue, otherwiseValue));
                }
            };
        }
        if ((thenValue instanceof ObservableFloatValue) || (otherwiseValue instanceof ObservableFloatValue)) {
            return new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.When.2
                final InvalidationListener observer;

                {
                    this.observer = new WhenListener(this, condition, thenValue, otherwiseValue);
                    condition.addListener(this.observer);
                    thenValue.addListener(this.observer);
                    otherwiseValue.addListener(this.observer);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    condition.removeListener(this.observer);
                    thenValue.removeListener(this.observer);
                    otherwiseValue.removeListener(this.observer);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    boolean conditionValue = condition.get();
                    return (conditionValue ? thenValue : otherwiseValue).floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableValue<?>> getDependencies() {
                    return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(condition, thenValue, otherwiseValue));
                }
            };
        }
        if ((thenValue instanceof ObservableLongValue) || (otherwiseValue instanceof ObservableLongValue)) {
            return new LongBinding() { // from class: com.brixcore.fakefx.beans.binding.When.3
                final InvalidationListener observer;

                {
                    this.observer = new WhenListener(this, condition, thenValue, otherwiseValue);
                    condition.addListener(this.observer);
                    thenValue.addListener(this.observer);
                    otherwiseValue.addListener(this.observer);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    condition.removeListener(this.observer);
                    thenValue.removeListener(this.observer);
                    otherwiseValue.removeListener(this.observer);
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding
                protected long computeValue() {
                    boolean conditionValue = condition.get();
                    return (conditionValue ? thenValue : otherwiseValue).longValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableValue<?>> getDependencies() {
                    return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(condition, thenValue, otherwiseValue));
                }
            };
        }
        return new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.When.4
            final InvalidationListener observer;

            {
                this.observer = new WhenListener(this, condition, thenValue, otherwiseValue);
                condition.addListener(this.observer);
                thenValue.addListener(this.observer);
                otherwiseValue.addListener(this.observer);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                condition.removeListener(this.observer);
                thenValue.removeListener(this.observer);
                otherwiseValue.removeListener(this.observer);
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
            protected int computeValue() {
                boolean conditionValue = condition.get();
                return (conditionValue ? thenValue : otherwiseValue).intValue();
            }

            @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
            public ObservableList<ObservableValue<?>> getDependencies() {
                return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(condition, thenValue, otherwiseValue));
            }
        };
    }

    public class NumberConditionBuilder {
        private ObservableNumberValue thenValue;

        private NumberConditionBuilder(ObservableNumberValue thenValue) {
            this.thenValue = thenValue;
        }

        public NumberBinding otherwise(ObservableNumberValue otherwiseValue) {
            if (otherwiseValue == null) {
                throw new NullPointerException("Value needs to be specified");
            }
            return When.createNumberCondition(When.this.condition, this.thenValue, otherwiseValue);
        }

        public DoubleBinding otherwise(double otherwiseValue) {
            return (DoubleBinding) otherwise(DoubleConstant.valueOf(otherwiseValue));
        }

        public NumberBinding otherwise(float otherwiseValue) {
            return otherwise(FloatConstant.valueOf(otherwiseValue));
        }

        public NumberBinding otherwise(long otherwiseValue) {
            return otherwise(LongConstant.valueOf(otherwiseValue));
        }

        public NumberBinding otherwise(int otherwiseValue) {
            return otherwise(IntegerConstant.valueOf(otherwiseValue));
        }
    }

    public NumberConditionBuilder then(ObservableNumberValue thenValue) {
        if (thenValue == null) {
            throw new NullPointerException("Value needs to be specified");
        }
        return new NumberConditionBuilder(thenValue);
    }

    public NumberConditionBuilder then(double thenValue) {
        return new NumberConditionBuilder(DoubleConstant.valueOf(thenValue));
    }

    public NumberConditionBuilder then(float thenValue) {
        return new NumberConditionBuilder(FloatConstant.valueOf(thenValue));
    }

    public NumberConditionBuilder then(long thenValue) {
        return new NumberConditionBuilder(LongConstant.valueOf(thenValue));
    }

    public NumberConditionBuilder then(int thenValue) {
        return new NumberConditionBuilder(IntegerConstant.valueOf(thenValue));
    }

    private class BooleanCondition extends BooleanBinding {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final ObservableBooleanValue falseResult;
        private final boolean falseResultValue;
        private final InvalidationListener observer;
        private final ObservableBooleanValue trueResult;
        private final boolean trueResultValue;

        private BooleanCondition(ObservableBooleanValue then, ObservableBooleanValue otherwise) {
            this.trueResult = then;
            this.trueResultValue = false;
            this.falseResult = otherwise;
            this.falseResultValue = false;
            this.observer = new WhenListener(this, When.this.condition, then, otherwise);
            When.this.condition.addListener(this.observer);
            then.addListener(this.observer);
            otherwise.addListener(this.observer);
        }

        private BooleanCondition(boolean then, ObservableBooleanValue otherwise) {
            this.trueResult = null;
            this.trueResultValue = then;
            this.falseResult = otherwise;
            this.falseResultValue = false;
            this.observer = new WhenListener(this, When.this.condition, null, otherwise);
            When.this.condition.addListener(this.observer);
            otherwise.addListener(this.observer);
        }

        private BooleanCondition(ObservableBooleanValue then, boolean otherwise) {
            this.trueResult = then;
            this.trueResultValue = false;
            this.falseResult = null;
            this.falseResultValue = otherwise;
            this.observer = new WhenListener(this, When.this.condition, then, null);
            When.this.condition.addListener(this.observer);
            then.addListener(this.observer);
        }

        private BooleanCondition(boolean then, boolean otherwise) {
            this.trueResult = null;
            this.trueResultValue = then;
            this.falseResult = null;
            this.falseResultValue = otherwise;
            this.observer = null;
            super.bind(When.this.condition);
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
        protected boolean computeValue() {
            boolean conditionValue = When.this.condition.get();
            if (conditionValue) {
                return this.trueResult != null ? this.trueResult.get() : this.trueResultValue;
            }
            return this.falseResult != null ? this.falseResult.get() : this.falseResultValue;
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            if (this.observer == null) {
                super.unbind(When.this.condition);
                return;
            }
            When.this.condition.removeListener(this.observer);
            if (this.trueResult != null) {
                this.trueResult.removeListener(this.observer);
            }
            if (this.falseResult != null) {
                this.falseResult.removeListener(this.observer);
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            if (When.this.condition == null) {
                throw new AssertionError();
            }
            ObservableList<ObservableValue<?>> seq = FXCollections.observableArrayList(When.this.condition);
            if (this.trueResult != null) {
                seq.add(this.trueResult);
            }
            if (this.falseResult != null) {
                seq.add(this.falseResult);
            }
            return FXCollections.unmodifiableObservableList(seq);
        }
    }

    public class BooleanConditionBuilder {
        private ObservableBooleanValue trueResult;
        private boolean trueResultValue;

        private BooleanConditionBuilder(ObservableBooleanValue thenValue) {
            this.trueResult = thenValue;
        }

        private BooleanConditionBuilder(boolean thenValue) {
            this.trueResultValue = thenValue;
        }

        public BooleanBinding otherwise(ObservableBooleanValue otherwiseValue) {
            if (otherwiseValue == null) {
                throw new NullPointerException("Value needs to be specified");
            }
            if (this.trueResult != null) {
                return new BooleanCondition(this.trueResult, otherwiseValue);
            }
            return new BooleanCondition(this.trueResultValue, otherwiseValue);
        }

        public BooleanBinding otherwise(boolean otherwiseValue) {
            if (this.trueResult != null) {
                return new BooleanCondition(this.trueResult, otherwiseValue);
            }
            return new BooleanCondition(this.trueResultValue, otherwiseValue);
        }
    }

    public BooleanConditionBuilder then(ObservableBooleanValue thenValue) {
        if (thenValue == null) {
            throw new NullPointerException("Value needs to be specified");
        }
        return new BooleanConditionBuilder(thenValue);
    }

    public BooleanConditionBuilder then(boolean thenValue) {
        return new BooleanConditionBuilder(thenValue);
    }

    private class StringCondition extends StringBinding {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final ObservableStringValue falseResult;
        private final String falseResultValue;
        private final InvalidationListener observer;
        private final ObservableStringValue trueResult;
        private final String trueResultValue;

        private StringCondition(ObservableStringValue then, ObservableStringValue otherwise) {
            this.trueResult = then;
            this.trueResultValue = "";
            this.falseResult = otherwise;
            this.falseResultValue = "";
            this.observer = new WhenListener(this, When.this.condition, then, otherwise);
            When.this.condition.addListener(this.observer);
            then.addListener(this.observer);
            otherwise.addListener(this.observer);
        }

        private StringCondition(String then, ObservableStringValue otherwise) {
            this.trueResult = null;
            this.trueResultValue = then;
            this.falseResult = otherwise;
            this.falseResultValue = "";
            this.observer = new WhenListener(this, When.this.condition, null, otherwise);
            When.this.condition.addListener(this.observer);
            otherwise.addListener(this.observer);
        }

        private StringCondition(ObservableStringValue then, String otherwise) {
            this.trueResult = then;
            this.trueResultValue = "";
            this.falseResult = null;
            this.falseResultValue = otherwise;
            this.observer = new WhenListener(this, When.this.condition, then, null);
            When.this.condition.addListener(this.observer);
            then.addListener(this.observer);
        }

        private StringCondition(String then, String otherwise) {
            this.trueResult = null;
            this.trueResultValue = then;
            this.falseResult = null;
            this.falseResultValue = otherwise;
            this.observer = null;
            super.bind(When.this.condition);
        }

        @Override // com.brixcore.fakefx.beans.binding.StringBinding
        protected String computeValue() {
            boolean conditionValue = When.this.condition.get();
            if (conditionValue) {
                return this.trueResult != null ? this.trueResult.get() : this.trueResultValue;
            }
            return this.falseResult != null ? this.falseResult.get() : this.falseResultValue;
        }

        @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            if (this.observer == null) {
                super.unbind(When.this.condition);
                return;
            }
            When.this.condition.removeListener(this.observer);
            if (this.trueResult != null) {
                this.trueResult.removeListener(this.observer);
            }
            if (this.falseResult != null) {
                this.falseResult.removeListener(this.observer);
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            if (When.this.condition == null) {
                throw new AssertionError();
            }
            ObservableList<ObservableValue<?>> seq = FXCollections.observableArrayList(When.this.condition);
            if (this.trueResult != null) {
                seq.add(this.trueResult);
            }
            if (this.falseResult != null) {
                seq.add(this.falseResult);
            }
            return FXCollections.unmodifiableObservableList(seq);
        }
    }

    public class StringConditionBuilder {
        private ObservableStringValue trueResult;
        private String trueResultValue;

        private StringConditionBuilder(ObservableStringValue thenValue) {
            this.trueResult = thenValue;
        }

        private StringConditionBuilder(String thenValue) {
            this.trueResultValue = thenValue;
        }

        public StringBinding otherwise(ObservableStringValue otherwiseValue) {
            if (this.trueResult != null) {
                return new StringCondition(this.trueResult, otherwiseValue);
            }
            return new StringCondition(this.trueResultValue, otherwiseValue);
        }

        public StringBinding otherwise(String otherwiseValue) {
            if (this.trueResult != null) {
                return new StringCondition(this.trueResult, otherwiseValue);
            }
            return new StringCondition(this.trueResultValue, otherwiseValue);
        }
    }

    public StringConditionBuilder then(ObservableStringValue thenValue) {
        if (thenValue == null) {
            throw new NullPointerException("Value needs to be specified");
        }
        return new StringConditionBuilder(thenValue);
    }

    public StringConditionBuilder then(String thenValue) {
        return new StringConditionBuilder(thenValue);
    }

    private class ObjectCondition<T> extends ObjectBinding<T> {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final ObservableObjectValue<T> falseResult;
        private final T falseResultValue;
        private final InvalidationListener observer;
        private final ObservableObjectValue<T> trueResult;
        private final T trueResultValue;

        private ObjectCondition(ObservableObjectValue<T> then, ObservableObjectValue<T> otherwise) {
            this.trueResult = then;
            this.trueResultValue = null;
            this.falseResult = otherwise;
            this.falseResultValue = null;
            this.observer = new WhenListener(this, When.this.condition, then, otherwise);
            When.this.condition.addListener(this.observer);
            then.addListener(this.observer);
            otherwise.addListener(this.observer);
        }

        private ObjectCondition(T then, ObservableObjectValue<T> otherwise) {
            this.trueResult = null;
            this.trueResultValue = then;
            this.falseResult = otherwise;
            this.falseResultValue = null;
            this.observer = new WhenListener(this, When.this.condition, null, otherwise);
            When.this.condition.addListener(this.observer);
            otherwise.addListener(this.observer);
        }

        private ObjectCondition(ObservableObjectValue<T> then, T otherwise) {
            this.trueResult = then;
            this.trueResultValue = null;
            this.falseResult = null;
            this.falseResultValue = otherwise;
            this.observer = new WhenListener(this, When.this.condition, then, null);
            When.this.condition.addListener(this.observer);
            then.addListener(this.observer);
        }

        private ObjectCondition(T then, T otherwise) {
            this.trueResult = null;
            this.trueResultValue = then;
            this.falseResult = null;
            this.falseResultValue = otherwise;
            this.observer = null;
            super.bind(When.this.condition);
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
        protected T computeValue() {
            boolean conditionValue = When.this.condition.get();
            if (conditionValue) {
                return this.trueResult != null ? this.trueResult.get() : this.trueResultValue;
            }
            return this.falseResult != null ? this.falseResult.get() : this.falseResultValue;
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            if (this.observer == null) {
                super.unbind(When.this.condition);
                return;
            }
            When.this.condition.removeListener(this.observer);
            if (this.trueResult != null) {
                this.trueResult.removeListener(this.observer);
            }
            if (this.falseResult != null) {
                this.falseResult.removeListener(this.observer);
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            if (When.this.condition == null) {
                throw new AssertionError();
            }
            ObservableList<ObservableValue<?>> seq = FXCollections.observableArrayList(When.this.condition);
            if (this.trueResult != null) {
                seq.add(this.trueResult);
            }
            if (this.falseResult != null) {
                seq.add(this.falseResult);
            }
            return FXCollections.unmodifiableObservableList(seq);
        }
    }

    public class ObjectConditionBuilder<T> {
        private ObservableObjectValue<T> trueResult;
        private T trueResultValue;

        private ObjectConditionBuilder(ObservableObjectValue<T> thenValue) {
            this.trueResult = thenValue;
        }

        private ObjectConditionBuilder(T thenValue) {
            this.trueResultValue = thenValue;
        }

        public ObjectBinding<T> otherwise(ObservableObjectValue<T> otherwiseValue) {
            if (otherwiseValue == null) {
                throw new NullPointerException("Value needs to be specified");
            }
            if (this.trueResult != null) {
                return new ObjectCondition((ObservableObjectValue) this.trueResult, (ObservableObjectValue) otherwiseValue);
            }
            return new ObjectCondition((Object) this.trueResultValue, (ObservableObjectValue) otherwiseValue);
        }

        public ObjectBinding<T> otherwise(T otherwiseValue) {
            if (this.trueResult != null) {
                return new ObjectCondition((ObservableObjectValue) this.trueResult, (Object) otherwiseValue);
            }
            return new ObjectCondition(this.trueResultValue, otherwiseValue);
        }
    }

    public <T> ObjectConditionBuilder<T> then(ObservableObjectValue<T> thenValue) {
        if (thenValue == null) {
            throw new NullPointerException("Value needs to be specified");
        }
        return new ObjectConditionBuilder<>((ObservableObjectValue) thenValue);
    }

    public <T> ObjectConditionBuilder<T> then(T thenValue) {
        return new ObjectConditionBuilder<>(thenValue);
    }
}

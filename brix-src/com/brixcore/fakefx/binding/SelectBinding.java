package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakInvalidationListener;
import com.brixcore.fakefx.beans.binding.Binding;
import com.brixcore.fakefx.beans.binding.BooleanBinding;
import com.brixcore.fakefx.beans.binding.DoubleBinding;
import com.brixcore.fakefx.beans.binding.FloatBinding;
import com.brixcore.fakefx.beans.binding.IntegerBinding;
import com.brixcore.fakefx.beans.binding.LongBinding;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.fakefx.beans.binding.StringBinding;
import com.brixcore.fakefx.beans.value.ObservableBooleanValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.property.JavaBeanAccessHelper;
import com.brixcore.fakefx.property.PropertyReference;
import java.util.Arrays;

/* JADX INFO: loaded from: classes6.dex */
public class SelectBinding {
    private SelectBinding() {
    }

    public static class AsObject<T> extends ObjectBinding<T> {
        private final SelectBindingHelper helper;

        public AsObject(ObservableValue<?> root, String... steps) {
            this.helper = new SelectBindingHelper((Binding) this, (ObservableValue) root, steps);
        }

        public AsObject(Object root, String... steps) {
            this.helper = new SelectBindingHelper(this, root, steps);
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
        protected void onInvalidating() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
        protected T computeValue() {
            ObservableValue<?> observableValue = this.helper.getObservableValue();
            if (observableValue == null) {
                return null;
            }
            try {
                return (T) observableValue.getValue2();
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            return this.helper.getDependencies();
        }
    }

    public static class AsBoolean extends BooleanBinding {
        private static final boolean DEFAULT_VALUE = false;
        private final SelectBindingHelper helper;

        public AsBoolean(ObservableValue<?> root, String... steps) {
            this.helper = new SelectBindingHelper((Binding) this, (ObservableValue) root, steps);
        }

        public AsBoolean(Object root, String... steps) {
            this.helper = new SelectBindingHelper(this, root, steps);
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
        protected void onInvalidating() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
        protected boolean computeValue() {
            ObservableValue<?> observable = this.helper.getObservableValue();
            if (observable == null) {
                return false;
            }
            if (observable instanceof ObservableBooleanValue) {
                return ((ObservableBooleanValue) observable).get();
            }
            try {
                return ((Boolean) observable.getValue2()).booleanValue();
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                return false;
            } catch (NullPointerException ex2) {
                ex2.printStackTrace();
                return false;
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            return this.helper.getDependencies();
        }
    }

    public static class AsDouble extends DoubleBinding {
        private static final double DEFAULT_VALUE = 0.0d;
        private final SelectBindingHelper helper;

        public AsDouble(ObservableValue<?> root, String... steps) {
            this.helper = new SelectBindingHelper((Binding) this, (ObservableValue) root, steps);
        }

        public AsDouble(Object root, String... steps) {
            this.helper = new SelectBindingHelper(this, root, steps);
        }

        @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
        protected void onInvalidating() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
        protected double computeValue() {
            ObservableValue<?> observable = this.helper.getObservableValue();
            if (observable == null) {
                return 0.0d;
            }
            if (observable instanceof ObservableNumberValue) {
                return ((ObservableNumberValue) observable).doubleValue();
            }
            try {
                return ((Number) observable.getValue2()).doubleValue();
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                return 0.0d;
            } catch (NullPointerException ex2) {
                ex2.printStackTrace();
                return 0.0d;
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            return this.helper.getDependencies();
        }
    }

    public static class AsFloat extends FloatBinding {
        private static final float DEFAULT_VALUE = 0.0f;
        private final SelectBindingHelper helper;

        public AsFloat(ObservableValue<?> root, String... steps) {
            this.helper = new SelectBindingHelper((Binding) this, (ObservableValue) root, steps);
        }

        public AsFloat(Object root, String... steps) {
            this.helper = new SelectBindingHelper(this, root, steps);
        }

        @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.FloatBinding
        protected void onInvalidating() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.FloatBinding
        protected float computeValue() {
            ObservableValue<?> observable = this.helper.getObservableValue();
            if (observable == null) {
                return 0.0f;
            }
            if (observable instanceof ObservableNumberValue) {
                return ((ObservableNumberValue) observable).floatValue();
            }
            try {
                return ((Number) observable.getValue2()).floatValue();
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                return 0.0f;
            } catch (NullPointerException ex2) {
                ex2.printStackTrace();
                return 0.0f;
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            return this.helper.getDependencies();
        }
    }

    public static class AsInteger extends IntegerBinding {
        private static final int DEFAULT_VALUE = 0;
        private final SelectBindingHelper helper;

        public AsInteger(ObservableValue<?> root, String... steps) {
            this.helper = new SelectBindingHelper((Binding) this, (ObservableValue) root, steps);
        }

        public AsInteger(Object root, String... steps) {
            this.helper = new SelectBindingHelper(this, root, steps);
        }

        @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
        protected void onInvalidating() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
        protected int computeValue() {
            ObservableValue<?> observable = this.helper.getObservableValue();
            if (observable == null) {
                return 0;
            }
            if (observable instanceof ObservableNumberValue) {
                return ((ObservableNumberValue) observable).intValue();
            }
            try {
                return ((Number) observable.getValue2()).intValue();
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                return 0;
            } catch (NullPointerException ex2) {
                ex2.printStackTrace();
                return 0;
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            return this.helper.getDependencies();
        }
    }

    public static class AsLong extends LongBinding {
        private static final long DEFAULT_VALUE = 0;
        private final SelectBindingHelper helper;

        public AsLong(ObservableValue<?> root, String... steps) {
            this.helper = new SelectBindingHelper((Binding) this, (ObservableValue) root, steps);
        }

        public AsLong(Object root, String... steps) {
            this.helper = new SelectBindingHelper(this, root, steps);
        }

        @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.LongBinding
        protected void onInvalidating() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.LongBinding
        protected long computeValue() {
            ObservableValue<?> observable = this.helper.getObservableValue();
            if (observable == null) {
                return 0L;
            }
            if (observable instanceof ObservableNumberValue) {
                return ((ObservableNumberValue) observable).longValue();
            }
            try {
                return ((Number) observable.getValue2()).longValue();
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                return 0L;
            } catch (NullPointerException ex2) {
                ex2.printStackTrace();
                return 0L;
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.LongBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            return this.helper.getDependencies();
        }
    }

    public static class AsString extends StringBinding {
        private static final String DEFAULT_VALUE = null;
        private final SelectBindingHelper helper;

        public AsString(ObservableValue<?> root, String... steps) {
            this.helper = new SelectBindingHelper((Binding) this, (ObservableValue) root, steps);
        }

        public AsString(Object root, String... steps) {
            this.helper = new SelectBindingHelper(this, root, steps);
        }

        @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.StringBinding
        protected void onInvalidating() {
            this.helper.unregisterListener();
        }

        @Override // com.brixcore.fakefx.beans.binding.StringBinding
        protected String computeValue() {
            ObservableValue<?> observable = this.helper.getObservableValue();
            if (observable == null) {
                return DEFAULT_VALUE;
            }
            try {
                return observable.getValue2().toString();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                return DEFAULT_VALUE;
            }
        }

        @Override // com.brixcore.fakefx.beans.binding.StringBinding, com.brixcore.fakefx.beans.binding.Binding
        public ObservableList<ObservableValue<?>> getDependencies() {
            return this.helper.getDependencies();
        }
    }

    private static class SelectBindingHelper implements InvalidationListener {
        private final Binding<?> binding;
        private ObservableList<ObservableValue<?>> dependencies;
        private final WeakInvalidationListener observer;
        private final PropertyReference<?>[] propRefs;
        private final ObservableValue<?>[] properties;
        private final String[] propertyNames;

        private SelectBindingHelper(Binding<?> binding, ObservableValue<?> firstProperty, String... steps) {
            if (firstProperty == null) {
                throw new NullPointerException("Must specify the root");
            }
            steps = steps == null ? new String[0] : steps;
            this.binding = binding;
            int n = steps.length;
            for (String str : steps) {
                if (str == null) {
                    throw new NullPointerException("all steps must be specified");
                }
            }
            this.observer = new WeakInvalidationListener(this);
            this.propertyNames = new String[n];
            System.arraycopy(steps, 0, this.propertyNames, 0, n);
            this.propRefs = new PropertyReference[n];
            this.properties = new ObservableValue[n + 1];
            this.properties[0] = firstProperty;
            this.properties[0].addListener(this.observer);
        }

        private static ObservableValue<?> checkAndCreateFirstStep(Object root, String[] steps) {
            if (root == null || steps == null || steps[0] == null) {
                throw new NullPointerException("Must specify the root and the first property");
            }
            try {
                return JavaBeanAccessHelper.createReadOnlyJavaBeanProperty(root, steps[0]);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("The first property '" + steps[0] + "' doesn't exist");
            }
        }

        private SelectBindingHelper(Binding<?> binding, Object root, String... steps) {
            this(binding, checkAndCreateFirstStep(root, steps), (String[]) Arrays.copyOfRange(steps, 1, steps.length));
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            this.binding.invalidate();
        }

        public ObservableValue<?> getObservableValue() {
            int n = this.properties.length;
            for (int i = 0; i < n - 1; i++) {
                Object obj = this.properties[i].getValue2();
                try {
                    if (this.propRefs[i] == null || !obj.getClass().equals(this.propRefs[i].getContainingClass())) {
                        this.propRefs[i] = new PropertyReference<>(obj.getClass(), this.propertyNames[i]);
                    }
                    if (this.propRefs[i].hasProperty()) {
                        this.properties[i + 1] = this.propRefs[i].getProperty(obj);
                    } else {
                        this.properties[i + 1] = JavaBeanAccessHelper.createReadOnlyJavaBeanProperty(obj, this.propRefs[i].getName());
                    }
                    this.properties[i + 1].addListener(this.observer);
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                    updateDependencies();
                    return null;
                } catch (RuntimeException ex2) {
                    ex2.printStackTrace();
                    updateDependencies();
                    return null;
                }
            }
            updateDependencies();
            ObservableValue<?> result = this.properties[n - 1];
            return result;
        }

        private String stepsToString() {
            return Arrays.toString(this.propertyNames);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void unregisterListener() {
            int n = this.properties.length;
            for (int i = 1; i < n && this.properties[i] != null; i++) {
                this.properties[i].removeListener(this.observer);
                this.properties[i] = null;
            }
            updateDependencies();
        }

        private void updateDependencies() {
            if (this.dependencies != null) {
                this.dependencies.clear();
                int n = this.properties.length;
                for (int i = 0; i < n && this.properties[i] != null; i++) {
                    this.dependencies.add(this.properties[i]);
                }
            }
        }

        public ObservableList<ObservableValue<?>> getDependencies() {
            if (this.dependencies == null) {
                this.dependencies = FXCollections.observableArrayList();
                updateDependencies();
            }
            return FXCollections.unmodifiableObservableList(this.dependencies);
        }
    }
}

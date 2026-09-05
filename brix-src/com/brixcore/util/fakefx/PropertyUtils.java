package com.brixcore.util.fakefx;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.property.Property;
import com.brixcore.fakefx.beans.value.WritableValue;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;
import com.brixcore.fakefx.collections.ObservableSet;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/* JADX INFO: loaded from: classes15.dex */
public final class PropertyUtils {
    private PropertyUtils() {
    }

    public static class PropertyHandle {
        public final WritableValue<Object> accessor;
        public final Observable observable;

        public PropertyHandle(WritableValue<Object> accessor, Observable observable) {
            this.accessor = accessor;
            this.observable = observable;
        }
    }

    public static Map<String, Function<Object, PropertyHandle>> getPropertyHandleFactories(Class<?> type) {
        final Map<String, Method> collectionGetMethods = new LinkedHashMap<>();
        Map<String, Method> propertyMethods = new LinkedHashMap<>();
        for (Method method : type.getMethods()) {
            Class<?> returnType = method.getReturnType();
            if (method.getParameterCount() == 0 && !returnType.equals(Void.TYPE)) {
                String name = method.getName();
                if (name.endsWith("Property")) {
                    String propertyName = name.substring(0, name.length() - "Property".length());
                    if (!propertyName.isEmpty() && Property.class.isAssignableFrom(returnType)) {
                        propertyMethods.put(propertyName, method);
                    }
                } else if (name.startsWith("get")) {
                    String propertyName2 = name.substring("get".length());
                    if (!propertyName2.isEmpty() && (ObservableList.class.isAssignableFrom(returnType) || ObservableSet.class.isAssignableFrom(returnType) || ObservableMap.class.isAssignableFrom(returnType))) {
                        collectionGetMethods.put(Character.toLowerCase(propertyName2.charAt(0)) + propertyName2.substring(1), method);
                    }
                }
            }
        }
        Set<String> setKeySet = propertyMethods.keySet();
        Objects.requireNonNull(collectionGetMethods);
        setKeySet.forEach(new Consumer() { // from class: com.brixcore.util.fakefx.PropertyUtils$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                collectionGetMethods.remove((String) obj);
            }
        });
        final Map<String, Function<Object, PropertyHandle>> result = new LinkedHashMap<>();
        propertyMethods.forEach(new BiConsumer() { // from class: com.brixcore.util.fakefx.PropertyUtils$$ExternalSyntheticLambda5
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                result.put((String) obj, new Function() { // from class: com.brixcore.util.fakefx.PropertyUtils$$ExternalSyntheticLambda1
                    @Override // java.util.function.Function
                    public final Object apply(Object obj3) {
                        return PropertyUtils.lambda$getPropertyHandleFactories$0(method, obj3);
                    }
                });
            }
        });
        collectionGetMethods.forEach(new BiConsumer() { // from class: com.brixcore.util.fakefx.PropertyUtils$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                result.put((String) obj, new Function() { // from class: com.brixcore.util.fakefx.PropertyUtils$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj3) {
                        return PropertyUtils.lambda$getPropertyHandleFactories$2(method, obj3);
                    }
                });
            }
        });
        return result;
    }

    static /* synthetic */ PropertyHandle lambda$getPropertyHandleFactories$0(Method method, Object instance) {
        try {
            Property returnValue = (Property) method.invoke(instance, new Object[0]);
            return new PropertyHandle(returnValue, returnValue);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    static /* synthetic */ PropertyHandle lambda$getPropertyHandleFactories$2(Method method, Object instance) {
        WritableValue<Object> accessor;
        try {
            final Object returnValue = method.invoke(instance, new Object[0]);
            if (returnValue instanceof ObservableList) {
                accessor = new WritableValue<Object>() { // from class: com.brixcore.util.fakefx.PropertyUtils.1
                    @Override // com.brixcore.fakefx.beans.value.WritableValue
                    public Object getValue() {
                        return returnValue;
                    }

                    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
                    public void setValue(Object value) {
                        ((ObservableList) returnValue).setAll((List) value);
                    }
                };
            } else if (returnValue instanceof ObservableSet) {
                accessor = new WritableValue<Object>() { // from class: com.brixcore.util.fakefx.PropertyUtils.2
                    @Override // com.brixcore.fakefx.beans.value.WritableValue
                    public Object getValue() {
                        return returnValue;
                    }

                    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
                    public void setValue(Object value) {
                        ObservableSet target = (ObservableSet) returnValue;
                        target.clear();
                        target.addAll((Set) value);
                    }
                };
            } else if (returnValue instanceof ObservableMap) {
                accessor = new WritableValue<Object>() { // from class: com.brixcore.util.fakefx.PropertyUtils.3
                    @Override // com.brixcore.fakefx.beans.value.WritableValue
                    public Object getValue() {
                        return returnValue;
                    }

                    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
                    public void setValue(Object value) {
                        ObservableMap target = (ObservableMap) returnValue;
                        target.clear();
                        target.putAll((Map) value);
                    }
                };
            } else {
                throw new IllegalStateException();
            }
            return new PropertyHandle(accessor, (Observable) returnValue);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void copyProperties(final Object from, final Object to) {
        Class<?> type = from.getClass();
        while (!type.isInstance(to)) {
            type = type.getSuperclass();
        }
        getPropertyHandleFactories(type).forEach(new BiConsumer() { // from class: com.brixcore.util.fakefx.PropertyUtils$$ExternalSyntheticLambda2
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PropertyUtils.lambda$copyProperties$4(from, to, (String) obj, (Function) obj2);
            }
        });
    }

    static /* synthetic */ void lambda$copyProperties$4(Object from, Object to, String name, Function factory) {
        PropertyHandle src = (PropertyHandle) factory.apply(from);
        PropertyHandle target = (PropertyHandle) factory.apply(to);
        target.accessor.setValue(src.accessor.getValue());
    }

    public static void attachListener(final Object instance, final InvalidationListener listener) {
        getPropertyHandleFactories(instance.getClass()).forEach(new BiConsumer() { // from class: com.brixcore.util.fakefx.PropertyUtils$$ExternalSyntheticLambda3
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((PropertyUtils.PropertyHandle) ((Function) obj2).apply(instance)).observable.addListener(listener);
            }
        });
    }
}

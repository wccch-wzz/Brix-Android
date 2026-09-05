package com.brixcore.util.gson.fakefx.factories;

import com.brixcore.fakefx.beans.property.BooleanProperty;
import com.brixcore.fakefx.beans.property.DoubleProperty;
import com.brixcore.fakefx.beans.property.FloatProperty;
import com.brixcore.fakefx.beans.property.IntegerProperty;
import com.brixcore.fakefx.beans.property.ListProperty;
import com.brixcore.fakefx.beans.property.LongProperty;
import com.brixcore.fakefx.beans.property.MapProperty;
import com.brixcore.fakefx.beans.property.Property;
import com.brixcore.fakefx.beans.property.SetProperty;
import com.brixcore.fakefx.beans.property.StringProperty;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.util.gson.fakefx.properties.ListPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.MapPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.ObjectPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.SetPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.StringPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.primitives.BooleanPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.primitives.DoublePropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.primitives.FloatPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.primitives.IntegerPropertyTypeAdapter;
import com.brixcore.util.gson.fakefx.properties.primitives.LongPropertyTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* JADX INFO: loaded from: classes3.dex */
public class JavaFxPropertyTypeAdapterFactory implements TypeAdapterFactory {
    private final boolean strictPrimitives;
    private final boolean strictProperties;

    public JavaFxPropertyTypeAdapterFactory() {
        this(true, true);
    }

    public JavaFxPropertyTypeAdapterFactory(boolean throwOnNullProperties, boolean throwOnNullPrimitives) {
        this.strictProperties = throwOnNullProperties;
        this.strictPrimitives = throwOnNullPrimitives;
    }

    @Override // com.google.gson.TypeAdapterFactory
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> clazz = type.getRawType();
        if (!Property.class.isAssignableFrom(clazz)) {
            return null;
        }
        if (BooleanProperty.class.isAssignableFrom(clazz)) {
            return new BooleanPropertyTypeAdapter(gson.getAdapter(Boolean.TYPE), this.strictProperties, this.strictPrimitives);
        }
        if (IntegerProperty.class.isAssignableFrom(clazz)) {
            return new IntegerPropertyTypeAdapter(gson.getAdapter(Integer.TYPE), this.strictProperties, this.strictPrimitives);
        }
        if (LongProperty.class.isAssignableFrom(clazz)) {
            return new LongPropertyTypeAdapter(gson.getAdapter(Long.TYPE), this.strictProperties, this.strictPrimitives);
        }
        if (FloatProperty.class.isAssignableFrom(clazz)) {
            return new FloatPropertyTypeAdapter(gson.getAdapter(Float.TYPE), this.strictProperties, this.strictPrimitives);
        }
        if (DoubleProperty.class.isAssignableFrom(clazz)) {
            return new DoublePropertyTypeAdapter(gson.getAdapter(Double.TYPE), this.strictProperties, this.strictPrimitives);
        }
        if (StringProperty.class.isAssignableFrom(clazz)) {
            return new StringPropertyTypeAdapter(gson.getAdapter(String.class), this.strictProperties);
        }
        if (ListProperty.class.isAssignableFrom(clazz)) {
            return new ListPropertyTypeAdapter(gson.getAdapter(TypeHelper.withRawType(type, ObservableList.class)), this.strictProperties);
        }
        if (SetProperty.class.isAssignableFrom(clazz)) {
            return new SetPropertyTypeAdapter(gson.getAdapter(TypeHelper.withRawType(type, ObservableSet.class)), this.strictProperties);
        }
        if (MapProperty.class.isAssignableFrom(clazz)) {
            return new MapPropertyTypeAdapter(gson.getAdapter(TypeHelper.withRawType(type, ObservableMap.class)), this.strictProperties);
        }
        Type[] typeParams = ((ParameterizedType) type.getType()).getActualTypeArguments();
        Type param = typeParams[0];
        return new ObjectPropertyTypeAdapter(gson.getAdapter(TypeToken.get(param)), this.strictProperties);
    }
}

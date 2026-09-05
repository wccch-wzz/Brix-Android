package com.brixcore.util.gson.fakefx.factories;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* JADX INFO: loaded from: classes3.dex */
final class TypeHelper {
    private TypeHelper() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    static TypeToken<?> withRawType(TypeToken<?> sourceTypeToken, Type newRawType) {
        ParameterizedType sourceType = (ParameterizedType) sourceTypeToken.getType();
        Type[] typeParams = sourceType.getActualTypeArguments();
        Type targetType = newParametrizedType(newRawType, typeParams);
        return TypeToken.get(targetType);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static ParameterizedType newParametrizedType(Type type, Type... typeArr) {
        return new CustomParameterizedType(type, null, typeArr);
    }

    private static class CustomParameterizedType implements ParameterizedType {
        private Type ownerType;
        private Type rawType;
        private Type[] typeArguments;

        private CustomParameterizedType(Type rawType, Type ownerType, Type... typeArgs) {
            this.rawType = rawType;
            this.ownerType = ownerType;
            this.typeArguments = typeArgs;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type[] getActualTypeArguments() {
            return this.typeArguments;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getRawType() {
            return this.rawType;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getOwnerType() {
            return this.ownerType;
        }
    }
}

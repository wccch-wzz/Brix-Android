package com.github.steveice10.opennbt.conversion;

import com.github.steveice10.opennbt.conversion.builtin.ByteArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.ByteTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.CompoundTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.DoubleTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.FloatTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.IntArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.IntTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.ListTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.LongArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.LongTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.ShortTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.StringTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.DoubleArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.FloatArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.ShortArrayTagConverter;
import com.github.steveice10.opennbt.tag.builtin.ByteArrayTag;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntArrayTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.LongArrayTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.DoubleArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.FloatArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.ShortArrayTag;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ConverterRegistry {
    private static final Map<Class<? extends Tag>, TagConverter<? extends Tag, ?>> tagToConverter = new HashMap();
    private static final Map<Class<?>, TagConverter<? extends Tag, ?>> typeToConverter = new HashMap();

    static {
        register(ByteTag.class, Byte.class, new ByteTagConverter());
        register(ShortTag.class, Short.class, new ShortTagConverter());
        register(IntTag.class, Integer.class, new IntTagConverter());
        register(LongTag.class, Long.class, new LongTagConverter());
        register(FloatTag.class, Float.class, new FloatTagConverter());
        register(DoubleTag.class, Double.class, new DoubleTagConverter());
        register(ByteArrayTag.class, byte[].class, new ByteArrayTagConverter());
        register(StringTag.class, String.class, new StringTagConverter());
        register(ListTag.class, List.class, new ListTagConverter());
        register(CompoundTag.class, Map.class, new CompoundTagConverter());
        register(IntArrayTag.class, int[].class, new IntArrayTagConverter());
        register(LongArrayTag.class, long[].class, new LongArrayTagConverter());
        register(DoubleArrayTag.class, double[].class, new DoubleArrayTagConverter());
        register(FloatArrayTag.class, float[].class, new FloatArrayTagConverter());
        register(ShortArrayTag.class, short[].class, new ShortArrayTagConverter());
    }

    public static <T extends Tag, V> void register(Class<T> tag, Class<V> type, TagConverter<T, V> converter) throws ConverterRegisterException {
        if (tagToConverter.containsKey(tag)) {
            throw new ConverterRegisterException("Type conversion to tag " + tag.getName() + " is already registered.");
        }
        if (typeToConverter.containsKey(type)) {
            throw new ConverterRegisterException("Tag conversion to type " + type.getName() + " is already registered.");
        }
        tagToConverter.put(tag, converter);
        typeToConverter.put(type, converter);
    }

    public static <T extends Tag, V> void unregister(Class<T> tag, Class<V> type) {
        tagToConverter.remove(tag);
        typeToConverter.remove(type);
    }

    public static <T extends Tag, V> V convertToValue(T t) throws ConversionException {
        if (t == null || t.getValue() == null) {
            return null;
        }
        if (!tagToConverter.containsKey(t.getClass())) {
            throw new ConversionException("Tag type " + t.getClass().getName() + " has no converter.");
        }
        return (V) tagToConverter.get(t.getClass()).convert(t);
    }

    public static <V, T extends Tag> T convertToTag(String str, V v) throws ConversionException {
        if (v == null) {
            return null;
        }
        TagConverter<? extends Tag, ?> tagConverter = typeToConverter.get(v.getClass());
        if (tagConverter == null) {
            for (Class<?> cls : getAllClasses(v.getClass())) {
                if (typeToConverter.containsKey(cls)) {
                    try {
                        tagConverter = typeToConverter.get(cls);
                        break;
                    } catch (ClassCastException e) {
                    }
                }
            }
        }
        if (tagConverter == null) {
            throw new ConversionException("Value type " + v.getClass().getName() + " has no converter.");
        }
        return (T) tagConverter.convert(str, v);
    }

    private static Set<Class<?>> getAllClasses(Class<?> clazz) {
        Set<Class<?>> ret = new LinkedHashSet<>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            ret.add(c);
            ret.addAll(getAllSuperInterfaces(c));
        }
        if (ret.contains(Serializable.class)) {
            ret.remove(Serializable.class);
            ret.add(Serializable.class);
        }
        return ret;
    }

    private static Set<Class<?>> getAllSuperInterfaces(Class<?> clazz) {
        Set<Class<?>> ret = new HashSet<>();
        for (Class<?> c : clazz.getInterfaces()) {
            ret.add(c);
            ret.addAll(getAllSuperInterfaces(c));
        }
        return ret;
    }
}

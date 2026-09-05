package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.StringExpression;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlyStringProperty extends StringExpression implements ReadOnlyProperty<String> {
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("ReadOnlyStringProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }
}

package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.SNBTIO;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes.dex */
public class FloatArrayTag extends Tag {
    private float[] value;

    public FloatArrayTag(String name) {
        this(name, new float[0]);
    }

    public FloatArrayTag(String name, float[] value) {
        super(name);
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public float[] getValue() {
        return (float[]) this.value.clone();
    }

    public void setValue(float[] value) {
        if (value == null) {
            return;
        }
        this.value = (float[]) value.clone();
    }

    public float getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, float value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.value = new float[in.readInt()];
        for (int index = 0; index < this.value.length; index++) {
            this.value[index] = in.readFloat();
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; index++) {
            out.writeFloat(this.value[index]);
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        String s = in.readUntil(true, ']');
        String[] valueStrings = s.substring(s.indexOf(59) + 1, s.length() - 1).replaceAll(StringUtils.SPACE, "").split(",");
        this.value = new float[valueStrings.length];
        for (int i = 0; i < this.value.length; i++) {
            this.value[i] = Float.parseFloat(valueStrings[i]);
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        StringBuilder sb = new StringBuilder("[F; ");
        for (float b : this.value) {
            sb.append(b);
            sb.append(',');
            sb.append(' ');
        }
        sb.setLength(sb.length() - 2);
        sb.append(']');
        out.append((CharSequence) sb.toString());
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public FloatArrayTag mo360clone() {
        return new FloatArrayTag(getName(), getValue());
    }
}

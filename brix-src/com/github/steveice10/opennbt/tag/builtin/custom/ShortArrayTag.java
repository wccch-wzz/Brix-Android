package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.SNBTIO;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes.dex */
public class ShortArrayTag extends Tag {
    private short[] value;

    public ShortArrayTag(String name) {
        this(name, new short[0]);
    }

    public ShortArrayTag(String name, short[] value) {
        super(name);
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public short[] getValue() {
        return (short[]) this.value.clone();
    }

    public void setValue(short[] value) {
        if (value == null) {
            return;
        }
        this.value = (short[]) value.clone();
    }

    public short getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, short value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.value = new short[in.readInt()];
        for (int index = 0; index < this.value.length; index++) {
            this.value[index] = in.readShort();
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; index++) {
            out.writeShort(this.value[index]);
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        String s = in.readUntil(true, ']');
        String[] valueStrings = s.substring(s.indexOf(59) + 1, s.length() - 1).replaceAll(StringUtils.SPACE, "").split(",");
        this.value = new short[valueStrings.length];
        for (int i = 0; i < this.value.length; i++) {
            this.value[i] = Short.parseShort(valueStrings[i]);
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        StringBuilder sb = new StringBuilder("[S; ");
        for (short b : this.value) {
            sb.append((int) b);
            sb.append(',');
            sb.append(' ');
        }
        sb.setLength(sb.length() - 2);
        sb.append(']');
        out.append((CharSequence) sb.toString());
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public ShortArrayTag mo360clone() {
        return new ShortArrayTag(getName(), getValue());
    }
}

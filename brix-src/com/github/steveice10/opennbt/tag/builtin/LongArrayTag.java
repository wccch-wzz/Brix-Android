package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.SNBTIO;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes.dex */
public class LongArrayTag extends Tag {
    private long[] value;

    public LongArrayTag(String name) {
        this(name, new long[0]);
    }

    public LongArrayTag(String name, long[] value) {
        super(name);
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public long[] getValue() {
        return (long[]) this.value.clone();
    }

    public void setValue(long[] value) {
        if (value == null) {
            return;
        }
        this.value = (long[]) value.clone();
    }

    public long getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, long value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.value = new long[in.readInt()];
        for (int index = 0; index < this.value.length; index++) {
            this.value[index] = in.readLong();
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; index++) {
            out.writeLong(this.value[index]);
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        String s = in.readUntil(true, ']');
        String[] valueStrings = s.substring(s.indexOf(59) + 1, s.length() - 1).replaceAll(StringUtils.SPACE, "").split(",");
        this.value = new long[valueStrings.length];
        for (int i = 0; i < this.value.length; i++) {
            this.value[i] = Long.parseLong(valueStrings[i]);
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        StringBuilder sb = new StringBuilder("[L; ");
        for (long b : this.value) {
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
    public LongArrayTag mo360clone() {
        return new LongArrayTag(getName(), getValue());
    }
}

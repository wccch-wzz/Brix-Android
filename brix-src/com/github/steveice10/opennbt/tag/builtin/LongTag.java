package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.SNBTIO;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class LongTag extends Tag {
    private long value;

    public LongTag(String name) {
        this(name, 0L);
    }

    public LongTag(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public Long getValue() {
        return Long.valueOf(this.value);
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.value = in.readLong();
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        out.writeLong(this.value);
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        String s = in.readNextSingleValueString();
        this.value = Long.parseLong(s.toLowerCase().substring(0, s.length() - 1));
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(this.value);
        sb.append('l');
        out.append((CharSequence) sb.toString());
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public LongTag mo360clone() {
        return new LongTag(getName(), getValue().longValue());
    }
}

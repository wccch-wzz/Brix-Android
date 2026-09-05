package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.SNBTIO;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class ShortTag extends Tag {
    private short value;

    public ShortTag(String name) {
        this(name, (short) 0);
    }

    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public Short getValue() {
        return Short.valueOf(this.value);
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.value = in.readShort();
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        out.writeShort(this.value);
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        String s = in.readNextSingleValueString();
        this.value = Short.parseShort(s.toLowerCase().substring(0, s.length() - 1));
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((int) this.value);
        sb.append('s');
        out.append((CharSequence) sb.toString());
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public ShortTag mo360clone() {
        return new ShortTag(getName(), getValue().shortValue());
    }
}

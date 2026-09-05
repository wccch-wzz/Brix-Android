package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.SNBTIO;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class ByteTag extends Tag {
    private byte value;

    public ByteTag(String name) {
        this(name, (byte) 0);
    }

    public ByteTag(String name, byte value) {
        super(name);
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public Byte getValue() {
        return Byte.valueOf(this.value);
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.value = in.readByte();
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        out.writeByte(this.value);
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        String s = in.readNextSingleValueString();
        this.value = Byte.parseByte(s.toLowerCase().substring(0, s.length() - 1));
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((int) this.value);
        sb.append('b');
        out.append((CharSequence) sb.toString());
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public ByteTag mo360clone() {
        return new ByteTag(getName(), getValue().byteValue());
    }
}

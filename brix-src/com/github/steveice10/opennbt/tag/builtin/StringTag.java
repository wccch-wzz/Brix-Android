package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.SNBTIO;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class StringTag extends Tag {
    private String value;

    public StringTag(String name) {
        this(name, "");
    }

    public StringTag(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.value = in.readUTF();
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.value);
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        String s = in.readNextSingleValueString();
        if (s.charAt(0) == '\"') {
            this.value = s.substring(1, s.length() - 1).replaceAll("\\\\\"", "\"");
        } else if (s.charAt(0) == '\'') {
            this.value = s.substring(1, s.length() - 1).replaceAll("\\\\'", "'");
        } else {
            this.value = s;
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        if (this.value.matches("(?!\\d+)[\\w\\d]*")) {
            out.append((CharSequence) this.value);
            return;
        }
        if (this.value.contains("\"")) {
            if (this.value.contains("'")) {
                out.append((CharSequence) ("\"" + this.value.replaceAll("\"", "\\\\\"") + "\""));
                return;
            }
            out.append((CharSequence) ("'" + this.value + "'"));
            return;
        }
        out.append((CharSequence) ("\"" + this.value + "\""));
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public StringTag mo360clone() {
        return new StringTag(getName(), getValue());
    }
}

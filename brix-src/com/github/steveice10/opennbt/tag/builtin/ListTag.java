package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.SNBTIO;
import com.github.steveice10.opennbt.tag.TagCreateException;
import com.github.steveice10.opennbt.tag.TagRegistry;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ListTag extends Tag implements Iterable<Tag> {
    private Class<? extends Tag> type;
    private List<Tag> value;

    public ListTag(String name) {
        super(name);
        this.type = null;
        this.value = new ArrayList();
    }

    public ListTag(String name, Class<? extends Tag> type) {
        this(name);
        this.type = type;
    }

    public ListTag(String name, List<Tag> value) throws IllegalArgumentException {
        this(name);
        setValue(value);
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public List<Tag> getValue() {
        return new ArrayList(this.value);
    }

    public void setValue(List<Tag> value) throws IllegalArgumentException {
        this.type = null;
        this.value.clear();
        for (Tag tag : value) {
            add(tag);
        }
    }

    public Class<? extends Tag> getElementType() {
        return this.type;
    }

    public boolean add(Tag tag) throws IllegalArgumentException {
        if (tag == null) {
            return false;
        }
        if (this.type == null) {
            this.type = tag.getClass();
        } else if (tag.getClass() != this.type) {
            throw new IllegalArgumentException("Tag type cannot differ from ListTag type.");
        }
        return this.value.add(tag);
    }

    public boolean remove(Tag tag) {
        return this.value.remove(tag);
    }

    public <T extends Tag> T get(int index) {
        return (T) this.value.get(index);
    }

    public int size() {
        return this.value.size();
    }

    @Override // java.lang.Iterable
    public Iterator<Tag> iterator() {
        return this.value.iterator();
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        this.type = null;
        this.value.clear();
        int id = in.readUnsignedByte();
        if (id != 0) {
            this.type = TagRegistry.getClassFor(id);
            if (this.type == null) {
                throw new IOException("Unknown tag ID in ListTag: " + id);
            }
        }
        int count = in.readInt();
        for (int index = 0; index < count; index++) {
            try {
                Tag tag = TagRegistry.createInstance(id, "");
                tag.read(in);
                add(tag);
            } catch (TagCreateException e) {
                throw new IOException("Failed to create tag.", e);
            }
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        if (this.type == null) {
            out.writeByte(0);
        } else {
            int id = TagRegistry.getIdFor(this.type);
            if (id == -1) {
                throw new IOException("ListTag contains unregistered tag class.");
            }
            out.writeByte(id);
        }
        out.writeInt(this.value.size());
        for (Tag tag : this.value) {
            tag.write(out);
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        in.readSkipWhitespace();
        while (true) {
            add(in.readNextTag(""));
            char endChar = in.readSkipWhitespace();
            if (endChar != ',' && endChar == ']') {
                return;
            }
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        out.append('[');
        boolean first = true;
        for (Tag t : this.value) {
            if (first) {
                first = false;
            } else {
                out.append(',');
                if (!linebreak) {
                    out.append(' ');
                }
            }
            out.writeTag(t, linebreak, depth + 1);
        }
        if (linebreak) {
            out.append('\n');
            out.indent(depth);
        }
        out.append(']');
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public ListTag mo360clone() {
        List<Tag> newList = new ArrayList<>();
        for (Tag value : this.value) {
            newList.add(value.mo360clone());
        }
        return new ListTag(getName(), newList);
    }
}

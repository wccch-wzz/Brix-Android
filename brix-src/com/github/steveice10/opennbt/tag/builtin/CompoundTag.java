package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.SNBTIO;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.text.Typography;

/* JADX INFO: loaded from: classes.dex */
public class CompoundTag extends Tag implements Iterable<Tag> {
    private Map<String, Tag> value;

    public CompoundTag(String name) {
        this(name, new LinkedHashMap());
    }

    public CompoundTag(String name, Map<String, Tag> value) {
        super(name);
        this.value = new LinkedHashMap(value);
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public Map<String, Tag> getValue() {
        return new LinkedHashMap(this.value);
    }

    public void setValue(Map<String, Tag> value) {
        this.value = new LinkedHashMap(value);
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    public <T extends Tag> T get(String tagName) {
        return (T) this.value.get(tagName);
    }

    public <T extends Tag> T put(T tag) {
        return (T) this.value.put(tag.getName(), tag);
    }

    public <T extends Tag> T remove(String tagName) {
        return (T) this.value.remove(tagName);
    }

    public Set<String> keySet() {
        return this.value.keySet();
    }

    public Collection<Tag> values() {
        return this.value.values();
    }

    public int size() {
        return this.value.size();
    }

    public void clear() {
        this.value.clear();
    }

    @Override // java.lang.Iterable
    public Iterator<Tag> iterator() {
        return values().iterator();
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void read(DataInput in) throws IOException {
        List<Tag> tags = new ArrayList<>();
        while (true) {
            try {
                Tag tag = NBTIO.readTag(in);
                if (tag == null) {
                    break;
                } else {
                    tags.add(tag);
                }
            } catch (EOFException e) {
                throw new IOException("Closing EndTag was not found!");
            }
        }
        Iterator<Tag> it = tags.iterator();
        while (it.hasNext()) {
            put(it.next());
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void write(DataOutput out) throws IOException {
        for (Tag tag : this.value.values()) {
            NBTIO.writeTag(out, tag);
        }
        out.writeByte(0);
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void destringify(SNBTIO.StringifiedNBTReader in) throws IOException {
        in.readSkipWhitespace();
        while (true) {
            String str = "" + in.readSkipWhitespace();
            String tagName = str;
            if (str.equals("\"")) {
                tagName = in.readUntil(false, Typography.quote);
                in.read();
            }
            String tagName2 = tagName + in.readUntil(false, ':');
            in.read();
            put(in.readNextTag(tagName2));
            char endChar = in.readSkipWhitespace();
            if (endChar != ',' && endChar == '}') {
                return;
            }
        }
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    public void stringify(SNBTIO.StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        out.append('{');
        boolean first = true;
        for (Tag t : this.value.values()) {
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
        out.append('}');
    }

    @Override // com.github.steveice10.opennbt.tag.builtin.Tag
    /* JADX INFO: renamed from: clone */
    public CompoundTag mo360clone() {
        Map<String, Tag> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().mo360clone());
        }
        return new CompoundTag(getName(), newMap);
    }
}

package com.github.steveice10.opennbt;

import com.github.steveice10.opennbt.tag.builtin.ByteArrayTag;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntArrayTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.LongArrayTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.DoubleArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.FloatArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.ShortArrayTag;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.util.regex.Pattern;
import kotlin.text.Typography;
import org.apache.commons.lang3.CharUtils;

/* JADX INFO: loaded from: classes.dex */
public class SNBTIO {
    public static CompoundTag readFile(String path) throws IOException {
        return readFile(new File(path));
    }

    public static CompoundTag readFile(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            Tag tag = readTag(in);
            if (!(tag instanceof CompoundTag)) {
                throw new IOException("Root tag is not a CompoundTag!");
            }
            CompoundTag compoundTag = (CompoundTag) tag;
            in.close();
            return compoundTag;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                try {
                    in.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    public static void writeFile(CompoundTag tag, String path) throws IOException {
        writeFile(tag, new File(path));
    }

    public static void writeFile(CompoundTag tag, File file) throws IOException {
        writeFile(tag, file, false);
    }

    public static void writeFile(CompoundTag tag, String path, boolean linebreak) throws IOException {
        writeFile(tag, new File(path), linebreak);
    }

    public static void writeFile(CompoundTag tag, File file, boolean linebreak) throws IOException {
        if (!file.exists()) {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        OutputStream out = new FileOutputStream(file);
        try {
            writeTag(out, tag, linebreak);
            out.close();
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                try {
                    out.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    public static Tag readTag(InputStream in) throws IOException {
        StringifiedNBTReader reader = new StringifiedNBTReader(in);
        try {
            Tag nextTag = reader.readNextTag("");
            reader.close();
            return nextTag;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                try {
                    reader.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    public static void writeTag(OutputStream out, Tag tag) throws IOException {
        writeTag(out, tag, false);
    }

    public static void writeTag(OutputStream out, Tag tag, boolean linebreak) throws IOException {
        StringifiedNBTWriter writer = new StringifiedNBTWriter(out);
        try {
            writer.writeTag(tag, linebreak);
            writer.close();
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                try {
                    writer.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    public static class StringifiedNBTReader extends PushbackReader {
        static final Pattern byteTagValuePattern = Pattern.compile("[-+]?\\d+[bB]");
        static final Pattern doubleTagValuePattern = Pattern.compile("[-+]?((\\d+(\\.\\d*)?)|(\\.\\d+))[dD]");
        static final Pattern floatTagValuePattern = Pattern.compile("[-+]?((\\d+(\\.\\d*)?)|(\\.\\d+))[fF]");
        static final Pattern intTagValuePattern = Pattern.compile("[-+]?\\d+");
        static final Pattern longTagValuePattern = Pattern.compile("[-+]?\\d+[lL]");
        static final Pattern shortTagValuePattern = Pattern.compile("[-+]?\\d+[sS]");

        public StringifiedNBTReader(InputStream in) {
            super(new InputStreamReader(in), 32);
        }

        public Tag readNextTag(String name) throws IOException {
            skipWhitespace();
            if (lookAhead(0) == '{') {
                return readCompoundTag(name);
            }
            if (lookAhead(0) == '[') {
                return readListOrArrayTag(name);
            }
            return readPrimitiveTag(name);
        }

        public Tag readCompoundTag(String name) throws IOException {
            return parseTag(new CompoundTag(name));
        }

        private Tag readListOrArrayTag(String name) throws IOException {
            if (lookAhead(2) == ';') {
                switch (lookAhead(1)) {
                    case 'B':
                        return parseTag(new ByteArrayTag(name));
                    case 'D':
                        return parseTag(new DoubleArrayTag(name));
                    case 'F':
                        return parseTag(new FloatArrayTag(name));
                    case 'I':
                        return parseTag(new IntArrayTag(name));
                    case 'L':
                        return parseTag(new LongArrayTag(name));
                    case 'S':
                        return parseTag(new ShortArrayTag(name));
                }
            }
            return parseTag(new ListTag(name));
        }

        private Tag readPrimitiveTag(String name) throws IOException {
            String valueString = readNextSingleValueString(32);
            unread(valueString.toCharArray());
            return parseTag(getTagForStringifiedValue(name, valueString));
        }

        public String readNextSingleValueString() throws IOException {
            return readNextSingleValueString(Integer.MAX_VALUE);
        }

        public String readNextSingleValueString(int maxReadLenght) throws IOException {
            if (lookAhead(0) != '\'' && lookAhead(0) != '\"') {
                String valueString = readUntil(maxReadLenght, false, ',', '}', ']', CharUtils.CR, '\n', '\t');
                return valueString;
            }
            char c = (char) read();
            String valueString2 = c + readUntil(maxReadLenght, true, c);
            return valueString2;
        }

        private Tag getTagForStringifiedValue(String name, String stringifiedValue) {
            if (byteTagValuePattern.matcher(stringifiedValue).matches()) {
                return new ByteTag(name);
            }
            if (doubleTagValuePattern.matcher(stringifiedValue).matches()) {
                return new DoubleTag(name);
            }
            if (floatTagValuePattern.matcher(stringifiedValue).matches()) {
                return new FloatTag(name);
            }
            if (intTagValuePattern.matcher(stringifiedValue).matches()) {
                return new IntTag(name);
            }
            if (longTagValuePattern.matcher(stringifiedValue).matches()) {
                return new LongTag(name);
            }
            if (shortTagValuePattern.matcher(stringifiedValue).matches()) {
                return new ShortTag(name);
            }
            return new StringTag(name);
        }

        public Tag parseTag(Tag tag) throws IOException {
            tag.destringify(this);
            return tag;
        }

        public void skipWhitespace() throws IOException {
            while (true) {
                char c = (char) read();
                if (c != 65535) {
                    if (c != '\t' && c != '\r' && c != '\n' && c != ' ') {
                        unread(c);
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        public char readSkipWhitespace() throws IOException {
            skipWhitespace();
            return (char) read();
        }

        public String readUntil(boolean includeEndChar, char... endChar) throws IOException {
            return readUntil(Integer.MAX_VALUE, includeEndChar, endChar);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public String readUntil(int maxReadLenght, boolean includeEndChar, char... endChar) throws IOException {
            char c;
            StringBuilder sb = new StringBuilder();
            boolean escapeEnd = false;
            int reads = 0;
            while (true) {
                reads++;
                if (reads < maxReadLenght && (c = (char) read()) != -1) {
                    if (c == 92) {
                        sb.append(c);
                        escapeEnd = true;
                    } else {
                        if (!escapeEnd && matchesAny(c, endChar)) {
                            if (includeEndChar) {
                                sb.append(c);
                                break;
                            }
                            unread(c);
                            break;
                        }
                        sb.append(c);
                        escapeEnd = false;
                    }
                } else {
                    break;
                }
            }
            return sb.toString();
        }

        public char lookAhead(int offset) throws IOException {
            char[] future = new char[offset + 1];
            read(future);
            unread(future);
            return future[offset];
        }

        public static boolean matchesAny(char c, char[] matchable) {
            for (char m : matchable) {
                if (c == m) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class StringifiedNBTWriter extends OutputStreamWriter {
        public static Pattern nonEscapedTagName = Pattern.compile("(?!\\d+)[\\w\\d]*");

        public StringifiedNBTWriter(OutputStream out) {
            super(out);
        }

        public void writeTag(Tag tag, boolean linebreak) throws IOException {
            writeTag(tag, linebreak, 0);
            flush();
        }

        public void writeTag(Tag tag, boolean linebreak, int depth) throws IOException {
            if (linebreak && depth > 0) {
                append('\n');
                indent(depth);
            }
            if (tag.getName() != null && !tag.getName().equals("")) {
                appendTagName(tag.getName());
                append(':');
                append(' ');
            }
            if (tag instanceof CompoundTag) {
                tag.stringify(this, linebreak, depth);
            } else if (tag instanceof ListTag) {
                tag.stringify(this, linebreak, depth);
            } else {
                tag.stringify(this, linebreak, depth);
            }
        }

        public void appendTagName(String tagName) throws IOException {
            if (!nonEscapedTagName.matcher(tagName).matches()) {
                append(Typography.quote);
                append((CharSequence) tagName.replaceAll("\\\"", "\\\""));
                append(Typography.quote);
                return;
            }
            append((CharSequence) tagName);
        }

        public void indent(int depth) throws IOException {
            for (int i = 0; i < depth; i++) {
                append('\t');
            }
        }
    }
}

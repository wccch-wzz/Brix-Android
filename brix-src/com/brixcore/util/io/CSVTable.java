package com.brixcore.util.io;

import com.brixcore.util.InfiniteSizeList;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class CSVTable {
    private final List<List<String>> table = new InfiniteSizeList();

    private CSVTable() {
    }

    public static CSVTable createEmpty() {
        return new CSVTable();
    }

    public String get(int x, int y) {
        List<String> row = this.table.get(y);
        if (row == null) {
            return null;
        }
        return row.get(x);
    }

    public void set(int x, int y, String txt) {
        List<String> row = this.table.get(y);
        if (row == null) {
            row = new InfiniteSizeList(x);
            this.table.set(y, row);
        }
        row.set(x, txt);
    }

    public void write(OutputStream outputStream) throws IOException {
        PrintWriter printWriter = new PrintWriter((Writer) new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)), false);
        try {
            for (List<String> row : this.table) {
                if (row != null) {
                    for (int j = 0; j < row.size(); j++) {
                        String txt = row.get(j);
                        if (txt != null) {
                            printWriter.write(escape(txt));
                        }
                        if (j != row.size() - 1) {
                            printWriter.write(44);
                        }
                    }
                }
                printWriter.write(10);
            }
            printWriter.close();
        } catch (Throwable th) {
            try {
                printWriter.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private String escape(String txt) {
        if (txt.contains("\"") || txt.contains(",")) {
            return "\"" + txt.replace("\"", "\"\"") + "\"";
        }
        return txt;
    }
}

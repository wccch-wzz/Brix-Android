package com.brixcore.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;

/* JADX INFO: loaded from: classes11.dex */
public final class Logging {
    public static final Logger LOG = Logger.getLogger("Brix");
    private static final ByteArrayOutputStream storedLogs = new ByteArrayOutputStream(8192);
    private static volatile String[] accessTokens = new String[0];
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    private Logging() {
    }

    public static synchronized void registerAccessToken(String token) {
        String[] oldAccessTokens = accessTokens;
        String[] newAccessTokens = (String[]) Arrays.copyOf(oldAccessTokens, oldAccessTokens.length + 1);
        newAccessTokens[oldAccessTokens.length] = token;
        accessTokens = newAccessTokens;
    }

    public static String filterForbiddenToken(String message) {
        if (message == null) {
            return "";
        }
        for (String token : accessTokens) {
            message = message.replace(token, "<access token>");
        }
        return message;
    }

    public static void start(Path logFolder) {
        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);
        LOG.setFilter(new Filter() { // from class: com.brixcore.util.Logging$$ExternalSyntheticLambda0
            @Override // java.util.logging.Filter
            public final boolean isLoggable(LogRecord logRecord) {
                return Logging.lambda$start$0(logRecord);
            }
        });
        DefaultFormatter formatter = new DefaultFormatter();
        try {
            if (Files.isRegularFile(logFolder, new LinkOption[0])) {
                Files.delete(logFolder);
            }
            Files.createDirectories(logFolder, new FileAttribute[0]);
            FileHandler fileHandler = new FileHandler(logFolder.resolve("Brix.log").toAbsolutePath().toString());
            fileHandler.setLevel(Level.FINEST);
            fileHandler.setFormatter(formatter);
            fileHandler.setEncoding(CharEncoding.UTF_8);
            LOG.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Unable to create Brix.log\n" + StringUtils.getStackTrace(e));
        }
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(Level.FINER);
        LOG.addHandler(consoleHandler);
        StreamHandler streamHandler = new StreamHandler(storedLogs, formatter) { // from class: com.brixcore.util.Logging.1
            @Override // java.util.logging.StreamHandler, java.util.logging.Handler
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        try {
            streamHandler.setEncoding(CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        streamHandler.setLevel(Level.ALL);
        LOG.addHandler(streamHandler);
    }

    static /* synthetic */ boolean lambda$start$0(LogRecord record) {
        record.setMessage(format(record));
        return true;
    }

    public static void initForTest() {
        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new DefaultFormatter());
        consoleHandler.setLevel(Level.FINER);
        LOG.addHandler(consoleHandler);
    }

    public static byte[] getRawLogs() {
        return storedLogs.toByteArray();
    }

    public static String getLogs() {
        try {
            return storedLogs.toString(CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new InternalError(e);
        }
    }

    private static String format(LogRecord record) {
        String message = filterForbiddenToken(record.getMessage());
        StringBuilder builder = new StringBuilder(message.length() + 128);
        builder.append('[');
        TIME_FORMATTER.formatTo(Instant.ofEpochMilli(record.getMillis()), builder);
        builder.append(']');
        builder.append(" [").append(record.getSourceClassName()).append('.').append(record.getSourceMethodName()).append(IOUtils.DIR_SEPARATOR_UNIX).append(record.getLevel().getName()).append("] ").append(message).append('\n');
        Throwable thrown = record.getThrown();
        if (thrown == null) {
            return builder.toString();
        }
        StringWriter writer = new StringWriter(builder.length() + 2048);
        writer.getBuffer().append((CharSequence) builder);
        PrintWriter printWriter = new PrintWriter(writer);
        try {
            thrown.printStackTrace(printWriter);
            printWriter.close();
            return writer.toString();
        } catch (Throwable th) {
            try {
                printWriter.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static final class DefaultFormatter extends Formatter {
        private DefaultFormatter() {
        }

        @Override // java.util.logging.Formatter
        public String format(LogRecord record) {
            return record.getMessage();
        }
    }
}

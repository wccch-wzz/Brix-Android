package com.brixcore.util.png;

import java.io.Serializable;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes12.dex */
public final class PNGMetadata implements Serializable {
    public static final String KEY_AUTHOR = "Author";
    public static final String KEY_COMMENT = "Comment";
    public static final String KEY_COPYRIGHT = "Copyright";
    public static final String KEY_CREATION_TIME = "Creation Time";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_DISCLAIMER = "Disclaimer";
    public static final String KEY_SOFTWARE = "Software";
    public static final String KEY_SOURCE = "Source";
    public static final String KEY_TITLE = "Title";
    public static final String KEY_WARNING = "Warning";
    private static final long serialVersionUID = 0;
    Map<String, String> texts = Collections.emptyMap();

    public PNGMetadata setText(String key, String value) {
        if (this.texts.isEmpty()) {
            this.texts = new LinkedHashMap();
        }
        this.texts.put(key, value);
        return this;
    }

    public String getText(String key) {
        return this.texts.get(key);
    }

    public PNGMetadata setTitle(String title) {
        setText(KEY_TITLE, title);
        return this;
    }

    public PNGMetadata setAuthor(String author) {
        setText(KEY_AUTHOR, author);
        return this;
    }

    public PNGMetadata setAuthor() {
        setText(KEY_AUTHOR, System.getProperty("user.name"));
        return this;
    }

    public PNGMetadata setDescription(String description) {
        setText(KEY_DESCRIPTION, description);
        return this;
    }

    public PNGMetadata setCopyright(String copyright) {
        setText(KEY_COPYRIGHT, copyright);
        return this;
    }

    public PNGMetadata setCreationTime(String creationTime) {
        setText(KEY_CREATION_TIME, creationTime);
        return this;
    }

    public PNGMetadata setCreationTime(LocalDateTime time) {
        setCreationTime(ZonedDateTime.of(time, ZoneOffset.UTC).toOffsetDateTime());
        return this;
    }

    public PNGMetadata setCreationTime(OffsetDateTime time) {
        setCreationTime(time.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        return this;
    }

    public PNGMetadata setCreationTime(FileTime time) {
        setCreationTime(ZonedDateTime.ofInstant(time.toInstant(), ZoneOffset.UTC).toOffsetDateTime());
        return this;
    }

    public PNGMetadata setCreationTime() {
        setCreationTime(LocalDateTime.now());
        return this;
    }

    public PNGMetadata setSoftware(String software) {
        setText(KEY_SOFTWARE, software);
        return this;
    }

    public PNGMetadata setDisclaimer(String disclaimer) {
        setText(KEY_DISCLAIMER, disclaimer);
        return this;
    }

    public PNGMetadata setWarning(String warning) {
        setText(KEY_WARNING, warning);
        return this;
    }

    public PNGMetadata setSource(String source) {
        setText(KEY_SOURCE, source);
        return this;
    }

    public PNGMetadata setComment(String comment) {
        setText(KEY_COMMENT, comment);
        return this;
    }
}

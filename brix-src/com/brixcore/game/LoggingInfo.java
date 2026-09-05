package com.brixcore.game;

import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes2.dex */
public final class LoggingInfo implements Validation {

    @SerializedName("argument")
    private final String argument;

    @SerializedName("file")
    private final IdDownloadInfo file;

    @SerializedName("type")
    private final String type;

    public LoggingInfo() {
        this(new IdDownloadInfo());
    }

    public LoggingInfo(IdDownloadInfo file) {
        this(file, "");
    }

    public LoggingInfo(IdDownloadInfo file, String argument) {
        this(file, argument, "");
    }

    public LoggingInfo(IdDownloadInfo file, String argument, String type) {
        this.file = file;
        this.argument = argument;
        this.type = type;
    }

    public IdDownloadInfo getFile() {
        return this.file;
    }

    public String getArgument() {
        return this.argument;
    }

    public String getType() {
        return this.type;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        this.file.validate();
        if (StringUtils.isBlank(this.argument)) {
            throw new JsonParseException("LoggingInfo.argument is empty.");
        }
        if (StringUtils.isBlank(this.type)) {
            throw new JsonParseException("LoggingInfo.type is empty.");
        }
    }
}

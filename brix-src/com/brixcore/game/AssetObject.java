package com.brixcore.game;

import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.file.Path;

/* JADX INFO: loaded from: classes2.dex */
public final class AssetObject implements Validation {
    private final String hash;
    private final long size;

    public AssetObject() {
        this("", 0L);
    }

    public AssetObject(String hash, long size) {
        this.hash = hash;
        this.size = size;
    }

    public String getHash() {
        return this.hash;
    }

    public long getSize() {
        return this.size;
    }

    public String getLocation() {
        return this.hash.substring(0, 2) + "/" + this.hash;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(this.hash) || this.hash.length() < 2) {
            throw new JsonParseException("AssetObject hash cannot be blank.");
        }
    }

    public boolean validateChecksum(Path file, boolean defaultValue) throws IOException {
        return this.hash == null ? defaultValue : DigestUtils.digestToString(CacheRepository.SHA1, file).equalsIgnoreCase(this.hash);
    }
}

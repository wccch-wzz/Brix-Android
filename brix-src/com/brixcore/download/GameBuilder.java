package com.brixcore.download;

import com.brixcore.task.Task;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes14.dex */
public abstract class GameBuilder {
    protected String name = "";
    protected String gameVersion = "";
    protected final Map<String, String> toolVersions = new HashMap();
    protected final Set<RemoteVersion> remoteVersions = new HashSet();

    public abstract Task<?> buildAsync();

    public String getName() {
        return this.name;
    }

    public GameBuilder name(String name) {
        this.name = (String) Objects.requireNonNull(name);
        return this;
    }

    public GameBuilder gameVersion(String version) {
        this.gameVersion = (String) Objects.requireNonNull(version);
        return this;
    }

    public GameBuilder version(String id, String version) {
        if ("game".equals(id)) {
            gameVersion(version);
        } else {
            this.toolVersions.put(id, version);
        }
        return this;
    }

    public GameBuilder version(RemoteVersion remoteVersion) {
        this.remoteVersions.add(remoteVersion);
        return this;
    }
}

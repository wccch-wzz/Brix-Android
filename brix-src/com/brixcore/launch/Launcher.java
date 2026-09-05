package com.brixcore.launch;

import android.content.Context;
import com.brixcore.auth.AuthInfo;
import com.brixcore.bridge.BrixBridge;
import com.brixcore.game.GameRepository;
import com.brixcore.game.LaunchOptions;
import com.brixcore.game.Version;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Launcher {
    protected final AuthInfo authInfo;
    protected final Context context;
    protected final LaunchOptions options;
    protected final GameRepository repository;
    protected final Version version;

    public abstract BrixBridge launch() throws InterruptedException, IOException;

    public Launcher(Context context) {
        this(context, null, null, null, null);
    }

    public Launcher(Context context, GameRepository repository, Version version, AuthInfo authInfo, LaunchOptions options) {
        this.context = context;
        this.repository = repository;
        this.version = version;
        this.authInfo = authInfo;
        this.options = options;
    }
}

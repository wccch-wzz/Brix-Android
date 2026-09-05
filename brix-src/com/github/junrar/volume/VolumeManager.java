package com.github.junrar.volume;

import com.github.junrar.Archive;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public interface VolumeManager {
    Volume nextVolume(Archive archive, Volume volume) throws IOException;
}

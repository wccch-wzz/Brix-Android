package com.github.junrar;

import com.github.junrar.volume.Volume;

/* JADX INFO: loaded from: classes.dex */
public interface UnrarCallback {
    boolean isNextVolumeReady(Volume volume);

    void volumeProgressChanged(long j, long j2);
}

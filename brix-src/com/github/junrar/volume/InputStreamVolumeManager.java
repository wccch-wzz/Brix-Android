package com.github.junrar.volume;

import com.github.junrar.Archive;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class InputStreamVolumeManager implements VolumeManager {
    private final Map<Integer, InputStream> streams = new HashMap();

    public InputStreamVolumeManager(InputStream is) {
        this.streams.put(1, is);
    }

    public InputStreamVolumeManager(List<InputStream> streams) {
        for (int i = 0; i < streams.size(); i++) {
            this.streams.put(Integer.valueOf(i + 1), streams.get(i));
        }
    }

    @Override // com.github.junrar.volume.VolumeManager
    public Volume nextVolume(Archive archive, Volume lastVolume) {
        if (lastVolume == null) {
            return new InputStreamVolume(archive, this.streams.get(1), 1);
        }
        InputStreamVolume lastStreamVolume = (InputStreamVolume) lastVolume;
        int nextPosition = lastStreamVolume.getPosition() + 1;
        InputStream next = this.streams.get(Integer.valueOf(nextPosition));
        if (next != null) {
            return new InputStreamVolume(archive, next, nextPosition);
        }
        return null;
    }
}

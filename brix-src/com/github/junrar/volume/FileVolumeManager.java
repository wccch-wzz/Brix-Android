package com.github.junrar.volume;

import com.github.junrar.Archive;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class FileVolumeManager implements VolumeManager {
    private final File firstVolume;

    public FileVolumeManager(File firstVolume) {
        this.firstVolume = firstVolume;
    }

    @Override // com.github.junrar.volume.VolumeManager
    public Volume nextVolume(Archive archive, Volume last) {
        if (last == null) {
            return new FileVolume(archive, this.firstVolume);
        }
        FileVolume lastFileVolume = (FileVolume) last;
        boolean oldNumbering = !archive.getMainHeader().isNewNumbering() || archive.isOldFormat();
        String nextName = VolumeHelper.nextVolumeName(lastFileVolume.getFile().getAbsolutePath(), oldNumbering);
        File nextVolume = new File(nextName);
        return new FileVolume(archive, nextVolume);
    }
}

package com.github.junrar.volume;

import com.github.junrar.Archive;
import com.github.junrar.io.SeekableReadOnlyByteChannel;
import com.github.junrar.io.SeekableReadOnlyInputStream;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class InputStreamVolume implements Volume {
    private final Archive archive;
    private final InputStream inputStream;
    private final int position;

    public InputStreamVolume(Archive archive, InputStream inputStream, int position) {
        this.archive = archive;
        this.inputStream = inputStream;
        this.position = position;
    }

    @Override // com.github.junrar.volume.Volume
    public SeekableReadOnlyByteChannel getChannel() {
        return new SeekableReadOnlyInputStream(this.inputStream);
    }

    @Override // com.github.junrar.volume.Volume
    public long getLength() {
        return Long.MAX_VALUE;
    }

    @Override // com.github.junrar.volume.Volume
    public Archive getArchive() {
        return this.archive;
    }

    public int getPosition() {
        return this.position;
    }
}

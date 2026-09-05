package com.github.junrar.volume;

import com.github.junrar.Archive;
import com.github.junrar.io.SeekableReadOnlyByteChannel;
import com.github.junrar.io.SeekableReadOnlyFile;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class FileVolume implements Volume {
    private final Archive archive;
    private final File file;

    public FileVolume(Archive archive, File file) {
        this.archive = archive;
        this.file = file;
    }

    @Override // com.github.junrar.volume.Volume
    public SeekableReadOnlyByteChannel getChannel() throws IOException {
        return new SeekableReadOnlyFile(this.file);
    }

    @Override // com.github.junrar.volume.Volume
    public long getLength() {
        return this.file.length();
    }

    @Override // com.github.junrar.volume.Volume
    public Archive getArchive() {
        return this.archive;
    }

    public File getFile() {
        return this.file;
    }
}

package com.github.junrar.volume;

import com.github.junrar.Archive;
import com.github.junrar.io.SeekableReadOnlyByteChannel;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public interface Volume {
    Archive getArchive();

    SeekableReadOnlyByteChannel getChannel() throws IOException;

    long getLength();
}

package com.brixcore.util.io;

import com.brixcore.download.ArtifactMalformedException;
import com.brixcore.util.DigestUtils;
import java.io.IOException;
import java.nio.file.Path;

/* JADX INFO: loaded from: classes3.dex */
public final class ChecksumMismatchException extends ArtifactMalformedException {
    private final String actualChecksum;
    private final String algorithm;
    private final String expectedChecksum;

    public ChecksumMismatchException(String algorithm, String expectedChecksum, String actualChecksum) {
        super("Incorrect checksum (" + algorithm + "), expected: " + expectedChecksum + ", actual: " + actualChecksum);
        this.algorithm = algorithm;
        this.expectedChecksum = expectedChecksum;
        this.actualChecksum = actualChecksum;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public String getExpectedChecksum() {
        return this.expectedChecksum;
    }

    public String getActualChecksum() {
        return this.actualChecksum;
    }

    public static void verifyChecksum(Path file, String algorithm, String expectedChecksum) throws IOException {
        String checksum = DigestUtils.digestToString(algorithm, file);
        if (!checksum.equalsIgnoreCase(expectedChecksum)) {
            throw new ChecksumMismatchException(algorithm, expectedChecksum, checksum);
        }
    }
}

package com.github.junrar.unpack;

import com.github.junrar.Archive;
import com.github.junrar.UnrarCallback;
import com.github.junrar.crc.RarCRC;
import com.github.junrar.crypt.Rijndael;
import com.github.junrar.exception.CrcErrorException;
import com.github.junrar.exception.InitDeciphererFailedException;
import com.github.junrar.exception.RarException;
import com.github.junrar.io.RawDataIo;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.volume.Volume;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Cipher;

/* JADX INFO: loaded from: classes.dex */
public class ComprDataIO {
    private final Archive archive;
    private long curPackRead;
    private long curPackWrite;
    private long curUnpRead;
    private long curUnpWrite;
    private int decryption;
    private int encryption;
    private boolean nextVolumeMissing;
    private OutputStream outputStream;
    private long packFileCRC;
    private boolean packVolume;
    private long packedCRC;
    private long processedArcSize;
    private boolean skipUnpCRC;
    private FileHeader subHead;
    private boolean testMode;
    private long totalArcSize;
    private long totalPackRead;
    private RawDataIo underlyingDataIo;
    private long unpArcSize;
    private long unpFileCRC;
    private long unpPackedSize;
    private boolean unpVolume;

    public ComprDataIO(Archive arc) {
        this.archive = arc;
    }

    public void init(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.unpPackedSize = 0L;
        this.testMode = false;
        this.skipUnpCRC = false;
        this.packVolume = false;
        this.unpVolume = false;
        this.nextVolumeMissing = false;
        this.encryption = 0;
        this.decryption = 0;
        this.totalPackRead = 0L;
        this.curUnpWrite = 0L;
        this.curUnpRead = 0L;
        this.curPackWrite = 0L;
        this.curPackRead = 0L;
        this.packedCRC = -1L;
        this.unpFileCRC = -1L;
        this.packFileCRC = -1L;
        this.subHead = null;
        this.totalArcSize = 0L;
        this.processedArcSize = 0L;
    }

    public void init(FileHeader hd) throws RarException, IOException {
        long startPos = hd.getPositionInFile() + ((long) hd.getHeaderSize(this.archive.isEncrypted()));
        this.unpPackedSize = hd.getFullPackSize();
        this.archive.getChannel().setPosition(startPos);
        this.underlyingDataIo = new RawDataIo(this.archive.getChannel());
        this.subHead = hd;
        this.curUnpRead = 0L;
        this.curPackWrite = 0L;
        this.packedCRC = -1L;
        if (hd.isEncrypted()) {
            try {
                Cipher cipher = Rijndael.buildDecipherer(this.archive.getPassword(), hd.getSalt());
                this.underlyingDataIo.setCipher(cipher);
            } catch (Exception e) {
                throw new InitDeciphererFailedException(e);
            }
        }
    }

    public int unpRead(byte[] addr, int offset, int count) throws RarException, IOException {
        int retCode = 0;
        int totalRead = 0;
        while (count > 0) {
            int readSize = ((long) count) > this.unpPackedSize ? (int) this.unpPackedSize : count;
            retCode = this.underlyingDataIo.read(addr, offset, readSize);
            if (retCode < 0) {
                throw new EOFException();
            }
            if (this.subHead.isSplitAfter()) {
                this.packedCRC = RarCRC.checkCrc((int) this.packedCRC, addr, offset, retCode);
            }
            totalRead += retCode;
            count -= retCode;
            offset += retCode;
            this.unpPackedSize -= (long) retCode;
            this.curUnpRead += (long) retCode;
            this.archive.bytesReadRead(retCode);
            if (this.unpPackedSize != 0 || !this.subHead.isSplitAfter()) {
                break;
            }
            Volume nextVolume = this.archive.getVolumeManager().nextVolume(this.archive, this.archive.getVolume());
            if (nextVolume == null) {
                this.nextVolumeMissing = true;
                return -1;
            }
            FileHeader hd = getSubHeader();
            if (hd.getUnpVersion() >= 20 && hd.getFileCRC() != -1 && getPackedCRC() != (~hd.getFileCRC())) {
                throw new CrcErrorException();
            }
            UnrarCallback callback = this.archive.getUnrarCallback();
            if (callback != null && !callback.isNextVolumeReady(nextVolume)) {
                return -1;
            }
            this.archive.setVolume(nextVolume);
            FileHeader hd2 = this.archive.nextFileHeader();
            if (hd2 == null) {
                return -1;
            }
            init(hd2);
        }
        if (retCode != -1) {
            int retCode2 = totalRead;
            return retCode2;
        }
        return retCode;
    }

    public void unpWrite(byte[] addr, int offset, int count) throws IOException {
        if (!this.testMode) {
            this.outputStream.write(addr, offset, count);
        }
        this.curUnpWrite += (long) count;
        if (!this.skipUnpCRC) {
            if (this.archive.isOldFormat()) {
                this.unpFileCRC = RarCRC.checkOldCrc((short) this.unpFileCRC, addr, count);
            } else {
                this.unpFileCRC = RarCRC.checkCrc((int) this.unpFileCRC, addr, offset, count);
            }
        }
    }

    public void setPackedSizeToRead(long size) {
        this.unpPackedSize = size;
    }

    public void setTestMode(boolean mode) {
        this.testMode = mode;
    }

    public void setSkipUnpCRC(boolean skip) {
        this.skipUnpCRC = skip;
    }

    public void setSubHeader(FileHeader hd) {
        this.subHead = hd;
    }

    public long getCurPackRead() {
        return this.curPackRead;
    }

    public void setCurPackRead(long curPackRead) {
        this.curPackRead = curPackRead;
    }

    public long getCurPackWrite() {
        return this.curPackWrite;
    }

    public void setCurPackWrite(long curPackWrite) {
        this.curPackWrite = curPackWrite;
    }

    public long getCurUnpRead() {
        return this.curUnpRead;
    }

    public void setCurUnpRead(long curUnpRead) {
        this.curUnpRead = curUnpRead;
    }

    public long getCurUnpWrite() {
        return this.curUnpWrite;
    }

    public void setCurUnpWrite(long curUnpWrite) {
        this.curUnpWrite = curUnpWrite;
    }

    public int getDecryption() {
        return this.decryption;
    }

    public void setDecryption(int decryption) {
        this.decryption = decryption;
    }

    public int getEncryption() {
        return this.encryption;
    }

    public void setEncryption(int encryption) {
        this.encryption = encryption;
    }

    public boolean isNextVolumeMissing() {
        return this.nextVolumeMissing;
    }

    public void setNextVolumeMissing(boolean nextVolumeMissing) {
        this.nextVolumeMissing = nextVolumeMissing;
    }

    public long getPackedCRC() {
        return this.packedCRC;
    }

    public void setPackedCRC(long packedCRC) {
        this.packedCRC = packedCRC;
    }

    public long getPackFileCRC() {
        return this.packFileCRC;
    }

    public void setPackFileCRC(long packFileCRC) {
        this.packFileCRC = packFileCRC;
    }

    public boolean isPackVolume() {
        return this.packVolume;
    }

    public void setPackVolume(boolean packVolume) {
        this.packVolume = packVolume;
    }

    public long getProcessedArcSize() {
        return this.processedArcSize;
    }

    public void setProcessedArcSize(long processedArcSize) {
        this.processedArcSize = processedArcSize;
    }

    public long getTotalArcSize() {
        return this.totalArcSize;
    }

    public void setTotalArcSize(long totalArcSize) {
        this.totalArcSize = totalArcSize;
    }

    public long getTotalPackRead() {
        return this.totalPackRead;
    }

    public void setTotalPackRead(long totalPackRead) {
        this.totalPackRead = totalPackRead;
    }

    public long getUnpArcSize() {
        return this.unpArcSize;
    }

    public void setUnpArcSize(long unpArcSize) {
        this.unpArcSize = unpArcSize;
    }

    public long getUnpFileCRC() {
        return this.unpFileCRC;
    }

    public void setUnpFileCRC(long unpFileCRC) {
        this.unpFileCRC = unpFileCRC;
    }

    public boolean isUnpVolume() {
        return this.unpVolume;
    }

    public void setUnpVolume(boolean unpVolume) {
        this.unpVolume = unpVolume;
    }

    public FileHeader getSubHeader() {
        return this.subHead;
    }
}

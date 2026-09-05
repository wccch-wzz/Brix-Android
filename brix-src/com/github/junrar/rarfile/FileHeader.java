package com.github.junrar.rarfile;

import com.github.junrar.exception.CorruptHeaderException;
import com.github.junrar.io.Raw;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import kotlin.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class FileHeader extends BlockHeader {
    private static final long NANOS_PER_UNIT = 100;
    private static final byte NEWLHD_SIZE = 32;
    private static final byte SALT_SIZE = 8;
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) FileHeader.class);
    private FileTime aTime;
    private FileTime arcTime;
    private FileTime cTime;
    private int fileAttr;
    private final int fileCRC;
    private String fileName;
    private final byte[] fileNameBytes;
    private String fileNameW;
    private long fullPackSize;
    private long fullUnpackSize;
    private final int highPackSize;
    private int highUnpackSize;
    private final HostSystem hostOS;
    private FileTime mTime;
    private short nameSize;
    private int recoverySectors;
    private final byte[] salt;
    private byte[] subData;
    private int subFlags;
    private byte unpMethod;
    private final long unpSize;
    private byte unpVersion;

    public FileHeader(BlockHeader bh, byte[] fileHeader) throws CorruptHeaderException {
        short extTimeFlags;
        super(bh);
        this.salt = new byte[8];
        this.recoverySectors = -1;
        this.unpSize = Raw.readIntLittleEndianAsLong(fileHeader, 0);
        this.hostOS = HostSystem.findHostSystem(fileHeader[4]);
        int position = 0 + 4 + 1;
        this.fileCRC = Raw.readIntLittleEndian(fileHeader, position);
        int position2 = position + 4;
        int fileTime = Raw.readIntLittleEndian(fileHeader, position2);
        this.unpVersion = (byte) (this.unpVersion | (fileHeader[13] & UByte.MAX_VALUE));
        this.unpMethod = (byte) (this.unpMethod | (fileHeader[14] & UByte.MAX_VALUE));
        int position3 = position2 + 4 + 1 + 1;
        this.nameSize = Raw.readShortLittleEndian(fileHeader, position3);
        int position4 = position3 + 2;
        this.fileAttr = Raw.readIntLittleEndian(fileHeader, position4);
        int position5 = position4 + 4;
        if (isLargeBlock()) {
            this.highPackSize = Raw.readIntLittleEndian(fileHeader, position5);
            int position6 = position5 + 4;
            this.highUnpackSize = Raw.readIntLittleEndian(fileHeader, position6);
            position5 = position6 + 4;
        } else {
            this.highPackSize = 0;
            this.highUnpackSize = 0;
            if (this.unpSize == -1) {
                this.highUnpackSize = Integer.MAX_VALUE;
            }
        }
        this.fullPackSize |= (long) this.highPackSize;
        this.fullPackSize <<= 32;
        this.fullPackSize |= getPackSize();
        this.fullUnpackSize |= (long) this.highUnpackSize;
        this.fullUnpackSize <<= 32;
        this.fullUnpackSize += this.unpSize;
        this.nameSize = this.nameSize <= 4096 ? this.nameSize : BaseBlock.LHD_EXTTIME;
        if (this.nameSize <= 0) {
            throw new CorruptHeaderException("Invalid file name with negative size");
        }
        this.fileNameBytes = new byte[this.nameSize];
        System.arraycopy(fileHeader, position5, this.fileNameBytes, 0, this.nameSize);
        int position7 = position5 + this.nameSize;
        if (isFileHeader()) {
            if (isUnicode()) {
                int length = 0;
                while (length < this.fileNameBytes.length && this.fileNameBytes[length] != 0) {
                    length++;
                }
                this.fileName = new String(this.fileNameBytes, 0, length);
                if (length != this.nameSize) {
                    this.fileNameW = FileNameDecoder.decode(this.fileNameBytes, length + 1);
                } else {
                    this.fileNameW = "";
                }
            } else {
                this.fileName = new String(this.fileNameBytes);
                this.fileNameW = "";
            }
            if (!isFilenameValid(getFileName())) {
                throw new CorruptHeaderException("Invalid filename: " + getFileName());
            }
        }
        if (UnrarHeadertype.NewSubHeader.equals(this.headerType)) {
            int datasize = (this.headerSize - 32) - this.nameSize;
            datasize = hasSalt() ? datasize - 8 : datasize;
            if (datasize > 0) {
                this.subData = new byte[datasize];
                for (int i = 0; i < datasize; i++) {
                    this.subData[i] = fileHeader[position7];
                    position7++;
                }
            }
            if (NewSubHeaderType.SUBHEAD_TYPE_RR.byteEquals(this.fileNameBytes)) {
                this.recoverySectors = (this.subData[8] & UByte.MAX_VALUE) + ((this.subData[9] & UByte.MAX_VALUE) << 8) + ((this.subData[10] & UByte.MAX_VALUE) << 16) + ((this.subData[11] & UByte.MAX_VALUE) << 24);
            }
        }
        if (hasSalt()) {
            for (int i2 = 0; i2 < 8; i2++) {
                this.salt[i2] = fileHeader[position7];
                position7++;
            }
        }
        this.mTime = FileTime.fromMillis(getDateDos(fileTime));
        if (hasExtTime()) {
            if (position7 + 1 < fileHeader.length) {
                extTimeFlags = Raw.readShortLittleEndian(fileHeader, position7);
                position7 += 2;
            } else {
                extTimeFlags = 0;
                logger.warn("FileHeader for entry '{}' signals extended time data, but does not contain any", getFileName());
            }
            TimePositionTuple mTimeTuple = parseExtTime(12, extTimeFlags, fileHeader, position7, this.mTime);
            this.mTime = mTimeTuple.time;
            TimePositionTuple cTimeTuple = parseExtTime(8, extTimeFlags, fileHeader, mTimeTuple.position);
            this.cTime = cTimeTuple.time;
            TimePositionTuple aTimeTuple = parseExtTime(4, extTimeFlags, fileHeader, cTimeTuple.position);
            this.aTime = aTimeTuple.time;
            TimePositionTuple arcTimeTuple = parseExtTime(0, extTimeFlags, fileHeader, aTimeTuple.position);
            this.arcTime = arcTimeTuple.time;
            int unused = arcTimeTuple.position;
        }
    }

    private static boolean isFilenameValid(String filename) {
        try {
            new File(filename).getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static final class TimePositionTuple {
        private final int position;
        private final FileTime time;

        private TimePositionTuple(int position, FileTime time) {
            this.position = position;
            this.time = time;
        }
    }

    private static TimePositionTuple parseExtTime(int shift, short flags, byte[] fileHeader, int position) {
        return parseExtTime(shift, flags, fileHeader, position, null);
    }

    private static TimePositionTuple parseExtTime(int shift, short flags, byte[] fileHeader, int position, FileTime baseTime) {
        long seconds;
        int position2 = position;
        int flag = flags >>> shift;
        if ((flag & 8) != 0) {
            if (baseTime != null) {
                seconds = baseTime.to(TimeUnit.SECONDS);
            } else {
                seconds = TimeUnit.MILLISECONDS.toSeconds(getDateDos(Raw.readIntLittleEndian(fileHeader, position)));
                position2 += 4;
            }
            int count = flag & 3;
            long remainder = 0;
            for (int i = 0; i < count; i++) {
                int b = fileHeader[position2] & UByte.MAX_VALUE;
                remainder = ((long) (b << 16)) | (remainder >>> 8);
                position2++;
            }
            long nanos = NANOS_PER_UNIT * remainder;
            if ((flag & 4) != 0) {
                nanos += TimeUnit.SECONDS.toNanos(1L);
            }
            FileTime time = FileTime.from(Instant.ofEpochSecond(seconds, nanos));
            return new TimePositionTuple(position2, time);
        }
        return new TimePositionTuple(position2, baseTime);
    }

    @Override // com.github.junrar.rarfile.BlockHeader, com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (logger.isInfoEnabled()) {
            StringBuilder str = new StringBuilder();
            str.append("unpSize: ").append(getUnpSize());
            str.append("\nHostOS: ").append(this.hostOS.name());
            str.append("\nMTime: ").append(this.mTime);
            str.append("\nCTime: ").append(this.cTime);
            str.append("\nATime: ").append(this.aTime);
            str.append("\nArcTime: ").append(this.arcTime);
            str.append("\nFileName: ").append(this.fileName);
            str.append("\nFileNameW: ").append(this.fileNameW);
            str.append("\nunpMethod: ").append(Integer.toHexString(getUnpMethod()));
            str.append("\nunpVersion: ").append(Integer.toHexString(getUnpVersion()));
            str.append("\nfullpackedsize: ").append(getFullPackSize());
            str.append("\nfullunpackedsize: ").append(getFullUnpackSize());
            str.append("\nisEncrypted: ").append(isEncrypted());
            str.append("\nisfileHeader: ").append(isFileHeader());
            str.append("\nisSolid: ").append(isSolid());
            str.append("\nisSplitafter: ").append(isSplitAfter());
            str.append("\nisSplitBefore:").append(isSplitBefore());
            str.append("\nunpSize: ").append(getUnpSize());
            str.append("\ndataSize: ").append(getDataSize());
            str.append("\nisUnicode: ").append(isUnicode());
            str.append("\nhasVolumeNumber: ").append(hasVolumeNumber());
            str.append("\nhasArchiveDataCRC: ").append(hasArchiveDataCRC());
            str.append("\nhasSalt: ").append(hasSalt());
            str.append("\nhasEncryptVersions: ").append(hasEncryptVersion());
            str.append("\nisSubBlock: ").append(isSubBlock());
            logger.info(str.toString());
        }
    }

    private static long getDateDos(int time) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, (time >>> 25) + 1980);
        cal.set(2, ((time >>> 21) & 15) - 1);
        cal.set(5, (time >>> 16) & 31);
        cal.set(11, (time >>> 11) & 31);
        cal.set(12, (time >>> 5) & 63);
        cal.set(13, (time & 31) * 2);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    private static Date toDate(FileTime time) {
        if (time != null) {
            return new Date(time.toMillis());
        }
        return null;
    }

    private static FileTime toFileTime(Date time) {
        if (time != null) {
            return FileTime.fromMillis(time.getTime());
        }
        return null;
    }

    public FileTime getArchivalTime() {
        return this.arcTime;
    }

    public void setArchivalTime(FileTime archivalTime) {
        this.arcTime = archivalTime;
    }

    public Date getArcTime() {
        return toDate(getArchivalTime());
    }

    public void setArcTime(Date arcTime) {
        setArchivalTime(toFileTime(arcTime));
    }

    public FileTime getLastAccessTime() {
        return this.aTime;
    }

    public void setLastAccessTime(FileTime time) {
        this.aTime = time;
    }

    public Date getATime() {
        return toDate(getLastAccessTime());
    }

    public void setATime(Date time) {
        setLastAccessTime(toFileTime(time));
    }

    public FileTime getCreationTime() {
        return this.cTime;
    }

    public void setCreationTime(FileTime time) {
        this.cTime = time;
    }

    public Date getCTime() {
        return toDate(getCreationTime());
    }

    public void setCTime(Date time) {
        setCreationTime(toFileTime(time));
    }

    public int getFileAttr() {
        return this.fileAttr;
    }

    public void setFileAttr(int fileAttr) {
        this.fileAttr = fileAttr;
    }

    public int getFileCRC() {
        return this.fileCRC;
    }

    public byte[] getFileNameByteArray() {
        return this.fileNameBytes;
    }

    @Deprecated
    public String getFileNameString() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Deprecated
    public String getFileNameW() {
        return this.fileNameW;
    }

    public void setFileNameW(String fileNameW) {
        this.fileNameW = fileNameW;
    }

    public int getHighPackSize() {
        return this.highPackSize;
    }

    public int getHighUnpackSize() {
        return this.highUnpackSize;
    }

    public HostSystem getHostOS() {
        return this.hostOS;
    }

    public FileTime getLastModifiedTime() {
        return this.mTime;
    }

    public void setLastModifiedTime(FileTime time) {
        this.mTime = time;
    }

    public Date getMTime() {
        return toDate(getLastModifiedTime());
    }

    public void setMTime(Date time) {
        setLastModifiedTime(toFileTime(time));
    }

    public short getNameSize() {
        return this.nameSize;
    }

    public int getRecoverySectors() {
        return this.recoverySectors;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public byte[] getSubData() {
        return this.subData;
    }

    public int getSubFlags() {
        return this.subFlags;
    }

    public byte getUnpMethod() {
        return this.unpMethod;
    }

    public long getUnpSize() {
        return this.unpSize;
    }

    public byte getUnpVersion() {
        return this.unpVersion;
    }

    public long getFullPackSize() {
        return this.fullPackSize;
    }

    public long getFullUnpackSize() {
        return this.fullUnpackSize;
    }

    public String toString() {
        return super.toString();
    }

    public boolean isSplitAfter() {
        return (this.flags & 2) != 0;
    }

    public boolean isSplitBefore() {
        return (this.flags & 1) != 0;
    }

    public boolean isSolid() {
        return (this.flags & 16) != 0;
    }

    public boolean isEncrypted() {
        return (this.flags & 4) != 0;
    }

    public boolean isUnicode() {
        return (this.flags & 512) != 0;
    }

    public boolean isFileHeader() {
        return UnrarHeadertype.FileHeader.equals(this.headerType);
    }

    public boolean hasSalt() {
        return (this.flags & BaseBlock.LHD_SALT) != 0;
    }

    public boolean hasExtTime() {
        return (this.flags & BaseBlock.LHD_EXTTIME) != 0;
    }

    public boolean isLargeBlock() {
        return (this.flags & 256) != 0;
    }

    public boolean isDirectory() {
        return (this.flags & 224) == 224;
    }

    public String getFileName() {
        return (!isUnicode() || this.fileNameW == null || this.fileNameW.isEmpty()) ? this.fileName : this.fileNameW;
    }
}

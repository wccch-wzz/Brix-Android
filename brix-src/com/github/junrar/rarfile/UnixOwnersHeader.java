package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import kotlin.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class UnixOwnersHeader extends SubBlockHeader {
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) UnixOwnersHeader.class);
    private String group;
    private int groupNameSize;
    private String owner;
    private int ownerNameSize;

    public UnixOwnersHeader(SubBlockHeader sb, byte[] uoHeader) {
        super(sb);
        this.ownerNameSize = Raw.readShortLittleEndian(uoHeader, 0) & UShort.MAX_VALUE;
        int pos = 0 + 2;
        this.groupNameSize = Raw.readShortLittleEndian(uoHeader, pos) & UShort.MAX_VALUE;
        int pos2 = pos + 2;
        if (this.ownerNameSize + pos2 < uoHeader.length) {
            this.owner = new String(uoHeader, pos2, this.ownerNameSize);
        }
        int pos3 = pos2 + this.ownerNameSize;
        if (this.groupNameSize + pos3 < uoHeader.length) {
            this.group = new String(uoHeader, pos3, this.groupNameSize);
        }
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getGroupNameSize() {
        return this.groupNameSize;
    }

    public void setGroupNameSize(int groupNameSize) {
        this.groupNameSize = groupNameSize;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getOwnerNameSize() {
        return this.ownerNameSize;
    }

    public void setOwnerNameSize(int ownerNameSize) {
        this.ownerNameSize = ownerNameSize;
    }

    @Override // com.github.junrar.rarfile.SubBlockHeader, com.github.junrar.rarfile.BlockHeader, com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (logger.isInfoEnabled()) {
            logger.info("ownerNameSize: {}", Integer.valueOf(this.ownerNameSize));
            logger.info("owner: {}", this.owner);
            logger.info("groupNameSize: {}", Integer.valueOf(this.groupNameSize));
            logger.info("group: {}", this.group);
        }
    }
}

package com.github.junrar.unpack.ppm;

import com.github.junrar.io.Raw;
import kotlin.UShort;

/* JADX INFO: loaded from: classes.dex */
public class RarMemBlock extends Pointer {
    public static final int size = 12;
    private int NU;
    private int next;
    private int prev;
    private int stamp;

    public RarMemBlock(byte[] mem) {
        super(mem);
    }

    public void insertAt(RarMemBlock p) {
        RarMemBlock temp = new RarMemBlock(this.mem);
        setPrev(p.getAddress());
        temp.setAddress(getPrev());
        setNext(temp.getNext());
        temp.setNext(this);
        temp.setAddress(getNext());
        temp.setPrev(this);
    }

    public void remove() {
        RarMemBlock temp = new RarMemBlock(this.mem);
        temp.setAddress(getPrev());
        temp.setNext(getNext());
        temp.setAddress(getNext());
        temp.setPrev(getPrev());
    }

    public int getNext() {
        if (this.mem != null) {
            this.next = Raw.readIntLittleEndian(this.mem, this.pos + 4);
        }
        return this.next;
    }

    public void setNext(RarMemBlock next) {
        setNext(next.getAddress());
    }

    public void setNext(int next) {
        this.next = next;
        if (this.mem != null) {
            Raw.writeIntLittleEndian(this.mem, this.pos + 4, next);
        }
    }

    public int getNU() {
        if (this.mem != null) {
            this.NU = Raw.readShortLittleEndian(this.mem, this.pos + 2) & UShort.MAX_VALUE;
        }
        return this.NU;
    }

    public void setNU(int nu) {
        this.NU = 65535 & nu;
        if (this.mem != null) {
            Raw.writeShortLittleEndian(this.mem, this.pos + 2, (short) nu);
        }
    }

    public int getPrev() {
        if (this.mem != null) {
            this.prev = Raw.readIntLittleEndian(this.mem, this.pos + 8);
        }
        return this.prev;
    }

    public void setPrev(RarMemBlock prev) {
        setPrev(prev.getAddress());
    }

    public void setPrev(int prev) {
        this.prev = prev;
        if (this.mem != null) {
            Raw.writeIntLittleEndian(this.mem, this.pos + 8, prev);
        }
    }

    public int getStamp() {
        if (this.mem != null) {
            this.stamp = Raw.readShortLittleEndian(this.mem, this.pos) & UShort.MAX_VALUE;
        }
        return this.stamp;
    }

    public void setStamp(int stamp) {
        this.stamp = stamp;
        if (this.mem != null) {
            Raw.writeShortLittleEndian(this.mem, this.pos, (short) stamp);
        }
    }
}

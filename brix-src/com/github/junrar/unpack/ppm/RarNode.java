package com.github.junrar.unpack.ppm;

import com.github.junrar.io.Raw;

/* JADX INFO: loaded from: classes.dex */
public class RarNode extends Pointer {
    public static final int size = 4;
    private int next;

    public RarNode(byte[] mem) {
        super(mem);
    }

    public int getNext() {
        if (this.mem != null) {
            this.next = Raw.readIntLittleEndian(this.mem, this.pos);
        }
        return this.next;
    }

    public void setNext(RarNode next) {
        setNext(next.getAddress());
    }

    public void setNext(int next) {
        this.next = next;
        if (this.mem != null) {
            Raw.writeIntLittleEndian(this.mem, this.pos, next);
        }
    }

    public String toString() {
        return "State[\n  pos=" + this.pos + "\n  size=4\n  next=" + getNext() + "\n]";
    }
}

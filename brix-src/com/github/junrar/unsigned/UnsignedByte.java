package com.github.junrar.unsigned;

/* JADX INFO: loaded from: classes.dex */
public class UnsignedByte {
    public static byte longToByte(long unsignedByte1) {
        return (byte) (255 & unsignedByte1);
    }

    public static byte intToByte(int unsignedByte1) {
        return (byte) (unsignedByte1 & 255);
    }

    public static byte shortToByte(short unsignedByte1) {
        return (byte) (unsignedByte1 & 255);
    }

    public static short add(byte unsignedByte1, byte unsignedByte2) {
        return (short) (unsignedByte1 + unsignedByte2);
    }

    public static short sub(byte unsignedByte1, byte unsignedByte2) {
        return (short) (unsignedByte1 - unsignedByte2);
    }
}

package com.github.junrar.rarfile;

import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class FileNameDecoder {
    public static int getChar(byte[] name, int pos) {
        return name[pos] & UByte.MAX_VALUE;
    }

    public static String decode(byte[] name, int encPos) {
        int decPos = 0;
        int flags = 0;
        int flagBits = 0;
        int encPos2 = encPos + 1;
        int highByte = getChar(name, encPos);
        StringBuilder buf = new StringBuilder();
        while (encPos2 < name.length) {
            if (flagBits == 0) {
                flags = getChar(name, encPos2);
                flagBits = 8;
                encPos2++;
            }
            switch (flags >>> 6) {
                case 0:
                    buf.append((char) getChar(name, encPos2));
                    decPos++;
                    encPos2++;
                    break;
                case 1:
                    buf.append((char) (getChar(name, encPos2) + (highByte << 8)));
                    decPos++;
                    encPos2++;
                    break;
                case 2:
                    int low = getChar(name, encPos2);
                    int high = getChar(name, encPos2 + 1);
                    buf.append((char) ((high << 8) + low));
                    decPos++;
                    encPos2 += 2;
                    break;
                case 3:
                    int encPos3 = encPos2 + 1;
                    int length = getChar(name, encPos2);
                    if ((length & 128) != 0) {
                        int encPos4 = encPos3 + 1;
                        int correction = getChar(name, encPos3);
                        int length2 = (length & 127) + 2;
                        while (length2 > 0 && decPos < name.length) {
                            int low2 = (getChar(name, decPos) + correction) & 255;
                            buf.append((char) ((highByte << 8) + low2));
                            length2--;
                            decPos++;
                        }
                        encPos2 = encPos4;
                    } else {
                        int length3 = length + 2;
                        while (length3 > 0 && decPos < name.length) {
                            buf.append((char) getChar(name, decPos));
                            length3--;
                            decPos++;
                        }
                        encPos2 = encPos3;
                    }
                    break;
            }
            flags = (flags << 2) & 255;
            flagBits -= 2;
        }
        return buf.toString();
    }
}

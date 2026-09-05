package com.github.junrar.unpack.ppm;

import com.github.junrar.rarfile.MainHeader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class AnalyzeHeapDump {
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) MainHeader.class);

    /* JADX WARN: Code duplicated, block: B:27:0x00a9  */
    public static void main(String[] argv) throws Throwable {
        Throwable th;
        File cfile = new File("P:\\test\\heapdumpc");
        File jfile = new File("P:\\test\\heapdumpj");
        if (!cfile.exists()) {
            logger.error("File not found: {}", cfile.getAbsolutePath());
            return;
        }
        if (!jfile.exists()) {
            logger.error("File not found: {}", jfile.getAbsolutePath());
            return;
        }
        long clen = cfile.length();
        long jlen = jfile.length();
        if (clen != jlen) {
            logger.info("File size mismatch");
            logger.info("clen = {}", Long.valueOf(clen));
            logger.info("jlen = {}", Long.valueOf(jlen));
        }
        long len = Math.min(clen, jlen);
        InputStream cin = null;
        InputStream jin = null;
        try {
            try {
                cin = new BufferedInputStream(new FileInputStream(cfile), 262144);
                jin = new BufferedInputStream(new FileInputStream(jfile), 262144);
                boolean matching = true;
                boolean mismatchFound = false;
                long startOff = 0;
                long off = 0;
                while (off < len) {
                    boolean matching2 = matching;
                    try {
                        long clen2 = clen;
                        try {
                            try {
                                if (cin.read() != jin.read()) {
                                    if (matching2) {
                                        long startOff2 = off;
                                        matching = false;
                                        mismatchFound = true;
                                        startOff = startOff2;
                                    } else {
                                        matching = matching2;
                                    }
                                } else if (!matching2) {
                                    printMismatch(startOff, off);
                                    matching = true;
                                } else {
                                    matching = matching2;
                                }
                                off++;
                                clen = clen2;
                            } catch (IOException e) {
                                e = e;
                                logger.error("", (Throwable) e);
                                cin.close();
                                jin.close();
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            try {
                                cin.close();
                                jin.close();
                                throw th;
                            } catch (IOException e2) {
                                logger.error("", (Throwable) e2);
                                throw th;
                            }
                        }
                    } catch (IOException e3) {
                        e = e3;
                        logger.error("", (Throwable) e);
                        cin.close();
                        jin.close();
                    } catch (Throwable th3) {
                        th = th3;
                        cin.close();
                        jin.close();
                        throw th;
                    }
                }
                if (!matching) {
                    printMismatch(startOff, off);
                }
                if (!mismatchFound) {
                    logger.info("Files are identical");
                }
                logger.info("Done");
                cin.close();
            } catch (IOException e4) {
                logger.error("", (Throwable) e4);
                return;
            }
        } catch (IOException e5) {
            e = e5;
        } catch (Throwable th4) {
            th = th4;
        }
        jin.close();
    }

    private static void printMismatch(long startOff, long bytesRead) {
        if (logger.isInfoEnabled()) {
            logger.info("Mismatch: off={}(0x{}), len={}", Long.valueOf(startOff), Long.toHexString(startOff), Long.valueOf(bytesRead - startOff));
        }
    }
}

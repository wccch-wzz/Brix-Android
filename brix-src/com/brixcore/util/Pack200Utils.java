package com.brixcore.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;

/* JADX INFO: loaded from: classes11.dex */
public class Pack200Utils {
    public static void unpack(String nativeLibraryDir, String dir) {
        File basePath = new File(dir);
        Collection<File> files = FileUtils.listFiles(basePath, new String[]{"pack"}, true);
        File workdir = new File(nativeLibraryDir);
        ProcessBuilder processBuilder = new ProcessBuilder(new String[0]).directory(workdir);
        for (File jarFile : files) {
            try {
                Process process = processBuilder.command("./libunpack200.so", "-r", jarFile.getAbsolutePath(), jarFile.getAbsolutePath().replace(".pack", "")).start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new IOException("unpack200 failed with exit code " + exitCode);
                }
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Failed to unpack file: " + jarFile.getAbsolutePath(), (Throwable) e);
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
                Logging.LOG.log(Level.WARNING, "Failed to unpack file: " + jarFile.getAbsolutePath(), (Throwable) e2);
            }
        }
    }

    public static void unpack(String nativeLibraryDir, String in, String out) {
        try {
            File workdir = new File(nativeLibraryDir);
            ProcessBuilder processBuilder = new ProcessBuilder(new String[0]).directory(workdir);
            Process process = processBuilder.command("./libunpack200.so", "-r", in, out).start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("unpack200 failed with exit code " + exitCode);
            }
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Failed to unpack file: " + in, (Throwable) e);
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            Logging.LOG.log(Level.WARNING, "Failed to unpack file: " + in, (Throwable) e2);
        }
    }
}

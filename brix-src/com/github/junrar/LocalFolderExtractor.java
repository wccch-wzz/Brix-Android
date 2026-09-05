package com.github.junrar;

import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
class LocalFolderExtractor {
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) LocalFolderExtractor.class);
    private final File folderDestination;

    LocalFolderExtractor(File destination) {
        this.folderDestination = destination;
    }

    File createDirectory(FileHeader fh) {
        String fileName = null;
        if (fh.isDirectory()) {
            fileName = fh.getFileName();
        }
        if (fileName == null) {
            return null;
        }
        File f = new File(this.folderDestination, fileName);
        try {
            String fileCanonPath = f.getCanonicalPath();
            if (!fileCanonPath.startsWith(this.folderDestination.getCanonicalPath())) {
                String errorMessage = "Rar contains invalid path: '" + fileCanonPath + "'";
                throw new IllegalStateException(errorMessage);
            }
            return f;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    File extract(Archive arch, FileHeader fileHeader) throws RarException, IOException {
        File f = createFile(fileHeader, this.folderDestination);
        OutputStream stream = new FileOutputStream(f);
        try {
            arch.extractFile(fileHeader, stream);
            stream.close();
            return f;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                try {
                    stream.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    private File createFile(FileHeader fh, File destination) throws IOException {
        String name = fh.getFileName();
        File f = new File(destination, name);
        String dirCanonPath = f.getCanonicalPath();
        if (!dirCanonPath.startsWith(destination.getCanonicalPath())) {
            String errorMessage = "Rar contains file with invalid path: '" + dirCanonPath + "'";
            throw new IllegalStateException(errorMessage);
        }
        if (!f.exists()) {
            try {
                return makeFile(destination, name);
            } catch (IOException e) {
                logger.error("error creating the new file: {}", f.getName(), e);
                return f;
            }
        }
        return f;
    }

    private File makeFile(File destination, String name) throws IOException {
        String[] dirs = name.split("\\\\");
        String path = "";
        int size = dirs.length;
        if (size == 1) {
            return new File(destination, name);
        }
        if (size > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                path = path + File.separator + dirs[i];
                File dir = new File(destination, path);
                dir.mkdir();
            }
            File f = new File(destination, path + File.separator + dirs[dirs.length - 1]);
            f.createNewFile();
            return f;
        }
        return null;
    }
}

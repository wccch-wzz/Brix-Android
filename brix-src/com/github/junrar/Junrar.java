package com.github.junrar;

import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.volume.VolumeManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class Junrar {
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) Junrar.class);

    public static List<File> extract(String rarPath, String destinationPath) throws RarException, IOException {
        return extract(rarPath, destinationPath, (String) null);
    }

    public static List<File> extract(String rarPath, String destinationPath, String password) throws RarException, IOException {
        if (rarPath == null || destinationPath == null) {
            throw new IllegalArgumentException("archive and destination must be set");
        }
        return extract(new File(rarPath), new File(destinationPath), password);
    }

    public static List<File> extract(File rar, File destinationFolder) throws RarException, IOException {
        return extract(rar, destinationFolder, (String) null);
    }

    public static List<File> extract(File rar, File destinationFolder, String password) throws Exception {
        validateRarPath(rar);
        validateDestinationPath(destinationFolder);
        Archive archive = createArchiveOrThrowException(rar, password);
        LocalFolderExtractor lfe = new LocalFolderExtractor(destinationFolder);
        return extractArchiveTo(archive, lfe);
    }

    public static List<File> extract(InputStream resourceAsStream, File destinationFolder) throws RarException, IOException {
        return extract(resourceAsStream, destinationFolder, (String) null);
    }

    public static List<File> extract(InputStream resourceAsStream, File destinationFolder, String password) throws Exception {
        validateDestinationPath(destinationFolder);
        Archive arch = createArchiveOrThrowException(resourceAsStream, password);
        LocalFolderExtractor lfe = new LocalFolderExtractor(destinationFolder);
        return extractArchiveTo(arch, lfe);
    }

    public static List<File> extract(VolumeManager volumeManager, File destinationFolder) throws Exception {
        validateDestinationPath(destinationFolder);
        Archive arch = createArchiveOrThrowException(volumeManager, (String) null);
        LocalFolderExtractor lfe = new LocalFolderExtractor(destinationFolder);
        return extractArchiveTo(arch, lfe);
    }

    public static List<File> extract(VolumeManager volumeManager, File destinationFolder, String password) throws Exception {
        validateDestinationPath(destinationFolder);
        Archive arch = createArchiveOrThrowException(volumeManager, password);
        LocalFolderExtractor lfe = new LocalFolderExtractor(destinationFolder);
        return extractArchiveTo(arch, lfe);
    }

    public static List<ContentDescription> getContentsDescription(File rar) throws Exception {
        validateRarPath(rar);
        Archive arch = createArchiveOrThrowException(rar, (String) null);
        return getContentsDescriptionFromArchive(arch);
    }

    public static List<ContentDescription> getContentsDescription(InputStream resourceAsStream) throws Exception {
        Archive arch = createArchiveOrThrowException(resourceAsStream, (String) null);
        return getContentsDescriptionFromArchive(arch);
    }

    private static List<ContentDescription> getContentsDescriptionFromArchive(Archive arch) throws RarException, IOException {
        List<ContentDescription> contents = new ArrayList<>();
        try {
            if (arch.isEncrypted()) {
                logger.warn("archive is encrypted cannot extract");
                return new ArrayList();
            }
            for (FileHeader fileHeader : arch) {
                contents.add(new ContentDescription(fileHeader.getFileName(), fileHeader.getUnpSize()));
            }
            return contents;
        } finally {
            arch.close();
        }
    }

    private static Archive createArchiveOrThrowException(VolumeManager volumeManager, String password) throws Exception {
        try {
            return new Archive(volumeManager, (UnrarCallback) null, password);
        } catch (RarException | IOException e) {
            logger.error("Error while creating archive", (Throwable) e);
            throw e;
        }
    }

    private static Archive createArchiveOrThrowException(InputStream rarAsStream, String password) throws Exception {
        try {
            return new Archive(rarAsStream, password);
        } catch (RarException | IOException e) {
            logger.error("Error while creating archive", (Throwable) e);
            throw e;
        }
    }

    private static Archive createArchiveOrThrowException(File file, String password) throws Exception {
        try {
            return new Archive(file, password);
        } catch (RarException | IOException e) {
            logger.error("Error while creating archive", (Throwable) e);
            throw e;
        }
    }

    private static void validateDestinationPath(File destinationFolder) {
        if (destinationFolder == null) {
            throw new IllegalArgumentException("archive and destination must me set");
        }
        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            throw new IllegalArgumentException("the destination must exist and point to a directory: " + destinationFolder);
        }
    }

    private static void validateRarPath(File rar) {
        if (rar == null) {
            throw new IllegalArgumentException("archive and destination must me set");
        }
        if (!rar.exists()) {
            throw new IllegalArgumentException("the archive does not exit: " + rar);
        }
        if (!rar.isFile()) {
            throw new IllegalArgumentException("First argument should be a file but was " + rar.getAbsolutePath());
        }
    }

    private static List<File> extractArchiveTo(Archive arch, LocalFolderExtractor destination) throws RarException, IOException {
        List<File> extractedFiles = new ArrayList<>();
        try {
            for (FileHeader fh : arch) {
                try {
                    File file = tryToExtract(destination, arch, fh);
                    if (file != null) {
                        extractedFiles.add(file);
                    }
                } catch (RarException | IOException e) {
                    logger.error("error extracting the file", (Throwable) e);
                    throw e;
                }
            }
            arch.close();
            return extractedFiles;
        } catch (Throwable th) {
            arch.close();
            throw th;
        }
    }

    private static File tryToExtract(LocalFolderExtractor destination, Archive arch, FileHeader fileHeader) throws RarException, IOException {
        String fileNameString = fileHeader.getFileName();
        logger.info("extracting: {}", fileNameString);
        if (fileHeader.isDirectory()) {
            return destination.createDirectory(fileHeader);
        }
        return destination.extract(arch, fileHeader);
    }
}

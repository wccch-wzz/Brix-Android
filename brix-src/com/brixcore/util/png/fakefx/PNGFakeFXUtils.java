package com.brixcore.util.png.fakefx;

import android.graphics.Bitmap;
import com.brixcore.util.png.PNGType;
import com.brixcore.util.png.PNGWriter;
import com.brixcore.util.png.image.ArgbImageWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/* JADX INFO: loaded from: classes14.dex */
public final class PNGFakeFXUtils {
    private PNGFakeFXUtils() {
    }

    public static ArgbImageWrapper<Bitmap> asArgbImage(Bitmap image) {
        return new ArgbImageWrapper<Bitmap>(image, image.getWidth(), image.getHeight()) { // from class: com.brixcore.util.png.fakefx.PNGFakeFXUtils.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.brixcore.util.png.image.ArgbImage
            public int getArgb(int x, int y) {
                return ((Bitmap) this.image).getPixel(x, y);
            }
        };
    }

    public static void writeImage(Bitmap image, Path file) throws IOException {
        writeImage(image, Files.newOutputStream(file, new OpenOption[0]));
    }

    public static void writeImage(Bitmap image, Path file, PNGType type) throws IOException {
        writeImage(image, Files.newOutputStream(file, new OpenOption[0]), type);
    }

    public static void writeImage(Bitmap image, Path file, PNGType type, int compressLevel) throws IOException {
        writeImage(image, Files.newOutputStream(file, new OpenOption[0]), type, compressLevel);
    }

    public static byte[] writeImageToArray(Bitmap image) {
        return writeImageToArray(image, PNGType.RGBA, -1);
    }

    public static byte[] writeImageToArray(Bitmap image, PNGType type) {
        return writeImageToArray(image, type, -1);
    }

    public static byte[] writeImageToArray(Bitmap image, PNGType type, int compressLevel) {
        int estimatedSize = (image.getWidth() * image.getHeight() * (type == PNGType.RGB ? 3 : 4)) + 32;
        if (compressLevel != 1) {
            estimatedSize /= 2;
        }
        try {
            ByteArrayOutputStream temp = new ByteArrayOutputStream(Integer.max(estimatedSize, 32));
            writeImage(image, temp, type, compressLevel);
            return temp.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeImage(Bitmap image, OutputStream out) throws IOException {
        writeImage(image, out, PNGType.RGBA, -1);
    }

    private static void writeImage(Bitmap image, OutputStream out, PNGType type) throws IOException {
        writeImage(image, out, type, -1);
    }

    private static void writeImage(Bitmap image, OutputStream out, PNGType type, int compressLevel) throws IOException {
        new PNGWriter(out, type, compressLevel).write(asArgbImage(image));
    }
}

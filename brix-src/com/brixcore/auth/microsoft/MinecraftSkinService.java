package com.brixcore.auth.microsoft;

import com.brixcore.auth.ServerDisconnectException;
import com.brixcore.auth.ServerResponseMalformedException;
import com.brixcore.util.Logging;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.HttpMultipartRequest;
import com.brixcore.util.io.NetworkUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jsoup.helper.HttpConnection;

/* JADX INFO: compiled from: MinecraftSkinService.kt */
/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J \u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u0010\u0010\f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0007J\u0018\u0010\r\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u0005H\u0007J\u0010\u0010\u000f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/brixcore/auth/microsoft/MinecraftSkinService;", "", "<init>", "()V", "BASE_URL", "", "uploadSkin", "", "accessToken", "model", "file", "Ljava/nio/file/Path;", "resetSkin", "showCape", "capeId", "hideCape", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class MinecraftSkinService {
    private static final String BASE_URL = "https://api.minecraftservices.com/minecraft/profile";
    public static final MinecraftSkinService INSTANCE = new MinecraftSkinService();

    private MinecraftSkinService() {
    }

    @JvmStatic
    public static final void uploadSkin(String accessToken, String model, Path file) throws ServerResponseMalformedException, ServerDisconnectException {
        Intrinsics.checkNotNullParameter(accessToken, "accessToken");
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(file, "file");
        try {
            HttpURLConnection con = NetworkUtils.createHttpConnection(new URL("https://api.minecraftservices.com/minecraft/profile/skins"));
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setDoOutput(true);
            HttpMultipartRequest httpMultipartRequest = new HttpMultipartRequest(con);
            try {
                HttpMultipartRequest request = httpMultipartRequest;
                request.param("variant", model);
                InputStream inputStreamNewInputStream = Files.newInputStream(file, new OpenOption[0]);
                try {
                    InputStream fis = inputStreamNewInputStream;
                    request.file("file", FileUtils.getName(file), "image/" + FileUtils.getExtension(file), fis);
                    CloseableKt.closeFinally(inputStreamNewInputStream, null);
                    CloseableKt.closeFinally(httpMultipartRequest, null);
                    int responseCode = con.getResponseCode();
                    String response = NetworkUtils.readData(con);
                    if (responseCode / 100 != 2) {
                        throw new ServerResponseMalformedException("Failed to upload skin: HTTP " + responseCode + " - " + response);
                    }
                    Logging.LOG.info("Skin uploaded successfully, model: " + model);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        CloseableKt.closeFinally(inputStreamNewInputStream, th);
                        throw th2;
                    }
                }
            } catch (Throwable th3) {
                try {
                    throw th3;
                } catch (Throwable th4) {
                    CloseableKt.closeFinally(httpMultipartRequest, th3);
                    throw th4;
                }
            }
        } catch (IOException e) {
            throw new ServerDisconnectException(e);
        }
    }

    @JvmStatic
    public static final void resetSkin(String accessToken) throws ServerResponseMalformedException, ServerDisconnectException {
        Intrinsics.checkNotNullParameter(accessToken, "accessToken");
        try {
            HttpURLConnection con = NetworkUtils.createHttpConnection(new URL("https://api.minecraftservices.com/minecraft/profile/skins/active"));
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            int responseCode = con.getResponseCode();
            if (responseCode / 100 != 2) {
                String response = NetworkUtils.readData(con);
                throw new ServerResponseMalformedException("Failed to reset skin: HTTP " + responseCode + " - " + response);
            }
            Logging.LOG.info("Skin reset successfully");
        } catch (IOException e) {
            throw new ServerDisconnectException(e);
        }
    }

    @JvmStatic
    public static final void showCape(String accessToken, String capeId) throws ServerResponseMalformedException, ServerDisconnectException {
        Intrinsics.checkNotNullParameter(accessToken, "accessToken");
        Intrinsics.checkNotNullParameter(capeId, "capeId");
        try {
            byte[] payload = ("{\"capeId\":\"" + capeId + "\"}").getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(payload, "getBytes(...)");
            HttpURLConnection con = NetworkUtils.createHttpConnection(new URL("https://api.minecraftservices.com/minecraft/profile/capes/active"));
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setRequestProperty(HttpConnection.CONTENT_TYPE, "application/json");
            OutputStream outputStream = con.getOutputStream();
            try {
                OutputStream it = outputStream;
                it.write(payload);
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(outputStream, null);
                int responseCode = con.getResponseCode();
                if (responseCode / 100 != 2) {
                    String response = NetworkUtils.readData(con);
                    throw new ServerResponseMalformedException("Failed to show cape: HTTP " + responseCode + " - " + response);
                }
                Logging.LOG.info("Cape activated successfully, capeId: " + capeId);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(outputStream, th);
                    throw th2;
                }
            }
        } catch (IOException e) {
            throw new ServerDisconnectException(e);
        }
    }

    @JvmStatic
    public static final void hideCape(String accessToken) throws ServerResponseMalformedException, ServerDisconnectException {
        Intrinsics.checkNotNullParameter(accessToken, "accessToken");
        try {
            HttpURLConnection con = NetworkUtils.createHttpConnection(new URL("https://api.minecraftservices.com/minecraft/profile/capes/active"));
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            int responseCode = con.getResponseCode();
            if (responseCode / 100 != 2) {
                String response = NetworkUtils.readData(con);
                throw new ServerResponseMalformedException("Failed to hide cape: HTTP " + responseCode + " - " + response);
            }
            Logging.LOG.info("Cape hidden successfully");
        } catch (IOException e) {
            throw new ServerDisconnectException(e);
        }
    }
}

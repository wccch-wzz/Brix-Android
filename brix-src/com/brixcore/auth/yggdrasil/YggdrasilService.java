package com.brixcore.auth.yggdrasil;

import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.ServerDisconnectException;
import com.brixcore.auth.ServerResponseMalformedException;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.StringUtils;
import com.brixcore.util.fakefx.ObservableOptionalCache;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.UUIDTypeAdapter;
import com.brixcore.util.gson.ValidationTypeAdapterFactory;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.HttpMultipartRequest;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes3.dex */
public class YggdrasilService {
    public static final String PURCHASE_URL = "https://www.microsoft.com/store/productid/9nxp44l49shj";
    private final ObservableOptionalCache<UUID, CompleteGameProfile, AuthenticationException> profileRepository;
    private final YggdrasilProvider provider;
    private static final ThreadPoolExecutor POOL = Lang.threadPool("YggdrasilProfileProperties", true, 2, 10, TimeUnit.SECONDS);
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UUID.class, UUIDTypeAdapter.INSTANCE).registerTypeAdapterFactory(ValidationTypeAdapterFactory.INSTANCE).create();

    public YggdrasilService(final YggdrasilProvider provider) {
        this.provider = provider;
        this.profileRepository = new ObservableOptionalCache<>(new ExceptionalFunction() { // from class: com.brixcore.auth.yggdrasil.YggdrasilService$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$new$0(provider, (UUID) obj);
            }
        }, new BiConsumer() { // from class: com.brixcore.auth.yggdrasil.YggdrasilService$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Throwable th = (Throwable) obj2;
                Logging.LOG.log(Level.WARNING, "Failed to fetch properties of " + ((UUID) obj) + " from " + provider, th);
            }
        }, POOL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Optional lambda$new$0(YggdrasilProvider provider, UUID uuid) throws AuthenticationException {
        Logging.LOG.info("Fetching properties of " + uuid + " from " + provider);
        return getCompleteGameProfile(uuid);
    }

    public ObservableOptionalCache<UUID, CompleteGameProfile, AuthenticationException> getProfileRepository() {
        return this.profileRepository;
    }

    public YggdrasilSession authenticate(String username, String password, String clientToken) throws AuthenticationException {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(clientToken);
        Map<String, Object> request = new HashMap<>();
        request.put("agent", Lang.mapOf(Pair.pair("name", "Minecraft"), Pair.pair("version", 1)));
        request.put("username", username);
        request.put("password", password);
        request.put("clientToken", clientToken);
        request.put("requestUser", true);
        return handleAuthenticationResponse(request(this.provider.getAuthenticationURL(), request), clientToken);
    }

    private static Map<String, Object> createRequestWithCredentials(String accessToken, String clientToken) {
        Map<String, Object> request = new HashMap<>();
        request.put("accessToken", accessToken);
        request.put("clientToken", clientToken);
        return request;
    }

    public YggdrasilSession refresh(String accessToken, String clientToken, GameProfile characterToSelect) throws AuthenticationException {
        Objects.requireNonNull(accessToken);
        Objects.requireNonNull(clientToken);
        Map<String, Object> request = createRequestWithCredentials(accessToken, clientToken);
        request.put("requestUser", true);
        if (characterToSelect != null) {
            request.put("selectedProfile", Lang.mapOf(Pair.pair("id", characterToSelect.getId()), Pair.pair("name", characterToSelect.getName())));
        }
        YggdrasilSession response = handleAuthenticationResponse(request(this.provider.getRefreshmentURL(), request), clientToken);
        if (characterToSelect != null && (response.getSelectedProfile() == null || !response.getSelectedProfile().getId().equals(characterToSelect.getId()))) {
            throw new ServerResponseMalformedException("Failed to select character");
        }
        return response;
    }

    public boolean validate(String accessToken) throws AuthenticationException {
        return validate(accessToken, null);
    }

    public boolean validate(String accessToken, String clientToken) throws AuthenticationException {
        Objects.requireNonNull(accessToken);
        try {
            requireEmpty(request(this.provider.getValidationURL(), createRequestWithCredentials(accessToken, clientToken)));
            return true;
        } catch (RemoteAuthenticationException e) {
            if ("ForbiddenOperationException".equals(e.getRemoteName())) {
                return false;
            }
            throw e;
        }
    }

    public void invalidate(String accessToken) throws AuthenticationException {
        invalidate(accessToken, null);
    }

    public void invalidate(String accessToken, String clientToken) throws AuthenticationException {
        Objects.requireNonNull(accessToken);
        requireEmpty(request(this.provider.getInvalidationURL(), createRequestWithCredentials(accessToken, clientToken)));
    }

    public void uploadSkin(UUID uuid, String accessToken, String model, Path file) throws UnsupportedOperationException, AuthenticationException {
        try {
            HttpURLConnection con = NetworkUtils.createHttpConnection(this.provider.getSkinUploadURL(uuid));
            con.setRequestMethod("PUT");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setDoOutput(true);
            HttpMultipartRequest request = new HttpMultipartRequest(con);
            try {
                request.param("model", model);
                InputStream fis = Files.newInputStream(file, new OpenOption[0]);
                try {
                    request.file("file", FileUtils.getName(file), "image/" + FileUtils.getExtension(file), fis);
                    if (fis != null) {
                        fis.close();
                    }
                    request.close();
                    requireEmpty(NetworkUtils.readData(con));
                } catch (Throwable th) {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                try {
                    request.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    public Optional<CompleteGameProfile> getCompleteGameProfile(UUID uuid) throws AuthenticationException {
        Objects.requireNonNull(uuid);
        return Optional.ofNullable((CompleteGameProfile) fromJson(request(this.provider.getProfilePropertiesURL(uuid), null), CompleteGameProfile.class));
    }

    public static Optional<Map<TextureType, Texture>> getTextures(CompleteGameProfile profile) throws ServerResponseMalformedException {
        Objects.requireNonNull(profile);
        String encodedTextures = profile.getProperties().get("textures");
        if (encodedTextures != null) {
            try {
                byte[] decodedBinary = Base64.getDecoder().decode(encodedTextures);
                TextureResponse texturePayload = (TextureResponse) fromJson(new String(decodedBinary, StandardCharsets.UTF_8), TextureResponse.class);
                return Optional.ofNullable(texturePayload.textures);
            } catch (IllegalArgumentException e) {
                throw new ServerResponseMalformedException(e);
            }
        }
        return Optional.empty();
    }

    private static YggdrasilSession handleAuthenticationResponse(String responseText, String clientToken) throws AuthenticationException {
        AuthenticationResponse response = (AuthenticationResponse) fromJson(responseText, AuthenticationResponse.class);
        handleErrorMessage(response);
        if (!clientToken.equals(response.clientToken)) {
            throw new AuthenticationException("Client token changed from " + clientToken + " to " + response.clientToken);
        }
        return new YggdrasilSession(response.clientToken, response.accessToken, response.selectedProfile, response.availableProfiles == null ? null : Collections.unmodifiableList(response.availableProfiles), response.user != null ? response.user.getProperties() : null);
    }

    private static void requireEmpty(String response) throws AuthenticationException {
        if (StringUtils.isBlank(response)) {
            return;
        }
        handleErrorMessage((ErrorResponse) fromJson(response, ErrorResponse.class));
    }

    private static void handleErrorMessage(ErrorResponse response) throws AuthenticationException {
        if (!StringUtils.isBlank(response.error)) {
            throw new RemoteAuthenticationException(response.error, response.errorMessage, response.cause);
        }
    }

    private static String request(URL url, Object payload) throws AuthenticationException {
        try {
            if (payload == null) {
                return NetworkUtils.doGet(url);
            }
            return NetworkUtils.doPost(url, payload instanceof String ? (String) payload : GSON.toJson(payload), "application/json");
        } catch (IOException e) {
            throw new ServerDisconnectException(e);
        }
    }

    private static <T> T fromJson(String str, Class<T> cls) throws ServerResponseMalformedException {
        try {
            return (T) GSON.fromJson(str, (Class) cls);
        } catch (JsonParseException e) {
            throw new ServerResponseMalformedException(str, e);
        }
    }

    private static class TextureResponse {
        public Map<TextureType, Texture> textures;

        private TextureResponse() {
        }
    }

    private static class AuthenticationResponse extends ErrorResponse {
        public String accessToken;
        public List<GameProfile> availableProfiles;
        public String clientToken;
        public GameProfile selectedProfile;
        public User user;

        private AuthenticationResponse() {
            super();
        }
    }

    private static class ErrorResponse {
        public String cause;
        public String error;
        public String errorMessage;

        private ErrorResponse() {
        }
    }
}

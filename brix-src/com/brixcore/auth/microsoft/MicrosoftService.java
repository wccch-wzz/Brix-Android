package com.brixcore.auth.microsoft;

import com.android.tools.r8.RecordTag;
import com.brixcore.auth.AuthenticationException;
import com.brixcore.auth.OAuth;
import com.brixcore.auth.ServerDisconnectException;
import com.brixcore.auth.ServerResponseMalformedException;
import com.brixcore.auth.yggdrasil.CompleteGameProfile;
import com.brixcore.auth.yggdrasil.RemoteAuthenticationException;
import com.brixcore.auth.yggdrasil.Texture;
import com.brixcore.auth.yggdrasil.TextureType;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.fakefx.ObservableOptionalCache;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.UUIDTypeAdapter;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.gson.ValidationTypeAdapterFactory;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.io.NetworkUtils;
import com.brixcore.util.io.ResponseCodeException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes5.dex */
public class MicrosoftService {
    private static final String SCOPE = "XboxLive.signin offline_access";
    private final OAuth.Callback callback;
    private final ObservableOptionalCache<UUID, CompleteGameProfile, AuthenticationException> profileRepository = new ObservableOptionalCache<>(new ExceptionalFunction() { // from class: com.brixcore.auth.microsoft.MicrosoftService$$ExternalSyntheticLambda1
        @Override // com.brixcore.util.function.ExceptionalFunction
        public final Object apply(Object obj) {
            return this.f$0.lambda$new$0((UUID) obj);
        }
    }, new BiConsumer() { // from class: com.brixcore.auth.microsoft.MicrosoftService$$ExternalSyntheticLambda2
        @Override // java.util.function.BiConsumer
        public final void accept(Object obj, Object obj2) {
            Logging.LOG.log(Level.WARNING, "Failed to fetch properties of " + ((UUID) obj), (Throwable) obj2);
        }
    }, POOL);
    private static final ThreadPoolExecutor POOL = Lang.threadPool("MicrosoftProfileProperties", true, 2, 10, TimeUnit.SECONDS);
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UUID.class, UUIDTypeAdapter.INSTANCE).registerTypeAdapterFactory(ValidationTypeAdapterFactory.INSTANCE).create();

    public static final class MinecraftJavaEditionLicenseNotFoundException extends AuthenticationException {
    }

    public static final class MinecraftJavaEditionProfileNotFoundException extends AuthenticationException {
    }

    public static class NoXuiException extends AuthenticationException {
    }

    public static class XBox400Exception extends AuthenticationException {
    }

    public MicrosoftService(OAuth.Callback callback) {
        this.callback = (OAuth.Callback) Objects.requireNonNull(callback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Optional lambda$new$0(UUID uuid) throws AuthenticationException {
        Logging.LOG.info("Fetching properties of " + uuid);
        return getCompleteGameProfile(uuid);
    }

    public ObservableOptionalCache<UUID, CompleteGameProfile, AuthenticationException> getProfileRepository() {
        return this.profileRepository;
    }

    public MicrosoftSession authenticate() throws AuthenticationException {
        try {
            OAuth.Result result = OAuth.MICROSOFT.authenticate(OAuth.GrantFlow.DEVICE, new OAuth.Options(SCOPE, this.callback));
            return authenticateViaLiveAccessToken(result.accessToken(), result.refreshToken());
        } catch (JsonParseException e) {
            throw new ServerResponseMalformedException(e);
        } catch (IOException e2) {
            throw new ServerDisconnectException(e2);
        }
    }

    public MicrosoftSession refresh(MicrosoftSession oldSession) throws AuthenticationException {
        try {
            OAuth.Result result = OAuth.MICROSOFT.refresh(oldSession.getRefreshToken(), new OAuth.Options(SCOPE, this.callback));
            return authenticateViaLiveAccessToken(result.accessToken(), result.refreshToken());
        } catch (JsonParseException e) {
            throw new ServerResponseMalformedException(e);
        } catch (IOException e2) {
            throw new ServerDisconnectException(e2);
        }
    }

    private String getUhs(XBoxLiveAuthenticationResponse response, String existingUhs) throws AuthenticationException {
        if (response.errorCode != 0) {
            throw new XboxAuthorizationException(response.errorCode, response.redirectUrl);
        }
        if (response.displayClaims == null || response.displayClaims.xui == null || response.displayClaims.xui.size() == 0 || !response.displayClaims.xui.get(0).containsKey("uhs")) {
            Logging.LOG.log(Level.WARNING, "Unrecognized xbox authorization response " + GSON.toJson(response));
            throw new NoXuiException();
        }
        String uhs = (String) response.displayClaims.xui.get(0).get("uhs");
        if (existingUhs != null && !Objects.equals(uhs, existingUhs)) {
            throw new ServerResponseMalformedException("uhs mismatched");
        }
        return uhs;
    }

    private MicrosoftSession authenticateViaLiveAccessToken(String liveAccessToken, String liveRefreshToken) throws JsonParseException, AuthenticationException, IOException {
        try {
            HttpRequest.HttpPostRequest httpPostRequestPOST = HttpRequest.POST("https://user.auth.xboxlive.com/user/authenticate");
            Pair[] pairArr = new Pair[3];
            Pair[] pairArr2 = new Pair[3];
            pairArr2[0] = Pair.pair("AuthMethod", "RPS");
            pairArr2[1] = Pair.pair("SiteName", "user.auth.xboxlive.com");
            try {
                pairArr2[2] = Pair.pair("RpsTicket", "d=" + liveAccessToken);
                pairArr[0] = Pair.pair("Properties", Lang.mapOf(pairArr2));
                pairArr[1] = Pair.pair("RelyingParty", "http://auth.xboxlive.com");
                pairArr[2] = Pair.pair("TokenType", "JWT");
                XBoxLiveAuthenticationResponse xboxResponse = (XBoxLiveAuthenticationResponse) httpPostRequestPOST.json(Lang.mapOf(pairArr)).retry(5).accept("application/json").getJson(XBoxLiveAuthenticationResponse.class);
                String uhs = getUhs(xboxResponse, null);
                XBoxLiveAuthenticationResponse minecraftXstsResponse = (XBoxLiveAuthenticationResponse) HttpRequest.POST("https://xsts.auth.xboxlive.com/xsts/authorize").json(Lang.mapOf(Pair.pair("Properties", Lang.mapOf(Pair.pair("SandboxId", "RETAIL"), Pair.pair("UserTokens", Collections.singletonList(xboxResponse.token)))), Pair.pair("RelyingParty", "rp://api.minecraftservices.com/"), Pair.pair("TokenType", "JWT"))).ignoreHttpErrorCode(401).retry(5).getJson(XBoxLiveAuthenticationResponse.class);
                getUhs(minecraftXstsResponse, uhs);
                MinecraftLoginWithXBoxResponse minecraftResponse = (MinecraftLoginWithXBoxResponse) HttpRequest.POST("https://api.minecraftservices.com/authentication/login_with_xbox").json(Lang.mapOf(Pair.pair("identityToken", "XBL3.0 x=" + uhs + ";" + minecraftXstsResponse.token))).retry(5).accept("application/json").getJson(MinecraftLoginWithXBoxResponse.class);
                long notAfter = (((long) minecraftResponse.expiresIn) * 1000) + System.currentTimeMillis();
                HttpURLConnection request = HttpRequest.GET("https://api.minecraftservices.com/entitlements/mcstore").authorization("Bearer " + minecraftResponse.accessToken).retry(5).accept("application/json").createConnection();
                if (request.getResponseCode() != 200) {
                    throw new ResponseCodeException(new URL("https://api.minecraftservices.com/entitlements/mcstore"), request.getResponseCode());
                }
                MinecraftProfileResponse profileResponse = getMinecraftProfile(minecraftResponse.tokenType, minecraftResponse.accessToken);
                handleErrorResponse(profileResponse);
                return new MicrosoftSession(minecraftResponse.tokenType, minecraftResponse.accessToken, notAfter, liveRefreshToken, new MicrosoftSession.User(minecraftResponse.username), new MicrosoftSession.GameProfile(profileResponse.id, profileResponse.name));
            } catch (ResponseCodeException e) {
                e = e;
                if (e.getResponseCode() == 400) {
                    throw new XBox400Exception();
                }
                throw e;
            }
        } catch (ResponseCodeException e2) {
            e = e2;
        }
    }

    public Optional<MinecraftProfileResponse> getCompleteProfile(String authorization) throws AuthenticationException {
        try {
            return Optional.ofNullable((MinecraftProfileResponse) HttpRequest.GET("https://api.minecraftservices.com/minecraft/profile").authorization(authorization).getJson(MinecraftProfileResponse.class));
        } catch (JsonParseException e) {
            throw new ServerResponseMalformedException(e);
        } catch (IOException e2) {
            throw new ServerDisconnectException(e2);
        }
    }

    public boolean validate(long notAfter, String tokenType, String accessToken) throws AuthenticationException {
        Objects.requireNonNull(tokenType);
        Objects.requireNonNull(accessToken);
        if (System.currentTimeMillis() > notAfter) {
            return false;
        }
        try {
            getMinecraftProfile(tokenType, accessToken);
            return true;
        } catch (ResponseCodeException e) {
            return false;
        } catch (IOException e2) {
            throw new ServerDisconnectException(e2);
        }
    }

    private static void handleErrorResponse(MinecraftErrorResponse response) throws AuthenticationException {
        if (response.error != null) {
            throw new RemoteAuthenticationException(response.error, response.errorMessage, response.developerMessage);
        }
    }

    public static Optional<Map<TextureType, Texture>> getTextures(MinecraftProfileResponse profile) {
        Objects.requireNonNull(profile);
        Map<TextureType, Texture> textures = new EnumMap<>(TextureType.class);
        if (!profile.skins.isEmpty()) {
            textures.put(TextureType.SKIN, new Texture(profile.skins.get(0).url, null));
        }
        if (!profile.capes.isEmpty()) {
            textures.put(TextureType.CAPE, new Texture(profile.capes.get(0).url, null));
        }
        return Optional.of(textures);
    }

    private static void getXBoxProfile(String uhs, String xstsToken) throws IOException {
        HttpRequest.GET("https://profile.xboxlive.com/users/me/profile/settings", Pair.pair("settings", "GameDisplayName,AppDisplayName,AppDisplayPicRaw,GameDisplayPicRaw,PublicGamerpic,ShowUserAsAvatar,Gamerscore,Gamertag,ModernGamertag,ModernGamertagSuffix,UniqueModernGamertag,AccountTier,TenureLevel,XboxOneRep,PreferredColor,Location,Bio,Watermarks,RealName,RealNameOverride,IsQuarantined")).accept("application/json").authorization(String.format("XBL3.0 x=%s;%s", uhs, xstsToken)).header("x-xbl-contract-version", "3").getString();
    }

    private static MinecraftProfileResponse getMinecraftProfile(String tokenType, String accessToken) throws IOException, AuthenticationException {
        HttpURLConnection conn = HttpRequest.GET("https://api.minecraftservices.com/minecraft/profile").authorization(tokenType, accessToken).createConnection();
        int responseCode = conn.getResponseCode();
        if (responseCode == 404) {
            MinecraftLicense license = (MinecraftLicense) HttpRequest.GET("https://api.minecraftservices.com/entitlements/license").authorization(tokenType, accessToken).getJson(MinecraftLicense.class);
            boolean hasMinecraftLicense = (license == null || license.items() == null || !license.items().stream().anyMatch(new Predicate() { // from class: com.brixcore.auth.microsoft.MicrosoftService$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return "game_minecraft".equals(((MicrosoftService.MinecraftLicenseItem) obj).name());
                }
            })) ? false : true;
            if (!hasMinecraftLicense) {
                throw new MinecraftJavaEditionLicenseNotFoundException();
            }
            throw new MinecraftJavaEditionProfileNotFoundException();
        }
        if (responseCode != 200) {
            throw new ResponseCodeException(new URL("https://api.minecraftservices.com/minecraft/profile"), responseCode);
        }
        String result = NetworkUtils.readData(conn);
        return (MinecraftProfileResponse) JsonUtils.fromNonNullJson(result, MinecraftProfileResponse.class);
    }

    public Optional<CompleteGameProfile> getCompleteGameProfile(UUID uuid) throws AuthenticationException {
        Objects.requireNonNull(uuid);
        return Optional.ofNullable((CompleteGameProfile) GSON.fromJson(request(NetworkUtils.toURL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(uuid)), null), CompleteGameProfile.class));
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

    public static class XboxAuthorizationException extends AuthenticationException {
        public static final long ADD_FAMILY = 2148916238L;
        public static final long BANNED = 2148916227L;
        public static final long COUNTRY_UNAVAILABLE = 2148916235L;
        public static final long MISSING_XBOX_ACCOUNT = 2148916233L;
        private final long errorCode;
        private final String redirect;

        public XboxAuthorizationException(long errorCode, String redirect) {
            this.errorCode = errorCode;
            this.redirect = redirect;
        }

        public long getErrorCode() {
            return this.errorCode;
        }

        public String getRedirect() {
            return this.redirect;
        }
    }

    private static class XBoxLiveAuthenticationResponseDisplayClaims {
        List<Map<Object, Object>> xui;

        private XBoxLiveAuthenticationResponseDisplayClaims() {
        }
    }

    private static class MicrosoftErrorResponse {

        @SerializedName("XErr")
        long errorCode;

        @SerializedName("Message")
        String message;

        @SerializedName("Redirect")
        String redirectUrl;

        private MicrosoftErrorResponse() {
        }
    }

    private static class XBoxLiveAuthenticationResponse extends MicrosoftErrorResponse {

        @SerializedName("DisplayClaims")
        XBoxLiveAuthenticationResponseDisplayClaims displayClaims;

        @SerializedName("IssueInstant")
        String issueInstant;

        @SerializedName("NotAfter")
        String notAfter;

        @SerializedName("Token")
        String token;

        private XBoxLiveAuthenticationResponse() {
            super();
        }
    }

    private static class MinecraftLoginWithXBoxResponse {

        @SerializedName("access_token")
        String accessToken;

        @SerializedName("expires_in")
        int expiresIn;

        @SerializedName("roles")
        List<String> roles;

        @SerializedName("token_type")
        String tokenType;

        @SerializedName("username")
        String username;

        private MinecraftLoginWithXBoxResponse() {
        }
    }

    private static class MinecraftStoreResponseItem {

        @SerializedName("name")
        String name;

        @SerializedName("signature")
        String signature;

        private MinecraftStoreResponseItem() {
        }
    }

    private static class MinecraftStoreResponse extends MinecraftErrorResponse {

        @SerializedName("items")
        List<MinecraftStoreResponseItem> items;

        @SerializedName("keyId")
        String keyId;

        @SerializedName("signature")
        String signature;

        private MinecraftStoreResponse() {
            super();
        }
    }

    public static class MinecraftProfileResponseSkin implements Validation {
        public String alias;
        public String id;
        public String state;
        public String url;
        public String variant;

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(this.id, "id cannot be null");
            Validation.requireNonNull(this.state, "state cannot be null");
            Validation.requireNonNull(this.url, "url cannot be null");
            Validation.requireNonNull(this.variant, "variant cannot be null");
        }
    }

    public static class MinecraftProfileResponseCape implements Validation {
        public String alias;
        public String id;
        public String state;
        public String url;

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(this.id, "cape id cannot be null");
            Validation.requireNonNull(this.state, "cape state cannot be null");
            Validation.requireNonNull(this.url, "cape url cannot be null");
        }
    }

    public static final class MinecraftLicense extends RecordTag {

        @SerializedName("items")
        private final List<MinecraftLicenseItem> items;

        @SerializedName("keyId")
        private final String keyId;

        @SerializedName("signature")
        private final String signature;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof MinecraftLicense)) {
                return false;
            }
            MinecraftLicense minecraftLicense = (MinecraftLicense) obj;
            return Objects.equals(this.items, minecraftLicense.items) && Objects.equals(this.signature, minecraftLicense.signature) && Objects.equals(this.keyId, minecraftLicense.keyId);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.items, this.signature, this.keyId};
        }

        public MinecraftLicense(List<MinecraftLicenseItem> items, String signature, String keyId) {
            this.items = items;
            this.signature = signature;
            this.keyId = keyId;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return MicrosoftService$MinecraftLicense$$ExternalSyntheticRecord1.m(this.items, this.signature, this.keyId);
        }

        @SerializedName("items")
        public List<MinecraftLicenseItem> items() {
            return this.items;
        }

        @SerializedName("keyId")
        public String keyId() {
            return this.keyId;
        }

        @SerializedName("signature")
        public String signature() {
            return this.signature;
        }

        public final String toString() {
            return MicrosoftService$MinecraftLicense$$ExternalSyntheticRecord0.m($record$getFieldsAsObjects(), MinecraftLicense.class, "items;signature;keyId");
        }
    }

    public static final class MinecraftLicenseItem extends RecordTag {

        @SerializedName("name")
        private final String name;

        @SerializedName("signature")
        private final String signature;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof MinecraftLicenseItem)) {
                return false;
            }
            MinecraftLicenseItem minecraftLicenseItem = (MinecraftLicenseItem) obj;
            return Objects.equals(this.name, minecraftLicenseItem.name) && Objects.equals(this.signature, minecraftLicenseItem.signature);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.name, this.signature};
        }

        public MinecraftLicenseItem(String name, String signature) {
            this.name = name;
            this.signature = signature;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return MicrosoftService$MinecraftLicenseItem$$ExternalSyntheticRecord0.m(this.name, this.signature);
        }

        @SerializedName("name")
        public String name() {
            return this.name;
        }

        @SerializedName("signature")
        public String signature() {
            return this.signature;
        }

        public final String toString() {
            return MicrosoftService$MinecraftLicense$$ExternalSyntheticRecord0.m($record$getFieldsAsObjects(), MinecraftLicenseItem.class, "name;signature");
        }
    }

    public static class MinecraftProfileResponse extends MinecraftErrorResponse implements Validation {

        @SerializedName("capes")
        List<MinecraftProfileResponseCape> capes;

        @SerializedName("id")
        UUID id;

        @SerializedName("name")
        String name;

        @SerializedName("skins")
        List<MinecraftProfileResponseSkin> skins;

        public MinecraftProfileResponse() {
            super();
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(this.id, "id cannot be null");
            Validation.requireNonNull(this.name, "name cannot be null");
            Validation.requireNonNull(this.skins, "skins cannot be null");
            Validation.requireNonNull(this.capes, "capes cannot be null");
        }

        public List<MinecraftProfileResponseCape> getCapes() {
            return this.capes;
        }
    }

    private static class MinecraftErrorResponse {
        public String developerMessage;
        public String error;
        public String errorMessage;
        public String errorType;
        public String path;

        private MinecraftErrorResponse() {
        }
    }
}

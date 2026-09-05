package com.brixcore.auth;

import com.android.tools.r8.RecordTag;
import com.brixcore.auth.yggdrasil.RemoteAuthenticationException;
import com.brixcore.util.Lang;
import com.brixcore.util.Pair;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes8.dex */
public class OAuth {
    private final String accessTokenURL;
    private final String authorizationURL;
    private final String deviceCodeURL;
    private final String tokenURL;
    public static final OAuth MICROSOFT = new OAuth("https://login.live.com/oauth20_authorize.srf", "https://login.live.com/oauth20_token.srf", "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode", "https://login.microsoftonline.com/consumers/oauth2/v2.0/token");
    public static boolean IS_CANCELED = false;

    public interface Callback {
        String getClientId();

        String getClientSecret();

        void grantDeviceCode(String str, String str2);

        boolean isPublicClient();

        void openBrowser(String str) throws IOException;

        Session startServer() throws IOException, AuthenticationException;
    }

    public enum GrantFlow {
        AUTHORIZATION_CODE,
        DEVICE
    }

    public OAuth(String authorizationURL, String accessTokenURL, String deviceCodeURL, String tokenURL) {
        this.authorizationURL = authorizationURL;
        this.accessTokenURL = accessTokenURL;
        this.deviceCodeURL = deviceCodeURL;
        this.tokenURL = tokenURL;
    }

    public Result authenticate(GrantFlow grantFlow, Options options) throws AuthenticationException {
        try {
            switch (grantFlow) {
                case AUTHORIZATION_CODE:
                    return authenticateAuthorizationCode(options);
                case DEVICE:
                    return authenticateDevice(options);
                default:
                    throw new UnsupportedOperationException("grant flow " + grantFlow);
            }
        } catch (JsonParseException e) {
            throw new ServerResponseMalformedException(e);
        } catch (IOException e2) {
            throw new ServerDisconnectException(e2);
        } catch (InterruptedException e3) {
            throw new NoSelectedCharacterException();
        } catch (ExecutionException e4) {
            if (e4.getCause() instanceof InterruptedException) {
                throw new NoSelectedCharacterException();
            }
            throw new ServerDisconnectException(e4);
        }
    }

    private Result authenticateAuthorizationCode(Options options) throws ExecutionException, JsonParseException, InterruptedException, IOException, AuthenticationException {
        Session session = options.callback.startServer();
        options.callback.openBrowser(NetworkUtils.withQuery(this.authorizationURL, Lang.mapOf(Pair.pair("client_id", options.callback.getClientId()), Pair.pair("response_type", "code"), Pair.pair("redirect_uri", session.getRedirectURI()), Pair.pair("scope", options.scope), Pair.pair("prompt", "select_account"))));
        String code = session.waitFor();
        AuthorizationResponse response = (AuthorizationResponse) HttpRequest.POST(this.accessTokenURL).form(Pair.pair("client_id", options.callback.getClientId()), Pair.pair("code", code), Pair.pair("grant_type", "authorization_code"), Pair.pair("client_secret", options.callback.getClientSecret()), Pair.pair("redirect_uri", session.getRedirectURI()), Pair.pair("scope", options.scope)).ignoreHttpCode().retry(5).getJson(AuthorizationResponse.class);
        handleErrorResponse(response);
        return new Result(response.accessToken, response.refreshToken);
    }

    private Result authenticateDevice(Options options) throws JsonParseException, InterruptedException, AuthenticationException, IOException {
        char c = 2;
        char c2 = 0;
        char c3 = 1;
        DeviceTokenResponse deviceTokenResponse = (DeviceTokenResponse) HttpRequest.POST(this.deviceCodeURL).form(Pair.pair("client_id", options.callback.getClientId()), Pair.pair("scope", options.scope)).ignoreHttpCode().retry(5).getJson(DeviceTokenResponse.class);
        handleErrorResponse(deviceTokenResponse);
        options.callback.grantDeviceCode(deviceTokenResponse.userCode, deviceTokenResponse.verificationURI);
        options.callback.openBrowser(deviceTokenResponse.verificationURI);
        long startTime = System.nanoTime();
        long interval = TimeUnit.MILLISECONDS.convert(deviceTokenResponse.interval, TimeUnit.SECONDS);
        IS_CANCELED = false;
        while (!IS_CANCELED) {
            Thread.sleep(Math.max(interval, 1L));
            long estimatedTime = System.nanoTime() - startTime;
            c = c;
            c2 = c2;
            c3 = c3;
            if (TimeUnit.SECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS) >= Math.min(deviceTokenResponse.expiresIn, 900)) {
                throw new NoSelectedCharacterException();
            }
            try {
                HttpRequest.HttpPostRequest httpPostRequestPOST = HttpRequest.POST(this.tokenURL);
                Pair<String, String>[] pairArr = new Pair[3];
                pairArr[c2] = Pair.pair("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
                pairArr[c3] = Pair.pair("code", deviceTokenResponse.deviceCode);
                pairArr[c] = Pair.pair("client_id", options.callback.getClientId());
                TokenResponse tokenResponse = (TokenResponse) httpPostRequestPOST.form(pairArr).ignoreHttpCode().retry(5).getJson(TokenResponse.class);
                if (!"authorization_pending".equals(tokenResponse.error)) {
                    if ("expired_token".equals(tokenResponse.error)) {
                        throw new NoSelectedCharacterException();
                    }
                    if ("slow_down".equals(tokenResponse.error)) {
                        interval += 5000;
                    } else {
                        return new Result(tokenResponse.accessToken, tokenResponse.refreshToken);
                    }
                }
            } catch (UnknownHostException e) {
            }
        }
        throw new CancellationException();
    }

    public Result refresh(String refreshToken, Options options) throws AuthenticationException {
        try {
            Map<String, String> query = Lang.mapOf(Pair.pair("client_id", options.callback.getClientId()), Pair.pair("refresh_token", refreshToken), Pair.pair("grant_type", "refresh_token"));
            if (!options.callback.isPublicClient()) {
                query.put("client_secret", options.callback.getClientSecret());
            }
            RefreshResponse response = (RefreshResponse) HttpRequest.POST(this.tokenURL).form(query).accept("application/json").ignoreHttpCode().retry(5).getJson(RefreshResponse.class);
            handleErrorResponse(response);
            return new Result(response.accessToken, response.refreshToken);
        } catch (JsonParseException e) {
            throw new ServerResponseMalformedException(e);
        } catch (IOException e2) {
            throw new ServerDisconnectException(e2);
        }
    }

    private static void handleErrorResponse(ErrorResponse response) throws AuthenticationException {
        if (response.error == null || response.errorDescription == null) {
            return;
        }
        switch (response.error) {
            case "invalid_grant":
                if (response.errorDescription.contains("AADSTS70000")) {
                    throw new CredentialExpiredException();
                }
                break;
        }
        throw new RemoteAuthenticationException(response.error, response.errorDescription, "");
    }

    public static class Options {
        private final Callback callback;
        private final String scope;
        private String userAgent;

        public Options(String scope, Callback callback) {
            this.scope = scope;
            this.callback = callback;
        }

        public Options setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
    }

    public interface Session {
        String getRedirectURI();

        String waitFor() throws ExecutionException, InterruptedException;

        default String getIdToken() {
            return null;
        }
    }

    public static final class Result extends RecordTag {
        private final String accessToken;
        private final String refreshToken;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Result)) {
                return false;
            }
            Result result = (Result) obj;
            return Objects.equals(this.accessToken, result.accessToken) && Objects.equals(this.refreshToken, result.refreshToken);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.accessToken, this.refreshToken};
        }

        public Result(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String accessToken() {
            return this.accessToken;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return OAuth$Result$$ExternalSyntheticRecord0.m(this.accessToken, this.refreshToken);
        }

        public String refreshToken() {
            return this.refreshToken;
        }

        public final String toString() {
            return OAuth$Result$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Result.class, "accessToken;refreshToken");
        }
    }

    private static class DeviceTokenResponse extends ErrorResponse {

        @SerializedName("device_code")
        public String deviceCode;

        @SerializedName("expires_in")
        public int expiresIn;

        @SerializedName("interval")
        public int interval;

        @SerializedName("user_code")
        public String userCode;

        @SerializedName("verification_uri")
        public String verificationURI;

        private DeviceTokenResponse() {
            super();
        }
    }

    private static class TokenResponse extends ErrorResponse {

        @SerializedName("access_token")
        public String accessToken;

        @SerializedName("expires_in")
        public int expiresIn;

        @SerializedName("ext_expires_in")
        public int extExpiresIn;

        @SerializedName("refresh_token")
        public String refreshToken;

        @SerializedName("scope")
        public String scope;

        @SerializedName("token_type")
        public String tokenType;

        private TokenResponse() {
            super();
        }
    }

    private static class ErrorResponse {

        @SerializedName("correlation_id")
        public String correlationId;

        @SerializedName("error")
        public String error;

        @SerializedName("error_description")
        public String errorDescription;

        private ErrorResponse() {
        }
    }

    public static class AuthorizationResponse extends ErrorResponse {

        @SerializedName("access_token")
        public String accessToken;

        @SerializedName("expires_in")
        public int expiresIn;

        @SerializedName("foci")
        public String foci;

        @SerializedName("refresh_token")
        public String refreshToken;

        @SerializedName("scope")
        public String scope;

        @SerializedName("token_type")
        public String tokenType;

        @SerializedName("user_id")
        public String userId;

        public AuthorizationResponse() {
            super();
        }
    }

    private static class RefreshResponse extends ErrorResponse {

        @SerializedName("access_token")
        String accessToken;

        @SerializedName("expires_in")
        int expiresIn;

        @SerializedName("refresh_token")
        String refreshToken;

        private RefreshResponse() {
            super();
        }
    }
}

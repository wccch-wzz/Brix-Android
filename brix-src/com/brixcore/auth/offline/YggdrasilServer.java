package com.brixcore.auth.offline;

import com.brixcore.auth.yggdrasil.GameProfile;
import com.brixcore.auth.yggdrasil.TextureModel;
import com.brixcore.util.KeyUtils;
import com.brixcore.util.Lang;
import com.brixcore.util.Pair;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.gson.UUIDTypeAdapter;
import com.brixcore.util.io.HttpServer;
import com.brixcore.util.png.fakefx.PNGFakeFXUtils;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes14.dex */
public class YggdrasilServer extends HttpServer {
    private static final KeyPair keyPair = KeyUtils.generateKey();
    private final Map<String, Character> charactersByName;
    private final Map<UUID, Character> charactersByUuid;

    public YggdrasilServer(int port) {
        super(port);
        this.charactersByUuid = new HashMap();
        this.charactersByName = new HashMap();
        addRoute(NanoHTTPD.Method.GET, Pattern.compile("^/$"), new ExceptionalFunction() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda4
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.root((HttpServer.Request) obj);
            }
        });
        addRoute(NanoHTTPD.Method.GET, Pattern.compile("/status"), new ExceptionalFunction() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda5
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.status((HttpServer.Request) obj);
            }
        });
        addRoute(NanoHTTPD.Method.POST, Pattern.compile("/api/profiles/minecraft"), new ExceptionalFunction() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda6
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.profiles((HttpServer.Request) obj);
            }
        });
        addRoute(NanoHTTPD.Method.GET, Pattern.compile("/sessionserver/session/minecraft/hasJoined"), new ExceptionalFunction() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda7
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.hasJoined((HttpServer.Request) obj);
            }
        });
        addRoute(NanoHTTPD.Method.POST, Pattern.compile("/sessionserver/session/minecraft/join"), new ExceptionalFunction() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda8
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.joinServer((HttpServer.Request) obj);
            }
        });
        addRoute(NanoHTTPD.Method.GET, Pattern.compile("/sessionserver/session/minecraft/profile/(?<uuid>[a-f0-9]{32})"), new ExceptionalFunction() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda9
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.profile((HttpServer.Request) obj);
            }
        });
        addRoute(NanoHTTPD.Method.GET, Pattern.compile("/textures/(?<hash>[a-f0-9]{64})"), new ExceptionalFunction() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda10
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.texture((HttpServer.Request) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NanoHTTPD.Response root(HttpServer.Request request) {
        return ok(Lang.mapOf(Pair.pair("signaturePublickey", KeyUtils.toPEMPublicKey(getSignaturePublicKey())), Pair.pair("skinDomains", Arrays.asList("127.0.0.1", "localhost")), Pair.pair("meta", Lang.mapOf(Pair.pair("serverName", "Brix"), Pair.pair("implementationName", "Brix"), Pair.pair("implementationVersion", "1.0"), Pair.pair("feature.non_email_login", true)))));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NanoHTTPD.Response status(HttpServer.Request request) {
        return ok(Lang.mapOf(Pair.pair("user.count", Integer.valueOf(this.charactersByUuid.size())), Pair.pair("token.count", 0), Pair.pair("pendingAuthentication.count", 0)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NanoHTTPD.Response profiles(HttpServer.Request request) throws IOException {
        List<String> names = (List) JsonUtils.fromNonNullJsonFully(request.getSession().getInputStream(), new TypeToken<List<String>>() { // from class: com.brixcore.auth.offline.YggdrasilServer.1
        }.getType());
        return ok(names.stream().distinct().map(new Function() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.findCharacterByName((String) obj);
            }
        }).flatMap(new YggdrasilServer$$ExternalSyntheticLambda2()).map(new Function() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((YggdrasilServer.Character) obj).toSimpleResponse();
            }
        }).collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NanoHTTPD.Response hasJoined(HttpServer.Request request) {
        if (!request.getQuery().containsKey("username")) {
            return badRequest();
        }
        Optional<Character> character = findCharacterByName(request.getQuery().get("username"));
        if (character.isPresent()) {
            return ok(character.get().toCompleteResponse(getRootUrl()));
        }
        return HttpServer.noContent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NanoHTTPD.Response joinServer(HttpServer.Request request) {
        return noContent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NanoHTTPD.Response profile(HttpServer.Request request) {
        String uuid = request.getPathVariables().group("uuid");
        Optional<Character> character = findCharacterByUuid(UUIDTypeAdapter.fromString(uuid));
        if (character.isPresent()) {
            return ok(character.get().toCompleteResponse(getRootUrl()));
        }
        return HttpServer.noContent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NanoHTTPD.Response texture(HttpServer.Request request) {
        String hash = request.getPathVariables().group("hash");
        if (Texture.hasTexture(hash)) {
            Texture texture = Texture.getTexture(hash);
            byte[] data = PNGFakeFXUtils.writeImageToArray(texture.getImage());
            NanoHTTPD.Response response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "image/png", new ByteArrayInputStream(data), data.length);
            response.addHeader("Etag", String.format("\"%s\"", hash));
            response.addHeader("Cache-Control", "max-age=2592000, public");
            return response;
        }
        return notFound();
    }

    private Optional<Character> findCharacterByUuid(UUID uuid) {
        return Optional.ofNullable(this.charactersByUuid.get(uuid));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Optional<Character> findCharacterByName(String uuid) {
        return Optional.ofNullable(this.charactersByName.get(uuid));
    }

    public void addCharacter(Character character) {
        this.charactersByUuid.put(character.getUUID(), character);
        this.charactersByName.put(character.getName(), character);
    }

    public static class Character {
        private final String name;
        private final Skin.LoadedSkin skin;
        private final UUID uuid;

        public Character(UUID uuid, String name, Skin.LoadedSkin skin) {
            this.uuid = uuid;
            this.name = name;
            this.skin = skin;
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }

        public GameProfile toSimpleResponse() {
            return new GameProfile(this.uuid, this.name);
        }

        public Object toCompleteResponse(String rootUrl) {
            Map<String, Object> realTextures = new HashMap<>();
            if (this.skin != null && this.skin.skin() != null) {
                if (this.skin.model() == TextureModel.ALEX) {
                    realTextures.put("SKIN", Lang.mapOf(Pair.pair("url", rootUrl + "/textures/" + this.skin.skin().getHash()), Pair.pair("metadata", Lang.mapOf(Pair.pair("model", "slim")))));
                } else {
                    realTextures.put("SKIN", Lang.mapOf(Pair.pair("url", rootUrl + "/textures/" + this.skin.skin().getHash())));
                }
            }
            if (this.skin != null && this.skin.cape() != null) {
                realTextures.put("CAPE", Lang.mapOf(Pair.pair("url", rootUrl + "/textures/" + this.skin.cape().getHash())));
            }
            Map<String, Object> textureResponse = Lang.mapOf(Pair.pair("timestamp", Long.valueOf(System.currentTimeMillis())), Pair.pair("profileId", this.uuid), Pair.pair("profileName", this.name), Pair.pair("textures", realTextures));
            return Lang.mapOf(Pair.pair("id", this.uuid), Pair.pair("name", this.name), Pair.pair("properties", YggdrasilServer.properties(true, Pair.pair("textures", new String(Base64.getEncoder().encode(JsonUtils.GSON.toJson(textureResponse).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)))));
        }
    }

    public static PublicKey getSignaturePublicKey() {
        return keyPair.getPublic();
    }

    private static String sign(String data) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(keyPair.getPrivate(), new SecureRandom());
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    public static List<?> properties(final boolean sign, Pair<String, String>... entries) {
        return (List) Stream.of((Object[]) entries).map(new Function() { // from class: com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return YggdrasilServer.lambda$properties$0(sign, (Pair) obj);
            }
        }).collect(Collectors.toList());
    }

    static /* synthetic */ LinkedHashMap lambda$properties$0(boolean sign, Pair entry) {
        LinkedHashMap<String, String> property = new LinkedHashMap<>();
        property.put("name", (String) entry.getKey());
        property.put("value", (String) entry.getValue());
        if (sign) {
            property.put("signature", sign((String) entry.getValue()));
        }
        return property;
    }
}

package com.brixcore.auth;

import com.brixcore.auth.yggdrasil.GameProfile;
import com.brixcore.auth.yggdrasil.YggdrasilService;
import java.util.List;

/* JADX INFO: loaded from: classes8.dex */
public interface CharacterSelector {
    GameProfile select(YggdrasilService yggdrasilService, List<GameProfile> list) throws NoSelectedCharacterException;
}

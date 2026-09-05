package com.brix.brixlauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes18.dex */
public class FriendManager {
    private static final String PREFS_NAME = "brix_friends";
    private static final String TAG = "FriendManager";
    private FriendApi api;
    private final Context context;
    private List<Friend> friends = Collections.emptyList();
    private List<FriendRequest> pendingRequests = Collections.emptyList();
    private final SharedPreferencesCompat prefs;

    public interface OnErrorListener {
        void onClick();
    }

    public FriendManager(Context context, String token) {
        this.context = context.getApplicationContext();
        this.prefs = new SharedPreferencesCompat(context.getSharedPreferences(PREFS_NAME, 0));
        setToken(token);
        loadCachedData();
    }

    public void setToken(String token) {
        if (token != null && !token.isEmpty()) {
            this.api = new FriendApi(token);
        }
    }

    private void loadCachedData() {
        String cachedFriends = this.prefs.get("friends_json", "");
        if (!cachedFriends.isEmpty()) {
            try {
                this.friends = parseFriends(new JSONArray(cachedFriends));
            } catch (Exception e) {
                Log.w(TAG, "解析缓存好友失败: " + e.getMessage());
                this.friends = new ArrayList();
            }
        }
        String cachedPending = this.prefs.get("pending_json", "");
        if (!cachedPending.isEmpty()) {
            try {
                this.pendingRequests = parseRequests(new JSONArray(cachedPending));
            } catch (Exception e2) {
                Log.w(TAG, "解析缓存请求失败: " + e2.getMessage());
                this.pendingRequests = new ArrayList();
            }
        }
    }

    private void cacheFriends() {
        try {
            this.prefs.put("friends_json", friendsToJson());
        } catch (Exception e) {
            Log.w(TAG, "缓存好友失败: " + e.getMessage());
        }
    }

    private void cachePending() {
        try {
            this.prefs.put("pending_json", pendingToJson());
        } catch (Exception e) {
            Log.w(TAG, "缓存请求失败: " + e.getMessage());
        }
    }

    public List<Friend> getFriends() {
        return this.friends;
    }

    public void refreshFriends(final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$refreshFriends$0(onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshFriends$0(Runnable onSuccess, OnErrorListener onError) {
        try {
            JSONArray arr = this.api.getFriendList();
            this.friends = parseFriends(arr);
            cacheFriends();
            if (onSuccess != null) {
                onSuccess.run();
            }
            Log.d(TAG, "好友列表刷新成功，共 " + this.friends.size() + " 个好友");
        } catch (Exception e) {
            Log.e(TAG, "获取好友列表失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public List<FriendRequest> getPendingRequests() {
        return this.pendingRequests;
    }

    public void refreshPendingRequests(final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$refreshPendingRequests$1(onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshPendingRequests$1(Runnable onSuccess, OnErrorListener onError) {
        try {
            JSONArray arr = this.api.getPendingRequests();
            this.pendingRequests = parseRequests(arr);
            cachePending();
            if (onSuccess != null) {
                onSuccess.run();
            }
            Log.d(TAG, "待处理请求刷新成功，共 " + this.pendingRequests.size() + " 条");
        } catch (Exception e) {
            Log.e(TAG, "获取待处理请求失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public void sendFriendRequest(final String targetId, final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$sendFriendRequest$2(targetId, onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendFriendRequest$2(String targetId, Runnable onSuccess, OnErrorListener onError) {
        try {
            this.api.addFriend(targetId);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            Log.e(TAG, "发送好友申请失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public void acceptRequest(final String requesterId, final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$acceptRequest$3(requesterId, onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptRequest$3(String requesterId, Runnable onSuccess, OnErrorListener onError) {
        try {
            this.api.acceptFriend(requesterId);
            refreshFriends(null, null);
            refreshPendingRequests(null, null);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            Log.e(TAG, "接受好友申请失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public void rejectRequest(final String requesterId, final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$rejectRequest$4(requesterId, onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$rejectRequest$4(String requesterId, Runnable onSuccess, OnErrorListener onError) {
        try {
            this.api.rejectFriend(requesterId);
            refreshPendingRequests(null, null);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            Log.e(TAG, "拒绝好友申请失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public void deleteFriend(final String friendId, final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$deleteFriend$5(friendId, onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteFriend$5(String friendId, Runnable onSuccess, OnErrorListener onError) {
        try {
            this.api.deleteFriend(friendId);
            refreshFriends(null, null);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            Log.e(TAG, "删除好友失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public void blockUser(final String userId, final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$blockUser$6(userId, onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$blockUser$6(String userId, Runnable onSuccess, OnErrorListener onError) {
        try {
            this.api.blockUser(userId);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            Log.e(TAG, "拉黑用户失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public void unblockUser(final String userId, final Runnable onSuccess, final OnErrorListener onError) {
        if (this.api == null) {
            onError.onClick();
        } else {
            new Thread(new Runnable() { // from class: com.brix.brixlauncher.FriendManager$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$unblockUser$7(userId, onSuccess, onError);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$unblockUser$7(String userId, Runnable onSuccess, OnErrorListener onError) {
        try {
            this.api.unblockUser(userId);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            Log.e(TAG, "解除拉黑失败: " + e.getMessage());
            if (onError != null) {
                onError.onClick();
            }
        }
    }

    public static class Friend {
        public final String email;
        public final String friendSince;
        public final int id;
        public final String phone;
        public final String qq;
        public final String username;

        public Friend(int id, String username, String qq, String phone, String email, String friendSince) {
            this.id = id;
            this.username = username;
            this.qq = qq;
            this.phone = phone;
            this.email = email;
            this.friendSince = friendSince;
        }

        public static Friend fromJson(JSONObject obj) throws JSONException {
            return new Friend(obj.optInt("id"), obj.optString("username", "未知用户"), obj.optString("qq", ""), obj.optString("phone", ""), obj.optString(NotificationCompat.CATEGORY_EMAIL, ""), obj.optString("friend_since", ""));
        }
    }

    public static class FriendRequest {
        public final String createdAt;
        public final int id;
        public final String qq;
        public final int userId;
        public final String username;

        public FriendRequest(int id, int userId, String username, String qq, String createdAt) {
            this.id = id;
            this.userId = userId;
            this.username = username;
            this.qq = qq;
            this.createdAt = createdAt;
        }

        public static FriendRequest fromJson(JSONObject obj) throws JSONException {
            return new FriendRequest(obj.optInt("id"), obj.optInt("user_id"), obj.optString("username", "未知用户"), obj.optString("qq", ""), obj.optString("created_at", ""));
        }
    }

    private List<Friend> parseFriends(JSONArray arr) throws JSONException {
        List<Friend> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(Friend.fromJson(arr.getJSONObject(i)));
        }
        return list;
    }

    private List<FriendRequest> parseRequests(JSONArray arr) throws JSONException {
        List<FriendRequest> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(FriendRequest.fromJson(arr.getJSONObject(i)));
        }
        return list;
    }

    private String friendsToJson() throws JSONException {
        JSONArray arr = new JSONArray();
        for (Friend f : this.friends) {
            JSONObject obj = new JSONObject();
            obj.put("id", f.id);
            obj.put("username", f.username);
            obj.put("qq", f.qq);
            obj.put("phone", f.phone);
            obj.put(NotificationCompat.CATEGORY_EMAIL, f.email);
            obj.put("friend_since", f.friendSince);
            arr.put(obj);
        }
        return arr.toString();
    }

    private String pendingToJson() throws JSONException {
        JSONArray arr = new JSONArray();
        for (FriendRequest r : this.pendingRequests) {
            JSONObject obj = new JSONObject();
            obj.put("id", r.id);
            obj.put("user_id", r.userId);
            obj.put("username", r.username);
            obj.put("qq", r.qq);
            obj.put("created_at", r.createdAt);
            arr.put(obj);
        }
        return arr.toString();
    }

    static class SharedPreferencesCompat {
        private final SharedPreferences prefs;

        SharedPreferencesCompat(SharedPreferences prefs) {
            this.prefs = prefs;
        }

        String get(String key, String defVal) {
            return this.prefs.getString(key, defVal);
        }

        void put(String key, String value) {
            this.prefs.edit().putString(key, value).apply();
        }
    }
}

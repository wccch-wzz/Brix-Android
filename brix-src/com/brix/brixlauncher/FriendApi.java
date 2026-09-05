package com.brix.brixlauncher;

import androidx.core.app.NotificationCompat;
import com.brixcore.util.io.HttpRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.helper.HttpConnection;

/* JADX INFO: loaded from: classes18.dex */
public class FriendApi {
    private static final String BASE_URL = "https://brix.zdyfkj.work/api/v1";
    private static final String TAG = "FriendApi";
    private final String token;

    public FriendApi(String token) {
        this.token = token;
    }

    private String get(String path) throws FriendException {
        try {
            String response = HttpRequest.GET(BASE_URL + path).header("Authorization", this.token).header(HttpConnection.CONTENT_TYPE, "application/json; charset=utf-8").retry(3).getString();
            return parseResponse(response);
        } catch (IOException e) {
            throw new FriendException("网络错误: " + e.getMessage(), e);
        }
    }

    private String post(String path) throws FriendException {
        return post(path, null);
    }

    private String post(String path, Map<String, String> body) throws FriendException {
        try {
            HttpRequest.HttpPostRequest builder = (HttpRequest.HttpPostRequest) HttpRequest.POST(BASE_URL + path).header("Authorization", this.token).header(HttpConnection.CONTENT_TYPE, "application/json; charset=utf-8").retry(3);
            if (body != null && !body.isEmpty()) {
                builder.string(new JSONObject(body).toString(), "application/json; charset=utf-8");
            }
            String response = builder.getString();
            return parseResponse(response);
        } catch (IOException e) {
            throw new FriendException("网络错误: " + e.getMessage(), e);
        }
    }

    private String parseResponse(String raw) throws FriendException {
        try {
            JSONObject json = new JSONObject(raw);
            int code = json.optInt("code");
            String msg = json.optString(NotificationCompat.CATEGORY_MESSAGE, "");
            if (code != 200) {
                throw new FriendException(msg.isEmpty() ? "操作失败" : msg);
            }
            Object data = json.opt("data");
            if (data instanceof String) {
                return (String) data;
            }
            if (data == null) {
                return "";
            }
            return data.toString();
        } catch (FriendException e) {
            throw e;
        } catch (Exception e2) {
            throw new FriendException("解析响应失败: " + e2.getMessage(), e2);
        }
    }

    public void addFriend(String targetId) throws FriendException {
        post("/friend/add/" + targetId);
    }

    public void acceptFriend(String requesterId) throws FriendException {
        post("/friend/accept/" + requesterId);
    }

    public void rejectFriend(String requesterId) throws FriendException {
        post("/friend/reject/" + requesterId);
    }

    public void deleteFriend(String friendId) throws FriendException {
        post("/friend/delete/" + friendId);
    }

    public JSONArray getFriendList() throws FriendException {
        String data = get("/friend/list");
        try {
            return new JSONArray(data);
        } catch (Exception e) {
            throw new FriendException("解析好友列表失败: " + e.getMessage(), e);
        }
    }

    public JSONArray getPendingRequests() throws FriendException {
        String data = get("/friend/requests");
        try {
            return new JSONArray(data);
        } catch (Exception e) {
            throw new FriendException("解析待处理请求失败: " + e.getMessage(), e);
        }
    }

    public void blockUser(String userId) throws FriendException {
        post("/friend/block/" + userId);
    }

    public void unblockUser(String userId) throws FriendException {
        post("/friend/unblock/" + userId);
    }

    public void sendMessage(String friendId, String content) throws FriendException {
        Map<String, String> body = new HashMap<>();
        body.put("content", content);
        if (friendId != null && !friendId.isEmpty()) {
            body.put("friend_id", friendId);
        }
        post("/friend/message/send", body);
    }

    public JSONArray getMessages(String friendId, int limit, int offset) throws FriendException {
        StringBuilder sb = new StringBuilder("/friend/messages?limit=" + limit + "&offset=" + offset);
        if (friendId != null && !friendId.isEmpty()) {
            sb.append("&friend_id=").append(friendId);
        }
        String data = get(sb.toString());
        try {
            return new JSONArray(data);
        } catch (Exception e) {
            throw new FriendException("解析消息列表失败: " + e.getMessage(), e);
        }
    }

    public static class FriendException extends Exception {
        public FriendException(String message) {
            super(message);
        }

        public FriendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

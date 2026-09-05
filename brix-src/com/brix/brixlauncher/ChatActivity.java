package com.brix.brixlauncher;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.brixcore.utils.BrixPath;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes18.dex */
public class ChatActivity extends AppCompatActivity {
    private static final int MSG_LIMIT = 50;
    private static final int MSG_OFFSET_STEP = 50;
    private static final String TAG = "ChatActivity";
    private AuthManager authManager;
    private ChatAdapter chatAdapter;
    private FriendApi friendApi;
    private String friendId;
    private String friendName;
    private List<ChatMessage> messages = new ArrayList();
    private int currentOffset = 0;
    private boolean loadingMore = false;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BrixPath.CONTEXT == null) {
            BrixPath.loadPaths(this);
        }
        setContentView(R.layout.activity_chat);
        this.authManager = new AuthManager(this);
        if (!this.authManager.isLoggedIn()) {
            finish();
            return;
        }
        this.friendId = getIntent().getStringExtra("friend_id");
        this.friendName = getIntent().getStringExtra("friend_name");
        if (this.friendId == null || this.friendId.isEmpty()) {
            Toast.makeText(this, "参数错误", 0).show();
            finish();
            return;
        }
        this.friendApi = new FriendApi(this.authManager.getAccessToken());
        TextView tvTitle = (TextView) findViewById(R.id.tv_chat_title);
        if (tvTitle != null) {
            tvTitle.setText(this.friendName != null ? this.friendName : "聊天");
        }
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$0(view);
            }
        });
        ListView lv = (ListView) findViewById(R.id.lv_messages);
        this.chatAdapter = new ChatAdapter();
        lv.setAdapter((ListAdapter) this.chatAdapter);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: com.brix.brixlauncher.ChatActivity.1
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && totalItemCount > 0 && !ChatActivity.this.loadingMore) {
                    ChatActivity.this.loadMoreMessages();
                }
            }
        });
        Button btnSend = (Button) findViewById(R.id.btn_send);
        final EditText etMsg = (EditText) findViewById(R.id.et_message);
        btnSend.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$1(etMsg, view);
            }
        });
        etMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda2
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return this.f$0.lambda$onCreate$2(textView, i, keyEvent);
            }
        });
        loadMessages();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View v) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(EditText etMsg, View v) {
        String content = etMsg.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }
        sendMsg(content);
        etMsg.setText("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$2(TextView v, int actionId, KeyEvent event) {
        String content = v.getText().toString().trim();
        if (!content.isEmpty()) {
            sendMsg(content);
            v.setText("");
            return true;
        }
        return true;
    }

    private void loadMessages() {
        new Thread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadMessages$5();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMessages$5() {
        try {
            JSONArray arr = this.friendApi.getMessages(this.friendId, 50, 0);
            this.messages.clear();
            for (int i = arr.length() - 1; i >= 0; i--) {
                this.messages.add(parseMessage(arr.getJSONObject(i)));
            }
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$loadMessages$3();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "加载消息失败: " + e.getMessage());
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$loadMessages$4();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMessages$3() {
        this.chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMessages$4() {
        Toast.makeText(this, "加载消息失败", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadMoreMessages() {
        this.loadingMore = true;
        new Thread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadMoreMessages$7();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMoreMessages$7() {
        try {
            JSONArray arr = this.friendApi.getMessages(this.friendId, 50, this.currentOffset + 50);
            if (arr.length() == 0) {
                this.loadingMore = false;
                return;
            }
            final List<ChatMessage> old = new ArrayList<>(this.messages);
            for (int i = arr.length() - 1; i >= 0; i--) {
                old.add(0, parseMessage(arr.getJSONObject(i)));
            }
            this.messages = old;
            this.currentOffset += arr.length();
            this.loadingMore = false;
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$loadMoreMessages$6(old);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "加载更多失败: " + e.getMessage());
            this.loadingMore = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMoreMessages$6(List old) {
        this.chatAdapter.notifyDataSetChanged();
        int pos = Math.max(0, this.chatAdapter.getCount() - old.size());
        ((ListView) findViewById(R.id.lv_messages)).setSelection(pos);
    }

    private void sendMsg(final String content) {
        final Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setEnabled(false);
        new Thread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$sendMsg$10(content, btnSend);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMsg$10(String content, final Button btnSend) {
        try {
            this.friendApi.sendMessage(this.friendId, content);
            ChatMessage selfMsg = new ChatMessage(content, true, getCurrentTimeStr());
            this.messages.add(selfMsg);
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$sendMsg$8(btnSend);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "发送消息失败: " + e.getMessage());
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$sendMsg$9(btnSend);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMsg$8(Button btnSend) {
        this.chatAdapter.notifyDataSetChanged();
        scrollToBottom();
        btnSend.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMsg$9(Button btnSend) {
        Toast.makeText(this, "发送失败，请重试", 0).show();
        btnSend.setEnabled(true);
    }

    private ChatMessage parseMessage(JSONObject obj) throws Exception {
        String rawContent = obj.optString("content", "");
        String decrypted = rawContent;
        if (!rawContent.isEmpty()) {
            try {
                decrypted = MessageCrypt.decrypt(rawContent);
            } catch (Exception e) {
                Log.w(TAG, "解密失败，显示原始内容: " + e.getMessage());
                decrypted = rawContent;
            }
        }
        boolean isSelf = String.valueOf(obj.optInt("user_id")).equals(this.authManager.getUserId());
        return new ChatMessage(decrypted, isSelf, obj.optString("created_at", ""));
    }

    private String getCurrentTimeStr() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void scrollToBottom() {
        final ListView lv = (ListView) findViewById(R.id.lv_messages);
        if (lv != null && this.chatAdapter.getCount() > 0) {
            lv.post(new Runnable() { // from class: com.brix.brixlauncher.ChatActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$scrollToBottom$11(lv);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scrollToBottom$11(ListView lv) {
        lv.setSelection(this.chatAdapter.getCount() - 1);
    }

    static class ChatMessage {
        final String content;
        final boolean isSelf;
        final String time;

        ChatMessage(String content, boolean isSelf, String time) {
            this.content = content;
            this.isSelf = isSelf;
            this.time = time;
        }
    }

    private class ChatAdapter extends BaseAdapter {
        private ChatAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return ChatActivity.this.messages.size();
        }

        @Override // android.widget.Adapter
        public ChatMessage getItem(int position) {
            return (ChatMessage) ChatActivity.this.messages.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 2;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int i) {
            return ((ChatMessage) ChatActivity.this.messages.get(i)).isSelf ? 1 : 0;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ChatMessage msg = getItem(position);
            if (msg.isSelf) {
                if (convertView == null) {
                    convertView = ChatActivity.this.getLayoutInflater().inflate(R.layout.item_chat_message, parent, false);
                }
                convertView.findViewById(R.id.msg_other_layout).setVisibility(8);
                LinearLayout selfLayout = (LinearLayout) convertView.findViewById(R.id.msg_self_layout);
                selfLayout.setVisibility(0);
                TextView tvText = (TextView) convertView.findViewById(R.id.msg_self_text);
                TextView tvTime = (TextView) convertView.findViewById(R.id.msg_self_time);
                if (tvText != null) {
                    tvText.setText(msg.content);
                }
                if (tvTime != null) {
                    tvTime.setText(msg.time);
                }
            } else {
                if (convertView == null) {
                    convertView = ChatActivity.this.getLayoutInflater().inflate(R.layout.item_chat_message, parent, false);
                }
                convertView.findViewById(R.id.msg_self_layout).setVisibility(8);
                LinearLayout otherLayout = (LinearLayout) convertView.findViewById(R.id.msg_other_layout);
                otherLayout.setVisibility(0);
                TextView tvName = (TextView) convertView.findViewById(R.id.msg_other_name);
                TextView tvText2 = (TextView) convertView.findViewById(R.id.msg_other_text);
                TextView tvLetter = (TextView) convertView.findViewById(R.id.msg_other_avatar_letter);
                View vBg = convertView.findViewById(R.id.msg_other_avatar_bg);
                if (tvName != null) {
                    tvName.setText(ChatActivity.this.friendName != null ? ChatActivity.this.friendName : "好友");
                }
                if (tvText2 != null) {
                    tvText2.setText(msg.content);
                }
                if (tvLetter != null) {
                    String letter = (ChatActivity.this.friendName == null || ChatActivity.this.friendName.length() <= 0) ? "?" : String.valueOf(ChatActivity.this.friendName.charAt(0)).toUpperCase();
                    tvLetter.setText(letter);
                }
                if (vBg != null) {
                    Random rand = new Random(ChatActivity.this.friendId.hashCode());
                    int greenBase = rand.nextInt(80) + 80;
                    int blueBase = rand.nextInt(60) + 40;
                    vBg.setBackgroundColor(Color.argb(220, 27, greenBase, blueBase));
                }
            }
            return convertView;
        }
    }
}

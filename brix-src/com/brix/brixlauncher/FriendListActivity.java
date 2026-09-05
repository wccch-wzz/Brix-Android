package com.brix.brixlauncher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.brixcore.utils.BrixPath;
import java.util.Random;

/* JADX INFO: loaded from: classes18.dex */
public class FriendListActivity extends AppCompatActivity {
    private static final String TAG = "FriendListActivity";
    private FriendAdapter friendAdapter;
    private FriendManager friendManager;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BrixPath.CONTEXT == null) {
            BrixPath.loadPaths(this);
        }
        setContentView(R.layout.activity_friends);
        AuthManager authManager = new AuthManager(this);
        if (!authManager.isLoggedIn()) {
            goToLogin();
            return;
        }
        this.friendManager = new FriendManager(this, authManager.getAccessToken());
        initViews();
        loadFriends();
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$initViews$0(view);
            }
        });
        findViewById(R.id.btn_pending_requests).setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda13
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$initViews$1(view);
            }
        });
        final Button btnAdd = (Button) findViewById(R.id.btn_add_friend);
        final EditText etId = (EditText) findViewById(R.id.et_friend_id);
        btnAdd.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$initViews$6(etId, btnAdd, view);
            }
        });
        ListView lv = (ListView) findViewById(R.id.lv_friends);
        this.friendAdapter = new FriendAdapter();
        lv.setAdapter((ListAdapter) this.friendAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda15
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                this.f$0.lambda$initViews$7(adapterView, view, i, j);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda16
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public final boolean onItemLongClick(AdapterView adapterView, View view, int i, long j) {
                return this.f$0.lambda$initViews$8(adapterView, view, i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$0(View v) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$1(View v) {
        startActivity(new Intent(this, (Class<?>) FriendsPendingActivity.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$6(final EditText etId, final Button btnAdd, View v) {
        String id = etId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "请输入用户ID", 0).show();
            return;
        }
        btnAdd.setEnabled(false);
        btnAdd.setText("发送中...");
        this.friendManager.sendFriendRequest(id, new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$initViews$3(btnAdd, etId);
            }
        }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda7
            @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
            public final void onClick() {
                this.f$0.lambda$initViews$5(btnAdd);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$3(final Button btnAdd, final EditText etId) {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$initViews$2(btnAdd, etId);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$2(Button btnAdd, EditText etId) {
        btnAdd.setEnabled(true);
        btnAdd.setText("添加");
        Toast.makeText(this, "好友申请已发送", 0).show();
        etId.setText("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$5(final Button btnAdd) {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$initViews$4(btnAdd);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$4(Button btnAdd) {
        btnAdd.setEnabled(true);
        btnAdd.setText("添加");
        Toast.makeText(this, "发送失败，请重试", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$7(AdapterView parent, View view, int position, long id) {
        FriendManager.Friend friend = this.friendAdapter.getItem(position);
        if (friend != null) {
            openChat(friend);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initViews$8(AdapterView parent, View view, int position, long id) {
        FriendManager.Friend friend = this.friendAdapter.getItem(position);
        if (friend != null) {
            showFriendOptions(friend);
            return true;
        }
        return true;
    }

    private void loadFriends() {
        this.friendManager.refreshFriends(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadFriends$10();
            }
        }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda5
            @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
            public final void onClick() {
                this.f$0.lambda$loadFriends$12();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFriends$10() {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadFriends$9();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFriends$9() {
        this.friendAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFriends$12() {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadFriends$11();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFriends$11() {
        Toast.makeText(this, "加载好友列表失败", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openChat(FriendManager.Friend friend) {
        Intent intent = new Intent(this, (Class<?>) ChatActivity.class);
        intent.putExtra("friend_id", String.valueOf(friend.id));
        intent.putExtra("friend_name", friend.username);
        startActivity(intent);
    }

    private void showFriendOptions(final FriendManager.Friend friend) {
        new AlertDialog.Builder(this).setTitle(friend.username).setItems(new CharSequence[]{"删除好友"}, new DialogInterface.OnClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.lambda$showFriendOptions$17(friend, dialogInterface, i);
            }
        }).setNegativeButton("取消", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFriendOptions$17(FriendManager.Friend friend, DialogInterface dialog, int which) {
        this.friendManager.deleteFriend(String.valueOf(friend.id), new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showFriendOptions$14();
            }
        }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda10
            @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
            public final void onClick() {
                this.f$0.lambda$showFriendOptions$16();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFriendOptions$14() {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showFriendOptions$13();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFriendOptions$13() {
        this.friendAdapter.notifyDataSetChanged();
        updateEmptyView();
        Toast.makeText(this, "已删除好友", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFriendOptions$16() {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showFriendOptions$15();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFriendOptions$15() {
        Toast.makeText(this, "删除失败", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmptyView() {
        int count = this.friendAdapter.getCount();
        findViewById(R.id.lv_friends).setVisibility(count > 0 ? 0 : 8);
        findViewById(R.id.tv_empty_friends).setVisibility(count > 0 ? 8 : 0);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, (Class<?>) BrixAuthActivity.class);
        intent.setFlags(268468224);
        startActivity(intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    class FriendAdapter extends BaseAdapter {
        private FriendAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return FriendListActivity.this.friendManager.getFriends().size();
        }

        @Override // android.widget.Adapter
        public FriendManager.Friend getItem(int position) {
            return FriendListActivity.this.friendManager.getFriends().get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = FriendListActivity.this.getLayoutInflater().inflate(R.layout.item_friend, parent, false);
            }
            final FriendManager.Friend f = getItem(position);
            TextView tvName = (TextView) convertView.findViewById(R.id.friend_username);
            TextView tvLetter = (TextView) convertView.findViewById(R.id.friend_avatar_letter);
            View vBg = convertView.findViewById(R.id.friend_avatar_bg);
            if (tvName != null) {
                tvName.setText(f.username);
            }
            if (tvLetter != null) {
                String letter = f.username.length() > 0 ? String.valueOf(f.username.charAt(0)).toUpperCase() : "?";
                tvLetter.setText(letter);
            }
            if (vBg != null) {
                Random rand = new Random(f.id);
                int greenBase = rand.nextInt(80) + 80;
                int blueBase = rand.nextInt(60) + 40;
                vBg.setBackgroundColor(Color.argb(220, 27, greenBase, blueBase));
            }
            Button btnChat = (Button) convertView.findViewById(R.id.btn_chat);
            Button btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
            if (btnChat != null) {
                btnChat.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$FriendAdapter$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$getView$0(f, view);
                    }
                });
            }
            if (btnDelete != null) {
                btnDelete.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$FriendAdapter$$ExternalSyntheticLambda6
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$getView$6(f, view);
                    }
                });
            }
            return convertView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$0(FriendManager.Friend f, View v) {
            FriendListActivity.this.openChat(f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$6(final FriendManager.Friend f, View v) {
            new AlertDialog.Builder(FriendListActivity.this).setTitle("确认删除").setMessage("确定要删除好友「" + f.username + "」吗？").setPositiveButton("删除", new DialogInterface.OnClickListener() { // from class: com.brix.brixlauncher.FriendListActivity$FriendAdapter$$ExternalSyntheticLambda4
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.lambda$getView$5(f, dialogInterface, i);
                }
            }).setNegativeButton("取消", (DialogInterface.OnClickListener) null).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$5(FriendManager.Friend f, DialogInterface dialog, int which) {
            FriendListActivity.this.friendManager.deleteFriend(String.valueOf(f.id), new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$FriendAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$2();
                }
            }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendListActivity$FriendAdapter$$ExternalSyntheticLambda1
                @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
                public final void onClick() {
                    this.f$0.lambda$getView$4();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$2() {
            FriendListActivity.this.runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$FriendAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$1() {
            notifyDataSetChanged();
            FriendListActivity.this.updateEmptyView();
            Toast.makeText(FriendListActivity.this, "已删除", 0).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$4() {
            FriendListActivity.this.runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendListActivity$FriendAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$3();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$3() {
            Toast.makeText(FriendListActivity.this, "删除失败", 0).show();
        }
    }
}

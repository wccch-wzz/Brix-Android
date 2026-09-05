package com.brix.brixlauncher;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.brixcore.utils.BrixPath;
import java.util.Random;

/* JADX INFO: loaded from: classes18.dex */
public class FriendsPendingActivity extends AppCompatActivity {
    private static final String TAG = "FriendsPendingActivity";
    private PendingAdapter adapter;
    private FriendManager friendManager;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BrixPath.CONTEXT == null) {
            BrixPath.loadPaths(this);
        }
        setContentView(R.layout.activity_friends_pending);
        AuthManager authManager = new AuthManager(this);
        if (!authManager.isLoggedIn()) {
            goToLogin();
            return;
        }
        this.friendManager = new FriendManager(this, authManager.getAccessToken());
        initViews();
        loadPending();
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$initViews$0(view);
            }
        });
        ListView lv = (ListView) findViewById(R.id.lv_pending);
        this.adapter = new PendingAdapter();
        lv.setAdapter((ListAdapter) this.adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda5
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                this.f$0.lambda$initViews$1(adapterView, view, i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$0(View v) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$1(AdapterView parent, View view, int position, long id) {
        FriendManager.FriendRequest req = this.adapter.getItem(position);
        if (req != null) {
            acceptRequest(req);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPending() {
        this.friendManager.refreshPendingRequests(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadPending$3();
            }
        }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda3
            @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
            public final void onClick() {
                this.f$0.lambda$loadPending$5();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPending$3() {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadPending$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPending$2() {
        this.adapter.notifyDataSetChanged();
        updateEmptyView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPending$5() {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadPending$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPending$4() {
        Toast.makeText(this, "加载失败", 0).show();
    }

    private void acceptRequest(final FriendManager.FriendRequest req) {
        this.friendManager.acceptRequest(String.valueOf(req.userId), new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$acceptRequest$7(req);
            }
        }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda9
            @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
            public final void onClick() {
                this.f$0.lambda$acceptRequest$9();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptRequest$7(final FriendManager.FriendRequest req) {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$acceptRequest$6(req);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptRequest$6(FriendManager.FriendRequest req) {
        Toast.makeText(this, "已接受「" + req.username + "」的好友申请", 0).show();
        loadPending();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptRequest$9() {
        runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$acceptRequest$8();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptRequest$8() {
        Toast.makeText(this, "操作失败，请重试", 0).show();
    }

    private void updateEmptyView() {
        int count = this.adapter.getCount();
        findViewById(R.id.lv_pending).setVisibility(count > 0 ? 0 : 8);
        findViewById(R.id.tv_empty_pending).setVisibility(count > 0 ? 8 : 0);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, (Class<?>) BrixAuthActivity.class);
        intent.setFlags(268468224);
        startActivity(intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    class PendingAdapter extends BaseAdapter {
        private PendingAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return FriendsPendingActivity.this.friendManager.getPendingRequests().size();
        }

        @Override // android.widget.Adapter
        public FriendManager.FriendRequest getItem(int position) {
            return FriendsPendingActivity.this.friendManager.getPendingRequests().get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = FriendsPendingActivity.this.getLayoutInflater().inflate(R.layout.item_pending_friend, parent, false);
            }
            final FriendManager.FriendRequest req = getItem(position);
            TextView tvUsername = (TextView) convertView.findViewById(R.id.request_username);
            TextView tvTime = (TextView) convertView.findViewById(R.id.request_time);
            TextView tvLetter = (TextView) convertView.findViewById(R.id.request_avatar_letter);
            View vBg = convertView.findViewById(R.id.request_avatar_bg);
            if (tvUsername != null) {
                tvUsername.setText(req.username);
            }
            if (tvTime != null) {
                tvTime.setText(req.createdAt);
            }
            if (tvLetter != null) {
                String letter = req.username.length() > 0 ? String.valueOf(req.username.charAt(0)).toUpperCase() : "?";
                tvLetter.setText(letter);
            }
            if (vBg != null) {
                Random rand = new Random(req.userId);
                int greenBase = rand.nextInt(80) + 80;
                int blueBase = rand.nextInt(60) + 40;
                vBg.setBackgroundColor(Color.argb(220, 27, greenBase, blueBase));
            }
            final Button btnAccept = (Button) convertView.findViewById(R.id.btn_accept);
            final Button btnReject = (Button) convertView.findViewById(R.id.btn_reject);
            if (btnAccept != null) {
                btnAccept.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda6
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$getView$4(btnAccept, btnReject, req, view);
                    }
                });
            }
            if (btnReject != null) {
                btnReject.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda7
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$getView$9(btnAccept, btnReject, req, view);
                    }
                });
            }
            return convertView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$4(final Button btnAccept, final Button btnReject, final FriendManager.FriendRequest req, View v) {
            btnAccept.setEnabled(false);
            btnReject.setEnabled(false);
            FriendsPendingActivity.this.friendManager.acceptRequest(String.valueOf(req.userId), new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$1(req);
                }
            }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda9
                @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
                public final void onClick() {
                    this.f$0.lambda$getView$3(btnAccept, btnReject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$1(final FriendManager.FriendRequest req) {
            FriendsPendingActivity.this.runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$0(req);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$0(FriendManager.FriendRequest req) {
            Toast.makeText(FriendsPendingActivity.this, "已接受「" + req.username + "」的好友申请", 0).show();
            FriendsPendingActivity.this.loadPending();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$3(final Button btnAccept, final Button btnReject) {
            FriendsPendingActivity.this.runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$2(btnAccept, btnReject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$2(Button btnAccept, Button btnReject) {
            btnAccept.setEnabled(true);
            btnReject.setEnabled(true);
            Toast.makeText(FriendsPendingActivity.this, "操作失败", 0).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$9(final Button btnAccept, final Button btnReject, final FriendManager.FriendRequest req, View v) {
            btnAccept.setEnabled(false);
            btnReject.setEnabled(false);
            FriendsPendingActivity.this.friendManager.rejectRequest(String.valueOf(req.userId), new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$6(req);
                }
            }, new FriendManager.OnErrorListener() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda1
                @Override // com.brix.brixlauncher.FriendManager.OnErrorListener
                public final void onClick() {
                    this.f$0.lambda$getView$8(btnAccept, btnReject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$6(final FriendManager.FriendRequest req) {
            FriendsPendingActivity.this.runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$5(req);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$5(FriendManager.FriendRequest req) {
            Toast.makeText(FriendsPendingActivity.this, "已拒绝「" + req.username + "」的好友申请", 0).show();
            FriendsPendingActivity.this.loadPending();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$8(final Button btnAccept, final Button btnReject) {
            FriendsPendingActivity.this.runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.FriendsPendingActivity$PendingAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getView$7(btnAccept, btnReject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$7(Button btnAccept, Button btnReject) {
            btnAccept.setEnabled(true);
            btnReject.setEnabled(true);
            Toast.makeText(FriendsPendingActivity.this, "操作失败", 0).show();
        }
    }
}

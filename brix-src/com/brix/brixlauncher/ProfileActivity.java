package com.brix.brixlauncher;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.brixcore.utils.BrixPath;
import java.util.Random;

/* JADX INFO: loaded from: classes18.dex */
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private AuthManager authManager;
    private View avatarBg;
    private TextView avatarLetter;
    private Button btnLogout;
    private CheckBox cbRemember;
    private TextView tvUid;
    private TextView tvUsername;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (BrixPath.CONTEXT == null) {
                BrixPath.loadPaths(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "BrixPath初始化失败: " + e.getMessage());
        }
        setContentView(R.layout.activity_profile);
        this.authManager = new AuthManager(this);
        initViews();
        if (!this.authManager.isLoggedIn()) {
            Log.d(TAG, "用户未登录，跳转登录页");
            goToLogin();
        } else {
            loadUserInfo();
            setupListeners();
        }
    }

    private void initViews() {
        try {
            ((TextView) findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.ProfileActivity$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$initViews$0(view);
                }
            });
            this.tvUsername = (TextView) findViewById(R.id.tv_username);
            this.tvUid = (TextView) findViewById(R.id.tv_uid);
            this.avatarLetter = (TextView) findViewById(R.id.avatar_letter);
            this.avatarBg = findViewById(R.id.avatar_bg);
            this.cbRemember = (CheckBox) findViewById(R.id.cb_remember);
            this.btnLogout = (Button) findViewById(R.id.btn_logout);
        } catch (Exception e) {
            Log.e(TAG, "初始化视图失败: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$0(View v) {
        finish();
    }

    private void loadUserInfo() {
        try {
            String username = this.authManager.getUsername();
            String uid = this.authManager.getUserId();
            boolean remember = this.authManager.isRememberMe();
            if (this.tvUsername != null) {
                this.tvUsername.setText(username);
            }
            if (this.tvUid != null) {
                this.tvUid.setText("UID: " + uid);
            }
            if (this.cbRemember != null) {
                this.cbRemember.setChecked(remember);
            }
            if (this.avatarLetter != null) {
                String letter = username.length() > 0 ? String.valueOf(username.charAt(0)).toUpperCase() : "?";
                this.avatarLetter.setText(letter);
            }
            if (this.avatarBg != null) {
                Random rand = new Random(uid.hashCode());
                int greenBase = rand.nextInt(80) + 80;
                int blueBase = rand.nextInt(60) + 40;
                this.avatarBg.setBackgroundColor(Color.argb(220, 27, greenBase, blueBase));
            }
        } catch (Exception e) {
            Log.e(TAG, "加载用户信息失败: " + e.getMessage());
        }
    }

    private void setupListeners() {
        View friendsItem = findViewById(R.id.item_friends);
        if (friendsItem != null) {
            friendsItem.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.ProfileActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$setupListeners$1(view);
                }
            });
        }
        View rememberItem = findViewById(R.id.item_remember);
        if (rememberItem != null) {
            rememberItem.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.ProfileActivity$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$setupListeners$2(view);
                }
            });
        }
        if (this.btnLogout != null) {
            this.btnLogout.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.ProfileActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$setupListeners$3(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupListeners$1(View v) {
        try {
            startActivity(new Intent(this, (Class<?>) FriendListActivity.class));
        } catch (Exception e) {
            Log.e(TAG, "跳转好友页面失败: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupListeners$2(View v) {
        if (this.cbRemember != null) {
            this.cbRemember.setChecked(!this.cbRemember.isChecked());
            this.authManager.setRememberMe(this.cbRemember.isChecked());
            Toast.makeText(this, this.cbRemember.isChecked() ? "已开启30天免登录" : "已关闭30天免登录", 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupListeners$3(View v) {
        try {
            this.authManager.logout();
            Toast.makeText(this, "已退出登录", 0).show();
            goToLogin();
        } catch (Exception e) {
            Log.e(TAG, "退出登录失败: " + e.getMessage());
        }
    }

    private void goToLogin() {
        try {
            Intent intent = new Intent(this, (Class<?>) BrixAuthActivity.class);
            intent.setFlags(268468224);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "跳转登录页失败: " + e.getMessage());
            finish();
        }
    }
}

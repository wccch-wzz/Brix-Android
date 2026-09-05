package com.brix.brixlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.brixcore.bridge.BrixBridge;
import com.brixcore.utils.Architecture;
import com.brixcore.utils.BrixPath;
import java.util.Random;

/* JADX INFO: loaded from: classes18.dex */
public class BrixLauncher extends Activity {
    private static final String TAG = "BrixLauncher";
    private static Context sAppContext;
    private AuthManager authManager;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (BrixPath.CONTEXT == null) {
                BrixPath.loadPaths(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "BrixPath初始化失败: " + e.getMessage());
        }
        sAppContext = this;
        this.authManager = new AuthManager(this);
        if (!this.authManager.isLoggedIn()) {
            Log.d(TAG, "未登录，跳转到登录页");
            Intent intent = new Intent(this, (Class<?>) BrixAuthActivity.class);
            intent.setFlags(268468224);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        loadUserInfo();
        findViewById(R.id.btn_profile).setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.BrixLauncher$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View v) {
        try {
            startActivity(new Intent(this, (Class<?>) ProfileActivity.class));
        } catch (Exception e) {
            Log.e(TAG, "跳转个人中心失败: " + e.getMessage());
        }
    }

    private void loadUserInfo() {
        try {
            String username = this.authManager.getUsername();
            String uid = this.authManager.getUserId();
            TextView tvUsername = (TextView) findViewById(R.id.tv_main_username);
            TextView tvAvatarLetter = (TextView) findViewById(R.id.main_avatar_letter);
            View avatarBg = findViewById(R.id.main_avatar_bg);
            if (tvUsername != null) {
                tvUsername.setText(username);
            }
            if (tvAvatarLetter != null) {
                String letter = username.length() > 0 ? String.valueOf(username.charAt(0)).toUpperCase() : "?";
                tvAvatarLetter.setText(letter);
            }
            if (avatarBg != null) {
                Random rand = new Random(uid.hashCode());
                int greenBase = rand.nextInt(80) + 80;
                int blueBase = rand.nextInt(60) + 40;
                avatarBg.setBackgroundColor(Color.argb(220, 27, greenBase, blueBase));
            }
        } catch (Exception e) {
            Log.e(TAG, "加载用户信息失败: " + e.getMessage());
        }
    }

    public static BrixBridge launchMinecraft(BrixConfig config) {
        return null;
    }

    public static String getSocName() {
        return Architecture.getSocName();
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}

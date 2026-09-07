package com.brix.brixlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.brix.brixlauncher.R;

/* JADX INFO: loaded from: classes18.dex */
public final class ActivityMainBinding implements ViewBinding {
    public final TextView btnProfile;
    public final View mainAvatarBg;
    public final TextView mainAvatarLetter;
    private final LinearLayout rootView;
    public final TextView tvMainUsername;
    public final LinearLayout userInfoArea;

    private ActivityMainBinding(LinearLayout rootView, TextView btnProfile, View mainAvatarBg, TextView mainAvatarLetter, TextView tvMainUsername, LinearLayout userInfoArea) {
        this.rootView = rootView;
        this.btnProfile = btnProfile;
        this.mainAvatarBg = mainAvatarBg;
        this.mainAvatarLetter = mainAvatarLetter;
        this.tvMainUsername = tvMainUsername;
        this.userInfoArea = userInfoArea;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityMainBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityMainBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_main, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityMainBinding bind(View rootView) {
        View mainAvatarBg;
        int id = R.id.btn_profile;
        TextView btnProfile = (TextView) ViewBindings.findChildViewById(rootView, id);
        if (btnProfile != null && (mainAvatarBg = ViewBindings.findChildViewById(rootView, (id = R.id.main_avatar_bg))) != null) {
            id = R.id.main_avatar_letter;
            TextView mainAvatarLetter = (TextView) ViewBindings.findChildViewById(rootView, id);
            if (mainAvatarLetter != null) {
                id = R.id.tv_main_username;
                TextView tvMainUsername = (TextView) ViewBindings.findChildViewById(rootView, id);
                if (tvMainUsername != null) {
                    id = R.id.user_info_area;
                    LinearLayout userInfoArea = (LinearLayout) ViewBindings.findChildViewById(rootView, id);
                    if (userInfoArea != null) {
                        return new ActivityMainBinding((LinearLayout) rootView, btnProfile, mainAvatarBg, mainAvatarLetter, tvMainUsername, userInfoArea);
                    }
                }
            }
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}

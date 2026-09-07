package com.brix.brixlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.brix.brixlauncher.R;

/* JADX INFO: loaded from: classes18.dex */
public final class ActivityProfileBinding implements ViewBinding {
    public final View avatarBg;
    public final TextView avatarLetter;
    public final ImageView bgImage;
    public final TextView btnBack;
    public final Button btnLogout;
    public final CheckBox cbRemember;
    public final LinearLayout itemFriends;
    public final LinearLayout itemRemember;
    private final FrameLayout rootView;
    public final TextView tvUid;
    public final TextView tvUsername;

    private ActivityProfileBinding(FrameLayout rootView, View avatarBg, TextView avatarLetter, ImageView bgImage, TextView btnBack, Button btnLogout, CheckBox cbRemember, LinearLayout itemFriends, LinearLayout itemRemember, TextView tvUid, TextView tvUsername) {
        this.rootView = rootView;
        this.avatarBg = avatarBg;
        this.avatarLetter = avatarLetter;
        this.bgImage = bgImage;
        this.btnBack = btnBack;
        this.btnLogout = btnLogout;
        this.cbRemember = cbRemember;
        this.itemFriends = itemFriends;
        this.itemRemember = itemRemember;
        this.tvUid = tvUid;
        this.tvUsername = tvUsername;
    }

    @Override // androidx.viewbinding.ViewBinding
    public FrameLayout getRoot() {
        return this.rootView;
    }

    public static ActivityProfileBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityProfileBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_profile, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityProfileBinding bind(View rootView) {
        int id = R.id.avatar_bg;
        View avatarBg = ViewBindings.findChildViewById(rootView, id);
        if (avatarBg != null) {
            id = R.id.avatar_letter;
            TextView avatarLetter = (TextView) ViewBindings.findChildViewById(rootView, id);
            if (avatarLetter != null) {
                id = R.id.bg_image;
                ImageView bgImage = (ImageView) ViewBindings.findChildViewById(rootView, id);
                if (bgImage != null) {
                    id = R.id.btn_back;
                    TextView btnBack = (TextView) ViewBindings.findChildViewById(rootView, id);
                    if (btnBack != null) {
                        id = R.id.btn_logout;
                        Button btnLogout = (Button) ViewBindings.findChildViewById(rootView, id);
                        if (btnLogout != null) {
                            id = R.id.cb_remember;
                            CheckBox cbRemember = (CheckBox) ViewBindings.findChildViewById(rootView, id);
                            if (cbRemember != null) {
                                id = R.id.item_friends;
                                LinearLayout itemFriends = (LinearLayout) ViewBindings.findChildViewById(rootView, id);
                                if (itemFriends != null) {
                                    id = R.id.item_remember;
                                    LinearLayout itemRemember = (LinearLayout) ViewBindings.findChildViewById(rootView, id);
                                    if (itemRemember != null) {
                                        id = R.id.tv_uid;
                                        TextView tvUid = (TextView) ViewBindings.findChildViewById(rootView, id);
                                        if (tvUid != null) {
                                            id = R.id.tv_username;
                                            TextView tvUsername = (TextView) ViewBindings.findChildViewById(rootView, id);
                                            if (tvUsername != null) {
                                                return new ActivityProfileBinding((FrameLayout) rootView, avatarBg, avatarLetter, bgImage, btnBack, btnLogout, cbRemember, itemFriends, itemRemember, tvUid, tvUsername);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}

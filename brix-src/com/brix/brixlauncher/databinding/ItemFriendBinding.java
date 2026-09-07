package com.brix.brixlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.brix.brixlauncher.R;

/* JADX INFO: loaded from: classes18.dex */
public final class ItemFriendBinding implements ViewBinding {
    public final Button btnChat;
    public final Button btnDelete;
    public final FrameLayout friendAvatarBg;
    public final TextView friendAvatarLetter;
    public final TextView friendUsername;
    private final LinearLayout rootView;

    private ItemFriendBinding(LinearLayout rootView, Button btnChat, Button btnDelete, FrameLayout friendAvatarBg, TextView friendAvatarLetter, TextView friendUsername) {
        this.rootView = rootView;
        this.btnChat = btnChat;
        this.btnDelete = btnDelete;
        this.friendAvatarBg = friendAvatarBg;
        this.friendAvatarLetter = friendAvatarLetter;
        this.friendUsername = friendUsername;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemFriendBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ItemFriendBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.item_friend, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ItemFriendBinding bind(View rootView) {
        int id = R.id.btn_chat;
        Button btnChat = (Button) ViewBindings.findChildViewById(rootView, id);
        if (btnChat != null) {
            id = R.id.btn_delete;
            Button btnDelete = (Button) ViewBindings.findChildViewById(rootView, id);
            if (btnDelete != null) {
                id = R.id.friend_avatar_bg;
                FrameLayout friendAvatarBg = (FrameLayout) ViewBindings.findChildViewById(rootView, id);
                if (friendAvatarBg != null) {
                    id = R.id.friend_avatar_letter;
                    TextView friendAvatarLetter = (TextView) ViewBindings.findChildViewById(rootView, id);
                    if (friendAvatarLetter != null) {
                        id = R.id.friend_username;
                        TextView friendUsername = (TextView) ViewBindings.findChildViewById(rootView, id);
                        if (friendUsername != null) {
                            return new ItemFriendBinding((LinearLayout) rootView, btnChat, btnDelete, friendAvatarBg, friendAvatarLetter, friendUsername);
                        }
                    }
                }
            }
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}

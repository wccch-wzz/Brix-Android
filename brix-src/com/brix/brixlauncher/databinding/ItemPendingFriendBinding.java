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
public final class ItemPendingFriendBinding implements ViewBinding {
    public final Button btnAccept;
    public final Button btnReject;
    public final FrameLayout requestAvatarBg;
    public final TextView requestAvatarLetter;
    public final TextView requestTime;
    public final TextView requestUsername;
    private final LinearLayout rootView;

    private ItemPendingFriendBinding(LinearLayout rootView, Button btnAccept, Button btnReject, FrameLayout requestAvatarBg, TextView requestAvatarLetter, TextView requestTime, TextView requestUsername) {
        this.rootView = rootView;
        this.btnAccept = btnAccept;
        this.btnReject = btnReject;
        this.requestAvatarBg = requestAvatarBg;
        this.requestAvatarLetter = requestAvatarLetter;
        this.requestTime = requestTime;
        this.requestUsername = requestUsername;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemPendingFriendBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ItemPendingFriendBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.item_pending_friend, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ItemPendingFriendBinding bind(View rootView) {
        int id = R.id.btn_accept;
        Button btnAccept = (Button) ViewBindings.findChildViewById(rootView, id);
        if (btnAccept != null) {
            id = R.id.btn_reject;
            Button btnReject = (Button) ViewBindings.findChildViewById(rootView, id);
            if (btnReject != null) {
                id = R.id.request_avatar_bg;
                FrameLayout requestAvatarBg = (FrameLayout) ViewBindings.findChildViewById(rootView, id);
                if (requestAvatarBg != null) {
                    id = R.id.request_avatar_letter;
                    TextView requestAvatarLetter = (TextView) ViewBindings.findChildViewById(rootView, id);
                    if (requestAvatarLetter != null) {
                        id = R.id.request_time;
                        TextView requestTime = (TextView) ViewBindings.findChildViewById(rootView, id);
                        if (requestTime != null) {
                            id = R.id.request_username;
                            TextView requestUsername = (TextView) ViewBindings.findChildViewById(rootView, id);
                            if (requestUsername != null) {
                                return new ItemPendingFriendBinding((LinearLayout) rootView, btnAccept, btnReject, requestAvatarBg, requestAvatarLetter, requestTime, requestUsername);
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

package com.brix.brixlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.brix.brixlauncher.R;

/* JADX INFO: loaded from: classes18.dex */
public final class ItemChatMessageBinding implements ViewBinding {
    public final FrameLayout msgOtherAvatarBg;
    public final TextView msgOtherAvatarLetter;
    public final LinearLayout msgOtherLayout;
    public final TextView msgOtherName;
    public final TextView msgOtherText;
    public final LinearLayout msgSelfLayout;
    public final TextView msgSelfText;
    public final TextView msgSelfTime;
    private final LinearLayout rootView;

    private ItemChatMessageBinding(LinearLayout rootView, FrameLayout msgOtherAvatarBg, TextView msgOtherAvatarLetter, LinearLayout msgOtherLayout, TextView msgOtherName, TextView msgOtherText, LinearLayout msgSelfLayout, TextView msgSelfText, TextView msgSelfTime) {
        this.rootView = rootView;
        this.msgOtherAvatarBg = msgOtherAvatarBg;
        this.msgOtherAvatarLetter = msgOtherAvatarLetter;
        this.msgOtherLayout = msgOtherLayout;
        this.msgOtherName = msgOtherName;
        this.msgOtherText = msgOtherText;
        this.msgSelfLayout = msgSelfLayout;
        this.msgSelfText = msgSelfText;
        this.msgSelfTime = msgSelfTime;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemChatMessageBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ItemChatMessageBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.item_chat_message, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ItemChatMessageBinding bind(View rootView) {
        int id = R.id.msg_other_avatar_bg;
        FrameLayout msgOtherAvatarBg = (FrameLayout) ViewBindings.findChildViewById(rootView, id);
        if (msgOtherAvatarBg != null) {
            id = R.id.msg_other_avatar_letter;
            TextView msgOtherAvatarLetter = (TextView) ViewBindings.findChildViewById(rootView, id);
            if (msgOtherAvatarLetter != null) {
                id = R.id.msg_other_layout;
                LinearLayout msgOtherLayout = (LinearLayout) ViewBindings.findChildViewById(rootView, id);
                if (msgOtherLayout != null) {
                    id = R.id.msg_other_name;
                    TextView msgOtherName = (TextView) ViewBindings.findChildViewById(rootView, id);
                    if (msgOtherName != null) {
                        id = R.id.msg_other_text;
                        TextView msgOtherText = (TextView) ViewBindings.findChildViewById(rootView, id);
                        if (msgOtherText != null) {
                            id = R.id.msg_self_layout;
                            LinearLayout msgSelfLayout = (LinearLayout) ViewBindings.findChildViewById(rootView, id);
                            if (msgSelfLayout != null) {
                                id = R.id.msg_self_text;
                                TextView msgSelfText = (TextView) ViewBindings.findChildViewById(rootView, id);
                                if (msgSelfText != null) {
                                    id = R.id.msg_self_time;
                                    TextView msgSelfTime = (TextView) ViewBindings.findChildViewById(rootView, id);
                                    if (msgSelfTime != null) {
                                        return new ItemChatMessageBinding((LinearLayout) rootView, msgOtherAvatarBg, msgOtherAvatarLetter, msgOtherLayout, msgOtherName, msgOtherText, msgSelfLayout, msgSelfText, msgSelfTime);
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

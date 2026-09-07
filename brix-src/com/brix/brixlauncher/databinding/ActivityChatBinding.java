package com.brix.brixlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.brix.brixlauncher.R;

/* JADX INFO: loaded from: classes18.dex */
public final class ActivityChatBinding implements ViewBinding {
    public final TextView btnBack;
    public final Button btnSend;
    public final EditText etMessage;
    public final ListView lvMessages;
    private final LinearLayout rootView;
    public final TextView tvChatTitle;

    private ActivityChatBinding(LinearLayout rootView, TextView btnBack, Button btnSend, EditText etMessage, ListView lvMessages, TextView tvChatTitle) {
        this.rootView = rootView;
        this.btnBack = btnBack;
        this.btnSend = btnSend;
        this.etMessage = etMessage;
        this.lvMessages = lvMessages;
        this.tvChatTitle = tvChatTitle;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityChatBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityChatBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_chat, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityChatBinding bind(View rootView) {
        int id = R.id.btn_back;
        TextView btnBack = (TextView) ViewBindings.findChildViewById(rootView, id);
        if (btnBack != null) {
            id = R.id.btn_send;
            Button btnSend = (Button) ViewBindings.findChildViewById(rootView, id);
            if (btnSend != null) {
                id = R.id.et_message;
                EditText etMessage = (EditText) ViewBindings.findChildViewById(rootView, id);
                if (etMessage != null) {
                    id = R.id.lv_messages;
                    ListView lvMessages = (ListView) ViewBindings.findChildViewById(rootView, id);
                    if (lvMessages != null) {
                        id = R.id.tv_chat_title;
                        TextView tvChatTitle = (TextView) ViewBindings.findChildViewById(rootView, id);
                        if (tvChatTitle != null) {
                            return new ActivityChatBinding((LinearLayout) rootView, btnBack, btnSend, etMessage, lvMessages, tvChatTitle);
                        }
                    }
                }
            }
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}

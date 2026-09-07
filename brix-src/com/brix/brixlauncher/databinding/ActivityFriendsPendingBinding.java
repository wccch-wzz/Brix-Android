package com.brix.brixlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.brix.brixlauncher.R;

/* JADX INFO: loaded from: classes18.dex */
public final class ActivityFriendsPendingBinding implements ViewBinding {
    public final TextView btnBack;
    public final ListView lvPending;
    private final LinearLayout rootView;
    public final TextView tvEmptyPending;

    private ActivityFriendsPendingBinding(LinearLayout rootView, TextView btnBack, ListView lvPending, TextView tvEmptyPending) {
        this.rootView = rootView;
        this.btnBack = btnBack;
        this.lvPending = lvPending;
        this.tvEmptyPending = tvEmptyPending;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityFriendsPendingBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityFriendsPendingBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_friends_pending, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityFriendsPendingBinding bind(View rootView) {
        int id = R.id.btn_back;
        TextView btnBack = (TextView) ViewBindings.findChildViewById(rootView, id);
        if (btnBack != null) {
            id = R.id.lv_pending;
            ListView lvPending = (ListView) ViewBindings.findChildViewById(rootView, id);
            if (lvPending != null) {
                id = R.id.tv_empty_pending;
                TextView tvEmptyPending = (TextView) ViewBindings.findChildViewById(rootView, id);
                if (tvEmptyPending != null) {
                    return new ActivityFriendsPendingBinding((LinearLayout) rootView, btnBack, lvPending, tvEmptyPending);
                }
            }
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}

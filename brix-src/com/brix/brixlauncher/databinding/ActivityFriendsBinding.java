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
public final class ActivityFriendsBinding implements ViewBinding {
    public final Button btnAddFriend;
    public final TextView btnBack;
    public final Button btnPendingRequests;
    public final EditText etFriendId;
    public final LinearLayout friendListContainer;
    public final ListView lvFriends;
    private final LinearLayout rootView;
    public final TextView tvEmptyFriends;

    private ActivityFriendsBinding(LinearLayout rootView, Button btnAddFriend, TextView btnBack, Button btnPendingRequests, EditText etFriendId, LinearLayout friendListContainer, ListView lvFriends, TextView tvEmptyFriends) {
        this.rootView = rootView;
        this.btnAddFriend = btnAddFriend;
        this.btnBack = btnBack;
        this.btnPendingRequests = btnPendingRequests;
        this.etFriendId = etFriendId;
        this.friendListContainer = friendListContainer;
        this.lvFriends = lvFriends;
        this.tvEmptyFriends = tvEmptyFriends;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityFriendsBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityFriendsBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_friends, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityFriendsBinding bind(View rootView) {
        int id = R.id.btn_add_friend;
        Button btnAddFriend = (Button) ViewBindings.findChildViewById(rootView, id);
        if (btnAddFriend != null) {
            id = R.id.btn_back;
            TextView btnBack = (TextView) ViewBindings.findChildViewById(rootView, id);
            if (btnBack != null) {
                id = R.id.btn_pending_requests;
                Button btnPendingRequests = (Button) ViewBindings.findChildViewById(rootView, id);
                if (btnPendingRequests != null) {
                    id = R.id.et_friend_id;
                    EditText etFriendId = (EditText) ViewBindings.findChildViewById(rootView, id);
                    if (etFriendId != null) {
                        id = R.id.friend_list_container;
                        LinearLayout friendListContainer = (LinearLayout) ViewBindings.findChildViewById(rootView, id);
                        if (friendListContainer != null) {
                            id = R.id.lv_friends;
                            ListView lvFriends = (ListView) ViewBindings.findChildViewById(rootView, id);
                            if (lvFriends != null) {
                                id = R.id.tv_empty_friends;
                                TextView tvEmptyFriends = (TextView) ViewBindings.findChildViewById(rootView, id);
                                if (tvEmptyFriends != null) {
                                    return new ActivityFriendsBinding((LinearLayout) rootView, btnAddFriend, btnBack, btnPendingRequests, etFriendId, friendListContainer, lvFriends, tvEmptyFriends);
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

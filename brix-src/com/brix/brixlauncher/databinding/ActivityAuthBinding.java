package com.brix.brixlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.brix.brixlauncher.R;

/* JADX INFO: loaded from: classes18.dex */
public final class ActivityAuthBinding implements ViewBinding {
    public final Button btnOpenBrowser;
    private final FrameLayout rootView;

    private ActivityAuthBinding(FrameLayout rootView, Button btnOpenBrowser) {
        this.rootView = rootView;
        this.btnOpenBrowser = btnOpenBrowser;
    }

    @Override // androidx.viewbinding.ViewBinding
    public FrameLayout getRoot() {
        return this.rootView;
    }

    public static ActivityAuthBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityAuthBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_auth, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityAuthBinding bind(View rootView) {
        int id = R.id.btn_open_browser;
        Button btnOpenBrowser = (Button) ViewBindings.findChildViewById(rootView, id);
        if (btnOpenBrowser != null) {
            return new ActivityAuthBinding((FrameLayout) rootView, btnOpenBrowser);
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}

package com.brixcore.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import androidx.core.app.NotificationCompat;
import com.brixcore.BrixConfig;
import com.brixcore.R;
import com.brixcore.data.Renderer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* JADX INFO: compiled from: ProcessService.kt */
/* JADX INFO: loaded from: classes14.dex */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J \u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0016J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u0010\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\tH\u0002J\b\u0010\u0014\u001a\u00020\u000fH\u0016J\b\u0010\u0015\u001a\u00020\u000fH\u0002J\b\u0010\u0016\u001a\u00020\u0017H\u0002R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/brixcore/download/ProcessService;", "Landroid/app/Service;", "<init>", "()V", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onStartCommand", "", "flags", "startId", "firstLog", "", "startProcess", "", "config", "Lcom/brixcore/BrixConfig;", "sendCode", "code", "onDestroy", "createNotificationChannel", "buildNotification", "Landroid/app/Notification;", "Companion", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ProcessService extends Service {
    public static final int PROCESS_SERVICE_PORT = 29118;
    private boolean firstLog = true;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intrinsics.checkNotNullParameter(intent, "intent");
        createNotificationChannel();
        startForeground(1, buildNotification());
        Bundle extras = intent.getExtras();
        Intrinsics.checkNotNull(extras);
        String[] command = extras.getStringArray("command");
        Bundle extras2 = intent.getExtras();
        Intrinsics.checkNotNull(extras2);
        int java = extras2.getInt("java");
        String jre = "jre" + java;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        BrixConfig config = new BrixConfig(applicationContext, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brix/log", getApplicationContext().getDir("runtime", 0).getAbsolutePath() + "/java/" + jre, getApplicationContext().getCacheDir().toString() + "/BrixLauncher", new Renderer("Holy-GL4ES", "", "libgl4es_114.so", "libEGL.so", "", null, null, Renderer.ID_GL4ES, "", ""), command == null ? new String[0] : command);
        startProcess(config);
        return 2;
    }

    public final void startProcess(BrixConfig config) {
        Intrinsics.checkNotNullParameter(config, "config");
    }

    private final void sendCode(int code) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(new InetSocketAddress("127.0.0.1", PROCESS_SERVICE_PORT));
            byte[] data = new StringBuilder().append(code).toString().getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(data, "getBytes(...)");
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

    private final void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("Brix_process", "Brix Process", 4);
        Object systemService = getSystemService("notification");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
        ((NotificationManager) systemService).createNotificationChannel(channel);
    }

    private final Notification buildNotification() {
        Notification notificationBuild = new NotificationCompat.Builder(this, "Brix_process").setContentTitle(getString(R.string.notification_title)).setSmallIcon(R.mipmap.ic_launcher).setPriority(1).build();
        Intrinsics.checkNotNullExpressionValue(notificationBuild, "build(...)");
        return notificationBuild;
    }
}

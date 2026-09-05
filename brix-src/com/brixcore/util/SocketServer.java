package com.brixcore.util;

import com.brixcore.task.Schedulers;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes11.dex */
public class SocketServer {
    private final String ip;
    private boolean isReceiving = false;
    private final Listener listener;
    private DatagramPacket packet;
    private final int port;
    private Object result;
    private DatagramSocket socket;

    public interface Listener {
        void onReceive(SocketServer socketServer, String str);
    }

    public SocketServer(final String ip, final int port, Listener listener) {
        this.listener = listener;
        this.ip = ip;
        this.port = port;
        Schedulers.androidUIThread().execute(new Runnable() { // from class: com.brixcore.util.SocketServer$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0(port, ip);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int port, String ip) {
        byte[] bytes = new byte[1024];
        this.packet = new DatagramPacket(bytes, bytes.length);
        try {
            this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
            Logging.LOG.log(Level.INFO, "Socket server init!");
        } catch (SocketException | UnknownHostException e) {
            Logging.LOG.log(Level.WARNING, "Failed to init socket server", (Throwable) e);
        }
    }

    public DatagramPacket getPacket() {
        return this.packet;
    }

    public DatagramSocket getSocket() {
        return this.socket;
    }

    public Listener getListener() {
        return this.listener;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public void start() {
        Schedulers.androidUIThread().execute(new Runnable() { // from class: com.brixcore.util.SocketServer$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$start$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$start$2() {
        if (this.packet == null || this.socket == null) {
            return;
        }
        Logging.LOG.log(Level.INFO, "Socket server " + this.ip + ":" + this.port + " start!");
        this.isReceiving = true;
        new Thread(new Runnable() { // from class: com.brixcore.util.SocketServer$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$start$1();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$start$1() {
        while (this.isReceiving) {
            try {
                this.socket.receive(this.packet);
                String receiveMsg = new String(this.packet.getData(), 0, this.packet.getLength());
                this.listener.onReceive(this, receiveMsg);
            } catch (IOException e) {
                e.printStackTrace();
                Logging.LOG.log(Level.INFO, "Socket server " + this.ip + ":" + this.port + " start!");
            }
        }
    }

    public void send(String msg) throws IOException {
        this.socket.connect(new InetSocketAddress(this.ip, this.port));
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length);
        this.socket.send(packet);
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return this.result;
    }

    public void stop() {
        this.isReceiving = false;
        this.socket.close();
        Logging.LOG.log(Level.INFO, "Socket server " + this.ip + ":" + this.port + " stopped!");
    }
}

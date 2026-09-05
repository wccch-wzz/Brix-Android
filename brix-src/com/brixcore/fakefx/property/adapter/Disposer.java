package com.brixcore.fakefx.property.adapter;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes7.dex */
public class Disposer implements Runnable {
    private static final ReferenceQueue queue = new ReferenceQueue();
    private static final Map<Object, Runnable> records = new ConcurrentHashMap();
    private static Disposer disposerInstance = new Disposer();

    public static void addRecord(Object target, Runnable rec) {
        PhantomReference ref = new PhantomReference(target, queue);
        records.put(ref, rec);
    }

    /* JADX INFO: Infinite loop detected, blocks: 8, insns: 0 */
    @Override // java.lang.Runnable
    public void run() {
        while (true) {
            try {
                Object obj = queue.remove();
                ((Reference) obj).clear();
                Runnable rec = records.remove(obj);
                rec.run();
            } catch (Exception e) {
                System.out.println("Exception while removing reference: " + e);
                e.printStackTrace();
            }
        }
    }
}

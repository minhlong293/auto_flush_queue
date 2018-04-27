package demo;

import longhm.auto_flush_queue.AutoFlushQueue;
import longhm.auto_flush_queue.IQueueWorker;

public class DemoAutoFlushQueue {
    public static void main(String[] args) {
        IQueueWorker<String> worker = qc -> {
            for (String str : qc) {
                System.out.println(str);
            }

        };
        AutoFlushQueue<String> autoFlushQueue = new AutoFlushQueue<>(4, 1000, 10000, 100, worker);
        for (int i = 0; i < 90; i++) {
            autoFlushQueue.add("s " + i);
        }
    }
}

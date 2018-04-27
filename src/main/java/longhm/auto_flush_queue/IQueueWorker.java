package longhm.auto_flush_queue;

import java.util.List;

/**
 * Created by longhm on 4/20/16.
 */
public interface IQueueWorker<T> {
    /**
     * Called when the queue flush
     * @param qc list object from queue
     */
    void doJob(List<T> qc);
}

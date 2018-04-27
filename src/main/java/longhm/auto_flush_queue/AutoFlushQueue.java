package longhm.auto_flush_queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by longhm on 4/20/16.
 *
 * AutoFlushQueue is a thread pool which can do a batch job.
 * Thread pool size, queue capacity, batch size can be set.
 *
 */
public class AutoFlushQueue<T> {
    private static final long AUTO_FLUSH_DELAY = 2000;
    public static final int CORE_POOL_SIZE = 1;
    public static final int THREAD_POOL_SIZE = 16;
    private static final int THREAD_QUEUE_CAPACITY = 1000000;
    private final Runnable command;
    private final ScheduledThreadPoolExecutor stp;
    private int nBatch = 1000;
    private ArrayList<T> q = new ArrayList<>();
    private IQueueWorker<T> worker;
    private ExecutorService executorService;
    private ScheduledFuture<?> sf;
    private long autoFlushDelay = AUTO_FLUSH_DELAY;
    private int threadPoolSize = THREAD_POOL_SIZE;
    private int threadQueueCapacity = THREAD_QUEUE_CAPACITY;

    /**
     * Default:<br>
     * - thread pool size = {@link AutoFlushQueue#THREAD_POOL_SIZE}<br>
     * - nBatch = {@link AutoFlushQueue#nBatch}<br>
     * - auto flush delay = {@link AutoFlushQueue#AUTO_FLUSH_DELAY}
     */
    public AutoFlushQueue(IQueueWorker<T> worker) {
        command = this::flush;
        stp = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        initExecutorService();

        this.setWorker(worker);
    }

    private void initExecutorService() {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>(this.threadQueueCapacity);
        this.executorService = new ThreadPoolExecutor(
                this.threadPoolSize,
                this.threadPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                workQueue
        );
    }

    /**
     * Custom AutoFlushQueue
     *
     * @param threadPoolSize
     * @param autoFlushDelay
     * @param threadQueueCapacity
     * @param nBatch
     */
    public AutoFlushQueue(int threadPoolSize, long autoFlushDelay, int threadQueueCapacity, int nBatch, IQueueWorker<T> worker) {
        command = this::flush;
        stp = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        this.nBatch = nBatch;
        this.autoFlushDelay = autoFlushDelay;
        this.threadPoolSize = threadPoolSize;
        this.threadQueueCapacity = threadQueueCapacity;
        initExecutorService();
        this.setWorker(worker);
    }

    public void setWorker(IQueueWorker<T> worker) {
        if (worker == null) throw new NullPointerException("Updater must not null!!");
        this.worker = worker;
    }

    public synchronized void add(T e) {
        if (q.size() == 0) {
            sf = stp.schedule(command, autoFlushDelay, TimeUnit.MILLISECONDS);
        }
        q.add(e);
        if (q.size() >= nBatch) {
            sf.cancel(true);
            flush();
        }
    }

    private synchronized void flush() {
        if (q.size() > 0) {
            final List<T> qc = new ArrayList<>(q);
            q.clear();
            executorService.execute(() -> worker.doJob(qc));
        }
    }
}
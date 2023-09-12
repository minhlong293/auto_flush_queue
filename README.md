AutoFlushQueue is a thread pool which can do a batch job. It can do an auto flush if size of job queue is less than batch size after a delay time.
Thread pool size, queue capacity, batch size can be config.

###### Example (see DemoAutoFlushQueue class):
```
        IQueueWorker<String> worker = qc -> {
            for (String str : qc) {
                System.out.println(str);
            }

        };
        AutoFlushQueue<String> autoFlushQueue = new AutoFlushQueue<>(
                             4, // threadPoolSize
                             1000, // flush delay in milliseconds
                             10000, // queue capacity
                             100, // batch size
                             worker // worker which do the batch job
        );
        .....
            autoFlushQueue.add(string);
        ....
```

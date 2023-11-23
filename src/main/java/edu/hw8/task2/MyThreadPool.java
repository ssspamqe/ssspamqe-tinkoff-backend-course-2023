package edu.hw8.task2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MyThreadPool implements ThreadPool {

    private final static Logger LOGGER = LogManager.getLogger();

    private final BlockingQueue<Runnable> taskQueue;
    private final List<Worker> workers;
    private volatile boolean closing = false;

    public MyThreadPool(int poolSize) {
        taskQueue = new LinkedBlockingQueue<>();

        workers = new ArrayList<>();
        for (int i = 0; i < poolSize; i++) {
            workers.add(new Worker());
            workers.getLast().start();
        }
    }

    @Override
    public void start() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void execute(Runnable newTask) {
        if(closing){
            throw new RuntimeException("Thread pool is closing now...");
        }
        taskQueue.add(newTask);
    }

    @Override
    public void close() throws Exception {
        closing = true;
        workers.forEach(it -> {
            try {
                it.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private class Worker extends Thread {
        public void run() {
            while (!closing || !taskQueue.isEmpty()) {
                Runnable task;
                try {
                    task = taskQueue.take();
                } catch (InterruptedException e) {
                    LOGGER.warn("Interrupted");
                    continue;
                }
                task.run();
            }
        }
    }
}

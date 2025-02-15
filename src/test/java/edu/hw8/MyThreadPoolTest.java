package edu.hw8;

import edu.hw8.task2.MyThreadPool;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MyThreadPoolTest {

    @Test
    @DisplayName("Thead pool should be able to execute tasks asynchronously (for example increment atomic integer)")
    void threadPool_should_executeTasksAsynchronously() {
        var atomicInteger = new AtomicInteger();
        int threadPoolSize = 5;
        int increment = 20;
        int sleepTime = 500;

        try (MyThreadPool threadPool = MyThreadPool.create(threadPoolSize)) {
            for (int i = 0; i < increment; i++) {
                threadPool.execute(() -> {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    atomicInteger.getAndIncrement();
                });
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        assertThat(atomicInteger.get()).isEqualTo(increment);
    }

}

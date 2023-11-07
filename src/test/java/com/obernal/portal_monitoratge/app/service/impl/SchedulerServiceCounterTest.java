package com.obernal.portal_monitoratge.app.service.impl;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.LocalDateTime;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulerServiceCounterTest {
    private int count = 0;
    private SchedulerService scheduler;

    @BeforeEach
    void setUp() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix(getClass().getPackageName());
        threadPoolTaskScheduler.initialize();
        scheduler = new SchedulerService(threadPoolTaskScheduler);
    }

    @DisplayName("Schedule a counter, verify that counts, then cancel the scheduled event and verify that it has stop counting")
    @Test
    void scheduleAndCancel() {
        assertEquals(0, count);
        scheduler.schedule("COUNTER", "* * * * * *", () -> this.count += 1);
        Awaitility.await().atMost(2, SECONDS).until(() -> count > 0);
        Assertions.assertTrue(count > 0);
        scheduler.cancel("COUNTER");
        int value = count;
        Awaitility.await().atMost(2, SECONDS).until(() -> count > 0);
        assertEquals(count, value);
    }

    @DisplayName("Schedule a task every 5 secs and block main thread for 20 secs")
    @Test
    //@Disabled
    void example() throws InterruptedException {
        // Programem una tasca
        scheduler.schedule(
                "ID", // identificador �nic
                "*/5 * * * * *", // Cada 5 segons
                () -> System.out.println(LocalDateTime.now() + " Hello scheduled world!") // tasca a executar
        );

        // Aturem el fil principal 60 segons
        Thread.sleep(20000);

        // Aturem la tasca programada
        System.out.println("Stop schedule!");
        scheduler.cancel("ID");
        Thread.sleep(20000);

    }

}

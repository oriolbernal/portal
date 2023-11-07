package com.obernal.portal_monitoratge.app.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchedulerServiceTest {

    private SchedulerService schedulerService;

    private TaskScheduler taskScheduler;
    private ScheduledFuture scheduledFuture;

    @BeforeEach
    void setUp() {
        taskScheduler = mock(TaskScheduler.class);
        scheduledFuture = mock(ScheduledFuture.class);
        schedulerService = new SchedulerService(taskScheduler);
    }

    @Test
    void testIsScheduledWhenScheduled() {
        when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule("test_id", "0 0 * * * ?", () -> {});
        assertTrue(schedulerService.isScheduled("test_id"));
    }

    @Test
    void testIsScheduledWhenNotScheduled() {
        assertFalse(schedulerService.isScheduled("non_existent_id"));
    }

    @Test
    void testScheduleWhenScheduled() {
        when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule("test_id", "0 0 * * * ?", () -> {});
        schedulerService.schedule("test_id", "0 0 0 * * ?", () -> {});
        assertTrue(schedulerService.isScheduled("test_id"));
    }

    @Test
    void testScheduleWhenNotScheduled() {
        when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule("test_id", "0 0 * * * ?", () -> {});
        assertTrue(schedulerService.isScheduled("test_id"));
    }

    @Test
    void testCancelWhenScheduled() {
        when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule("test_id", "0 0 * * * ?", () -> {});
        schedulerService.cancel("test_id");
        assertFalse(schedulerService.isScheduled("test_id"));
    }

    @Test
    void testCancelWhenNotScheduled() {
        schedulerService.cancel("non_existent_id");
        assertFalse(schedulerService.isScheduled("non_existent_id"));
    }

    @Test
    void testGetNextExecutionTime() {
        when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(scheduledFuture);
        when(scheduledFuture.getDelay(any(TimeUnit.class))).thenReturn(1000L);
        schedulerService.schedule("test_id", "0 0 * * * ?", () -> {});
        LocalDateTime nextExecutionTime = schedulerService.getNextExecutionTime("test_id").get();
        LocalDateTime expectedTime = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
        assertEquals(expectedTime.toLocalDate(), nextExecutionTime.toLocalDate());
    }

    @Test
    void testGetNextExecutionTimeWhenNotScheduled() {
        Optional<LocalDateTime> nextExecutionTime = schedulerService.getNextExecutionTime("non_existent_id");
        assertFalse(nextExecutionTime.isPresent());
    }

}
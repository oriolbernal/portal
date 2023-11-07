package com.obernal.portal_monitoratge.app.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

    private final Map<String, ScheduledFuture<?>> schedulers = new HashMap<>();
    private final TaskScheduler taskScheduler;

    public SchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public boolean isScheduled(String id) {
        return schedulers.containsKey(id) && schedulers.get(id) != null;
    }

    public void schedule(String id, String cron, Runnable runnable) {
        if (isScheduled(id)) {
            cancel(id);
        }
        Trigger trigger = new CronTrigger(cron);
        ScheduledFuture<?> job = taskScheduler.schedule(runnable, trigger);
        schedulers.put(id, job);
    }

    public void cancel(String id) {
        if (isScheduled(id)) {
            logger.info(id + " was already scheduled, it will be removed");
            schedulers.get(id).cancel(false);
        }
        schedulers.remove(id);
    }

    public Optional<LocalDateTime> getNextExecutionTime(String id) {
        return getDelay(id, TimeUnit.MILLISECONDS)
                .map(delay -> LocalDateTime.now().plus(delay, ChronoUnit.MILLIS));
    }

    private Optional<Long> getDelay(String id, TimeUnit unit) {
        return get(id).map(task -> task.getDelay(unit));
    }

    private Optional<ScheduledFuture<?>> get(String id) {
        if(isScheduled(id)) {
            return Optional.of(schedulers.get(id));
        }
        return Optional.empty();
    }

}

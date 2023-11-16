package com.obernal.portal_monitoratge.app;

import com.obernal.portal_monitoratge.app.service.impl.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ApplicationConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);
    private static final String DEFAULT_POOL_SIZE = "10";

    @Autowired
    private Environment env;

    @Bean
    public SchedulerService schedulerService() {
        int poolSize = Integer.parseInt(env.getProperty("scheduler.poolSize", DEFAULT_POOL_SIZE));
        logger.info("Creating scheduler with a thread pool size of: " + poolSize);
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setThreadNamePrefix(getClass().getPackageName());
        threadPoolTaskScheduler.initialize();
        return new SchedulerService(threadPoolTaskScheduler);
    }

}

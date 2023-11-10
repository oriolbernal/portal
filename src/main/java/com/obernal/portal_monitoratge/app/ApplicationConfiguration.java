package com.obernal.portal_monitoratge.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    public Object example() {
        return new Object();
    }

}

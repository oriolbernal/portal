package com.obernal.portal_monitoratge.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PortalMonitoratgeApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(PortalMonitoratgeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PortalMonitoratgeApplication.class, args);
	}

	@Override
	public void run(String... args) {
		logger.info("Running app!");
	}
}

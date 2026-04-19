package es.viewnext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebClientBlockingApplication {

	private static final Logger logger = LoggerFactory.getLogger(WebClientBlockingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WebClientBlockingApplication.class, args);
	}

	@Value("${server.tomcat.threads.max:200}")
	private int maxTomcatThreads;

	@Bean
	ApplicationRunner logThreadInfo() {
		return args -> {
			logger.info("=== Thread Info ===");
			logger.info("Available processors (cores): {}", Runtime.getRuntime().availableProcessors());
			logger.info("Max Tomcat threads:           {}", maxTomcatThreads);
			logger.info("Active threads on startup:    {}", Thread.activeCount());
			logger.info("===================");
		};
	}

}
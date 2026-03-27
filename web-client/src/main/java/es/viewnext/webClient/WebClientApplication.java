package es.viewnext.webClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "es.viewnext")
public class WebClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(Scaffolding1Application.class, args);
	}

}
package es.viewnext.scaffolding1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "es.viewnext")
public class Scaffolding1Application {

	public static void main(String[] args) {
		SpringApplication.run(Scaffolding1Application.class, args);
	}

}
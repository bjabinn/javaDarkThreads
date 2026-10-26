package es.viewnext.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Client-VirtualThreads")
                        .version("1.0.0")
                        .description("API para pruebas de rendimiento con Virtual Threads y OpenTelemetry")
                        .contact(new Contact()
                                .name("Viewnext")
                                .email("info@viewnext.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8095")
                                .description("Local server"),
                        new Server()
                                .url("http://localhost:8095")
                                .description("Docker container")
                ));
    }
}
package es.viewnext.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class RootController {

    @GetMapping("/")
    public Mono<Void> redirectToSwagger(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create("/swagger-ui.html"));
        return response.setComplete();
    }
}
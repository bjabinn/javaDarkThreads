package es.viewnext.controller;

import es.viewnext.dto.GreetingResponse;
import es.viewnext.services.GreetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "Greeting", description = "Endpoints de saludo para Codemotion")
public class GreetingController {
    
    private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);
    
    private final GreetingService greetingService;
    
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
    
    @GetMapping("/greeting-reactive")
    @Operation(
        summary = "Obtener saludo de Codemotion (Reactivo)",
        description = "Devuelve un mensaje 'Hola Codemotion' con el timestamp actual de forma reactiva con delay de 2 segundos"
    )
    @ApiResponse(responseCode = "200", description = "Saludo generado exitosamente")
    public Mono<GreetingResponse> getGreetingReactive() {
        logger.info("Recibida petición de saludo reactivo");
        
        return greetingService.generateGreetingReactive()
                              .doOnNext(response -> logger.info("Respuesta reactiva generada: {}", response));
    }
}
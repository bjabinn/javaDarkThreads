package es.viewnext.services;

import es.viewnext.dto.GreetingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GreetingService {
    
    private static final Logger logger = LoggerFactory.getLogger(GreetingService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * Genera un saludo para Codemotion con el timestamp actual
     * 
     * @return GreetingResponse con el mensaje y timestamp en formato YYYYMMDDHHmmss
     */
    public GreetingResponse generateGreeting() {
        String message = "Hola Codemotion";
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        return new GreetingResponse(message, timestamp);
    }
    
    /**
     * Genera un saludo para Codemotion de forma reactiva con delay de 2 segundos
     * 
     * @return Mono<GreetingResponse> con el mensaje y timestamp en formato YYYYMMDDHHmmss
     */
    public Mono<GreetingResponse> generateGreetingReactive() {
        return Mono.delay(Duration.ofSeconds(2))
                   .map(tick -> {
                       String message = "Hola Codemotion";
                       String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
                       return new GreetingResponse(message, timestamp);
                   });
    }
}

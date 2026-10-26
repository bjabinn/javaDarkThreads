package es.viewnext.controller;

import es.viewnext.dto.LoadRequest;
import es.viewnext.dto.LoadResponse;
import es.viewnext.services.VirtualThreadLoadGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Load Testing", description = "Endpoints para ejecutar pruebas de carga y obtener información del sistema")
public class LoadController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadController.class);
    
    private final VirtualThreadLoadGeneratorService loadGeneratorService;
    
    public LoadController(VirtualThreadLoadGeneratorService loadGeneratorService) {
        this.loadGeneratorService = loadGeneratorService;
    }
    
    @PostMapping("/load")
    @Operation(summary = "Ejecutar prueba de carga", 
               description = "Ejecuta una prueba de carga con los parámetros especificados usando Virtual Threads")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prueba ejecutada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno al ejecutar la prueba")
    })
    public ResponseEntity<LoadResponse> executeLoad(@RequestBody LoadRequest request) {
        logger.info("Received load request: {}", request);
        
        try {
            LoadResponse response = loadGeneratorService.executeLoad(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error executing load test", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Estado de salud de la aplicación", 
               description = "Obtiene información sobre el estado de salud de la aplicación y métricas de threads")
    @ApiResponse(responseCode = "200", description = "Estado de salud obtenido correctamente")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "virtual-threads-poc");
        health.put("threadType", "virtual");
        health.put("activeThreads", Thread.activeCount());
        health.put("totalThreads", Thread.getAllStackTraces().size());
        health.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/info")
    @Operation(summary = "Información de la aplicación", 
               description = "Obtiene información general sobre la aplicación, versión de Java y memoria")
    @ApiResponse(responseCode = "200", description = "Información obtenida correctamente")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "virtual-threads-poc");
        info.put("description", "PoC for Virtual Threads performance testing");
        info.put("threadType", "virtual");
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("runtime", System.getProperty("java.runtime.name"));
        
        Map<String, Object> memory = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        memory.put("freeMB", runtime.freeMemory() / 1024 / 1024);
        memory.put("totalMB", runtime.totalMemory() / 1024 / 1024);
        memory.put("maxMB", runtime.maxMemory() / 1024 / 1024);
        info.put("memory", memory);
        
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/threads")
    @Operation(summary = "Información de threads", 
               description = "Obtiene información detallada sobre los threads activos en el sistema")
    @ApiResponse(responseCode = "200", description = "Información de threads obtenida correctamente")
    public ResponseEntity<Map<String, Object>> threadsInfo() {
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put("activeThreads", Thread.activeCount());
        threadInfo.put("totalThreads", Thread.getAllStackTraces().size());
        threadInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        threadInfo.put("threadType", "virtual");
        
        return ResponseEntity.ok(threadInfo);
    }
}
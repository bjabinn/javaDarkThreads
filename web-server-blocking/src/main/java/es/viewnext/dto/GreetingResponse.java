package es.viewnext.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del saludo de Codemotion")
public class GreetingResponse {
    
    @Schema(description = "Mensaje de saludo", example = "Hola Codemotion")
    private String message;
    
    @Schema(description = "Timestamp en formato YYYYMMDDHHmmss", example = "20260327202648")
    private String timestamp;
    
    public GreetingResponse() {
    }
    
    public GreetingResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "GreetingResponse{" +
                "message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
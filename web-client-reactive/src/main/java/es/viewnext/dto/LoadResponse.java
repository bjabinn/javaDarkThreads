package es.viewnext.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class LoadResponse {
    
    private int totalRequests;
    private int concurrencyLevel;
    private long totalDurationMs;
    private double throughput; // requests per second
    private double avgResponseTimeMs;
    private long minResponseTimeMs;
    private long maxResponseTimeMs;
    private int successCount;
    private int failureCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Object> additionalMetrics;
    
    public LoadResponse() {
    }
    
    // Getters and Setters
    
    public int getTotalRequests() {
        return totalRequests;
    }
    
    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }
    
    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public void setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
    }
    
    public long getTotalDurationMs() {
        return totalDurationMs;
    }
    
    public void setTotalDurationMs(long totalDurationMs) {
        this.totalDurationMs = totalDurationMs;
    }
    
    public double getThroughput() {
        return throughput;
    }
    
    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }
    
    public double getAvgResponseTimeMs() {
        return avgResponseTimeMs;
    }
    
    public void setAvgResponseTimeMs(double avgResponseTimeMs) {
        this.avgResponseTimeMs = avgResponseTimeMs;
    }
    
    public long getMinResponseTimeMs() {
        return minResponseTimeMs;
    }
    
    public void setMinResponseTimeMs(long minResponseTimeMs) {
        this.minResponseTimeMs = minResponseTimeMs;
    }
    
    public long getMaxResponseTimeMs() {
        return maxResponseTimeMs;
    }
    
    public void setMaxResponseTimeMs(long maxResponseTimeMs) {
        this.maxResponseTimeMs = maxResponseTimeMs;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    
    public int getFailureCount() {
        return failureCount;
    }
    
    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Map<String, Object> getAdditionalMetrics() {
        return additionalMetrics;
    }
    
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) {
        this.additionalMetrics = additionalMetrics;
    }
}
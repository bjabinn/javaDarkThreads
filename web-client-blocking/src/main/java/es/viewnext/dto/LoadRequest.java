package es.viewnext.dto;

public class LoadRequest {
    
    private int requests = 100;
    private int threads = 10;
    private long delayMs = 500;
    private String targetUrl;
    
    public LoadRequest() {
    }
    
    public LoadRequest(int requests, int threads, long delayMs) {
        this.requests = requests;
        this.threads = threads;
        this.delayMs = delayMs;
    }
    
    // Getters and Setters
    
    public int getRequests() {
        return requests;
    }
    
    public void setRequests(int requests) {
        this.requests = requests;
    }
    
    public int getThreads() {
        return threads;
    }
    
    public void setThreads(int threads) {
        this.threads = threads;
    }
    
    public long getDelayMs() {
        return delayMs;
    }
    
    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }
    
    public String getTargetUrl() {
        return targetUrl;
    }
    
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    
    @Override
    public String toString() {
        return "LoadRequest{" +
                "requests=" + requests +
                ", threads=" + threads +
                ", delayMs=" + delayMs +
                ", targetUrl='" + targetUrl + '\'' +
                '}';
    }
}
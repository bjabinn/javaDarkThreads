package es.viewnext.dto;

public class LoadRequest {

    private int requests = 100;
    private int threads = 10;
    private String targetUrl;

    public LoadRequest() {
    }

    public LoadRequest(int requests, int threads) {
        this.requests = requests;
        this.threads = threads;
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
                ", targetUrl='" + targetUrl + '\'' +
                '}';
    }
}

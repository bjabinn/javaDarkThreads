package es.viewnext.dto;

public class LoadRequest {

    private int requests = 100;
    private int concurrency = 10;
    private String targetUrl;

    public LoadRequest() {
    }

    public LoadRequest(int requests, int concurrency) {
        this.requests = requests;
        this.concurrency = concurrency;
    }

    // Getters and Setters

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
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
                ", concurrency=" + concurrency +
                ", targetUrl='" + targetUrl + '\'' +
                '}';
    }
}

package es.viewnext.dto;

public class LoadRequest {

    private int requests = 100;
    private String targetUrl;

    public LoadRequest() {
    }

    public LoadRequest(int requests) {
        this.requests = requests;
    }

    // Getters and Setters

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
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
                ", targetUrl='" + targetUrl + '\'' +
                '}';
    }
}

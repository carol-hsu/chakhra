package com.example.statesaver.types;

public class RequestItem {
    private String requestId;
    private String request;
    private String lastHop;
    private String origin;

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) {this.requestId = requestId; }

    public String getRequest() {return request; }
    public void setRequest(String request) {this.request = request; }

    public String getLastHop() {return lastHop; }
    public void setLastHop(String lastHop) {this.lastHop = lastHop; }

    public String getOrigin() {return origin;}
    public void setOrigin(String origin) {this.origin = origin; }

    public RequestItem(String requestId, String request, String lastHop, String origin){
        this.requestId = requestId;
        this.request = request;
        this.lastHop = lastHop;
        this.origin = origin;
    }
}

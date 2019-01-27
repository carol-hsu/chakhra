package com.example.statesaver.types;

public class RequestItem {
    int requestId;
    private String request;
    private String lastHop;
    private String origin;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) {this.requestId = requestId; }

    public String getRequest() {return request; }
    public void setRequest(String request) {this.request = request; }

    public String getLastHop() {return lastHop; }
    public void setLastHop(String lastHop) {this.lastHop = lastHop; }

    public String getOrigin() {return origin;}
    public void setOrigin(String origin) {this.origin = origin; }
}

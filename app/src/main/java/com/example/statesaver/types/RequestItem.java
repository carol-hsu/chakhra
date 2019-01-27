package com.example.statesaver.types;

public class RequestItem {
    String requestId;
    String requestText;
    String lastHop;
    String origin;

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) {this.requestId = requestId; }

    public String getRequestText() {return requestText; }
    public void setRequestText(String requestText) {this.requestText = requestText; }

    public String getLastHop() {return lastHop; }
    public void setLastHop(String lastHop) {this.lastHop = lastHop; }

    public String getOrigin() {return origin;}
    public void setOrigin(String origin) {this.origin = origin; }
}

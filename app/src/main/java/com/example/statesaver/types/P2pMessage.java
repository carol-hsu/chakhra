package com.example.statesaver.types;

import java.io.Serializable;

public class P2pMessage implements Serializable {
    public enum Type {
        FILE,
        REQUEST,
        QUESTION,
        ANSWER
    }

    public Type type;

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getSearchRequest() {
        return searchRequest;
    }

    public void setSearchRequest(String searchRequest) {
        this.searchRequest = searchRequest;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    byte[] fileBytes;
    String searchRequest;
    String requestId;
    String filename;

    public P2pMessage(Type type, byte[] fileBytes, String filename, String requestId) {
        this.type = type;
        this.fileBytes = fileBytes;
    }

    public P2pMessage(Type type, String searchRequest, String requestId) {
        this.type = type;
        this.searchRequest = searchRequest;
        this.requestId = requestId;
    }
}

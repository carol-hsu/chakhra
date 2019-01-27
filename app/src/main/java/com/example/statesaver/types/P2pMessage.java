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
    byte[] fileBytes;
    String searchRequest;
    String requestId;
    String filename;

    public P2pMessage(Type type, byte[] fileBytes, String filename, String requestId) {
        this.type = type;
        this.fileBytes = fileBytes;
    }

    public P2pMessage(Type type, String searchRequest, String requestId) {

    }
}

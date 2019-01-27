package com.example.statesaver.utils;

public class IdManager {
    private static String id = null;
    public static void initialize(String userId) {
        id = userId;
    }

    public static String getId() {
        return id;
    }
}

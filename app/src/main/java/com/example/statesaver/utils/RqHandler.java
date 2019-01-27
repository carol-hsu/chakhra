package com.example.statesaver.utils;

import com.example.statesaver.types.RequestItem;
import java.util.List;
import android.content.Context;


public class RqHandler extends Thread {

    private DbHandler dbHandler;
    private boolean is_run = false;

    public RqHandler(Context context){
        is_run = true;
        dbHandler = DbHandler.getInstance(context);
    }

    public void run(){
        while(is_run) {
            try {
                sleep(3000);
                //check request DB
                List<RequestItem> requestList = dbHandler.getRequests();
                if(!requestList.isEmpty()) {
                    System.out.println("it has: "+requestList.size()+" items");
                }
            } catch (Exception e) {
                System.out.println("request handler: "+ e.toString());
            }
        }
    }
}

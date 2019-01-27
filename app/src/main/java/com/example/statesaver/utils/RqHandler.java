package com.example.statesaver.utils;

import com.example.statesaver.types.RequestItem;
import java.util.List;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class RqHandler extends Thread {

    private DbHandler dbHandler;
    private boolean is_run = false;
    private ConnectivityManager connectManager;

    public RqHandler(Context context){
        is_run = true;
        dbHandler = DbHandler.getInstance(context);
        connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private boolean hasInternet(){
        boolean is_connect = true;
        if(connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                != NetworkInfo.State.CONNECTED &&
                connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                        != NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            is_connect = false;
        }
        return is_connect;
    }

    public void run(){
        while(is_run) {
            try {
                sleep(3000);
                //check request DB
                List<RequestItem> requestList = dbHandler.getRequests();
                if(!requestList.isEmpty()) {
                    System.out.println("it has: "+requestList.size()+" items");
                    if(!hasInternet()){
                        System.out.println("No connect");
                    }
                }

            } catch (Exception e) {
                System.out.println("request handler: "+ e.toString());
            }
        }
    }
}

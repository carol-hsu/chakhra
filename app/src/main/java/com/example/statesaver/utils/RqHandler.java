package com.example.statesaver.utils;

import com.example.statesaver.MainActivity;
import com.example.statesaver.types.RequestItem;
import java.util.List;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class RqHandler extends Thread {

    private DbHandler dbHandler;
    private boolean is_run = false;
    private ConnectivityManager connectManager;
    private MainActivity mainActivity;

    public RqHandler(Context context, MainActivity mainActivity){
        is_run = true;
        this.mainActivity = mainActivity;
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
                    Log.d(MainActivity.TAG, "Search request recieved ...");
//                    System.out.println("it has: "+requestList.size()+" items");
                    Log.d(MainActivity.TAG, "Has internet : " + hasInternet());
                    if(!hasInternet()){
                        Log.d(MainActivity.TAG, "Need to request p2p");
                        this.mainActivity.sendRequestOverP2P(requestList.get(0));
                        dbHandler.insertOwnSearchRequestInDb("");
                    }else{
                        Log.d(MainActivity.TAG, "Have internet, must download the content");
                    }
                }else{
                    Log.d(MainActivity.TAG, "Thread is waiting ...");
                    
                }

            } catch (Exception e) {
                System.out.println("request handler: "+ e.toString());
            }
        }
    }
}

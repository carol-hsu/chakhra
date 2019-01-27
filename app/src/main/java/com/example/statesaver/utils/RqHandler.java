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

    private String[] dummy_run(String query){
        System.out.println("Get:" + query);
        String[] python_loop_urls = { "https://www.w3schools.com/python/python_for_loops.asp",
                "https://wiki.python.org/moin/ForLoop",
                "https://www.geeksforgeeks.org/loops-in-python/"};
        String[] java_class_urls = { "https://www.geeksforgeeks.org/classes-objects-java/",
                "https://www.w3schools.com/java/java_classes.asp",
                "https://www.programiz.com/java-programming/class-objects"};
        if(query.toLowerCase().contains("python"))
            return python_loop_urls;

        return java_class_urls;
    }

    private String[] dummy_dirs(String query){
        String[] python_dir = {"a", "b","c"};
        String[] java_dir = {"x", "y","z"};
        if(query.toLowerCase().contains("python"))
            return python_dir;

        return java_dir;
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
                    Log.d(MainActivity.TAG, "has internet : " + hasInternet());
                    if(!hasInternet()){
                        Log.d(MainActivity.TAG, "No connect");
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

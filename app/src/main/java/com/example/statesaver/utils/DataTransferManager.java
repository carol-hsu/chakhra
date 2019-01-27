package com.example.statesaver.utils;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DataTransferManager implements Runnable {
    private static final String TAG = "ChatHandler";

    private Socket socket = null;
    private final Handler handler;
    private boolean disable = false; //attribute to stop or start chatmanager
    private InputStream iStream;
    private OutputStream oStream;

    public DataTransferManager(Socket clientSocket, Handler handler) {
        this.handler = handler;
        this.socket = clientSocket;
    }

    /**
     * Method to execute the  Thread
     * To stop the execution, please use ".setDisable(true);".
     */
    @Override
    public void run() {
        Log.d(TAG,"DataTransfer manager started");
        try {
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[1024000];
            int bytes;

            handler.obtainMessage(Configuration.SET_MANAGER, this).sendToTarget();

            while (!disable) { //...if enabled
                try {
                    // Read from the InputStream
                    if(iStream!=null) {
                        bytes = iStream.read(buffer);
                        if (bytes == -1) {
                            break;
                        }

                        //this method's call is used to call handleMessage's case Configuration.MESSAGE_READ in the MainActivity.

                        handler.obtainMessage(Configuration.DATA_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG,"Exception : " + e.toString());
        } finally {
            try {
                iStream.close();
                socket.close();
            } catch (IOException e) {
                Log.e(TAG,"Exception during close socket or isStream",  e);
            }
        }
    }

    /**
     * Method to write a byte array (that can be a message) on the output stream.
     * @param buffer byte[] array that represents data to write. For example, a String converted in byte[] with ".getBytes();"
     */
    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

}

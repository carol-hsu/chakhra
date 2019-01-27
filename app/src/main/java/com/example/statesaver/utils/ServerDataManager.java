package com.example.statesaver.utils;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerDataManager extends Thread {
    private ServerSocket socket = null;
    private Handler handler;
    InetAddress ipAddress;
    DataTransferManager dataTransferManager = null;

    private static String TAG = "ServerDataManager";

    public DataTransferManager getDataTransferManager() {
        return dataTransferManager;
    }

    public ServerDataManager (Handler handler) throws IOException {
        try {
            socket = new ServerSocket(Configuration.SERVER_PORT);
            this.handler = handler;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            Log.e(TAG, "IOException during open ServerSockets with port "+Configuration.SERVER_PORT, e);
            throw e;
        }
    }

    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            Configuration.THREAD_COUNT, Configuration.THREAD_COUNT,
            Configuration.THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());


    /**
     * Method to close the group owner sockets and kill this entire thread.
     */
    public void closeSocketAndKillThisThread() {
        if(socket!=null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException during close Socket", e);
            }
            pool.shutdown();
        }
    }

    /**
     * Method to start the GroupOwnerSocketHandler.
     * Attention you can't stop this method, because there is a while(true) inside.
     */
    @Override
    public void run() {
        while (true) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
                if(socket!=null && !socket.isClosed()) {
                    Socket clientSocket = socket.accept(); //because now i'm connected with the client/peer device
                    dataTransferManager = new DataTransferManager(clientSocket, handler);
                    pool.execute(dataTransferManager);
                    ipAddress = clientSocket.getInetAddress();
                    Log.d(TAG, "Launching the I/O handler");
                }
            } catch (IOException e) {
                //if there is an exception, after closing socket and pool, the execution stops with a "break".
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, "IOException during close Socket", ioe);
                }
                pool.shutdownNow();
                break; //stop the while(true).
            }
        }
    }

}

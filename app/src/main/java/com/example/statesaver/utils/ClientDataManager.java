package com.example.statesaver.utils;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientDataManager extends Thread {
    private static String TAG = "ClientDataManager";
    boolean disable;
    private InputStream iStream;
    private OutputStream oStream;

    private final Handler handler;
    private final InetAddress mAddress; //this is the ip address, NOT THE MACADDRESS!!!
    private Socket socket;

    /**
     * Constructor of the class.
     *
     * @param handler           Represents the handler required in order to communicate
     * @param groupOwnerAddress Represents the ip address of the group owner of this client/peer
     */
    public ClientDataManager(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    /**
     *
     */
    @Override
    public void run() {
        DataTransferManager chat;
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    Configuration.SERVER_PORT), Configuration.CLIENT_TIMEOUT);
            Log.d(TAG, "Launching the I/O handler");
            chat = new DataTransferManager(socket, handler);
            new Thread(chat).start();
        } catch (IOException e) {
            Log.e(TAG, "IOException throwed by socket", e);
            try {
                socket.close();
            } catch (IOException e1) {
                Log.e(TAG, "IOException during close Socket", e1);
            }
        }
    }


    /**
     * Method to close the client/peer socket and kill this entire thread.
     */
    public void closeSocketAndKillThisThread() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException during close Socket", e);
            }
        }

        //to interrupt this thread, without the threadpoolexecutor
        if (!this.isInterrupted()) {
            Log.d(TAG, "Stopping ClientSocketHandler");
            this.interrupt();
        }
    }
}
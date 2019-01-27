package com.example.statesaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;
    private String TAG = "WiFiDirectBroadcastReceiver";
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            for (WifiP2pDevice device : peerList.getDeviceList()){
                Log.d(MainActivity.TAG, device.deviceName);
                peers.add(device);
            }
            if (peers.size() == 0) {
                Log.d(MainActivity.TAG, "No devices found");
                return;
            }
            Log.d(MainActivity.TAG, "Peers : " + peers.toString());
        }
    };

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
//                activity.resetData();
            }
            Log.d(MainActivity.TAG, "P2P state changed - " + state);
//            activity.discover();

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            if (manager != null) {
                manager.requestPeers(channel, peerListListener);
            }

            Log.d(MainActivity.TAG, "P2P peers changed *******");
            Log.d(MainActivity.TAG, peers.toString());

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            Log.d(MainActivity.TAG, "Wi-fi new connection request ...");
            if(manager == null){
                Log.e(MainActivity.TAG, "Wifi Manager null");
                return;
            }

            manager.requestPeers(channel, peerListListener);

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            Log.d(MainActivity.TAG, "Wi-fi state changing ...");
            WifiP2pDevice d = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.d(MainActivity.TAG, "My device name : " + d.deviceName);
        }
    }
}

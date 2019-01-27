package com.example.statesaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.TextView;

import com.example.statesaver.types.P2pMessage;
import com.example.statesaver.utils.ClientDataManager;
import com.example.statesaver.utils.Configuration;
import com.example.statesaver.utils.DataTransferManager;
import com.example.statesaver.utils.DbHandler;
import com.example.statesaver.utils.IdManager;
import com.example.statesaver.utils.ServerDataManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import com.example.statesaver.utils.RqHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ContentFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        SearchContentFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener,
        Handler.Callback {

    private Thread rqHander;

    public static String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    ServerDataManager serverDataManager = null;
    ClientDataManager clientDataManager = null;
    DataTransferManager dataTransferManager = null;

    IntentFilter intentFilter;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    final Handler dataHandler = new Handler(this);

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** Wifi P2P stuff starts */
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

         /** Wifi P2P stuff starts */

        IdManager.initialize("UNIQUE_ID"); // TODO Make this unique ! Duh !

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = SearchContentFragment.class;
//        fragmentClass = HelpFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DbHandler.getInstance(getApplicationContext());

    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
        manager.requestConnectionInfo(channel, this);

        rqHander = new RqHandler(getApplicationContext());
        rqHander.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.d(TAG, "Recieved Permission callback " + requestCode + " : " + permissions);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION:
                if  (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Coarse location permission is not granted!");
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_search) {
//            fragmentClass = SearchFragment.class;
            fragmentClass = SearchContentFragment.class;
        } else if (id == R.id.nav_help) {
            fragmentClass = HelpFragment.class;
        } else if (id == R.id.nav_content) {
            Log.d(TAG, "Clicked nav content");
            fragmentClass = ContentFragment.class;
        }
//        else if (id == R.id.nav_community){
//            fragmentClass = CommunityTrendFragment.class;
//        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void discover(){
        Log.d(TAG, "Starting discover process ...");
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Discovery Initiated");
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Discovery Failed : " + reasonCode);
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
//            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void sendP2p(String msg) {
        Log.d(TAG, "Trying to send msg = "+msg);
        if (dataTransferManager == null) {
            Log.e(TAG, "Data transfer manager is null");
            return;
        }
        P2pMessage p2pmsg = new P2pMessage(P2pMessage.Type.REQUEST, msg, "AAA");
        new AsyncWriter(dataTransferManager).execute(p2pmsg);
        //dataTransferManager.write(msg.getBytes());
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Log.d(MainActivity.TAG, "onConnectionInfoAvailable : " + wifiP2pInfo);
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            try {
                Log.d("GroupOwnerSocketHandler", "Socket Started");
                if (this.serverDataManager == null) {
                    this.serverDataManager = new ServerDataManager(dataHandler);
                    this.serverDataManager.start();
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException during open ServerSockets with port "+Configuration.SERVER_PORT, e);
            }

            /************ TODO DELETE FOLLOWING LINES ****************/

            /*Socket socket = new Socket();
            socket.bind(null);
            socket.connect(new InetSocketAddress("127.0.0.1",*/
                    /*Configuration.SERVER_PORT), Configuration.CLIENT_TIMEOUT);*/
            //this.clientDataManager = new ClientDataManager(dataHandler, wifiP2pInfo.groupOwnerAddress);
            this.clientDataManager = new ClientDataManager(dataHandler, wifiP2pInfo.groupOwnerAddress);
            this.clientDataManager.start();




        } else if (wifiP2pInfo.groupFormed){
            Log.d(TAG, "I am NOT the owner");
            Socket socket = new Socket();
        }
                /*socket.bind(null);
                socket.connect(new InetSocketAddress(wifiP2pInfo.groupOwnerAddress.getHostAddress(),
                        Configuration.SERVER_PORT), Configuration.CLIENT_TIMEOUT);*/
            this.clientDataManager = new ClientDataManager(dataHandler, wifiP2pInfo.groupOwnerAddress);

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case Configuration.SET_MANAGER:
                final Object obj = msg.obj;
                Log.d(TAG, "SETTING THE TRANSFER MANAGER");
                dataTransferManager = (DataTransferManager)obj;
                break;
            default:
                Log.d(TAG, "Got something weird");
                break;
        }
        return false;
    }

    public class AsyncWriter extends AsyncTask<P2pMessage, Integer, String> {

        DataTransferManager transferManager;
        AsyncWriter(DataTransferManager transferManager) {
            this.transferManager = transferManager;
        }

        @Override
        protected String doInBackground(P2pMessage... p2pMessages) {
            //this.transferManager.write(p2pMessages[0].getBytes());
            return "";
        }
    }

}

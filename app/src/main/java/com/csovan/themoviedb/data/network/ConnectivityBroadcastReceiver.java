package com.csovan.themoviedb.data.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {

    private ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityBroadcastReceiver(ConnectivityReceiverListener connectivityReceiverListener) {
        this.connectivityReceiverListener = connectivityReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (connectivityReceiverListener != null && NetworkConnection.isConnected(context))
            connectivityReceiverListener.onNetworkConnectionConnected();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionConnected();
    }

}

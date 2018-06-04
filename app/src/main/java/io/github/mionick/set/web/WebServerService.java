package io.github.mionick.set.web;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WebServerService extends Service {

    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new WebServiceBinder();
    private ServerGameClient webClient;


    public WebServerService() {
        Log.v(LOG_TAG, "in constructor");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        webClient = ServerGameClient.getInstance(getApplicationContext());
        webClient.start();


    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
        webClient.stop();
    }

    public class WebServiceBinder extends Binder {
        public WebServerService getService() {
            return WebServerService.this;
        }
    }

    public ServerGameClient getWebServer() {
        return webClient;
    }
}

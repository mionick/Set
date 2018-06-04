package io.github.mionick.set.web;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import io.github.mionick.set.R;
import io.github.mionick.set.android.MultiPlayerLobbyActivity;

public class WebServerService extends Service {

    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new WebServiceBinder();
    private ServerGameClient webClient;


    public WebServerService() {
        Log.v(LOG_TAG, "in constructor");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Received Start Foreground Intent ");
        showNotification();
        return START_STICKY;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MultiPlayerLobbyActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);



        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Set Server is Running")
                .setTicker("Set Server")
                .setContentText("Set Server is Running")
                .setSmallIcon(R.drawable.ic_launcher)
                //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(1,
                notification);

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
        Toast.makeText(this, "Set Server Stopped.", Toast.LENGTH_SHORT).show();
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

package io.github.mionick.set.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.github.mionick.events.EventInstance;
import io.github.mionick.set.IInputSource;
import io.github.mionick.set.R;
import io.github.mionick.set.RecordGenerator;
import io.github.mionick.set.SetGame;
import io.github.mionick.set.SetGameEvent;
import io.github.mionick.set.web.ServerGameClient;
import io.github.mionick.set.web.WebServerService;

/**
 * This Activity is meant as a stage where players can request to join the next game.
 * After they send there name and device identifying feature to us, they will be displayed in a list to the host here.
 *
 * By default they are in the next game, though the host can touch their name to toggle whether they are actually in or not.
 *
 * Once the players are set, The host presses Start to begin the game.
 *
 * We will need lag information here from each player.
 *
 */
public class MultiPlayerLobbyActivity extends AppCompatActivity {


    SetGame game;
    CanvasView customCanvas;
    String wifiIp = "";
    String hostIp = "";

    WebServerService webServerService;
    boolean serviceBound = false;
    private ServerGameClient webClient;

    @Override
    protected void onStop() {
        super.onStop();

        // Unbind the service if it's bound
        if (serviceBound) {
            unbindService(mServiceConnection);
            serviceBound = false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_lobby);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Start a web server thing?
        // I guess that will be running in the background.
        // Will have to see how the library works before I can really do anything here...

        webClient = ServerGameClient.getInstance(getApplicationContext());
        wifiIp = webClient.getWifiIp() + "/game.html";
        hostIp = webClient.getHostIp() + "/game.html";

        customCanvas = findViewById(R.id.signature_canvas);
        ((TextView)findViewById(R.id.wifiIp)).setText("Wifi: " + wifiIp);
        ((TextView)findViewById(R.id.hostIp)).setText("Host: " + hostIp);
        game = webClient.getGame();
        customCanvas.setGame(game);

        // On GAME END
        game.eventHandlerSet.AddHandler(SetGameEvent.GAME_END,
                (timestamp, params) -> {
                    customCanvas.endGame(RecordGenerator.getStats(game.eventHandlerSet.getHistory()).getDurationMs() * 1000000);
                }
        );


        ListView playersListView = (ListView) findViewById(R.id.playersListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                webClient.getPlayers()
                );


        // Assign adapter to ListView
        playersListView.setAdapter(adapter);
        webClient.addPlayerAddedHandler((time, nothing) -> runOnUiThread(() -> adapter.notifyDataSetChanged()));

        // Register canvas as an input source
        customCanvas.addSelectionEventHandler((sourceName, triple) -> game.SelectCards(sourceName, triple.toArray()));

        game.eventHandlerSet.AddHandlerForAllEvents((timestamp, objects) -> {
            // the general handler takes object[]
            // the only object in this array is the event that happened.
            // It's designed like this because more specific handlers only receive params, not the event itself.
            EventInstance<SetGameEvent> event = (EventInstance<SetGameEvent>) objects[0];
            // TODO: HERE is where we can add a delay. This is the wiring between the game and the views.
            EventInstance delayedEvent;
            if (event.getType() == SetGameEvent.GAME_START) {
                delayedEvent = new EventInstance(event.getTimestamp() + 3000000000L, event.getType(), event.getParams());
            } else {
                delayedEvent = event;
            }
            customCanvas.OnGameEvent(delayedEvent);
        });

    }

    public void startGame(View view) {
        customCanvas.setHostName(((EditText)findViewById(R.id.hostName)).getText().toString());
        customCanvas.setVisibility(View.VISIBLE);
        findViewById(R.id.selectionLayout).setVisibility(View.GONE);
        webClient.startGame();

    }

    public void shareWifi(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, wifiIp);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void shareHost(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, hostIp);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
/*
Need to shuffle deck, clear events, tell clients to clear events, make canvas dissapear, wow
    public void newGame() {
        webClient.newGame();
        game.newGame();
    }*/

// ======================= SERVICE METHODS =======================
    public void stopService(View v) {
        if (serviceBound) {
            unbindService(mServiceConnection);
            serviceBound = false;
        }
        Intent intent = new Intent(MultiPlayerLobbyActivity.this,
                WebServerService.class);
        stopService(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, WebServerService.class);
        // Start and bind the service, it will run indefinitely in the background and not stop until all components are unbound anf stop is called.
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebServerService.WebServiceBinder binder = (WebServerService.WebServiceBinder) service;
            webServerService = binder.getService();
            serviceBound = true;
        }
    };
}

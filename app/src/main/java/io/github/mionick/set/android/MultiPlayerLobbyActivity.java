package io.github.mionick.set.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.github.mionick.events.EventInstance;
import io.github.mionick.set.IInputSource;
import io.github.mionick.set.R;
import io.github.mionick.set.RecordGenerator;
import io.github.mionick.set.SetGame;
import io.github.mionick.set.SetGameEvent;
import io.github.mionick.set.web.ServerGameClient;

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

    ServerGameClient webClient;
    SetGame game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_lobby);

        // Start a web server thing?
        // I guess that will be running in the background.
        // Will have to see how the library works before I can really do anything here...
        webClient = ServerGameClient.getInstance(getApplicationContext());

        game = new SetGame();

        // Register all input sources.
        IInputSource[] sources = new IInputSource[]{
                webClient
        };

        for (IInputSource source : sources) {
            source.addSelectionEventHandler((sourceName, triple) -> game.SelectCards(sourceName, triple.toArray()));
        }

        game.eventHandlerSet.AddHandlerForAllEvents((timestamp, objects) -> {
            // the gerneal handler takes object[]
            // the only object in this array is the event that happened.
            // It's designe dlike this because more specific handlers only receive params, not the event itself.
            EventInstance<SetGameEvent> event = (EventInstance<SetGameEvent>) objects[0];
            webClient.OnGameEvent(event);
        });

    }

    protected void StartGame(View v) {
        Intent startGameIntent = new Intent(this, MultiPlayerGameActivity.class);
        startGameIntent.put("game", this.game);
        startActivity(startGameIntent);
    }



}

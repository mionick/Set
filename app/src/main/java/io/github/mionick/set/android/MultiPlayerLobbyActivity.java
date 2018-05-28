package io.github.mionick.set.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.mionick.set.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_lobby);

        // Start a web server thing?
        // I guess that will be running in the background.
        // Will have to see how the library works before I can really do anything here...
    }
}

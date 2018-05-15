package io.github.mionick.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// This class acts as a view for the game.
// It should not contain game logic itself, just display logic.
public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}

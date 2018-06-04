package io.github.mionick.set.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.mionick.set.IInputSource;
import io.github.mionick.set.R;
import io.github.mionick.set.RecordGenerator;
import io.github.mionick.set.SetGame;
import io.github.mionick.set.SetGameEvent;

public class MultiPlayerGameActivity extends AppCompatActivity {

    private SetGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_game);

        Intent intent = getIntent();
        this.game = (SetGame)intent.getExtras().get("game");

        CanvasView customCanvas;
        customCanvas = findViewById(R.id.signature_canvas);
        customCanvas.setGame(game);

        // On GAME END
        game.eventHandlerSet.AddHandler(SetGameEvent.GAME_END,
                (timestamp, params) -> {
                    customCanvas.endGame(RecordGenerator.getStats(game.eventHandlerSet.getHistory()).getDurationMs() * 1000000);
                }
        );

        // Register all input sources.
        customCanvas.addSelectionEventHandler((sourceName, triple) -> game.SelectCards(sourceName, triple.toArray()));
    }
}

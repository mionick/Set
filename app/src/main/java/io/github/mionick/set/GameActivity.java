package io.github.mionick.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

// This class acts as the controller for the game, even though it's actually an android view.
// When I talk about the view, I'll be talking about the canvas which actually displays things.
// This classes main responsibility is to map the event handlers of each.
// It should not contain game logic itself, just interaction logic, and statistics.
//
public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    SetGame game;
    CanvasView customCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
        game = new SetGame();
        customCanvas.setGame(game);

        // Used for testing to shorten games
        // game.deck.deckIndex = 81;

        long startime = System.nanoTime();

        game.AddGameOverHandler(numberOfSets -> {
            long duration = System.nanoTime() - startime;

            System.out.println(duration);
            customCanvas.endGame(duration);
        });
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void clearCanvas(View v) {
        customCanvas.clearCanvas();
    }
}

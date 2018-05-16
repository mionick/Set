package io.github.mionick.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

// This class acts as a view for the game.
// It should not contain game logic itself, just display logic.
//
public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    SetGame game = new SetGame();

    CanvasView customCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void clearCanvas(View v) {
        customCanvas.clearCanvas();
    }
}

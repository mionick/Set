package io.github.mionick.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Date;

import io.github.mionick.storage.AppDatabase;
import io.github.mionick.storage.Record;

// This class acts as the controller for the game, even though it's actually an android view.
// When I talk about the view, I'll be talking about the canvas which actually displays things.
// This classes main responsibility is to map the event handlers of each.
// It should not contain game logic itself, just interaction logic, and statistics.
//
public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    SetGame game;
    CanvasView customCanvas;
    private AppDatabase appDatabase;

    private long timeInBackground = 0;
    private long timePlacedInBackground = 0;
    private boolean appPlacedInBackground = false;

    @Override
    protected void onPause() {
        super.onPause();
        appPlacedInBackground = true;
        this.timePlacedInBackground = System.nanoTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appPlacedInBackground) {
            this.timeInBackground += System.nanoTime() - timePlacedInBackground;
            timePlacedInBackground = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        customCanvas = findViewById(R.id.signature_canvas);
        game = new SetGame();
        customCanvas.setGame(game);
        this.appDatabase = AppDatabase.getAppDatabase(getApplicationContext());


        // Used for testing to shorten games
        // game.deck.deckIndex = 81;

        long startTime = System.nanoTime();

        game.AddGameOverHandler(numberOfSets -> {
            long duration = System.nanoTime() - startTime;

            System.out.println(duration);
            customCanvas.endGame(duration);
            saveRecord(duration, numberOfSets, game.getSeed(), appPlacedInBackground, timeInBackground, game.getShortestSetNs(), game.getLongestSetNs());
            System.out.println( game.getShortestSetNs());
        });
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void saveRecord(
            long durationNs,
            int numberOfSets,
            long seed,
            boolean applicationPlacedInBackground,
            long timeInBackgroundNs,
            long shortestSetNs,
            long longestSetNs
    ) {
        long oneMillion = 1000000;
        Record record = new Record();
        record.setCreateDate(new Date());
        record.setAppVersionCode(BuildConfig.VERSION_CODE);
        record.setAppVersionName(BuildConfig.VERSION_NAME);
        record.setApplicationPlacedInBackground(applicationPlacedInBackground);
        record.setColors("");
        record.setSeed(seed);
        record.setDurationMs(durationNs / oneMillion);
        record.setNumberOfSets(numberOfSets);
        record.setTimePausedMs(timeInBackgroundNs / oneMillion);
        record.setShortestSetMs(shortestSetNs / oneMillion);
        record.setLongestSetMs(longestSetNs / oneMillion);

        appDatabase.recordDao().insertAll(record);
    }
}

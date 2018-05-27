package io.github.mionick.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Date;

import io.github.mionick.events.Action;
import io.github.mionick.events.EventInstance;
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

        // TODO: Remove this statistics logic.
        appPlacedInBackground = true;
        this.timePlacedInBackground = System.nanoTime();

        
        game.eventHandlerSet.triggerEvent(new EventInstance<SetGameEvents>(SetGameEvents.GAME_PAUSED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Remove this statistics logic.
        if (appPlacedInBackground) {
            this.timeInBackground += System.nanoTime() - timePlacedInBackground;
            timePlacedInBackground = 0;
        }
        game.eventHandlerSet.triggerEvent(new EventInstance<SetGameEvents>(SetGameEvents.GAME_RESUMED));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        customCanvas = findViewById(R.id.signature_canvas);
        game = new SetGame();
        customCanvas.setGame(game);
        this.appDatabase = AppDatabase.getAppDatabase(getApplicationContext());


        long startTime = System.nanoTime();

        game.eventHandlerSet.AddHandler(SetGameEvents.GAME_END,
                new Action() {
                    @Override
                    public void apply(Long timestamp, Object... params) {
                        long duration = timestamp - startTime;

                        // Tell the view to update
                        customCanvas.endGame(duration);

                        // Get the number of sets. It should be the only parameter.
                        Integer numberOfSets = (Integer) params[0]; // Let this fail if it does.

                        // TODO: We now have the complete history of the game (EXCEPT PAUSING)
                        // Use that for stats.
                        saveRecord(
                                duration,
                                numberOfSets,
                                game.getSeed(),
                                appPlacedInBackground,
                                timeInBackground,
                                game.getShortestSetNs(),
                                game.getLongestSetNs()
                        );
                    }
                }
        );


        // Register all input sources.
        IInputSource[] sources = new IInputSource[]{
          customCanvas
        };

        for (IInputSource source : sources) {
            source.addSelectionEventHandler((sourceName, triple) -> game.SelectCards(sourceName, triple.toArray()));
        }

        // There might be some set up here in the future, to ensure everyone starts at the same time.
        // for now though:
        game.startGame();


        // Used for testing to shorten games
        // game.deck.deckIndex = 81;


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

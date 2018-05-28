package io.github.mionick.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;

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

    @Override
    protected void onPause() {
        super.onPause();
        game.eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.GAME_PAUSED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.GAME_RESUMED));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        customCanvas = findViewById(R.id.signature_canvas);
        game = new SetGame();
        customCanvas.setGame(game);
        this.appDatabase = AppDatabase.getAppDatabase(getApplicationContext());

        // On GAME END
        game.eventHandlerSet.AddHandler(SetGameEvent.GAME_END,
                (timestamp, params) -> {
                    // TODO: Tell the view to update. This will change with internet things.
                    Record record = saveRecord( game.getSeed());
                    customCanvas.endGame(record.getDurationMs() * 1000000);
                }
        );


        // Register all input sources.
        IInputSource[] sources = new IInputSource[]{
          customCanvas
        };

        // TODO: INPUT
        // Might need a delay input thing, to simulate web delay fr the user of the host device. unfair advantage otherwise.
        // It would just be an inputsource that wraps another input source and adds lag.

        // Also, the web client won't JUST be input.
        // We have to notify it when someone else get's a set, and send it information about the board, or game over.
        // Though really, it can just get a copy of the game, and get register for events.

        // Also, in the case of delayed input, users should not be punished because the cards have changed.
        // Will have to have some kind of id (cards left?) attached to the input, so we know when they TRIED to select a set.
        // Throw the selection away if it was outdated
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

    public Record saveRecord( long seed ) {

        Record record = RecordGenerator.getStats(game.eventHandlerSet.getHistory());

        record.setSeed(seed);
        record.setAppVersionCode(BuildConfig.VERSION_CODE);
        record.setAppVersionName(BuildConfig.VERSION_NAME);
        record.setCreateDate(new Date());
        record.setColors("");

        appDatabase.recordDao().insertAll(record);

        return record;
    }
}

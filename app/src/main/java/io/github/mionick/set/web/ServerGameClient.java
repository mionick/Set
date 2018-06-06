package io.github.mionick.set.web;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.mionick.events.Action;
import io.github.mionick.events.EventInstance;
import io.github.mionick.set.IInputSource;
import io.github.mionick.set.ISelectCards;
import io.github.mionick.set.ISetGameView;
import io.github.mionick.set.IntTriple;
import io.github.mionick.set.SetGame;
import io.github.mionick.set.SetGameEvent;

import static android.content.Context.WIFI_SERVICE;

/**
 *
 * Created by Nick on 5/28/2018.
 */


/*
 To handle lag:
 At the beginning of the game they send their timestamp with their registration.
 That gives us a a way to map from our clock to theirs.

 They send timestamps with all their inputs.
 We map that time to our time, and see which one came first.

 Periodically then send their time again to update the difference.

 We take a running average of the last [n] inputs.

 Need a buffer time to wait for events to come in.

 buffer time 4* largest lag.


 OR:

 Server pings the client.
 Round trip time/2 = latency.

 Don't do the open connection thing, they'll have an ip address too you dummy.
 Wait but they won't be listening on any port.
 how am I supposed to ping them?

 Oh I see, that works if it's a large game and you can takeover a port on the clients device.

 can still ping, but we need our open connection.

 Finally,
 for now we're going to host the server in the activity, but need to move to hosting it in a service.
 If it's in an activity and we leave, the game is ruined for everyone.

 if it's in a service and we're only listening to the events, then we can come and go as we please, or not even play.
 Just host for others.

 */
public class ServerGameClient implements IInputSource, ISetGameView {

    private static ServerGameClient instance = null;
    private Context context;
    private Gson gson = new Gson();


    // This essentially contains the select cards method of the game.
    private ISelectCards selectCardsHandler;
    private ArrayList<String> players = new ArrayList<>();
    private ArrayList<Action> playerAddedHandlers = new ArrayList<>();
    private SetGame game;

    public static ServerGameClient getInstance(Context context) {
        if (instance == null) {
            instance = new ServerGameClient(context);
        }
        return instance;
    }

    private AsyncHttpServer server;

    private int port = 1337;

    private ServerGameClient(Context context) {
        this.context = context;
        getServer();
        // TODO: Redirect automatically to game.html
        System.out.println( "We are definitely running!");
        game = new SetGame();
        game.eventHandlerSet.AddHandlerForAllEvents((timestamp, objects) ->
                {
                    Log.v(LOG_TAG, "WEB CLIENT EVENT HANDLER");
                    EventInstance<SetGameEvent> event = (EventInstance<SetGameEvent>) objects[0];
                    OnGameEvent(event);
                }
        );
        addSelectionEventHandler((sourceName, triple) -> game.SelectCards(sourceName, triple.toArray()));

    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
        server = null;
    }
    public void start() {
        if (server == null) {
            getServer();
        }
        server.listen(port);
    }

    private void getServer() {
        server = new AsyncHttpServer();
        server.get("/api/event/.*", eventApiCallback );
        server.get("/api/connection/", connectionApiCallback );
        server.post("/api/user/", registerRequestApiCallback);
        server.post("/api/input/", postInputApiCallback );
        server.get("/\\w+\\.\\w+", websiteCallback );
        server.get("/test", (x, y) -> y.send("Server Running!"));
    }

    public void startGame() {
        gameStarted = false;
        game.reset();

        // Any players added during a game are allowed to play now.
        for ( String user : registeredUsers.keySet()) {
            registeredUsers.put(user, true);
        }
        for (int i = events.size()-1; i > -1; i--) {
            // clear all game related events from the list of events, players are still connected though.
            if (events.get(i).getType() instanceof SetGameEvent) {
                events.remove(i);
            }
        }

        game.startGame();
        // If the game is already in progress then ignore it, they'll have to catch up be getting the events that have happened so far.
    }

    // ================================== SERVER METHODS: =============================================

    HttpServerRequestCallback websiteCallback = (request, response) -> {
        AssetManager am = context.getAssets();
        try {
            InputStream stream = am.open(request.getPath().substring(1));
            response.sendStream(stream, stream.available()); // TODO: available was not meant to be used as the total length. Might not work.
        } catch (IOException e) {
            e.printStackTrace();
            response.code(404);
        }
    };



    HttpServerRequestCallback connectionApiCallback = (request, response) -> {


        // Send the two ip addresses, our local wifi and the android hotspot one
        response.send(gson.toJson(new EventInstance<CommunicationEvent>(CommunicationEvent.COMMUNICATION, getWifiIp() , getHostIp())));
    };
    // ================================== INPUT METHODS: =============================================
    // The boolean represents whether they were allowed in the game or not.
    private HashMap<String, Boolean> registeredUsers = new HashMap<>(10);

    private boolean gameStarted = false;

    private String LOG_TAG = "GAME CLIENT";
    HttpServerRequestCallback registerRequestApiCallback = (request, response) -> {
        Log.v(LOG_TAG, "register request");


        try {
            JSONObject theirInput = ((AsyncHttpRequestBody<JSONObject>)request.getBody()).get();

            // Identifier is meant t be used to prevent duplicate names
            //String identifier = theirInput.getString("identifier");
            String name = theirInput.getString("name");
            Log.v(LOG_TAG, name);
/*
            try {
                name =
                        new String(name.getBytes(), "UTF16") + " " +
                                new String(name.getBytes(), "UTF8") + " " +
                                        new String(name.getBytes(), "UTF32");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
*/


            if (registeredUsers.containsKey(name)) {
                response.send(gson.toJson(new EventInstance<CommunicationEvent>(CommunicationEvent.NAME_TAKEN)));
                Log.v(LOG_TAG, "Send NAME_TAKEN");
                return;
            } else if (!gameStarted) {
                {
                    registeredUsers.put(name, true);
                    players.add(name);
                    Log.v(LOG_TAG, "Adding them to the list.");
                    EventInstance event = new EventInstance<CommunicationEvent>(CommunicationEvent.PLAYER_JOINED, name);
                    OnGameEvent(event);
                    onPlayerAdded();
                    Log.v(LOG_TAG, "Added them to the list.");

                }
            } else {
                // Not in list and game started
                registeredUsers.put(name, false);
                players.add(name);
                Log.v(LOG_TAG, "Adding them to the list with false.");
                EventInstance event = new EventInstance<CommunicationEvent>(CommunicationEvent.GAME_IN_PROGRESS, name);
                OnGameEvent(event);
                onPlayerAdded();
                Log.v(LOG_TAG, "Added them to the list with false.");
                response.send(gson.toJson(event));
                return;
            }
        } catch (JSONException e) {
            response.code(500).send("Not Registered!");
            Log.v(LOG_TAG, "Send 500");
            e.printStackTrace();
        }
        response.send(gson.toJson(new EventInstance<CommunicationEvent>(CommunicationEvent.JOINED_SUCCESSFULLY)));
        Log.v(LOG_TAG, "Send JOINED_SUCCESSFULLY");

    };

    HttpServerRequestCallback postInputApiCallback = (request, response) -> {
        // IF they are registered in the current game session
        // TODO: take latency into account for that user, use the input that comes first on the game.
        // use this.selectCardsHandler
        AsyncHttpRequestBody<JSONObject> body = (AsyncHttpRequestBody<JSONObject>)request.getBody();

        try {
            JSONObject theirInput = body.get();


            String identifier = theirInput.getString("name");

            // if they registered and were allowed in the game
            if (registeredUsers.containsKey(identifier) && registeredUsers.get(identifier)) {
                // They are allowed to make inputs.
                // Get the indexes of the cards they chose.
                String name = theirInput.getString("name");
                JSONArray inputs = theirInput.getJSONArray("selectedCards");

                IntTriple triple = new IntTriple();
                triple.setInt1(inputs.getInt(0));
                triple.setInt2(inputs.getInt(1));
                triple.setInt3(inputs.getInt(2));
                this.selectCardsHandler.SelectSet(name, triple);
            }
        } catch (JSONException e) {
            response.code(500).send("Input failed!");
            e.printStackTrace();
        }

        response.send("Ok.");
    };

    @Override
    public void addSelectionEventHandler(ISelectCards handler) {
        this.selectCardsHandler = handler;
    }

    // ================================== EVENT METHODS: =============================================

    private final ArrayList<AsyncHttpServerResponse> outstandingRequests = new ArrayList<>(10);
    private ArrayList<EventInstance> events = new ArrayList<>(200);

    private HttpServerRequestCallback eventApiCallback = (request, response) -> {
        // They sent the number of the last event they received.
        // If we have more events than they've received, send them the missing ones.
        // Otherwise, add them to the list of people waiting for events.

        // 1) get the event number they sent in their request path:
        // this will be given to us as ?event={number}
        int theirEventNumber = Integer.parseInt(request.getQuery().get("event").get(0));
        Log.v(LOG_TAG, "RECIEVED EVENT REQUEST. NUM: " + theirEventNumber);

        if (theirEventNumber < this.events.size()) {
            // Build a JSON response containing all the events they've missed.
            int difference = this.events.size() - theirEventNumber;
            EventInstance[] eventsArray = new EventInstance[difference];
            for (int i = theirEventNumber; i < this.events.size(); i++) {
                eventsArray[i-theirEventNumber] = this.events.get(i);
            }

            String jsonResponse = gson.toJson(eventsArray);
            response.send(jsonResponse);

        } else {
            synchronized (this.outstandingRequests) {
                this.outstandingRequests.add(response);
            }
        }
    };


    // TODO: Event transform - for delaying events based on input, time or whatever, or for discounting inputs that might have been delayed
    @Override
    synchronized public void OnGameEvent(EventInstance event) {
        synchronized (this.outstandingRequests) {
            this.events.add(event);
            Log.v(LOG_TAG, "Recieved Event: " + event.getType().name());

                if (event.getType() == SetGameEvent.GAME_START) {
                // Players can no longer join
                gameStarted = true;
            }

            // TODO: Save the clock difference for each player, and send the game start event 7 seconds in the future.
            // SO that they can countdown to start.

            for (AsyncHttpServerResponse response :
                    this.outstandingRequests) {
                    String responseString = gson.toJson(new EventInstance[]{event});
                    //response.setContentType("application/json; charset=utf-16");
                response.send(responseString);
            }

            this.outstandingRequests.clear();
        }

    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    // TODO: I should not use action here, since the parameters are meaningless.
    // Really I want a void Action() interface
    public void addPlayerAddedHandler(Action action) {
        this.playerAddedHandlers.add(action);
    }

    public void onPlayerAdded() {
        for (Action action : playerAddedHandlers) {
            try {
                action.apply(0L);
            }catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public String getWifiIp() {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        int ip = wm != null ? wm.getConnectionInfo().getIpAddress() : 0;

        return ip != 0 ? Formatter.formatIpAddress(ip) + ":" + port : "No Wifi Address Found";
    }
    public String getHostIp() {
        return "192.168.43.1:" + port;
    }

    public SetGame getGame() {
        return game;
    }
}

package io.github.mionick.set;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.github.mionick.events.EventInstance;
import io.github.mionick.storage.Record;

/**
 * This class will take in a list of events, and process them to return a Record object.
 * Created by Nick on 5/27/2018.
 */

public class RecordGenerator {
    public static Record getStats(List<EventInstance<SetGameEvent>> history) {
        long oneMillion = 1000000;
        Record record = new Record();

        record.setDurationMs(getMatchLengthNs(history) / oneMillion);
        record.setNumberOfSets(getNumberOfSetsFound(history));
        record.setTimePausedMs(getTimeGameWasPausedNs(history) / oneMillion);
        record.setApplicationPlacedInBackground(record.getTimePausedMs() != 0);

        Pair<Long, Long> shortestLongest = getMinMaxSetTimeNs(history);
        record.setShortestSetMs(shortestLongest.first / oneMillion);
        record.setLongestSetMs(shortestLongest.second / oneMillion);

        return record;
    }

    private static Pair<Long, Long> getMinMaxSetTimeNs(List<EventInstance<SetGameEvent>> history) {

        ArrayList<Long> setDifferences = new ArrayList<>();
        EventInstance<SetGameEvent> pausedEvent = null;
        EventInstance<SetGameEvent> resumedEvent = null;

        long lastSetTime = Long.MAX_VALUE;// This should never be used, so make it obvious if it does get used somehow.


        // Build a list of the time differences between sets.
        for (int i = 0; i < history.size(); i++) {
            EventInstance<SetGameEvent> event = history.get(i);
            if (history.get(i).getType() == SetGameEvent.GAME_START) {
                lastSetTime = history.get(i).getTimestamp();
            } else if (history.get(i).getType() == SetGameEvent.CORRECT_SET) {
                // calculate how long it took to find this set.
                // It is not possible to do anything while paused, so a pause will  ALWAYS be followed by a resume.

                setDifferences.add(history.get(i).getTimestamp() - lastSetTime);
                // this is the new marker
                lastSetTime = history.get(i).getTimestamp();
            } else if (event.getType() == SetGameEvent.GAME_PAUSED) {
                pausedEvent = event;
            } else if (event.getType() == SetGameEvent.GAME_RESUMED) {
                resumedEvent = event;
            }


            if (pausedEvent != null && resumedEvent != null) {
                // If we have a pause and unpause then move the marker forward by that much time.
                lastSetTime += resumedEvent.getTimestamp() - pausedEvent.getTimestamp();
                pausedEvent = null;
                resumedEvent = null;
            }

        }


        return new Pair<Long, Long>(Collections.min(setDifferences), Collections.max(setDifferences));
    }

    private static int getNumberOfSetsFound(List<EventInstance<SetGameEvent>> history) {

        int result = 0;
        for (EventInstance<SetGameEvent> event : history) {
            if (event.getType() == SetGameEvent.CORRECT_SET) {
                result++;
            }
        }

        return result;

    }

    public static long getMatchLengthNs(List<EventInstance<SetGameEvent>> history) {
        EventInstance<SetGameEvent> startEvent = null;
        EventInstance<SetGameEvent> endEvent = null;

        for (EventInstance<SetGameEvent> event : history) {
            if (event.getType() == SetGameEvent.GAME_START) {
                startEvent = event;
            } else
            if (event.getType() == SetGameEvent.GAME_END) {
                endEvent = event;
                break;
            }
        }

        if (startEvent == null || endEvent == null) {
            throw new IllegalArgumentException("The history does not have a start and end event.");
        }

        // At this point we should have the start and stop events.
        // if we don't then the history passed in was wrong.
        return endEvent.getTimestamp() - startEvent.getTimestamp();
    }

    public static long getTimeGameWasPausedNs(List<EventInstance<SetGameEvent>> history) {
        EventInstance<SetGameEvent> pausedEvent = null;
        EventInstance<SetGameEvent> resumedEvent = null;
        long result = 0;

        for (EventInstance<SetGameEvent> event : history) {
            if (event.getType() == SetGameEvent.GAME_PAUSED) {
                pausedEvent = event;
            } else
            if (event.getType() == SetGameEvent.GAME_RESUMED) {
                resumedEvent = event;
            }


            if (pausedEvent != null && resumedEvent != null) {
                // If we have a pause and unpause, add that to the total time game was paused.
                // Then set them to null and continue looping.
                result += resumedEvent.getTimestamp() - pausedEvent.getTimestamp();
                pausedEvent = null;
                resumedEvent = null;
            }
        }

        return  result;
    }
}

package io.github.mionick.storage;

import android.arch.persistence.room.*;

import java.util.Date;

@Entity
public class Record {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "create_date")
    private Date createDate;

    @ColumnInfo(name = "duration_ms")
    private long durationMs;

    @ColumnInfo(name = "seed")
    private long seed;

    @ColumnInfo(name = "app_version_code")
    private long appVersionCode;

    @ColumnInfo(name = "app_version_name")
    private String appVersionName;

    @ColumnInfo(name = "number_of_sets")
    private int numberOfSets;

    @ColumnInfo(name = "application_was_in_background")
    private boolean applicationPlacedInBackground;

    @ColumnInfo(name = "colors")
    private String colors;

    @ColumnInfo(name = "longest_set_ms")
    private Long longestSetMs;

    @ColumnInfo(name = "shortest_set_ms")
    private Long ShortestSetMs;

    @ColumnInfo(name = "time_paused_ms")
    private Long timePausedMs;

    public Record() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public long  getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(long appVersionCode) {
        this.appVersionCode = appVersionCode;
    }
    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public int getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(int numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public boolean getApplicationPlacedInBackground() {
        return applicationPlacedInBackground;
    }

    public void setApplicationPlacedInBackground(boolean applicationPlacedInBackground) {
        this.applicationPlacedInBackground = applicationPlacedInBackground;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    @Ignore
    public double getSecondsPerSet(){
        return getDurationMs()/(double)getNumberOfSets() / 1000.0;
    }


    public Long getLongestSetMs() {
        return longestSetMs;
    }

    public void setLongestSetMs(Long longestSetMs) {
        this.longestSetMs = longestSetMs;
    }

    public Long getShortestSetMs() {
        // This is not possible, and I messed up programming originally so I have zeros in my records.
        if (ShortestSetMs == null || ShortestSetMs == 0) {
            return null;
        }
        return ShortestSetMs;
    }

    public void setShortestSetMs(Long shortestSetMs) {
        ShortestSetMs = shortestSetMs;
    }

    public Long getTimePausedMs() {
        if (!getApplicationPlacedInBackground()) {
            return 0L;
        }
        return timePausedMs;
    }

    public void setTimePausedMs(Long timePausedMs) {
        this.timePausedMs = timePausedMs;
    }
}

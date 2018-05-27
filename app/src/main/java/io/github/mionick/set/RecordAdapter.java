package io.github.mionick.set;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.github.mionick.storage.Record;

public class RecordAdapter extends ArrayAdapter<Record> {

    private final Context context;
    private final List<Record> records;
    private final int layoutResourceId;
    private final long bestRecordId;

    public RecordAdapter(Context context, int layoutResourceId, List<Record> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.records = data;

        this.bestRecordId = Collections.min(records,
                (record, record2)
                ->
                Double.compare(record.getSecondsPerSet(), record2.getSecondsPerSet()))
                .getId();

    }

    public void sortRecords( int index, boolean ascending ) {
        System.out.println( "SORTING");

        Collections.sort(records, ((t1, t2) ->
        {
            Record record1 = (Record) t1;
            Record record2 = (Record) t2;

            if (index == 0) {
                return record1.getCreateDate().compareTo(record2.getCreateDate());
            }
            else if (index == 1) {
                return (int)(record1.getDurationMs() - (record2.getDurationMs()));
            } else if (index == 2) {
                return Double.compare(record1.getSecondsPerSet(), (record2.getSecondsPerSet()));
            } else if (index == 7) {
                return longSafeComparion(record1.getShortestSetMs(), (record2.getShortestSetMs()));
            } else if (index == 8) {
                return longSafeComparion(record1.getLongestSetMs(), (record2.getLongestSetMs()));
            } else {
                return record1.getId() - (record2.getId());
            }
        }));
        if (!ascending) {
            Collections.reverse(records);
        }

    }

    // If an argument is null, it will treat it as the lowest possible value.
    private int longSafeComparion(Long long1, Long long2) {

        Long long1NotNull = long1;
        Long long2NotNull = long2;
        if (long1 == null) {
            long1NotNull = Long.MIN_VALUE;
        }
        if (long2 == null) {
            long2NotNull = Long.MIN_VALUE;
        }
        return Long.compare(long1NotNull, long2NotNull);
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Record getItem(int i) {
        return records.get(i);
    }

    @Override
    public long getItemId(int i) {
        return records.get(i).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;

        ViewHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, viewGroup, false);

            holder = new ViewHolder(row);
            row.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }
        Record record = records.get(i);

        int textColor;
        if (record.getId() == bestRecordId) {
            textColor = Color.rgb(255, 201, 14);
        } else  {
            textColor = getDefaultTextColor();
        }
        holder.getApplicationBackgroundText().setTextColor(textColor);
        holder.getCreateDateText().setTextColor(textColor);
        holder.getSeedText().setTextColor(textColor);
        holder.getNumberOfSetsText().setTextColor(textColor);
        holder.getDurationText().setTextColor(textColor);
        holder.getTimePerSetText().setTextColor(textColor);
        holder.getVersionText().setTextColor(textColor);
        holder.getVersionCodeText().setTextColor(textColor);
        holder.getShortestSetText().setTextColor(textColor);
        holder.getLongestSetText().setTextColor(textColor);

        long seconds = record.getDurationMs() / 1000;
        String time = String.format("%02d:%02d",seconds / 60, (seconds% 60));

        DecimalFormat df = new DecimalFormat("#.00");
        holder.getApplicationBackgroundText().setText(formatMsToSeconds(record.getTimePausedMs()) + "");
        holder.getLongestSetText().setText(formatMsToSeconds(record.getLongestSetMs()) + "");
        holder.getShortestSetText().setText(formatMsToSeconds(record.getShortestSetMs()) + "");
        holder.getCreateDateText().setText(record.getCreateDate().toString());
        holder.getSeedText().setText(Long.toString(record.getSeed()));
        holder.getNumberOfSetsText().setText(Integer.toString(record.getNumberOfSets()));
        holder.getDurationText().setText(time);
        holder.getTimePerSetText().setText(df.format(record.getSecondsPerSet()));
        holder.getVersionText().setText(record.getAppVersionName());
        holder.getVersionCodeText().setText(Long.toString(record.getAppVersionCode()));

        System.out.println( record.getShortestSetMs());


        return row;
    }

    private String formatMsToSeconds(Long ms) {
        if (ms == null) {
            return "N/A";
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(ms / 1000.0);
        }
    }

    private int getDefaultTextColor() {
        int[] attrs = new int[] { android.R.attr.textColorSecondary };
        TypedArray a = context.getTheme().obtainStyledAttributes(R.style.AppTheme, attrs);
        int color = a.getColor(0, Color.RED);
        a.recycle();
        return  color;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return records.isEmpty();
    }

    static class ViewHolder {
        private View row;
        private
        TextView createDateText = null,
                durationText = null,
                timePerSetText = null,
                numberOfSetsText = null,
                versionText = null,
                versionCodeText = null,
                applicationBackgroundText = null,
                shortestSetText = null,
                longestSetText = null,
                seedText = null;

        public ViewHolder(View row) {
            this.row = row;

        }

        public TextView getCreateDateText() {
            if (this.createDateText == null) {
                this.createDateText = row.findViewById(R.id.CreateDate);
            }
            return this.createDateText;
        }
        public TextView getDurationText() {
            if (this.durationText == null) {
                this.durationText = (TextView) row.findViewById(R.id.Time);
            }
            return this.durationText;
        }
        public TextView getTimePerSetText() {
            if (this.timePerSetText == null) {
                this.timePerSetText = (TextView) row.findViewById(R.id.TimePerSet);
            }
            return this.timePerSetText;
        }
        public TextView getNumberOfSetsText() {
            if (this.numberOfSetsText == null) {
                this.numberOfSetsText = (TextView) row.findViewById(R.id.NumberOfSets);
            }
            return this.numberOfSetsText;
        }
        public TextView getVersionText() {
            if (this.versionText == null) {
                this.versionText = (TextView) row.findViewById(R.id.Version);
            }
            return this.versionText;
        }
        public TextView getVersionCodeText() {
            if (this.versionCodeText == null) {
                this.versionCodeText = (TextView) row.findViewById(R.id.VersionCode);
            }
            return this.versionCodeText;
        }
        public TextView getApplicationBackgroundText() {
            if (this.applicationBackgroundText == null) {
                this.applicationBackgroundText = (TextView) row.findViewById(R.id.ApplicationInBackground);
            }
            return this.applicationBackgroundText;
        }

        public TextView getShortestSetText() {
            if (this.shortestSetText == null) {
                this.shortestSetText= (TextView) row.findViewById(R.id.ShortestSet);
            }
            return this.shortestSetText;
        }

        public TextView getLongestSetText() {
            if (this.longestSetText == null) {
                this.longestSetText = (TextView) row.findViewById(R.id.LongestSet);
            }
            return this.longestSetText;
        }
        public TextView getSeedText() {
            if (this.seedText == null) {
                this.seedText = (TextView) row.findViewById(R.id.Seed);
            }
            return this.seedText;
        }

    }
}

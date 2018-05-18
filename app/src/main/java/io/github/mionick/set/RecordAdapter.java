package io.github.mionick.set;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.mionick.storage.Record;

/**
 * Created by Nick on 5/18/2018.
 */

public class RecordAdapter extends ArrayAdapter<Record> {

    private final Context context;
    private final List<Record> records;
    private final int layoutResourceId;

    public RecordAdapter(Context context, int layoutResourceId, List<Record> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.records = data;
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

        long seconds = record.getDurationMs() / 1000;
        String time = String.format("%02d:%02d",seconds / 60, (seconds% 60));

        holder.getApplicationBackgroundText().setText(record.getApplicationPlacedInBackground() + "");
        holder.getCreateDateText().setText(record.getCreateDate().toString());
        holder.getSeedText().setText(Long.toString(record.getSeed()));
        holder.getNumberOfSetsText().setText(Integer.toString(record.getNumberOfSets()));
        holder.getDurationText().setText(time);
        holder.getVersionText().setText(record.getAppVersionName());
        holder.getVersionCodeText().setText(Long.toString(record.getAppVersionCode()));

        return row;
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
        numberOfSetsText = null,
        versionText = null,
        versionCodeText = null,
        applicationBackgroundText = null,
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
        public TextView getSeedText() {
            if (this.seedText == null) {
                this.seedText = (TextView) row.findViewById(R.id.Seed);
            }
            return this.seedText;
        }

    }
}

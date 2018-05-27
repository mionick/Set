package io.github.mionick.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.github.mionick.storage.AppDatabase;
import io.github.mionick.storage.Record;

public class RecordActivity extends AppCompatActivity {

    RecordAdapter adapter;
    int lastSortIndex = 0;
    boolean ascending = true;
    ListView recordView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // Populate table
        List<Record> records = AppDatabase.getAppDatabase(this).recordDao().getAll();
        adapter = new RecordAdapter(this,
                R.layout.record_entry, records);

        recordView = findViewById(R.id.RecordList);

        recordView.setAdapter(adapter);

        ((BaseAdapter) recordView.getAdapter()).notifyDataSetChanged();
        recordView.invalidate();
        System.out.println("There are this many records: " + records.size());

    }

    public void Sort(View view) {
        int index = 0;
        if (view.getId() == R.id.CreateDateHeader) {
            index = 0;
        } else if (view.getId() == R.id.TimeHeader) {
            index = 1;
        } else if (view.getId() == R.id.TimePerSetHeader) {
            index = 2;
        } else if (view.getId() == R.id.ShortestSetHeader) {
            index = 7;
        } else if (view.getId() == R.id.LongestSetHeader) {
            index = 8;
        }



        ascending = index != lastSortIndex || !ascending;

        lastSortIndex = index;

        adapter.sortRecords(index, ascending);

        recordView.invalidateViews();
    }
}

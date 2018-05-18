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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // Populate table
        List<Record> records = AppDatabase.getAppDatabase(this).recordDao().getAll();
        RecordAdapter adapter = new RecordAdapter(this,
                R.layout.record_entry, records);


        ListView recordView = (ListView) findViewById(R.id.RecordList);

        recordView.setAdapter(adapter);

        ((BaseAdapter) recordView.getAdapter()).notifyDataSetChanged();
        recordView.invalidate();
        System.out.println("There are this many records: " + records.size());

    }
}
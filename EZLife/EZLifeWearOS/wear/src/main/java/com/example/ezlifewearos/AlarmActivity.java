package com.example.ezlifewearos;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        List<AlarmItem> items = new ArrayList<AlarmItem>();
        items.add(new AlarmItem(
                getString(R.string.newAlarm), R.drawable.outline_add_24, AlarmEditActivity.class, true
        ));

        AlarmListAdapter alarmListAdapter = new AlarmListAdapter(this, items);
        WearableRecyclerView wearableRecyclerView = findViewById(R.id.alarm_recycler_launcher_view);


        wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        wearableRecyclerView.setAdapter(alarmListAdapter);
        wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
    }
}

package com.example.ezlifewearos;

import android.app.Activity;
import android.os.Bundle;

import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<AppFuncItem> mainItems = new ArrayList<AppFuncItem>();
        mainItems.add(new AppFuncItem(
                getString(R.string.alarm), R.drawable.outline_alarm_24, AlarmActivity.class
        ));
        mainItems.add(new AppFuncItem(
                getString(R.string.wol), R.drawable.outline_computer_24, WOLActivity.class
        ));

        MainMenuListAdapter mainMenuListAdapter = new MainMenuListAdapter(this, mainItems);
        WearableRecyclerView wearableRecyclerView = findViewById(R.id.recycler_launcher_view);


        wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        wearableRecyclerView.setAdapter(mainMenuListAdapter);
        wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
    }

}
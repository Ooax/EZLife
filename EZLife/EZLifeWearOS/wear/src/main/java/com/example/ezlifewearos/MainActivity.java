package com.example.ezlifewearos;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.ezlifewearos.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private WearableRecyclerView wearableRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wearableRecyclerView = findViewById(R.id.recycler_launcher_view);

        setContentView(R.layout.activity_main);

        List<AppFuncItem> mainItems = new ArrayList<AppFuncItem>();
        mainItems.add(new AppFuncItem(
                getString(R.string.alarm), R.drawable.googleg_standard_color_18
        ));

        RecyclerView.Adapter appListAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        }

        wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager((this)));
    }

}
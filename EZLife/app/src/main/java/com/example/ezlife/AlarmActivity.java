package com.example.ezlife;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private LinearLayout alarmsLayout;
    private LinearLayout cardsLayout;
    private Message message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.alarmsActivity);
        setContentView(R.layout.activity_alarms);
        bottomNavigationView = findViewById(R.id.btm_nav_alarms);
        alarmsLayout = findViewById(R.id.alarmsLayout);
        cardsLayout = findViewById(R.id.cardLayout);

        bottomNavigationView.setSelectedItemId(R.id.page_2);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_1:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.page_2:
                        startActivity(new Intent(getApplicationContext(), AlarmActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        message = new Message();
        message.addListener(this);
        if (!message.wearableDeviceConnected) {
            message.initialiseDevicePairing(this);
        }

        AlarmDatabase db = new AlarmDatabase(this);
        db.removeRecord(0);
        db.removeRecord(1);
        db.removeRecord(2);
        db.addRecord(new Alarm(0, "test", 1650802541, "Mon", "true", "false", "default.wav", "default"));
        db.addRecord(new Alarm(1, "test2", 1650803541, "Mon, Tue", "true", "true", "default2.wav", "default2"));
        db.addRecord(new Alarm(2, "test3", 1650804541, "Sat, Sun", "false", "false", "default3.wav", "default3"));
        CreateAlarms(db);

    }

    @Override
    protected void onPause() {
        super.onPause();
        message.removeListener(this);
    }

    public String booleanToString(String s) {
        return Boolean.parseBoolean(s) ? "On" : "Off";
    }

    public void CreateAlarms(AlarmDatabase db) {
        Alarm[] alarms = db.getAllRecords();
        for (Alarm a: alarms
             ) {
            MaterialCardView cardView = new MaterialCardView(this);
            cardView.setTag(a.alarmId);
            TextView textView = new TextView(this);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Log.d("Alarm", Integer.toString(a.time));
            Log.d("Alarm", (a.isOn));
            Log.d("Alarm", a.alarmSound);
            String alarmTime = simpleDateFormat.format(new Date(a.time * 1000L));
            textView.setText(alarmTime + " " + a.days);
            textView.setTextSize(36);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textViewParams.setMargins(5, 5, 5, 5);
            textView.setLayoutParams(textViewParams);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog  = new MaterialAlertDialogBuilder(new ContextThemeWrapper(AlarmActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)).create();
                    alertDialog.setTitle("Alarm details");
                    alertDialog.setMessage(
                            "Alarm time: " + alarmTime + '\n' + "Alarm days: " + a.days + '\n' + "Alarm state: " + booleanToString(a.isOn) + '\n' +
                            "Vibration state: " + booleanToString(a.isVibrationOn) + '\n' + "Alarm sound: " + a.alarmSound + '\n' +  "Alarm task: " + a.alarmTask + '\n'
                    );
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
            });
            cardView.addView(textView);
            cardView.setCardElevation(6);
            cardView.setRadius(10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(5,5,5,20);
            cardView.setLayoutParams(params);
            SwitchMaterial switchControl = new SwitchMaterial(this);
            RelativeLayout.LayoutParams switchParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            switchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            switchControl.setLayoutParams(switchParams);
            switchControl.setChecked(Boolean.parseBoolean(a.isOn));
            switchControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Alarm changedAlarm = a;
                    changedAlarm.isOn = Boolean.toString(b);
                    db.changeRecord(a.id, changedAlarm);
                    syncDatabase(db);
                }
            });
            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            relativeLayout.addView(switchControl);
            cardView.addView(relativeLayout);
            cardsLayout.addView(cardView);
        }
    }

    private void syncDatabase(AlarmDatabase db) {
        Alarm[] alarmList = db.getAllRecords();
        Gson gson = new Gson();
        String json = gson.toJson(alarmList);
        byte[] msg = json.getBytes(StandardCharsets.UTF_8);
        message.sendMessage(msg, this);
    }
}

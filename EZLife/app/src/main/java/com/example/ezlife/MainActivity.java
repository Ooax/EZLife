package com.example.ezlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayoutMain;
    LinearLayout linearLayoutButtons;
    ScrollView scrollView;
    Button startPcButton;
    EditText ipAddressText, macAddressText;
    Button savePcButton;
    ArrayList<String> buttonData = new ArrayList<String>();
    BottomNavigationView bottomNavigationView;

    //Button testMessageButton;

    //public void sendMessage(View view) {
       // Intent intent = new Intent(this, MessageActivity.class);
       // startActivity(intent);
    //}

    private void saveState(String name, String mac, String ip, int mode) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        StringBuilder sb = new StringBuilder();
        sb.append(mac).append(" ").append(ip);
        if (mode == 0) {
            editor.putString(name, sb.toString());
            editor.apply();
        }
        if (mode == 1) {
            editor.remove(name);
            editor.apply();
        }
    }

    private void loadState() {
        SharedPreferences state = getPreferences(MODE_PRIVATE);
        Map<String, ?> map = state.getAll();
        String[] buttonNames = map.keySet().toArray(new String[0]);
        String[] buttonPrefs = map.values().toArray(new String[0]);
        for (int i = 0; i < buttonNames.length; i++) {
            buttonData.add(buttonNames[i] + " " + buttonPrefs[i]);
        }
        Collections.sort(buttonData);
        restoreButtons(buttonData);
    }

    private void restoreButtons(ArrayList<String> buttonData) {
        for (int i = 0; i < buttonData.size(); i++) {
            Button btn = new Button(this);
            btn.setTag(buttonData.get(i));
            btn.setText(buttonData.get(i).split(" ")[0]);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = btn.getTag().toString();
                    String[] pcData = tag.split(" ");
                    macAddressText.setText(pcData[1]);
                    ipAddressText.setText(pcData[2]);
                }
            });
            btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog alertDialog  = new MaterialAlertDialogBuilder(new ContextThemeWrapper(MainActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)).create();
                    alertDialog.setTitle("Delete saved entry");
                    alertDialog.setMessage("Do you want to delete your entry?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Button btn = (Button) linearLayoutButtons.findViewWithTag(v.getTag().toString());
                                    saveState(btn.getTag().toString().split(" ")[0],
                                            btn.getTag().toString().split(" ")[1], btn.getTag().toString().split(" ")[2], 1);
                                    linearLayoutButtons.removeView(btn);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return true;
                }
            });
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_desktop_windows_48, 0, 0, 0);
            linearLayoutButtons.addView(btn);
        }
    }

    private Boolean areStringMatching() {
        Pattern IPV4_REGEX = Pattern.compile("(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))");
        Pattern MAC_REGEX = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        final String ipAddressString = ipAddressText.getText().toString();
        final String macAddressString = macAddressText.getText().toString();
        if (IPV4_REGEX.matcher(ipAddressString).matches() && MAC_REGEX.matcher(macAddressString).matches()) {
            return true;
        } else return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.wakeOnLanActivity);
        setContentView(R.layout.activity_main);
        linearLayoutMain = (LinearLayout) findViewById(R.id.mainLayout);
        linearLayoutButtons = (LinearLayout) findViewById(R.id.linearLayoutButtons);
        scrollView = (ScrollView) findViewById(R.id.scrollViewMain); 
        ipAddressText = (EditText) findViewById(R.id.inputIp);
        macAddressText = (EditText) findViewById(R.id.inputMac);
        startPcButton = (Button) findViewById(R.id.buttonAddresses);
        savePcButton = (Button) findViewById(R.id.savePC);
        bottomNavigationView = findViewById(R.id.btm_nav_main);
        loadState();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_1:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.page_2:
                        startActivity(new Intent(getApplicationContext(), MessageActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.page_3:
                        startActivity(new Intent(getApplicationContext(), AlarmsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        startPcButton.setOnClickListener((view) -> {
            if (areStringMatching()) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WakeOnLan.main(new String[]{ipAddressText.getText().toString(), macAddressText.getText().toString()});
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                thread.start();
            } else {
                Toast.makeText(getApplicationContext(), "Input data is invalid", Toast.LENGTH_LONG).show();
            }
        });

        ipAddressText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!areStringMatching()) {
                    savePcButton.setVisibility(View.INVISIBLE);
                } else {
                    savePcButton.setVisibility(View.VISIBLE);
                }
            }
        });

        macAddressText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!areStringMatching()) {
                    savePcButton.setVisibility(View.GONE);
                } else {
                    savePcButton.setVisibility(View.VISIBLE);
                }
            }
        });

        savePcButton.setOnClickListener((view) -> {
            if (areStringMatching()) {
                AlertDialog alertDialog  = new MaterialAlertDialogBuilder(new ContextThemeWrapper(MainActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)).create();
                alertDialog.setTitle("Save PC configuration");
                alertDialog.setMessage("Set a title for your PC configuration");
                final EditText input = new EditText(this);
                alertDialog.setView(input);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Button btn = new Button(new ContextThemeWrapper(MainActivity.this, com.google.android.material.R.style.Widget_Material3_Button));
                        btn.setTag(input.getText().toString() + " " + macAddressText.getText().toString() + " " + ipAddressText.getText().toString());
                        btn.setText(input.getText().toString());
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tag = btn.getTag().toString();
                                String[] pcData = tag.split(" ");
                                macAddressText.setText(pcData[1]);
                                ipAddressText.setText(pcData[2]);
                            }
                        });
                        btn.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog alertDialog  = new MaterialAlertDialogBuilder(new ContextThemeWrapper(MainActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)).create();
                                alertDialog.setTitle("Delete saved entry");
                                alertDialog.setMessage("Do you want to delete your entry?");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Button btn = (Button) linearLayoutButtons.findViewWithTag(v.getTag().toString());
                                                saveState(btn.getTag().toString().split(" ")[0],
                                                        btn.getTag().toString().split(" ")[1], btn.getTag().toString().split(" ")[2], 1);
                                                linearLayoutButtons.removeView(btn);
                                            }
                                        });
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                return true;
                            }
                        });
                        btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_desktop_windows_48, 0, 0, 0);
                        linearLayoutButtons.addView(btn);

                        saveState(input.getText().toString(), macAddressText.getText().toString(), ipAddressText.getText().toString(), 0);
                        Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
                alertDialog.show();

            } else {
                Toast.makeText(getApplicationContext(), "Data failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenuItem1:
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(new ContextThemeWrapper(MainActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)).create();
                alertDialog.setTitle("Wake on LAN info");
                alertDialog.setMessage(getText(R.string.wakeOnLanInfo));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
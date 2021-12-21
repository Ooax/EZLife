package com.example.ezlifeandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button startPcButton;
    ImageButton infoButton;
    EditText ipAddressText, macAddressText;
    Spinner savedPCs;
    Button savePcButton;
    ArrayList<String> savedPcsList = new ArrayList<String>();
    ArrayList<String> savedPcsNamesList = new ArrayList<String>();

    private void saveState() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        //editor.putString("IpAddress", String.valueOf(ipAddressText.getText()));
        //editor.putString("MacAddress", String.valueOf(macAddressText.getText()));
        //editor.putString(name, saveString);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < savedPcsList.size(); i++){
            sb.append(savedPcsList.get(i)).append("/end");
        }
        editor.putString("SAVEDPCS", sb.toString());
        editor.apply();
    }

    private void loadState(){
        SharedPreferences state = getPreferences(MODE_PRIVATE);
        //ipAddressText.setText(state.getString("IpAddress", ""));
        //macAddressText.setText(state.getString("MacAddress", ""));
        String json = state.getString("SAVEDPCS", null);
        if (json != null) {
            savedPcsList = new ArrayList<String>(Arrays.asList(json.split("/end")));
            savedPcsNamesList = new ArrayList<String>(Arrays.asList(json.split("/end")));
            loadJson();
        }
    }

    private void saveJson(String name, String mac, String ip) {
        PCData pcData = new PCData(name, mac, ip);
        Gson gson = new Gson();
        String json1 = gson.toJson(pcData);
        String json2 = gson.toJson(pcData.name);
        savedPcsList.add(json1);
        savedPcsNamesList.add(json2);
        saveState();
    }

    private void loadJson() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, savedPcsList);
        savedPCs.setAdapter(adapter);
    }

    private Boolean areStringMatching() {
        Pattern IPV4_REGEX = Pattern.compile("(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))");
        Pattern MAC_REGEX = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        final String ipAddressString = ipAddressText.getText().toString();
        final String macAddressString = macAddressText.getText().toString();
        if(IPV4_REGEX.matcher(ipAddressString).matches() && MAC_REGEX.matcher(macAddressString).matches()) {
            return true;
        }
        else return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.wakeOnLanActivity);
        setContentView(R.layout.activity_main);
        ipAddressText = (EditText) findViewById(R.id.inputIp);
        macAddressText = (EditText) findViewById(R.id.inputMac);
        startPcButton = (Button) findViewById(R.id.buttonAddresses);
        infoButton = (ImageButton) findViewById(R.id.buttonInfo);
        savedPCs = (Spinner) findViewById(R.id.savedPC);
        savePcButton = (Button) findViewById(R.id.savePC);
        loadState();
        startPcButton.setOnClickListener((view) -> {
            if (areStringMatching()){
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            WakeOnLan.main(new String[] {ipAddressText.getText().toString(), macAddressText.getText().toString()});
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            else{
                Toast.makeText(getApplicationContext(), "Input data is invalid", Toast.LENGTH_LONG).show();
            }
        });

        infoButton.setOnClickListener((view) -> {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
                    savePcButton.setVisibility(View.GONE);
                }
                else {
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
                }
                else {
                    savePcButton.setVisibility(View.VISIBLE);
                }
            }
        });

        savePcButton.setOnClickListener((view) -> {
            if (areStringMatching()){
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Save PC configuration");
                alertDialog.setMessage("Set a title for your PC configuration");
                final EditText input = new EditText(this);
                alertDialog.setView(input);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveJson(input.getText().toString(), macAddressText.getText().toString(), ipAddressText.getText().toString());
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

            }
            else {
                Toast.makeText(getApplicationContext(), "Data failure", Toast.LENGTH_LONG).show();
            }
        });

        savedPCs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                String json = savedPCs.getSelectedItem().toString();
                PCData pcData = gson.fromJson(json, PCData.class);
                if (pcData != null){
                    macAddressText.setText(pcData.mac);
                    ipAddressText.setText(pcData.ip);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                macAddressText.setText("");
                ipAddressText.setText("");
            }
        });

        };
}
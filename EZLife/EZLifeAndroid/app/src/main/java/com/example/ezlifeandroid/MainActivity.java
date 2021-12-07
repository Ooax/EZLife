package com.example.ezlifeandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.InetAddresses;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button startPcButton;
    ImageButton infoButton;
    EditText ipAddressText, macAddressText;

    private void saveState() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("IpAddress", String.valueOf(ipAddressText.getText()));
        editor.putString("MacAddress", String.valueOf(macAddressText.getText()));
        editor.commit();
    }

    private void LoadState(){
        SharedPreferences state = getPreferences(MODE_PRIVATE);
        ipAddressText.setText(state.getString("IpAddress", ""));
        macAddressText.setText(state.getString("MacAddress", ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
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
        startPcButton.setOnClickListener((view) -> {
            Pattern IPV4_REGEX = Pattern.compile("(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))");
            Pattern MAC_REGEX = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
            final String ipAddressString = ipAddressText.getText().toString();
            final String macAddressString = macAddressText.getText().toString();
            if (IPV4_REGEX.matcher(ipAddressString).matches() && MAC_REGEX.matcher(macAddressString).matches()){
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            WakeOnLan.main(new String[] {ipAddressString, macAddressString});
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

        };
}
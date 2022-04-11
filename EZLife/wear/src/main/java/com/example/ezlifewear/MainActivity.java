package com.example.ezlifewear;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.ambient.AmbientModeSupport;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements AmbientModeSupport.AmbientCallbackProvider,
        DataClient.OnDataChangedListener, MessageClient.OnMessageReceivedListener, CapabilityClient.OnCapabilityChangedListener {

    private Context activityContext;
    private String TAG_MESSAGE_RECEIVED = "receive1";
    private String APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD";
    private Boolean mobileDeviceConnected = false;

    private String wearableAppCheckPayloadReturnACK = "AppOpenWearableACK";
    private String MESSAGE_ITEM_RECEIVED_PATH = "/message-item-received";

    private MessageEvent messageEvent = null;
    private String mobileNodeUri = null;

    private AmbientModeSupport.AmbientController ambientController;

    private Button sendmessageButtonW;
    private EditText messagecontentEditTextW;
    private TextView deviceconnectionStatusTvW;
    private TextView messagelogTextViewW;
    private ScrollView scrollviewTextMessageLogW;
    private TextInputLayout textInputLayoutW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityContext = this;
        ambientController = AmbientModeSupport.attach(this);

        sendmessageButtonW = (Button) findViewById(R.id.sendmessageButtonW);
        messagecontentEditTextW = findViewById(R.id.messagecontentEditTextW);
        deviceconnectionStatusTvW = findViewById(R.id.deviceconnectionStatusTvW);
        messagelogTextViewW = findViewById(R.id.messagelogTextViewW);
        scrollviewTextMessageLogW = findViewById(R.id.scrollviewTextMessageLogW);
        textInputLayoutW = findViewById(R.id.textInputLayoutW);

        sendmessageButtonW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileDeviceConnected) {
                    if (!messagecontentEditTextW.getText().toString().isEmpty()) {
                        String nodeId = messageEvent.getSourceNodeId();
                        byte[] payload = messagecontentEditTextW.getText().toString().getBytes(StandardCharsets.UTF_8);

                        Task<Integer> sendMessageTask = Wearable.getMessageClient(activityContext).sendMessage(nodeId, MESSAGE_ITEM_RECEIVED_PATH, payload);
                        deviceconnectionStatusTvW.setVisibility(View.GONE);

                        sendMessageTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                if (task.isSuccessful()) {
                                    Log.d("send1", "Message sent successfully");
                                    StringBuilder sbTemp = new StringBuilder();
                                    sbTemp.append("\n");
                                    sbTemp.append(messagecontentEditTextW.getText().toString());
                                    sbTemp.append(" (Sent to mobile)");
                                    Log.d("receive1", sbTemp.toString());
                                    messagelogTextViewW.append(sbTemp);

                                    //scrollviewTextMessageLog.requestFocus();
                                    //scrollviewTextMessageLog.
                                }
                                else {
                                    Log.d("send1", "Message failed.");
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(
                                activityContext,
                                "Message content is empty. Please enter some message and proceed",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        });
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        try {
            Log.d(TAG_MESSAGE_RECEIVED, "onMessageReceived event received");
            String s1 = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String messageEventPath = messageEvent.getPath();

            Log.d(
                    TAG_MESSAGE_RECEIVED,
                    "onMessageReceived() A message from watch was received:"
                            + messageEvent.getRequestId()
                            + " "
                            + messageEventPath
                            + " "
                            + s1
            );

            if (!messageEventPath.isEmpty() && messageEventPath.equals(APP_OPEN_WEARABLE_PAYLOAD_PATH)) {
                try {
                    String nodeId = messageEvent.getSourceNodeId().toString();
                    String returnPayloadAck = wearableAppCheckPayloadReturnACK;
                    byte[] payload = returnPayloadAck.getBytes();

                    Task<Integer> sendMessageTask = Wearable.getMessageClient(activityContext).sendMessage(nodeId, APP_OPEN_WEARABLE_PAYLOAD_PATH, payload);
                    Log.d(
                            TAG_MESSAGE_RECEIVED,
                            "Acknowledgement message successfully with payload : $returnPayloadAck"
                    );
                    this.messageEvent = messageEvent;
                    mobileNodeUri = messageEvent.getSourceNodeId();

                    sendMessageTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                        @Override
                        public void onComplete(@NonNull Task<Integer> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG_MESSAGE_RECEIVED, "Message sent successfully");
                                messagelogTextViewW.setVisibility(View.VISIBLE);

                                StringBuilder sbTemp = new StringBuilder();
                                sbTemp.append("\nMobile device connected.");
                                Log.d("receive1", sbTemp.toString());
                                messagelogTextViewW.append(sbTemp);
                                mobileDeviceConnected = true;

                                textInputLayoutW.setVisibility(View.VISIBLE);
                                sendmessageButtonW.setVisibility(View.VISIBLE);
                                deviceconnectionStatusTvW.setVisibility(View.VISIBLE);
                                deviceconnectionStatusTvW.setText("Mobile device is connected");
                            }
                            else {
                                Log.d(TAG_MESSAGE_RECEIVED, "Message failed.");
                            }
                        }
                    });
                }
                catch (Exception e) {
                    Log.d(
                            TAG_MESSAGE_RECEIVED,
                            "Handled in sending message back to the sending node"
                    );
                    e.printStackTrace();
                }
            }
            else if (!messageEventPath.isEmpty() && messageEventPath.equals(MESSAGE_ITEM_RECEIVED_PATH)) {
                try {
                    messagelogTextViewW.setVisibility(View.VISIBLE);
                    textInputLayoutW.setVisibility(View.VISIBLE);
                    sendmessageButtonW.setVisibility(View.VISIBLE);
                    deviceconnectionStatusTvW.setVisibility(View.GONE);

                    StringBuilder sbTemp = new StringBuilder();
                    sbTemp.append("\n");
                    sbTemp.append(s1);
                    sbTemp.append(" - (Received from mobile)");
                    Log.d("receive1", " $sbTemp");
                    messagelogTextViewW.append(sbTemp);

                    scrollviewTextMessageLogW.requestFocus();
                    scrollviewTextMessageLogW.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollviewTextMessageLogW.scrollTo(0, scrollviewTextMessageLogW.getBottom());
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG_MESSAGE_RECEIVED, "Handled in onMessageReceived");
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Wearable.getDataClient(activityContext).removeListener(this);
            Wearable.getMessageClient(activityContext).removeListener(this);
            Wearable.getCapabilityClient(activityContext).removeListener(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Wearable.getDataClient(activityContext).addListener(this);
            Wearable.getMessageClient(activityContext).addListener(this);
            Wearable.getCapabilityClient(activityContext);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);
        }

        @Override
        public void onUpdateAmbient() {
            super.onUpdateAmbient();
        }

        @Override
        public void onExitAmbient() {
            super.onExitAmbient();
        }
    }
}
package com.example.ezlife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ezlife.databinding.ActivityMessageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements DataClient.OnDataChangedListener, MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    Context activityContext = null;
    private String wearableAppCheckPayload = "AppOpenWearable";
    private String wearableAppCheckPayloadReturnACK = "AppOpenWearableACK";
    private Boolean wearableDeviceConnected = false;

    private String currentAckFromWearForAppOpenCheck = null;
    private final String APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD";

    private final String MESSAGE_ITEM_RECEIVED_PATH = "/message-item-received";

    private final String TAG_GET_NODES = "getnodes1";
    private final String TAG_MESSAGE_RECEIVED = "receive1";

    boolean[] getNodesResBool = null;

    private MessageEvent messageEvent = null;
    private String wearableNodeUri = null;

    private ActivityMessageBinding binding;

    private Button checkweareablesButton;
    private EditText messagecontentEditText;
    private TextView messagelogTextView;
    private ScrollView scrollviewText;
    private TextView deviceconnectionStatusTv;
    private Button sendmessageButton;
    private TextInputLayout textInputLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.wearableDataLayerActivity);
        setContentView(R.layout.activity_message);
        activityContext = this;
        wearableDeviceConnected = false;
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        checkweareablesButton = findViewById(R.id.checkwearablesButton);
        messagecontentEditText = findViewById(R.id.messagecontentEditText);
        scrollviewText = findViewById(R.id.scrollviewText);
        messagelogTextView = findViewById(R.id.messagelogTextView);
        deviceconnectionStatusTv = findViewById(R.id.deviceconnectionStatusTv);
        sendmessageButton = findViewById(R.id.sendmessageButton);
        textInputLayout = findViewById(R.id.textInputLayout);
        bottomNavigationView = findViewById(R.id.btm_nav_message);

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
                        startActivity(new Intent(getApplicationContext(), MessageActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        checkweareablesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!wearableDeviceConnected) {
                    Activity tempActivity = (MessageActivity)activityContext;
                    initialiseDevicePairing(tempActivity);
                }
            }
        });

        sendmessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wearableDeviceConnected) {
                    if (!messagecontentEditText.getText().toString().isEmpty()) {
                        String nodeId = messageEvent.getSourceNodeId();
                        byte[] payload = messagecontentEditText.getText().toString().getBytes(StandardCharsets.UTF_8);
                        Task<Integer> sendMessageTask = Wearable.getMessageClient(activityContext).sendMessage(nodeId, MESSAGE_ITEM_RECEIVED_PATH, payload);
                        sendMessageTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                if (task.isSuccessful()) {
                                    Log.d("send1", "Message sent successfully");
                                    StringBuilder sbTemp = new StringBuilder();
                                    sbTemp.append("\n");
                                    sbTemp.append(messagecontentEditText.getText().toString());
                                    sbTemp.append(" (Sent to Wearable)");
                                    Log.d("receive1", sbTemp.toString());
                                    messagelogTextView.append(sbTemp.toString());

                                    scrollviewText.requestFocus();
                                    //scrollviewText.post(scrollviewText.scrollTo(0, scrollviewText.getBottom()));
                                }
                                else {
                                    Log.d("send1", "Message failed.");
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(activityContext, "Message content is empty. Please enter some message and proceed",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        });
    }

    public void initialiseDevicePairing (Activity activity) {
        //launch(Dispatchers.getDefault()) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    getNodesResBool = getNodes(activity.getApplicationContext());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (getNodesResBool[0]) {
                    if (getNodesResBool[1]) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        activityContext,
                                        "Wearable device paired and app is open. Tap the \"Send Message to Wearable\" button to send the message to your wearable device.",
                                        Toast.LENGTH_LONG
                                ).show();
                                deviceconnectionStatusTv.setText("Wearable device paired and app is open.");
                                deviceconnectionStatusTv.setVisibility(View.VISIBLE);
                                wearableDeviceConnected = true;
                                sendmessageButton.setVisibility(View.VISIBLE);
                            }
                        });


                    }
                    else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        activityContext,
                                        "A wearable device is paired but the wearable app on your watch isn't open. Launch the wearable app and try again.",
                                        Toast.LENGTH_LONG
                                ).show();
                                deviceconnectionStatusTv.setText("Wearable device paired but app isn't open.");
                                deviceconnectionStatusTv.setVisibility(View.VISIBLE);
                                wearableDeviceConnected = false;
                                sendmessageButton.setVisibility(View.GONE);
                            }
                        });


                    }
                }
                else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    activityContext,
                                    "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
                                    Toast.LENGTH_LONG
                            ).show();
                            deviceconnectionStatusTv.setText("Wearable device not paired and connected.");
                            deviceconnectionStatusTv.setVisibility(View.VISIBLE);
                            wearableDeviceConnected = false;
                            sendmessageButton.setVisibility(View.GONE);
                        }
                    });


                }
            }
        }).start();


           // withContext(Dispatchers.getMain()) {
                /*if (getNodesResBool[0]) {
                    if (getNodesResBool[1]) {
                        Toast.makeText(
                                activityContext,
                                "Wearable device paired and app is open. Tap the \"Send Message to Wearable\" button to send the message to your wearable device.",
                                Toast.LENGTH_LONG
                        ).show();
                        deviceconnectionStatusTv.setText("Wearable device paired and app is open.");
                        deviceconnectionStatusTv.setVisibility(View.VISIBLE);
                        wearableDeviceConnected = true;
                        sendmessageButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(
                                activityContext,
                                "A wearable device is paired but the wearable app on your watch isn't open. Launch the wearable app and try again.",
                                Toast.LENGTH_LONG
                        ).show();
                        deviceconnectionStatusTv.setText("Wearable device paired but app isn't open.");
                        deviceconnectionStatusTv.setVisibility(View.VISIBLE);
                        wearableDeviceConnected = false;
                        sendmessageButton.setVisibility(View.GONE);
                    }
                }
                else {
                    Toast.makeText(
                            activityContext,
                            "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
                            Toast.LENGTH_LONG
                    ).show();
                    deviceconnectionStatusTv.setText("Wearable device not paired and connected.");
                    deviceconnectionStatusTv.setVisibility(View.VISIBLE);
                    wearableDeviceConnected = false;
                    sendmessageButton.setVisibility(View.GONE);
                }*/
           // }
        //}
    }

    private boolean[] getNodes(Context context) {
        HashSet<String> nodeResults = new HashSet<String>();
        boolean[] resBool = new boolean[2];
        resBool[0] = false;
        resBool[1] = false;
        Task<List<Node>> nodeListTask = Wearable.getNodeClient(context).getConnectedNodes();
        try {
            List<Node> nodes = Tasks.await(nodeListTask);
            Log.e(TAG_GET_NODES, "Task fetched nodes");
            for (Node node : nodes) {
                Log.e(TAG_GET_NODES, "inside loop");
                nodeResults.add(node.getId());
                try {
                    String nodeId = node.getId();
                    byte[] payload = wearableAppCheckPayload.getBytes();
                    Task<Integer> sendMessageTask = Wearable.getMessageClient(context).sendMessage(nodeId, APP_OPEN_WEARABLE_PAYLOAD_PATH, payload);
                    try {
                       Integer result = Tasks.await(sendMessageTask);
                       Log.d(TAG_GET_NODES, "send message result : " + result);
                       resBool[0] = true;

                       if (currentAckFromWearForAppOpenCheck == null || !currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                           Thread.sleep(100);
                           Log.d(TAG_GET_NODES, "ACK thread sleep 1");
                       }
                       if (currentAckFromWearForAppOpenCheck != null && currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                           resBool[1] = true;
                           return  resBool;
                       }
                        if (currentAckFromWearForAppOpenCheck == null || !currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            Thread.sleep(150);
                            Log.d(TAG_GET_NODES, "ACK thread sleep 2");
                        }
                        if (currentAckFromWearForAppOpenCheck != null && currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            resBool[1] = true;
                            return  resBool;
                        }
                        if (currentAckFromWearForAppOpenCheck == null || !currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            Thread.sleep(200);
                            Log.d(TAG_GET_NODES, "ACK thread sleep 3");
                        }
                        if (currentAckFromWearForAppOpenCheck != null && currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            resBool[1] = true;
                            return  resBool;
                        }
                        if (currentAckFromWearForAppOpenCheck == null || !currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            Thread.sleep(250);
                            Log.d(TAG_GET_NODES, "ACK thread sleep 4");
                        }
                        if (currentAckFromWearForAppOpenCheck != null && currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            resBool[1] = true;
                            return  resBool;
                        }
                        if (currentAckFromWearForAppOpenCheck == null || !currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            Thread.sleep(350);
                            Log.d(TAG_GET_NODES, "ACK thread sleep 5");
                        }
                        if (currentAckFromWearForAppOpenCheck != null && currentAckFromWearForAppOpenCheck.equals(wearableAppCheckPayloadReturnACK)) {
                            resBool[1] = true;
                            return  resBool;
                        }
                        resBool[1] = false;
                        Log.d(TAG_GET_NODES, "ACK thread timeout, no message received from the wearable ");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e) {
                    Log.d(TAG_GET_NODES, "send message exception");
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG_GET_NODES, "Task failed: " + e);
            e.printStackTrace();
        }
        return resBool;
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        try {
            String s = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String messageEventPath = messageEvent.getPath();
            Log.d(
                    TAG_MESSAGE_RECEIVED,
                    "onMessageReceived() Received a message from watch:"
                            + messageEvent.getRequestId()
                            + " "
                            + messageEventPath
                            + " "
                            + s
            );
            if (messageEventPath.equals(APP_OPEN_WEARABLE_PAYLOAD_PATH)) {
                currentAckFromWearForAppOpenCheck = s;
                Log.d(
                        TAG_MESSAGE_RECEIVED,
                        "Received acknowledgement message that app is open in wear"
                );

                StringBuilder sbTemp = new StringBuilder();
                sbTemp.append(messagelogTextView.getText().toString());
                sbTemp.append("Wearable device connected");
                Log.d("receive1", sbTemp.toString());
                messagelogTextView.setText(sbTemp);
                textInputLayout.setVisibility(View.VISIBLE);
                checkweareablesButton.setVisibility(View.GONE);
                this.messageEvent = messageEvent;
                wearableNodeUri = messageEvent.getSourceNodeId();
            }
            else if (!messageEventPath.isEmpty() && messageEventPath.equals(MESSAGE_ITEM_RECEIVED_PATH)) {
                try {
                    messagelogTextView.setVisibility(View.VISIBLE);
                    textInputLayout.setVisibility(View.VISIBLE);
                    sendmessageButton.setVisibility(View.VISIBLE);

                    StringBuilder sbTemp = new StringBuilder();
                    sbTemp.append("\n");
                    sbTemp.append(s);
                    sbTemp.append(" - (Received from wearable)");
                    Log.d("receive1", sbTemp.toString());

                    messagelogTextView.append(sbTemp);
                    scrollviewText.requestFocus();
                    scrollviewText.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollviewText.scrollTo(0, scrollviewText.getBottom());
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("receive1", "Handled");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Wearable.getMessageClient(activityContext).removeListener(this);
            Wearable.getDataClient(activityContext).removeListener(this);
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
            Wearable.getCapabilityClient(activityContext).addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

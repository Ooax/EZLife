package com.example.ezlife;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

public class Message implements MessageClient.OnMessageReceivedListener {
    private static String wearableAppCheckPayload = "AppOpenWearable";
    private static String wearableAppCheckPayloadReturnACK = "AppOpenWearableACK";
    public Boolean wearableDeviceConnected = false;

    private static String currentAckFromWearForAppOpenCheck = null;
    private static final String APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD";

    private static final String MESSAGE_ITEM_RECEIVED_PATH = "/message-item-received";

    private static final String TAG_GET_NODES = "getnodes1";
    private static final String TAG_MESSAGE_RECEIVED = "receive1";

    static boolean[]  getNodesResBool = null;

    private static MessageEvent messageEvent = null;
    private static String wearableNodeUri = null;

    public void initialiseDevicePairing(Activity activityContext) {
        //launch(Dispatchers.getDefault()) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    getNodesResBool = getNodes(activityContext.getApplicationContext());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (getNodesResBool[0]) {
                    if (getNodesResBool[1]) {
                        activityContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        activityContext,
                                        "Wearable device paired and app is open. Tap the \"Send Message to Wearable\" button to send the message to your wearable device.",
                                        Toast.LENGTH_LONG
                                ).show();
                                wearableDeviceConnected = true;
                            }
                        });


                    }
                    else {
                        activityContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        activityContext,
                                        "A wearable device is paired but the wearable app on your watch isn't open. Launch the wearable app and try again.",
                                        Toast.LENGTH_LONG
                                ).show();
                                wearableDeviceConnected = false;
                            }
                        });


                    }
                }
                else {
                    activityContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    activityContext,
                                    "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
                                    Toast.LENGTH_LONG
                            ).show();
                            wearableDeviceConnected = false;
                        }
                    });


                }
            }
        }).start();
    }

    public void addListener(Activity activityContext) {
        Wearable.getMessageClient(activityContext).addListener(this);
    }

    public void removeListener(Activity activityContext) {
        Wearable.getMessageClient(activityContext).removeListener(this);
    }

    public void sendMessage(byte[] payload, Activity activityContext) {
        if (wearableDeviceConnected) {
                String nodeId = messageEvent.getSourceNodeId();
                Task<Integer> sendMessageTask = Wearable.getMessageClient(activityContext).sendMessage(nodeId, MESSAGE_ITEM_RECEIVED_PATH, payload);
                sendMessageTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                    @Override
                    public void onComplete(@NonNull Task<Integer> task) {
                        if (task.isSuccessful()) {
                            Log.d("send1", "Message sent successfully");
                        }
                        else {
                            Log.d("send1", "Message failed.");
                        }
                    }
                });
            }
        }

    private static boolean[] getNodes(Context context) {
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
                sbTemp.append("Wearable device connected");
                Log.d("receive1", sbTemp.toString());
                this.messageEvent = messageEvent;
                wearableNodeUri = messageEvent.getSourceNodeId();
            }
            else if (!messageEventPath.isEmpty() && messageEventPath.equals(MESSAGE_ITEM_RECEIVED_PATH)) {
                try {
                    StringBuilder sbTemp = new StringBuilder();
                    sbTemp.append("\n");
                    sbTemp.append(s);
                    sbTemp.append(" - (Received from wearable)");
                    Log.d("receive1", sbTemp.toString());
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
}

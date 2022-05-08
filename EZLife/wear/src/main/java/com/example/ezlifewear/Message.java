package com.example.ezlifewear;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;

public class Message implements MessageClient.OnMessageReceivedListener {

    private Context activityContext;
    private String TAG_MESSAGE_RECEIVED = "receive1";
    private String APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD";
    private Boolean mobileDeviceConnected = false;

    private String wearableAppCheckPayloadReturnACK = "AppOpenWearableACK";
    private String MESSAGE_ITEM_RECEIVED_PATH = "/message-item-received";

    private MessageEvent messageEvent = null;
    private String mobileNodeUri = null;

    public void addListener(Activity activityContext) {
        Wearable.getMessageClient(activityContext).addListener(this);
    }

    public void removeListener(Activity activityContext) {
        Wearable.getMessageClient(activityContext).removeListener(this);
    }

    public void SendMessage(byte[] payload, Activity activityContext) {
        if (mobileDeviceConnected) {
                String nodeId = messageEvent.getSourceNodeId();

                Task<Integer> sendMessageTask = Wearable.getMessageClient(activityContext).sendMessage(nodeId, MESSAGE_ITEM_RECEIVED_PATH, payload);

                sendMessageTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                    @Override
                    public void onComplete(@NonNull Task<Integer> task) {
                        if (task.isSuccessful()) {
                            Log.d("send1", "Message sent successfully");
                            StringBuilder sbTemp = new StringBuilder();
                            sbTemp.append("\n");
                            sbTemp.append(" (Sent to mobile)");
                            Log.d("receive1", sbTemp.toString());
                        }
                        else {
                            Log.d("send1", "Message failed.");
                        }
                    }
                });
        }
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
                                StringBuilder sbTemp = new StringBuilder();
                                sbTemp.append("\nMobile device connected.");
                                Log.d("receive1", sbTemp.toString());
                                mobileDeviceConnected = true;
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
                    StringBuilder sbTemp = new StringBuilder();
                    sbTemp.append("\n");
                    sbTemp.append(s1);
                    sbTemp.append(" - (Received from mobile)");
                    Log.d("receive1", " $sbTemp");
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
}

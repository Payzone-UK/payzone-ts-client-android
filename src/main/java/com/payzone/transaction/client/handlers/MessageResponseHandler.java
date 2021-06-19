package com.payzone.transaction.client.handlers;

// handler for message from service

import android.os.Handler;
import android.os.Message;

import com.payzone.transaction.client.MessageCommands;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageResponseHandler extends Handler {
    public JSONObject responseObject = null;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MessageCommands.MSG_REGISTER_DEVICE:
                System.out.println("## Device Registered Response");
                String response = msg.getData().getString("registerResponse"); //msg received from service
                System.out.println("## registerParams Response from Server = "+response);
                try {
                    responseObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case MessageCommands.MSG_INIT_TRANSACTION:
                System.out.println("## Transaction Initialised Response");
                break;
            case MessageCommands.MSG_MARK_TRANS_SUCCESS:
                System.out.println("## Marked Successful Response");
                break;
            case MessageCommands.MSG_MARK_TRANS_FAILED:
                System.out.println("## Marked Failed Response");
                break;
            case MessageCommands.MSG_MARK_RECEIPT_PRINTED:
                System.out.println("## Marked Receipt Printed Response");
                break;
            default:
                super.handleMessage(msg);
        }
    }
}
package com.payzone.transaction.client.handlers;

// handler for message from service

import android.os.Handler;
import android.os.Message;

import com.payzone.transaction.client.MessageConstants;

public class MessageResponseHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        String response;
        switch (msg.what) {
            case MessageConstants.MSG_REGISTER_DEVICE:
                response = msg.getData().getString(MessageConstants.RESP_REGISTER_DEVICE);
                System.out.println("## Register Device Response = "+response);
                break;
            case MessageConstants.MSG_INIT_TRANSACTION:
                response = msg.getData().getString(MessageConstants.RESP_INIT_TRANSACTION);
                System.out.println("## Transaction Initialised Response = "+response);
                break;
            case MessageConstants.MSG_MARK_TRANS_SUCCESS:
                response = msg.getData().getString(MessageConstants.RESP_MARK_TRANS_SUCCESS);
                System.out.println("## Marked Successful Response = "+response);
                break;
            case MessageConstants.MSG_MARK_TRANS_FAILED:
                response = msg.getData().getString(MessageConstants.RESP_MARK_TRANS_FAILED);
                System.out.println("## Marked Failed Response = "+response);
                break;
            case MessageConstants.MSG_MARK_RECEIPT_PRINTED:
                response = msg.getData().getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED);
                System.out.println("## Marked Receipt Printed Response = "+response);
                break;
            default:
                super.handleMessage(msg);
        }
    }
}
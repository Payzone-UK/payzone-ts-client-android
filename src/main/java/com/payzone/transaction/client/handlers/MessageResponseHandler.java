package com.payzone.transaction.client.handlers;

// handler for message from service

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.payzone.transaction.client.ApiClient;
import com.payzone.transaction.client.MessageConstants;

public class MessageResponseHandler extends Handler {
    private static final String TAG = MessageResponseHandler.class.getSimpleName();

    public MessageResponseHandler() {
        super(Looper.getMainLooper());
    }

    String response;

    @Override
    public void handleMessage(Message msg) {
        String error = msg.getData().getString(MessageConstants.RESP_SEND_FAILURE_REASON);
        if (error != null) {
            Log.e(TAG, "Send failure for request " + msg.what + ": " + error);
            return;
        }
        switch (msg.what) {
            case MessageConstants.MSG_REGISTER_DEVICE:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_REGISTER_DEVICE));
                Log.d(TAG, "Register Device Response received");
                break;
            case MessageConstants.MSG_INIT_TRANSACTION:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_INIT_TRANSACTION));
                Log.d(TAG, "Transaction Initialised Response received");
                break;
            case MessageConstants.MSG_COMPLETE_TRANS:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_COMPLETE_TRANS));
                Log.d(TAG, "Transaction Completed Response received");
                break;
            case MessageConstants.MSG_MARK_TRANS_SUCCESS:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_SUCCESS));
                Log.d(TAG, "Marked Successful Response received");
                break;
            case MessageConstants.MSG_MARK_TRANS_FAILED:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_FAILED));
                Log.d(TAG, "Marked Failed Response received");
                break;
            case MessageConstants.MSG_MARK_RECEIPT_PRINTED:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED));
                Log.d(TAG, "Marked Receipt Printed Response received");
                break;
            case MessageConstants.MSG_TALEXUS_READ_KEY:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_READ_KEY));
                Log.d(TAG, "Talexus Read key response received");
                break;
            case MessageConstants.MSG_TALEXUS_ADD_CREDIT:
            case MessageConstants.PZ_MSG_TALEXUS_ADD_CREDIT:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_ADD_CREDIT));
                Log.d(TAG, "Talexus add credit Response received");
                break;
            case MessageConstants.MSG_TALEXUS_RTI:
            case MessageConstants.PZ_MSG_TALEXUS_RTI:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_RTI));
                Log.d(TAG, "Talexus RTI Response received");
                break;
            case MessageConstants.MSG_TALEXUS_IS_KEY_INSERTED:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED));
                Log.d(TAG, "Talexus key inserted Response received");
                break;
            case MessageConstants.MSG_TALEXUS_REVERSE_CREDIT:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_REVERSE_CREDIT));
                Log.d(TAG, "Talexus Reversal Response received");
                break;
            case MessageConstants.MSG_INIT_TALEXUS:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_INIT_TALEXUS));
                Log.d(TAG, "Talexus initialisation Response received");
                break;
            case MessageConstants.MSG_STOP_TALEXUS:
                response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_STOP_TALEXUS));
                Log.d(TAG, "Talexus stop Response received");
                break;
            default:
                super.handleMessage(msg);
        }
    }
}
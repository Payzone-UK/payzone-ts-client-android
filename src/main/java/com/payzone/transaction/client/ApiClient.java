package com.payzone.transaction.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.payzone.transaction.client.handlers.MessageResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiClient extends Handler {
    /**
     * Messenger for communicating with the service.
     */
    Messenger mService;
    Context ctx;
    public MessageResponseHandler messageResponseHandler;
    public Messenger replyMessenger;
    //boolean variable to keep a check on service bind and unbind event
    public boolean mBound = false;
    private ServiceConnection mConnection;

    public ApiClient(Context ctx, Messenger messenger) {
        this.ctx = ctx;
        if(messenger != null) {
            this.replyMessenger = messenger;
        } else { // Use Default MessageResponseHandler from Library
            this.messageResponseHandler = new MessageResponseHandler();
            this.replyMessenger = new Messenger(messageResponseHandler);
        }

        this.mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the object we can use to
                // interact with the service.  We are communicating with the
                // service using a Messenger, so here we get a client-side
                // representation of that from the raw IBinder object.
                mService = new Messenger(service);
                mBound = true;
                System.out.println("## Service Connection Established...");
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                mService = null;
                mBound = false;
            }
        };
    }

    public void initService() {
        Intent intent = new Intent();
        intent.setComponent(
                new ComponentName("com.payzone.transaction",
                        "com.payzone.transaction.services.TransactionService"));
        boolean bindResult = ctx.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        System.out.println("## bindResult is: "+ bindResult);
    }

    public boolean destroyService(){
        if (mBound) {
            ctx.unbindService(mConnection);
            mBound = false;
        }
        return true;
    }

    public boolean registerDevice(JSONObject jsonParams) throws JSONException {
        JSONObject registerJsonObj = new JSONObject();
        registerJsonObj.put("terminal", jsonParams);
        return sendMessage(
                MessageConstants.MSG_REGISTER_DEVICE,
                MessageConstants.RESP_REGISTER_DEVICE,
                registerJsonObj.toString()
        );
    }

    public boolean initTransaction(JSONObject jsonParams) throws JSONException {
        JSONObject purchaseJsonObj = new JSONObject();
        purchaseJsonObj.put("purchase", jsonParams);
        return sendMessage(
                MessageConstants.MSG_INIT_TRANSACTION,
                MessageConstants.RESP_INIT_TRANSACTION,
                purchaseJsonObj.toString()
        );
    }

    public boolean completeTransaction(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_COMPLETE_TRANS,
                MessageConstants.RESP_COMPLETE_TRANS,
                jsonParams.toString()
        );
    }

    public boolean markTransactionSuccess(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_MARK_TRANS_SUCCESS,
                MessageConstants.RESP_MARK_TRANS_SUCCESS,
                jsonParams.toString()
        );
    }

    public boolean markTransactionFailed(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_MARK_TRANS_FAILED,
                MessageConstants.RESP_MARK_TRANS_FAILED,
                jsonParams.toString()
        );
    }

    public boolean markReceiptPrinted(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_MARK_RECEIPT_PRINTED,
                MessageConstants.RESP_MARK_RECEIPT_PRINTED,
                jsonParams.toString()
        );
    }

    public boolean getToken(JSONObject jsonParams) throws JSONException {
        return sendMessage(
                MessageConstants.MSG_GET_TOKEN,
                MessageConstants.RESP_GET_TOKEN,
                jsonParams.toString()
        );
    }

    public boolean startSession(JSONObject jsonParams) throws JSONException {
        return sendMessage(
                MessageConstants.MSG_START_SESSION,
                MessageConstants.RESP_START_SESSION,
                jsonParams.toString()
        );
    }

    public boolean storeCashierId(String cashierId) {
        if (cashierId == null) {
            throw new NullPointerException();
        }
        return sendMessage(
                MessageConstants.MSG_STORE_CID,
                MessageConstants.RESP_STORE_CID,
                cashierId
        );
    }

    private boolean sendMessage(int request, String responseKey, String payload) {
        return postDelayed(new Runnable() {
            public void run() {
                System.out.println("## MBOUND is: " + mBound);
                if (!mBound) {
                    throw new RuntimeException("No Bindings with Transaction service.");
                }
                // Create and send a message to the service, using a supported 'what' value
                Message msg = Message.obtain(null, request, 0, 0);
                msg.replyTo = replyMessenger;
                try {
                    Bundle data = new Bundle();
                    data.putString("responseKey", responseKey);
                    data.putString(responseKey, payload);
                    data.putString("packageName", ctx.getPackageName());
                    msg.setData(data);
                    mService.send(msg);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            }
        }, 3000);
    }
}

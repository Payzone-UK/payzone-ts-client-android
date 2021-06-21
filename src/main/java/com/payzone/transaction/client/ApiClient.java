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

    public boolean registerDevice(JSONObject registerJsonObj) {
        return postDelayed(new Runnable() {
            public void run() {
                System.out.println("## MBOUND is: " + mBound);
                if (!mBound) {
                    return;
                }
                // Create and send a message to the service, using a supported 'what' value
                Message msg = Message.obtain(null, MessageConstants.MSG_REGISTER_DEVICE, 0, 0);
                msg.replyTo = replyMessenger;
                try {
                    Bundle data = new Bundle();
                    data.putString(MessageConstants.RESP_REGISTER_DEVICE, registerJsonObj.toString());
                    msg.setData(data);
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }, 3000);
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
}

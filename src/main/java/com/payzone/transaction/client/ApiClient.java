package com.payzone.transaction.client;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;

import com.payzone.transaction.client.handlers.MessageResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

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
    private boolean isKeyInserted = false;
    private boolean isBoxConnected = false;
    public int retry = 0;
    final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isKeyInserted = intent.getExtras().getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED);
        }
    };
    private final BroadcastReceiver mHandleBoxStatusMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isBoxConnected = intent.getExtras().getBoolean(MessageConstants.RESP_TALEXUS_BOX_STATUS);
        }
    };

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
                fetchConfigData();
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
        ctx.registerReceiver(mHandleMessageReceiver, new IntentFilter(MessageConstants.ACTION_KEY_INSERTED));
        ctx.registerReceiver(mHandleBoxStatusMessageReceiver, new IntentFilter(MessageConstants.ACTION_TALEXUS_BOX_STATUS));
        Intent intent = new Intent();
        intent.setComponent(
                new ComponentName("com.payzone.transaction",
                        "com.payzone.transaction.services.TransactionService"));
        boolean bindResult = ctx.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        System.out.println("## Binding in progress: "+ bindResult);
    }

    /**
     * To destroy the service. Bringing back this for backward compatibility
     */
    public boolean destroyService(){
        ctx.unregisterReceiver(mHandleMessageReceiver);
        ctx.unregisterReceiver(mHandleBoxStatusMessageReceiver);
        if (mBound) {
            ctx.unbindService(mConnection);
            mBound = false;
        }
        return true;
    }

    public void fetchConfigData() {
        boolean res = fetchMyConfigData();
        System.out.println("## Fetch Config Data: "+ res);
    }

    private boolean fetchMyConfigData() {
        return sendMessage(
                MessageConstants.MSG_CONFIG_SETUP,
                MessageConstants.RESP_CONFIG_SETUP,
                ""
        );
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

    public boolean getToken(String tId) {
        return sendMessage(
                MessageConstants.MSG_GET_TOKEN,
                MessageConstants.RESP_GET_TOKEN,
                tId
        );
    }

    public boolean getTokenBySerialNumber(String serialNumber) {
        return sendMessage(
                MessageConstants.MSG_GET_TOKEN_BY_SERIAL_NUMBER,
                MessageConstants.RESP_GET_TOKEN_BY_SERIAL_NUMBER,
                serialNumber
        );
    }

    public boolean startSession(JSONObject jsonParams) throws JSONException {
        JSONObject sessionJsonObj = new JSONObject();
        sessionJsonObj.put("session", jsonParams);
        return sendMessage(
                MessageConstants.MSG_START_SESSION,
                MessageConstants.RESP_START_SESSION,
                sessionJsonObj.toString()
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

    public boolean isTransactionReady() {
        return sendMessage(
                MessageConstants.MSG_IS_TRANSACTION_READY,
                MessageConstants.RESP_IS_TRANSACTION_READY,
                "");
    }

    public boolean readKey() {
        return sendMessage(
                MessageConstants.MSG_TALEXUS_READ_KEY,
                MessageConstants.RESP_TALEXUS_READ_KEY,
                ""
        );
    }

    public boolean addCredit(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_TALEXUS_ADD_CREDIT,
                MessageConstants.RESP_TALEXUS_ADD_CREDIT,
                jsonParams.toString()
        );
    }

    public boolean rti(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_TALEXUS_RTI,
                MessageConstants.RESP_TALEXUS_RTI,
                jsonParams.toString()
        );
    }

    public boolean isKeyInserted() {
        return sendMessage(
                MessageConstants.MSG_TALEXUS_IS_KEY_INSERTED,
                MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED,
                ""
        );
    }

    public boolean isBoxConnected() {
        return sendMessage(
                MessageConstants.MSG_TALEXUS_BOX_CONNECTED,
                MessageConstants.RESP_TALEXUS_BOX_STATUS,
                ""
        );
    }

    public boolean reversal(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_TALEXUS_REVERSE_CREDIT,
                MessageConstants.RESP_TALEXUS_REVERSE_CREDIT,
                jsonParams.toString()
        );
    }

    public boolean nspHotcard(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_QUANTUM_NSP_HOT_CARD,
                MessageConstants.RESP_QUANTUM_NSP_HOT_CARD,
                jsonParams.toString()
        );
    }
    public boolean securityKeys(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_QUANTUM_SECURITY_KEYS,
                MessageConstants.RESP_QUANTUM_SECURITY_KEYS,
                jsonParams.toString()
        );
    }
    public boolean localSecretCode(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_QUANTUM_LOCAL_SECRET_CODE,
                MessageConstants.RESP_QUANTUM_LOCAL_SECRET_CODE,
                jsonParams.toString()
        );
    }
    public boolean csRegional(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_QUANTUM_CS_REGIONAL,
                MessageConstants.RESP_QUANTUM_CS_REGIONAL,
                jsonParams.toString()
        );
    }
    public boolean quantumTransactionComplete(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_QUANTUM_TRANSACTION_COMPLETE,
                MessageConstants.RESP_QUANTUM_TRANSACTION_COMPLETE,
                jsonParams.toString()
        );
    }
    public boolean quantumRtiTransaction(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_QUANTUM_RTI,
                MessageConstants.RESP_QUANTUM_RTI,
                jsonParams.toString()
        );
    }
    public boolean sale(JSONObject jsonParams) {
        return sendMessage(
                MessageConstants.MSG_QUANTUM_SALE,
                MessageConstants.RESP_QUANTUM_SALE,
                jsonParams.toString()
        );
    }
    public boolean openBasket(String basketId) {
        return sendMessage(
                MessageConstants.MSG_OPEN_BASKET,
                MessageConstants.RESP_OPEN_BASKET,
                basketId
        );
    }
    public boolean closeBasket(String basketId) {
        return sendMessage(
                MessageConstants.MSG_CLOSE_BASKET,
                MessageConstants.RESP_CLOSE_BASKET,
                basketId
        );
    }
    public static String decompressData(String zipText) {
        String sReturn = "";
        try {
            byte[] compressed = Base64.decode(zipText, Base64.DEFAULT);
            if (compressed.length > 4) {
                GZIPInputStream gzipInputStream = null;
                    gzipInputStream = new GZIPInputStream(
                            new ByteArrayInputStream(compressed, 4,
                                    compressed.length - 4));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int value = 0; value != -1;) {
                    value = gzipInputStream.read();
                    if (value != -1) {
                        baos.write(value);
                    }
                }
                gzipInputStream.close();
                baos.close();
                sReturn = new String(baos.toByteArray(), "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sReturn;
    }

    private boolean sendMessage(int request, String responseKey, String payload) {
        // Create and send a message to the service, using a supported 'what' value
        retry = 0;
        return postDelayed(new Runnable() {
            public void run() {
                long currentTime = System.currentTimeMillis();
                long stopTime = currentTime + 20000;
                System.out.println("## mBound is: " + mBound);
                // Create and send a message to the service, using a supported 'what' value
                Message msg = Message.obtain(null, request, 0, 0);
                msg.replyTo = replyMessenger;
                System.out.println("## Started sending message at: "+ currentTime);
                try {
                    Bundle data = new Bundle();
                    data.putString("responseKey", responseKey);
                    data.putString(responseKey, payload);
                    data.putString("packageName", ctx.getPackageName());
                    msg.setData(data);
                    while (currentTime <= stopTime) {
                        if (mBound) {
                            mService.send(msg);
                            System.out.println("## Message code "+ request +" sent at : "+ currentTime);
                            break;
                        }
                        currentTime = System.currentTimeMillis();
                    }
                    if (!mBound) {
                        retry++;
                        if(retry < 5) {
                            Thread.sleep(1000);
                            run();
                        }
                    }
                }
                catch (RemoteException e) {}
                catch (InterruptedException e) {}
            }
        }, 1000);
    }
}

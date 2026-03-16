package com.payzone.transaction.client;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.payzone.transaction.client.handlers.MessageResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class ApiClient {
    static final String TAG = ApiClient.class.getSimpleName();
    private final Handler handler = new Handler(Looper.getMainLooper());

    /** Number of bytes prepended to the GZIP payload before Base64 encoding. */
    private static final int GZIP_HEADER_SKIP_BYTES = 4;
    /** How long to wait for the service to bind before reporting a timeout failure. */
    private static final int SERVICE_BIND_TIMEOUT_SECONDS = 20;

    private static final String PAYZONE_SERVICE_PACKAGE = "com.payzone.transaction";

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
    private volatile CountDownLatch serviceBoundLatch = new CountDownLatch(1);
    final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                isKeyInserted = extras.getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED);
            }
        }
    };
    private final BroadcastReceiver mHandleBoxStatusMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                isBoxConnected = extras.getBoolean(MessageConstants.RESP_TALEXUS_BOX_STATUS);
            }
        }
    };

    public ApiClient(Context ctx, Messenger messenger) {
        this.ctx = ctx.getApplicationContext();
        if(messenger != null) {
            this.replyMessenger = messenger;
        } else { // Use Default MessageResponseHandler from Library
            this.messageResponseHandler = new MessageResponseHandler();
            this.replyMessenger = new Messenger(messageResponseHandler);
        }

        this.mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                if (!PAYZONE_SERVICE_PACKAGE.equals(className.getPackageName())) {
                    Log.e(TAG, "Rejecting connection from unexpected package: " + className.getPackageName());
                    ctx.unbindService(mConnection);
                    return;
                }
                mService = new Messenger(service);
                mBound = true;
                serviceBoundLatch.countDown();
                Log.d(TAG, "Service Connection Established");
                fetchConfigData();
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                mService = null;
                mBound = false;
                serviceBoundLatch = new CountDownLatch(1);
            }
        };
    }

    public void initService() {
        registerReceiverCompat(mHandleMessageReceiver, new IntentFilter(MessageConstants.ACTION_KEY_INSERTED));
        registerReceiverCompat(mHandleBoxStatusMessageReceiver, new IntentFilter(MessageConstants.ACTION_TALEXUS_BOX_STATUS));
        Intent intent = new Intent();
        intent.setComponent(
                new ComponentName(PAYZONE_SERVICE_PACKAGE,
                        PAYZONE_SERVICE_PACKAGE + ".services.TransactionService"));
        boolean bindResult = ctx.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Binding in progress: " + bindResult);
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
        Log.d(TAG, "Fetch Config Data: "+ res);
    }

    private boolean fetchMyConfigData() {
        return sendMessage(
                MessageConstants.MSG_CONFIG_SETUP,
                MessageConstants.RESP_CONFIG_SETUP,
                ""
        );
    }

    public boolean initTalexus() {
        return sendMessage(
                MessageConstants.MSG_INIT_TALEXUS,
                MessageConstants.RESP_INIT_TALEXUS,
                ""
        );
    }

    public boolean stopTalexus() {
        return sendMessage(
                MessageConstants.MSG_STOP_TALEXUS,
                MessageConstants.RESP_STOP_TALEXUS,
                ""
        );
    }

    public boolean registerDevice(JSONObject jsonParams) throws JSONException {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        JSONObject registerJsonObj = new JSONObject();
        registerJsonObj.put("terminal", jsonParams);
        return sendMessage(
                MessageConstants.MSG_REGISTER_DEVICE,
                MessageConstants.RESP_REGISTER_DEVICE,
                registerJsonObj.toString()
        );
    }

    public boolean initTransaction(JSONObject jsonParams) throws JSONException {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        JSONObject purchaseJsonObj = new JSONObject();
        purchaseJsonObj.put("purchase", jsonParams);
        return sendMessage(
                MessageConstants.MSG_INIT_TRANSACTION,
                MessageConstants.RESP_INIT_TRANSACTION,
                purchaseJsonObj.toString()
        );
    }

    public boolean completeTransaction(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_COMPLETE_TRANS,
                MessageConstants.RESP_COMPLETE_TRANS,
                jsonParams.toString()
        );
    }

    public boolean markTransactionSuccess(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_MARK_TRANS_SUCCESS,
                MessageConstants.RESP_MARK_TRANS_SUCCESS,
                jsonParams.toString()
        );
    }

    public boolean markTransactionFailed(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_MARK_TRANS_FAILED,
                MessageConstants.RESP_MARK_TRANS_FAILED,
                jsonParams.toString()
        );
    }

    public boolean markReceiptPrinted(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_MARK_RECEIPT_PRINTED,
                MessageConstants.RESP_MARK_RECEIPT_PRINTED,
                jsonParams.toString()
        );
    }

    public boolean getToken(String tId) {
        Objects.requireNonNull(tId, "tId must not be null");
        return sendMessage(
                MessageConstants.MSG_GET_TOKEN,
                MessageConstants.RESP_GET_TOKEN,
                tId
        );
    }

    public boolean getTokenBySerialNumber(String serialNumber) {
        Objects.requireNonNull(serialNumber, "serialNumber must not be null");
        return sendMessage(
                MessageConstants.MSG_GET_TOKEN_BY_SERIAL_NUMBER,
                MessageConstants.RESP_GET_TOKEN_BY_SERIAL_NUMBER,
                serialNumber
        );
    }

    public boolean startSession(JSONObject jsonParams) throws JSONException {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        JSONObject sessionJsonObj = new JSONObject();
        sessionJsonObj.put("session", jsonParams);
        return sendMessage(
                MessageConstants.MSG_START_SESSION,
                MessageConstants.RESP_START_SESSION,
                sessionJsonObj.toString()
        );
    }

    public boolean storeCashierId(String cashierId) {
        Objects.requireNonNull(cashierId, "cashierId must not be null");
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
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_TALEXUS_ADD_CREDIT,
                MessageConstants.RESP_TALEXUS_ADD_CREDIT,
                jsonParams.toString()
        );
    }

    public boolean rti(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_TALEXUS_RTI,
                MessageConstants.RESP_TALEXUS_RTI,
                jsonParams.toString()
        );
    }

    public boolean pzAddCredit(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.PZ_MSG_TALEXUS_ADD_CREDIT,
                MessageConstants.RESP_TALEXUS_ADD_CREDIT,
                jsonParams.toString()
        );
    }

    public boolean pzRti(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.PZ_MSG_TALEXUS_RTI,
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
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_TALEXUS_REVERSE_CREDIT,
                MessageConstants.RESP_TALEXUS_REVERSE_CREDIT,
                jsonParams.toString()
        );
    }

    public boolean nspHotcard(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_QUANTUM_NSP_HOT_CARD,
                MessageConstants.RESP_QUANTUM_NSP_HOT_CARD,
                jsonParams.toString()
        );
    }
    public boolean securityKeys(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_QUANTUM_SECURITY_KEYS,
                MessageConstants.RESP_QUANTUM_SECURITY_KEYS,
                jsonParams.toString()
        );
    }
    public boolean localSecretCode(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_QUANTUM_LOCAL_SECRET_CODE,
                MessageConstants.RESP_QUANTUM_LOCAL_SECRET_CODE,
                jsonParams.toString()
        );
    }
    public boolean csRegional(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_QUANTUM_CS_REGIONAL,
                MessageConstants.RESP_QUANTUM_CS_REGIONAL,
                jsonParams.toString()
        );
    }
    public boolean quantumTransactionComplete(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_QUANTUM_TRANSACTION_COMPLETE,
                MessageConstants.RESP_QUANTUM_TRANSACTION_COMPLETE,
                jsonParams.toString()
        );
    }
    public boolean quantumRtiTransaction(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_QUANTUM_RTI,
                MessageConstants.RESP_QUANTUM_RTI,
                jsonParams.toString()
        );
    }
    public boolean sale(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_QUANTUM_SALE,
                MessageConstants.RESP_QUANTUM_SALE,
                jsonParams.toString()
        );
    }
    public boolean openBasket(String basketId) {
        Objects.requireNonNull(basketId, "basketId must not be null");
        return sendMessage(
                MessageConstants.MSG_OPEN_BASKET,
                MessageConstants.RESP_OPEN_BASKET,
                basketId
        );
    }
    public boolean closeBasket(String basketId) {
        Objects.requireNonNull(basketId, "basketId must not be null");
        return sendMessage(
                MessageConstants.MSG_CLOSE_BASKET,
                MessageConstants.RESP_CLOSE_BASKET,
                basketId
        );
    }

    public boolean validateKeypadCode(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_VALIDATE_KEYPAD_CODE,
                MessageConstants.RESP_VALIDATE_KEYPAD_CODE,
                jsonParams.toString()
        );
    }

    public boolean keypadPurchase(JSONObject jsonParams) {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null");
        return sendMessage(
                MessageConstants.MSG_KEYPAD_PURCHASE,
                MessageConstants.RESP_KEYPAD_PURCHASE,
                jsonParams.toString()
        );
    }

    public static String decompressData(String zipText) {
        if (zipText == null) return "";
        return decompressBytes(Base64.decode(zipText, Base64.DEFAULT));
    }

    static String decompressBytes(byte[] compressed) {
        if (compressed == null || compressed.length <= GZIP_HEADER_SKIP_BYTES) {
            return "";
        }
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(
                     new ByteArrayInputStream(compressed, GZIP_HEADER_SKIP_BYTES,
                             compressed.length - GZIP_HEADER_SKIP_BYTES));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.e(TAG, "Failed to decompress data", e);
            return "";
        }
    }

    /**
     * Registers a broadcast receiver with {@code RECEIVER_NOT_EXPORTED} on API 33+
     * to prevent other apps from sending spoofed broadcasts.
     */
    private void registerReceiverCompat(BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ctx.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            ctx.registerReceiver(receiver, filter);
        }
    }

    boolean handleSendFailure(int request, Exception exception) {
        if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        Log.e(TAG, "Message sending failed for request: " + request, exception);
        try {
            Message msg = new Message();
            msg.what = request;
            Bundle data = new Bundle();
            data.putString(MessageConstants.RESP_SEND_FAILURE_REASON, exception.getMessage());
            msg.setData(data);
            replyMessenger.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to deliver send failure notification for request: " + request, e);
        }
        return false;
    }

    private boolean sendMessage(int request, String responseKey, String payload) {
        return handler.postDelayed(() -> {
            try {
                if (!serviceBoundLatch.await(SERVICE_BIND_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    handleSendFailure(request, new RemoteException(
                            "Service binding timed out after " + SERVICE_BIND_TIMEOUT_SECONDS + " seconds"));
                    return;
                }
                if (!mBound || mService == null) {
                    handleSendFailure(request, new RemoteException(
                            "Service disconnected before message could be sent"));
                    return;
                }
                Message msg = Message.obtain(null, request, 0, 0);
                msg.replyTo = replyMessenger;
                Bundle data = new Bundle();
                data.putString(MessageConstants.BUNDLE_RESPONSE_KEY, responseKey);
                data.putString(responseKey, payload);
                data.putString(MessageConstants.BUNDLE_PACKAGE_NAME, ctx.getPackageName());
                msg.setData(data);
                mService.send(msg);
                Log.d(TAG, "Message code " + request + " sent successfully");
            } catch (RemoteException | InterruptedException e) {
                handleSendFailure(request, e);
            }
        }, 0);
    }
}

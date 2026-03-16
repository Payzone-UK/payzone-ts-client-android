package com.payzone.transaction.client

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Base64
import android.util.Log
import com.payzone.transaction.client.handlers.MessageResponseHandler
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.Objects
import java.util.zip.GZIPInputStream

class ApiClient(ctx: Context, messenger: Messenger?) {

    /** Number of bytes prepended to the GZIP payload before Base64 encoding. */
    private val GZIP_HEADER_SKIP_BYTES = 4

    /** How long to wait for the service to bind before reporting a timeout failure. */
    private val SERVICE_BIND_TIMEOUT_MS = 20_000L

    private val ctx: Context = ctx.getApplicationContext()

    var messageResponseHandler: MessageResponseHandler? = null
    var replyMessenger: Messenger
    @JvmField var mBound = false
    var mService: Messenger? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile
    private var serviceBoundDeferred = CompletableDeferred<Unit>()

    private var isKeyInserted = false
    private var isBoxConnected = false

    private val mConnection: ServiceConnection

    val mHandleMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                isKeyInserted = extras.getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED)
            }
        }
    }

    private val mHandleBoxStatusMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                isBoxConnected = extras.getBoolean(MessageConstants.RESP_TALEXUS_BOX_STATUS)
            }
        }
    }

    init {
        if (messenger != null) {
            replyMessenger = messenger
        } else {
            messageResponseHandler = MessageResponseHandler()
            replyMessenger = Messenger(messageResponseHandler)
        }

        mConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                if (PAYZONE_SERVICE_PACKAGE != className.packageName) {
                    Log.e(TAG, "Rejecting connection from unexpected package: ${className.packageName}")
                    this@ApiClient.ctx.unbindService(this)
                    return
                }
                mService = Messenger(service)
                mBound = true
                serviceBoundDeferred.complete(Unit)
                Log.d(TAG, "Service Connection Established")
                fetchConfigData()
            }

            override fun onServiceDisconnected(className: ComponentName) {
                mService = null
                mBound = false
                serviceBoundDeferred = CompletableDeferred()
            }
        }
    }

    fun initService() {
        registerReceiverCompat(mHandleMessageReceiver, IntentFilter(MessageConstants.ACTION_KEY_INSERTED))
        registerReceiverCompat(mHandleBoxStatusMessageReceiver, IntentFilter(MessageConstants.ACTION_TALEXUS_BOX_STATUS))
        val intent = Intent().apply {
            component = ComponentName(
                PAYZONE_SERVICE_PACKAGE,
                "$PAYZONE_SERVICE_PACKAGE.services.TransactionService"
            )
        }
        val bindResult = ctx.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        Log.d(TAG, "Binding in progress: $bindResult")
    }

    fun destroyService(): Boolean {
        ctx.unregisterReceiver(mHandleMessageReceiver)
        ctx.unregisterReceiver(mHandleBoxStatusMessageReceiver)
        if (mBound) {
            ctx.unbindService(mConnection)
            mBound = false
        }
        serviceScope.cancel()
        return true
    }

    fun fetchConfigData() {
        val res = fetchMyConfigData()
        Log.d(TAG, "Fetch Config Data: $res")
    }

    private fun fetchMyConfigData() = sendMessage(
        MessageConstants.MSG_CONFIG_SETUP,
        MessageConstants.RESP_CONFIG_SETUP,
        ""
    )

    fun initTalexus() = sendMessage(MessageConstants.MSG_INIT_TALEXUS, MessageConstants.RESP_INIT_TALEXUS, "")

    fun stopTalexus() = sendMessage(MessageConstants.MSG_STOP_TALEXUS, MessageConstants.RESP_STOP_TALEXUS, "")

    @Throws(JSONException::class)
    fun registerDevice(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        val registerJsonObj = JSONObject()
        registerJsonObj.put("terminal", jsonParams)
        return sendMessage(MessageConstants.MSG_REGISTER_DEVICE, MessageConstants.RESP_REGISTER_DEVICE, registerJsonObj.toString())
    }

    @Throws(JSONException::class)
    fun initTransaction(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        val purchaseJsonObj = JSONObject()
        purchaseJsonObj.put("purchase", jsonParams)
        return sendMessage(MessageConstants.MSG_INIT_TRANSACTION, MessageConstants.RESP_INIT_TRANSACTION, purchaseJsonObj.toString())
    }

    fun completeTransaction(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_COMPLETE_TRANS, MessageConstants.RESP_COMPLETE_TRANS, jsonParams.toString())
    }

    fun markTransactionSuccess(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_MARK_TRANS_SUCCESS, MessageConstants.RESP_MARK_TRANS_SUCCESS, jsonParams.toString())
    }

    fun markTransactionFailed(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_MARK_TRANS_FAILED, MessageConstants.RESP_MARK_TRANS_FAILED, jsonParams.toString())
    }

    fun markReceiptPrinted(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_MARK_RECEIPT_PRINTED, MessageConstants.RESP_MARK_RECEIPT_PRINTED, jsonParams.toString())
    }

    fun getToken(tId: String): Boolean {
        Objects.requireNonNull(tId, "tId must not be null")
        return sendMessage(MessageConstants.MSG_GET_TOKEN, MessageConstants.RESP_GET_TOKEN, tId)
    }

    fun getTokenBySerialNumber(serialNumber: String): Boolean {
        Objects.requireNonNull(serialNumber, "serialNumber must not be null")
        return sendMessage(MessageConstants.MSG_GET_TOKEN_BY_SERIAL_NUMBER, MessageConstants.RESP_GET_TOKEN_BY_SERIAL_NUMBER, serialNumber)
    }

    @Throws(JSONException::class)
    fun startSession(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        val sessionJsonObj = JSONObject()
        sessionJsonObj.put("session", jsonParams)
        return sendMessage(MessageConstants.MSG_START_SESSION, MessageConstants.RESP_START_SESSION, sessionJsonObj.toString())
    }

    fun storeCashierId(cashierId: String): Boolean {
        Objects.requireNonNull(cashierId, "cashierId must not be null")
        return sendMessage(MessageConstants.MSG_STORE_CID, MessageConstants.RESP_STORE_CID, cashierId)
    }

    fun isTransactionReady() = sendMessage(MessageConstants.MSG_IS_TRANSACTION_READY, MessageConstants.RESP_IS_TRANSACTION_READY, "")

    fun readKey() = sendMessage(MessageConstants.MSG_TALEXUS_READ_KEY, MessageConstants.RESP_TALEXUS_READ_KEY, "")

    fun addCredit(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_TALEXUS_ADD_CREDIT, MessageConstants.RESP_TALEXUS_ADD_CREDIT, jsonParams.toString())
    }

    fun rti(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_TALEXUS_RTI, MessageConstants.RESP_TALEXUS_RTI, jsonParams.toString())
    }

    fun pzAddCredit(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.PZ_MSG_TALEXUS_ADD_CREDIT, MessageConstants.RESP_TALEXUS_ADD_CREDIT, jsonParams.toString())
    }

    fun pzRti(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.PZ_MSG_TALEXUS_RTI, MessageConstants.RESP_TALEXUS_RTI, jsonParams.toString())
    }

    fun isKeyInserted() = sendMessage(MessageConstants.MSG_TALEXUS_IS_KEY_INSERTED, MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED, "")

    fun isBoxConnected() = sendMessage(MessageConstants.MSG_TALEXUS_BOX_CONNECTED, MessageConstants.RESP_TALEXUS_BOX_STATUS, "")

    fun reversal(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_TALEXUS_REVERSE_CREDIT, MessageConstants.RESP_TALEXUS_REVERSE_CREDIT, jsonParams.toString())
    }

    fun nspHotcard(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_QUANTUM_NSP_HOT_CARD, MessageConstants.RESP_QUANTUM_NSP_HOT_CARD, jsonParams.toString())
    }

    fun securityKeys(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_QUANTUM_SECURITY_KEYS, MessageConstants.RESP_QUANTUM_SECURITY_KEYS, jsonParams.toString())
    }

    fun localSecretCode(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_QUANTUM_LOCAL_SECRET_CODE, MessageConstants.RESP_QUANTUM_LOCAL_SECRET_CODE, jsonParams.toString())
    }

    fun csRegional(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_QUANTUM_CS_REGIONAL, MessageConstants.RESP_QUANTUM_CS_REGIONAL, jsonParams.toString())
    }

    fun quantumTransactionComplete(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_QUANTUM_TRANSACTION_COMPLETE, MessageConstants.RESP_QUANTUM_TRANSACTION_COMPLETE, jsonParams.toString())
    }

    fun quantumRtiTransaction(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_QUANTUM_RTI, MessageConstants.RESP_QUANTUM_RTI, jsonParams.toString())
    }

    fun sale(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_QUANTUM_SALE, MessageConstants.RESP_QUANTUM_SALE, jsonParams.toString())
    }

    fun openBasket(basketId: String): Boolean {
        Objects.requireNonNull(basketId, "basketId must not be null")
        return sendMessage(MessageConstants.MSG_OPEN_BASKET, MessageConstants.RESP_OPEN_BASKET, basketId)
    }

    fun closeBasket(basketId: String): Boolean {
        Objects.requireNonNull(basketId, "basketId must not be null")
        return sendMessage(MessageConstants.MSG_CLOSE_BASKET, MessageConstants.RESP_CLOSE_BASKET, basketId)
    }

    fun validateKeypadCode(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_VALIDATE_KEYPAD_CODE, MessageConstants.RESP_VALIDATE_KEYPAD_CODE, jsonParams.toString())
    }

    fun keypadPurchase(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sendMessage(MessageConstants.MSG_KEYPAD_PURCHASE, MessageConstants.RESP_KEYPAD_PURCHASE, jsonParams.toString())
    }

    private fun sendMessage(request: Int, responseKey: String, payload: String?): Boolean {
        serviceScope.launch {
            try {
                withTimeout(SERVICE_BIND_TIMEOUT_MS) {
                    serviceBoundDeferred.await()
                }
                if (!mBound || mService == null) {
                    handleSendFailure(request, RemoteException("Service disconnected before message could be sent"))
                    return@launch
                }
                val msg = Message().apply {
                    what = request
                    replyTo = replyMessenger
                    data = Bundle().apply {
                        putString(MessageConstants.BUNDLE_RESPONSE_KEY, responseKey)
                        putString(responseKey, payload ?: "")
                        putString(MessageConstants.BUNDLE_PACKAGE_NAME, this@ApiClient.ctx.packageName)
                    }
                }
                mService!!.send(msg)
                Log.d(TAG, "Message code $request sent successfully")
            } catch (e: TimeoutCancellationException) {
                handleSendFailure(request, RemoteException("Service binding timed out after ${SERVICE_BIND_TIMEOUT_MS / 1000}s"))
            } catch (e: RemoteException) {
                handleSendFailure(request, e)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                handleSendFailure(request, e)
            }
        }
        return true
    }

    fun handleSendFailure(request: Int, exception: Exception): Boolean {
        if (exception is InterruptedException) {
            Thread.currentThread().interrupt()
        }
        Log.e(TAG, "Message sending failed for request: $request", exception)
        try {
            val msg = Message().apply {
                what = request
                data = Bundle().apply {
                    putString(MessageConstants.RESP_SEND_FAILURE_REASON, exception.message)
                }
            }
            replyMessenger.send(msg)
        } catch (e: RemoteException) {
            Log.e(TAG, "Failed to deliver send failure notification for request: $request", e)
        }
        return false
    }

    private fun registerReceiverCompat(receiver: BroadcastReceiver, filter: IntentFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ctx.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            ctx.registerReceiver(receiver, filter)
        }
    }

    companion object {
        internal val TAG = ApiClient::class.java.simpleName
        private const val PAYZONE_SERVICE_PACKAGE = "com.payzone.transaction"

        @JvmStatic
        fun decompressData(zipText: String?): String {
            if (zipText == null) return ""
            return decompressBytes(Base64.decode(zipText, Base64.DEFAULT))
        }

        @JvmStatic
        fun decompressBytes(compressed: ByteArray?): String {
            val headerSkip = 4
            if (compressed == null || compressed.size <= headerSkip) return ""
            return try {
                GZIPInputStream(ByteArrayInputStream(compressed, headerSkip, compressed.size - headerSkip))
                    .use { gzip ->
                        ByteArrayOutputStream().use { baos ->
                            val buffer = ByteArray(1024)
                            var bytesRead: Int
                            while (gzip.read(buffer).also { bytesRead = it } != -1) {
                                baos.write(buffer, 0, bytesRead)
                            }
                            baos.toString(StandardCharsets.UTF_8.name())
                        }
                    }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to decompress data", e)
                ""
            }
        }
    }
}

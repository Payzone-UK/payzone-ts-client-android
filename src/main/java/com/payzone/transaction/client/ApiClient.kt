package com.payzone.transaction.client

import android.content.Context
import android.os.Messenger
import com.payzone.transaction.client.handlers.MessageResponseHandler
import org.json.JSONException
import org.json.JSONObject
import java.util.Objects

class ApiClient(ctx: Context, messenger: Messenger?) {

    var messageResponseHandler: MessageResponseHandler? = null
    var replyMessenger: Messenger

    private val connectionManager: ServiceConnectionManager
    private val sender: MessageSender

    init {
        if (messenger != null) {
            replyMessenger = messenger
        } else {
            messageResponseHandler = MessageResponseHandler()
            replyMessenger = Messenger(messageResponseHandler)
        }
        connectionManager = ServiceConnectionManager(ctx) { fetchConfigData() }
        sender = MessageSender(connectionManager, replyMessenger)
    }

    val mBound: Boolean get() = connectionManager.mBound

    fun initService() = connectionManager.initService()

    fun destroyService() = connectionManager.destroyService()

    fun fetchConfigData() {
        val res = sender.send(MessageConstants.MSG_CONFIG_SETUP, MessageConstants.RESP_CONFIG_SETUP, "")
        android.util.Log.d(TAG, "Fetch Config Data: $res")
    }

    fun initTalexus() = sender.send(MessageConstants.MSG_INIT_TALEXUS, MessageConstants.RESP_INIT_TALEXUS, "")

    fun stopTalexus() = sender.send(MessageConstants.MSG_STOP_TALEXUS, MessageConstants.RESP_STOP_TALEXUS, "")

    @Throws(JSONException::class)
    fun registerDevice(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        val registerJsonObj = JSONObject()
        registerJsonObj.put("terminal", jsonParams)
        return sender.send(MessageConstants.MSG_REGISTER_DEVICE, MessageConstants.RESP_REGISTER_DEVICE, registerJsonObj.toString())
    }

    @Throws(JSONException::class)
    fun initTransaction(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        val purchaseJsonObj = JSONObject()
        purchaseJsonObj.put("purchase", jsonParams)
        return sender.send(MessageConstants.MSG_INIT_TRANSACTION, MessageConstants.RESP_INIT_TRANSACTION, purchaseJsonObj.toString())
    }

    fun completeTransaction(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_COMPLETE_TRANS, MessageConstants.RESP_COMPLETE_TRANS, jsonParams.toString())
    }

    fun markTransactionSuccess(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_MARK_TRANS_SUCCESS, MessageConstants.RESP_MARK_TRANS_SUCCESS, jsonParams.toString())
    }

    fun markTransactionFailed(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_MARK_TRANS_FAILED, MessageConstants.RESP_MARK_TRANS_FAILED, jsonParams.toString())
    }

    fun markReceiptPrinted(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_MARK_RECEIPT_PRINTED, MessageConstants.RESP_MARK_RECEIPT_PRINTED, jsonParams.toString())
    }

    fun getToken(tId: String): Boolean {
        Objects.requireNonNull(tId, "tId must not be null")
        return sender.send(MessageConstants.MSG_GET_TOKEN, MessageConstants.RESP_GET_TOKEN, tId)
    }

    fun getTokenBySerialNumber(serialNumber: String): Boolean {
        Objects.requireNonNull(serialNumber, "serialNumber must not be null")
        return sender.send(MessageConstants.MSG_GET_TOKEN_BY_SERIAL_NUMBER, MessageConstants.RESP_GET_TOKEN_BY_SERIAL_NUMBER, serialNumber)
    }

    @Throws(JSONException::class)
    fun startSession(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        val sessionJsonObj = JSONObject()
        sessionJsonObj.put("session", jsonParams)
        return sender.send(MessageConstants.MSG_START_SESSION, MessageConstants.RESP_START_SESSION, sessionJsonObj.toString())
    }

    fun storeCashierId(cashierId: String): Boolean {
        Objects.requireNonNull(cashierId, "cashierId must not be null")
        return sender.send(MessageConstants.MSG_STORE_CID, MessageConstants.RESP_STORE_CID, cashierId)
    }

    fun isTransactionReady() = sender.send(MessageConstants.MSG_IS_TRANSACTION_READY, MessageConstants.RESP_IS_TRANSACTION_READY, "")

    fun readKey() = sender.send(MessageConstants.MSG_TALEXUS_READ_KEY, MessageConstants.RESP_TALEXUS_READ_KEY, "")

    fun addCredit(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_TALEXUS_ADD_CREDIT, MessageConstants.RESP_TALEXUS_ADD_CREDIT, jsonParams.toString())
    }

    fun rti(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_TALEXUS_RTI, MessageConstants.RESP_TALEXUS_RTI, jsonParams.toString())
    }

    fun pzAddCredit(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.PZ_MSG_TALEXUS_ADD_CREDIT, MessageConstants.RESP_TALEXUS_ADD_CREDIT, jsonParams.toString())
    }

    fun pzRti(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.PZ_MSG_TALEXUS_RTI, MessageConstants.RESP_TALEXUS_RTI, jsonParams.toString())
    }

    fun isKeyInserted() = sender.send(MessageConstants.MSG_TALEXUS_IS_KEY_INSERTED, MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED, "")

    fun isBoxConnected() = sender.send(MessageConstants.MSG_TALEXUS_BOX_CONNECTED, MessageConstants.RESP_TALEXUS_BOX_STATUS, "")

    fun reversal(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_TALEXUS_REVERSE_CREDIT, MessageConstants.RESP_TALEXUS_REVERSE_CREDIT, jsonParams.toString())
    }

    fun nspHotcard(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_QUANTUM_NSP_HOT_CARD, MessageConstants.RESP_QUANTUM_NSP_HOT_CARD, jsonParams.toString())
    }

    fun securityKeys(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_QUANTUM_SECURITY_KEYS, MessageConstants.RESP_QUANTUM_SECURITY_KEYS, jsonParams.toString())
    }

    fun localSecretCode(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_QUANTUM_LOCAL_SECRET_CODE, MessageConstants.RESP_QUANTUM_LOCAL_SECRET_CODE, jsonParams.toString())
    }

    fun csRegional(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_QUANTUM_CS_REGIONAL, MessageConstants.RESP_QUANTUM_CS_REGIONAL, jsonParams.toString())
    }

    fun quantumTransactionComplete(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_QUANTUM_TRANSACTION_COMPLETE, MessageConstants.RESP_QUANTUM_TRANSACTION_COMPLETE, jsonParams.toString())
    }

    fun quantumRtiTransaction(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_QUANTUM_RTI, MessageConstants.RESP_QUANTUM_RTI, jsonParams.toString())
    }

    fun sale(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_QUANTUM_SALE, MessageConstants.RESP_QUANTUM_SALE, jsonParams.toString())
    }

    fun openBasket(basketId: String): Boolean {
        Objects.requireNonNull(basketId, "basketId must not be null")
        return sender.send(MessageConstants.MSG_OPEN_BASKET, MessageConstants.RESP_OPEN_BASKET, basketId)
    }

    fun closeBasket(basketId: String): Boolean {
        Objects.requireNonNull(basketId, "basketId must not be null")
        return sender.send(MessageConstants.MSG_CLOSE_BASKET, MessageConstants.RESP_CLOSE_BASKET, basketId)
    }

    fun validateKeypadCode(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_VALIDATE_KEYPAD_CODE, MessageConstants.RESP_VALIDATE_KEYPAD_CODE, jsonParams.toString())
    }

    fun keypadPurchase(jsonParams: JSONObject): Boolean {
        Objects.requireNonNull(jsonParams, "jsonParams must not be null")
        return sender.send(MessageConstants.MSG_KEYPAD_PURCHASE, MessageConstants.RESP_KEYPAD_PURCHASE, jsonParams.toString())
    }

    /** Exposed for testing. */
    fun handleSendFailure(request: Int, exception: Exception) = sender.handleSendFailure(request, exception)

    companion object {
        internal val TAG = ApiClient::class.java.simpleName

        @JvmStatic
        fun decompressData(zipText: String?) = CompressionUtils.decompressData(zipText)

        @JvmStatic
        fun decompressBytes(compressed: ByteArray?) = CompressionUtils.decompressBytes(compressed)
    }
}

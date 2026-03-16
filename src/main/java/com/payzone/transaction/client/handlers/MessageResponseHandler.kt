package com.payzone.transaction.client.handlers

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.payzone.transaction.client.ApiClient
import com.payzone.transaction.client.MessageConstants

class MessageResponseHandler : Handler(Looper.getMainLooper()) {

    var response: String? = null

    override fun handleMessage(msg: Message) {
        val error = msg.data.getString(MessageConstants.RESP_SEND_FAILURE_REASON)
        if (error != null) {
            Log.e(TAG, "Send failure for request ${msg.what}: $error")
            return
        }
        response = when (msg.what) {
            MessageConstants.MSG_REGISTER_DEVICE -> {
                Log.d(TAG, "Register Device Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_REGISTER_DEVICE))
            }
            MessageConstants.MSG_INIT_TRANSACTION -> {
                Log.d(TAG, "Transaction Initialised Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_INIT_TRANSACTION))
            }
            MessageConstants.MSG_COMPLETE_TRANS -> {
                Log.d(TAG, "Transaction Completed Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_COMPLETE_TRANS))
            }
            MessageConstants.MSG_MARK_TRANS_SUCCESS -> {
                Log.d(TAG, "Marked Successful Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_MARK_TRANS_SUCCESS))
            }
            MessageConstants.MSG_MARK_TRANS_FAILED -> {
                Log.d(TAG, "Marked Failed Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_MARK_TRANS_FAILED))
            }
            MessageConstants.MSG_MARK_RECEIPT_PRINTED -> {
                Log.d(TAG, "Marked Receipt Printed Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED))
            }
            MessageConstants.MSG_TALEXUS_READ_KEY -> {
                Log.d(TAG, "Talexus Read key response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_READ_KEY))
            }
            MessageConstants.MSG_TALEXUS_ADD_CREDIT,
            MessageConstants.PZ_MSG_TALEXUS_ADD_CREDIT -> {
                Log.d(TAG, "Talexus add credit Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_ADD_CREDIT))
            }
            MessageConstants.MSG_TALEXUS_RTI,
            MessageConstants.PZ_MSG_TALEXUS_RTI -> {
                Log.d(TAG, "Talexus RTI Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_RTI))
            }
            MessageConstants.MSG_TALEXUS_IS_KEY_INSERTED -> {
                Log.d(TAG, "Talexus key inserted Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED))
            }
            MessageConstants.MSG_TALEXUS_REVERSE_CREDIT -> {
                Log.d(TAG, "Talexus Reversal Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_REVERSE_CREDIT))
            }
            MessageConstants.MSG_INIT_TALEXUS -> {
                Log.d(TAG, "Talexus initialisation Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_INIT_TALEXUS))
            }
            MessageConstants.MSG_STOP_TALEXUS -> {
                Log.d(TAG, "Talexus stop Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_STOP_TALEXUS))
            }
            MessageConstants.MSG_CONFIG_SETUP -> {
                Log.d(TAG, "Config setup Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_CONFIG_SETUP))
            }
            MessageConstants.MSG_GET_TOKEN -> {
                Log.d(TAG, "Get token Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_GET_TOKEN))
            }
            MessageConstants.MSG_GET_TOKEN_BY_SERIAL_NUMBER -> {
                Log.d(TAG, "Get token by serial number Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_GET_TOKEN_BY_SERIAL_NUMBER))
            }
            MessageConstants.MSG_START_SESSION -> {
                Log.d(TAG, "Start session Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_START_SESSION))
            }
            MessageConstants.MSG_STORE_CID -> {
                Log.d(TAG, "Store cashier ID Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_STORE_CID))
            }
            MessageConstants.MSG_IS_TRANSACTION_READY -> {
                Log.d(TAG, "Is transaction ready Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_IS_TRANSACTION_READY))
            }
            MessageConstants.MSG_TALEXUS_BOX_CONNECTED -> {
                Log.d(TAG, "Talexus box status Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_BOX_STATUS))
            }
            MessageConstants.MSG_QUANTUM_SALE -> {
                Log.d(TAG, "Quantum sale Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_SALE))
            }
            MessageConstants.MSG_QUANTUM_SECURITY_KEYS -> {
                Log.d(TAG, "Quantum security keys Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_SECURITY_KEYS))
            }
            MessageConstants.MSG_QUANTUM_LOCAL_SECRET_CODE -> {
                Log.d(TAG, "Quantum local secret code Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_LOCAL_SECRET_CODE))
            }
            MessageConstants.MSG_QUANTUM_NSP_HOT_CARD -> {
                Log.d(TAG, "Quantum NSP hotcard Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_NSP_HOT_CARD))
            }
            MessageConstants.MSG_QUANTUM_CS_REGIONAL -> {
                Log.d(TAG, "Quantum CS regional Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_CS_REGIONAL))
            }
            MessageConstants.MSG_QUANTUM_TRANSACTION_COMPLETE -> {
                Log.d(TAG, "Quantum transaction complete Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_TRANSACTION_COMPLETE))
            }
            MessageConstants.MSG_QUANTUM_RTI -> {
                Log.d(TAG, "Quantum RTI Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_RTI))
            }
            MessageConstants.MSG_OPEN_BASKET -> {
                Log.d(TAG, "Open basket Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_OPEN_BASKET))
            }
            MessageConstants.MSG_CLOSE_BASKET -> {
                Log.d(TAG, "Close basket Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_CLOSE_BASKET))
            }
            MessageConstants.MSG_VALIDATE_KEYPAD_CODE -> {
                Log.d(TAG, "Validate keypad code Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_VALIDATE_KEYPAD_CODE))
            }
            MessageConstants.MSG_KEYPAD_PURCHASE -> {
                Log.d(TAG, "Keypad purchase Response received")
                ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_KEYPAD_PURCHASE))
            }
            else -> {
                super.handleMessage(msg)
                return
            }
        }
    }

    companion object {
        private val TAG = MessageResponseHandler::class.java.simpleName
    }
}

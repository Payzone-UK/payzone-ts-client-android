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

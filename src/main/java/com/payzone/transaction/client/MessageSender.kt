package com.payzone.transaction.client

import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

internal class MessageSender(
    private val manager: ServiceConnectionManager,
    private val replyMessenger: Messenger
) {
    fun send(request: Int, responseKey: String, payload: String?): Boolean {
        manager.serviceScope.launch {
            try {
                withTimeout(SERVICE_BIND_TIMEOUT_MS) {
                    manager.serviceBoundDeferred.await()
                }
                if (!manager.mBound || manager.mService == null) {
                    handleSendFailure(request, RemoteException("Service disconnected before message could be sent"))
                    return@launch
                }
                val msg = Message().apply {
                    what = request
                    replyTo = replyMessenger
                    data = Bundle().apply {
                        putString(MessageConstants.BUNDLE_RESPONSE_KEY, responseKey)
                        putString(responseKey, payload ?: "")
                        putString(MessageConstants.BUNDLE_PACKAGE_NAME, manager.appContext.packageName)
                    }
                }
                manager.mService!!.send(msg)
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

    companion object {
        private val TAG = MessageSender::class.java.simpleName
        private const val SERVICE_BIND_TIMEOUT_MS = 20_000L
    }
}

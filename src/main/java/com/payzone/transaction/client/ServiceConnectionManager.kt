package com.payzone.transaction.client

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

internal class ServiceConnectionManager(
    ctx: Context,
    private val onBoundChanged: (Boolean) -> Unit = {},
    private val onConnected: () -> Unit
) {
    val appContext: Context = ctx.applicationContext
    val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile var serviceBoundDeferred = CompletableDeferred<Unit>()
    @Volatile var mBound = false
    @Volatile var mService: Messenger? = null
    @Volatile var isKeyInserted = false
    @Volatile var isBoxConnected = false
    private var receiversRegistered = false

    private val keyInsertedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                isKeyInserted = extras.getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED)
            }
        }
    }

    private val boxStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                isBoxConnected = extras.getBoolean(MessageConstants.RESP_TALEXUS_BOX_STATUS)
            }
        }
    }

    val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            if (PAYZONE_SERVICE_PACKAGE != className?.packageName) {
                Log.e(TAG, "Rejecting connection from unexpected package: ${className?.packageName}")
                appContext.unbindService(this)
                return
            }
            mService = Messenger(service)
            mBound = true
            onBoundChanged(true)
            serviceBoundDeferred.complete(Unit)
            Log.d(TAG, "Service Connection Established")
            onConnected()
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            mService = null
            mBound = false
            onBoundChanged(false)
            serviceBoundDeferred.completeExceptionally(
                IllegalStateException("Service disconnected unexpectedly")
            )
            serviceBoundDeferred = CompletableDeferred()
        }
    }

    fun initService() {
        if (!receiversRegistered) {
            registerReceiverCompat(keyInsertedReceiver, IntentFilter(MessageConstants.ACTION_KEY_INSERTED))
            registerReceiverCompat(boxStatusReceiver, IntentFilter(MessageConstants.ACTION_TALEXUS_BOX_STATUS))
            receiversRegistered = true
        }
        val intent = Intent().apply {
            component = ComponentName(
                PAYZONE_SERVICE_PACKAGE,
                "$PAYZONE_SERVICE_PACKAGE.services.TransactionService"
            )
        }
        val bindResult = appContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        if (!bindResult) {
            Log.e(TAG, "bindService failed — Payzone service may not be installed")
            serviceBoundDeferred.completeExceptionally(
                IllegalStateException("bindService returned false — is the Payzone service installed?")
            )
        } else {
            Log.d(TAG, "Binding in progress")
        }
    }

    fun destroyService(): Boolean {
        if (receiversRegistered) {
            appContext.unregisterReceiver(keyInsertedReceiver)
            appContext.unregisterReceiver(boxStatusReceiver)
            receiversRegistered = false
        }
        if (mBound) {
            appContext.unbindService(mConnection)
            mBound = false
            onBoundChanged(false)
        }
        serviceScope.cancel()
        return true
    }

    private fun registerReceiverCompat(receiver: BroadcastReceiver, filter: IntentFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            appContext.registerReceiver(receiver, filter)
        }
    }

    companion object {
        private val TAG = ServiceConnectionManager::class.java.simpleName
        const val PAYZONE_SERVICE_PACKAGE = "com.payzone.transaction"
    }
}

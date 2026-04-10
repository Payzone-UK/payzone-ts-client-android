package com.payzone.transaction.client

import android.os.RemoteException
import android.test.mock.MockContext
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ApiClientKotlinTest {

    companion object {
        lateinit var apiClient: ApiClient

        @JvmStatic
        @BeforeClass
        fun testSetup() {
            val mContext = mock(MockContext::class.java)
            `when`(mContext.applicationContext).thenReturn(mContext)
            `when`(mContext.packageName).thenReturn("com.payzone.transaction.client.test")
            apiClient = ApiClient(mContext, null)
        }
    }

    @Test
    fun handleSendFailureReturnsFalseForRemoteException() {
        assertFalse(apiClient.handleSendFailure(101, RemoteException("service unavailable")))
    }

    @Test
    fun handleSendFailureRestoresInterruptFlag() {
        assertFalse(Thread.currentThread().isInterrupted)

        assertFalse(apiClient.handleSendFailure(102, InterruptedException("interrupted")))
        assertTrue(Thread.currentThread().isInterrupted)

        assertTrue(Thread.interrupted())
        assertFalse(Thread.currentThread().isInterrupted)
    }
}

package com.payzone.transaction.client;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.test.mock.MockContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ApiClientUnitTest {
    static ApiClient apiClient;
    static Context appContext;
    static MockContext mContext;

    @BeforeClass
    public static void testSetup() throws PackageManager.NameNotFoundException {
        mContext = mock(MockContext.class);
        when(mContext.getPackageName()).thenReturn("com.payzone.transaction.client.test");

//        appContext = mContext.createPackageContext("com.payzone.transaction.client.test", 0);
//        appContext = new MockContext() {
//
//            @Override
//            public String getPackageName(){
//                return "com.payzone.transaction.client.test";
//            }
//        };

        apiClient = new ApiClient(mContext, null);
        apiClient.initService();
        apiClient.fetchConfigData();
    }

    @Test
    public void serviceBindingDone() {
        assertEquals(false, apiClient.mBound);
    }

    @Test
    public void useAppContext() {
        assertEquals("com.payzone.transaction.client.test", mContext.getPackageName());
    }

    @Test
    public void registerDevice() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("barcode", "267693243349691");
        obj.put("deviceId", "1545D2053");
        obj.put("tId", "49691");
        assertEquals(false, apiClient.registerDevice(obj));
    }

    @Test
    public void getToken() {
        assertEquals(false, apiClient.getToken("49691"));
    }

    @Test
    public void getTokenBySerialNumber() {
        assertEquals(false, apiClient.getTokenBySerialNumber("1545D2053"));
    }

    @Test
    public void startSession() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("pin", 1234);
        assertEquals(false, apiClient.startSession(obj));
    }

    @Test
    public void initTransaction() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.initTransaction(obj));
    }

    @Test
    public void completeTransaction() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.completeTransaction(obj));
    }

    @Test
    public void markTransactionSuccess() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.markTransactionSuccess(obj));
        assertThrows(NullPointerException.class, () -> { apiClient.markTransactionSuccess(null);});
    }

    @Test
    public void markTransactionFailed() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.markTransactionFailed(obj));
        assertThrows(NullPointerException.class, () -> { apiClient.markTransactionFailed(null);});
    }

    @Test
    public void markReceiptPrinted() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.markReceiptPrinted(obj));
        assertThrows(NullPointerException.class, () -> { apiClient.markReceiptPrinted(null);});
    }

    @Test
    public void storeCashierId() {
        assertEquals(false, apiClient.storeCashierId("1234"));
        assertThrows(NullPointerException.class, () -> { apiClient.storeCashierId(null);});
    }

    @Test
    public void isTransactionReady() {
        assertEquals(false, apiClient.isTransactionReady());
        assertThrows(NullPointerException.class, () -> { apiClient.isTransactionReady();});
    }

    @Test
    public void nspHotcard() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.nspHotcard(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.nspHotcard(null);
        });
    }

    @Test
    public void securityKeys() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.securityKeys(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.securityKeys(null);
        });
    }

    @Test
    public void localSecretCode() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.localSecretCode(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.localSecretCode(null);
        });
    }

    @Test
    public void csRegional() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.csRegional(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.csRegional(null);
        });
    }

    @Test
    public void quantumTransactionComplete() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.quantumTransactionComplete(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.quantumTransactionComplete(null);
        });
    }

    @Test
    public void quantumRtiTransaction() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.quantumRtiTransaction(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.quantumRtiTransaction(null);
        });
    }

    @Test
    public void sale() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.sale(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.sale(null);
        });
    }
}

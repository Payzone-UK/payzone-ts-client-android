package com.payzone.transaction.client;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.RemoteException;

import android.test.mock.MockContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(mContext.getApplicationContext()).thenReturn(mContext);
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
        apiClient.initTalexus();
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
        assertThrows(NullPointerException.class, () -> apiClient.registerDevice(null));
    }

    @Test
    public void getToken() {
        assertEquals(false, apiClient.getToken("49691"));
        assertThrows(NullPointerException.class, () -> apiClient.getToken(null));
    }

    @Test
    public void getTokenBySerialNumber() {
        assertEquals(false, apiClient.getTokenBySerialNumber("1545D2053"));
        assertThrows(NullPointerException.class, () -> apiClient.getTokenBySerialNumber(null));
    }

    @Test
    public void startSession() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("pin", 1234);
        assertEquals(false, apiClient.startSession(obj));
        assertThrows(NullPointerException.class, () -> apiClient.startSession(null));
    }

    @Test
    public void initTransaction() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.initTransaction(obj));
        assertThrows(NullPointerException.class, () -> apiClient.initTransaction(null));
    }

    @Test
    public void completeTransaction() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.completeTransaction(obj));
        assertThrows(NullPointerException.class, () -> apiClient.completeTransaction(null));
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
    }

    @Test
    public void readKey() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.readKey());
    }

    @Test
    public void addCredit() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.addCredit(obj));
        assertThrows(NullPointerException.class, () -> { apiClient.addCredit(null);});
    }

    @Test
    public void rti() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.rti(obj));
        assertThrows(NullPointerException.class, () -> { apiClient.rti(null);});
    }

    @Test
    public void pzAddCredit() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.pzAddCredit(obj));
        assertThrows(NullPointerException.class, () -> apiClient.pzAddCredit(null));
    }

    @Test
    public void pzRti() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.pzRti(obj));
        assertThrows(NullPointerException.class, () -> apiClient.pzRti(null));
    }

    @Test
    public void isKeyInserted() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.isKeyInserted());
    }
  
    @Test
    public void reversal() throws JSONException {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.reversal(obj));
        assertThrows(NullPointerException.class, () -> { apiClient.reversal(null);});
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
    @Test
    public void openBasket() {
        assertEquals(false, apiClient.openBasket("123456-121"));
        assertThrows(NullPointerException.class, () -> apiClient.openBasket(null));
    }

    @Test
    public void closeBasket() {
        assertEquals(false, apiClient.closeBasket("12345"));
        assertThrows(NullPointerException.class, () -> apiClient.closeBasket(null));
    }

    @Test
    public void keypadPurchase() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.keypadPurchase(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.keypadPurchase(null);
        });
    }

    @Test
    public void validateKeypadCode() {
        JSONObject obj = new JSONObject();
        assertEquals(false, apiClient.validateKeypadCode(obj));
        assertThrows(NullPointerException.class, () -> {
            apiClient.validateKeypadCode(null);
        });
    }

    @Test
    public void handleSendFailureReturnsFalseForRemoteException() {
        assertFalse(apiClient.handleSendFailure(101, new RemoteException("service unavailable")));
    }

    @Test
    public void handleSendFailureRestoresInterruptFlag() {
        assertFalse(Thread.currentThread().isInterrupted());

        assertFalse(apiClient.handleSendFailure(102, new InterruptedException("interrupted")));
        assertTrue(Thread.currentThread().isInterrupted());

        assertTrue(Thread.interrupted());
        assertFalse(Thread.currentThread().isInterrupted());
    }

    @Test
    public void decompressBytesRoundTrip() throws IOException {
        String original = "Hello Payzone Transaction Service";
        assertEquals(original, ApiClient.decompressBytes(buildCompressedBytes(original)));
    }

    @Test
    public void decompressBytesReturnsEmptyForShortInput() {
        assertEquals("", ApiClient.decompressBytes(new byte[]{1, 2, 3, 4}));
    }

    @Test
    public void decompressBytesReturnsEmptyForCorruptPayload() {
        // 4-byte prefix followed by garbage — not valid GZIP
        assertEquals("", ApiClient.decompressBytes(new byte[]{0, 0, 0, 0, 1, 2, 3, 4, 5}));
    }

    @Test
    public void decompressBytesReturnsEmptyForNull() {
        assertEquals("", ApiClient.decompressBytes(null));
    }

    @Test
    public void decompressBytesIsThreadSafe() throws IOException, InterruptedException, ExecutionException {
        String original = "Concurrent decompression test payload";
        byte[] compressed = buildCompressedBytes(original);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> ApiClient.decompressBytes(compressed)));
        }
        executor.shutdown();

        for (Future<String> future : futures) {
            assertEquals(original, future.get());
        }
    }

    private static byte[] buildCompressedBytes(String text) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0, 0, 0, 0}); // 4-byte header skipped by decompressBytes
        try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
            gos.write(text.getBytes(StandardCharsets.UTF_8));
        }
        return baos.toByteArray();
    }
}

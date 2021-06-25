package com.payzone.transaction.client;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ApiClientTest {
    static ApiClient apiClient;
    static Context appContext;

    @BeforeClass
    public static void testSetup() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        apiClient = new ApiClient(appContext, null);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.payzone.transaction.client.test", appContext.getPackageName());
    }
}
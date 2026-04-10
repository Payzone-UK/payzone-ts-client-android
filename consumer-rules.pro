# Keep public API classes and methods
-keep public class com.payzone.transaction.client.ApiClient {
    public <methods>;
}

-keep public class com.payzone.transaction.client.MessageConstants {
    public static final <fields>;
}

-keep public class com.payzone.transaction.client.handlers.MessageResponseHandler {
    public <init>();
    public void handleMessage(android.os.Message);
}

# Keep Handler subclass methods (required for Messenger/IPC)
-keepclassmembers class com.payzone.transaction.client.** extends android.os.Handler {
    public void handleMessage(android.os.Message);
}

# Strip debug and verbose logs in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}


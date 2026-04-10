# Payzone Transaction Service API Client for Android

Android client library for communicating with the Payzone Transaction Service (PTS) component via Android IPC (Messenger/Handler).

- **Min SDK:** 23
- **Language:** Kotlin (Java compatible)
- **Version:** 0.2.2
- **Async:** All API calls return `true` immediately (message queued). Responses are delivered asynchronously via your `Handler`.

---

## Installation

Add the AAR to your project and declare the dependency in your `build.gradle`:

```groovy
dependencies {
    implementation 'com.payzone.transaction.client:payzone-ts-client-android:0.2.2'
}
```

---

## Quick Start

### 1. Create a Response Handler

Extend `MessageResponseHandler` to receive responses from the service:

```kotlin
class ResponseHandler : MessageResponseHandler() {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)  // populates response field
        when (msg.what) {
            MessageConstants.MSG_REGISTER_DEVICE ->
                Log.d(TAG, "Register Device: $response")
            MessageConstants.MSG_INIT_TRANSACTION ->
                Log.d(TAG, "Init Transaction: $response")
            MessageConstants.MSG_COMPLETE_TRANS ->
                Log.d(TAG, "Complete Transaction: $response")
        }
    }
}
```

Or use it directly from Java:

```java
public class ResponseHandler extends MessageResponseHandler {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg); // populates response field
        switch (msg.what) {
            case MessageConstants.MSG_REGISTER_DEVICE:
                Log.d(TAG, "Register Device: " + getResponse());
                break;
            case MessageConstants.MSG_INIT_TRANSACTION:
                Log.d(TAG, "Init Transaction: " + getResponse());
                break;
        }
    }
}
```

> **Note:** Responses are GZIP-compressed. `super.handleMessage(msg)` automatically decompresses and stores the result in the `response` field via `ApiClient.decompressData()`.

### 2. Initialise in `onStart` / `onStop`

```kotlin
private lateinit var apiClient: ApiClient

override fun onStart() {
    super.onStart()
    val handler = ResponseHandler()
    val messenger = Messenger(handler)
    apiClient = ApiClient(applicationContext, messenger)
    apiClient.initService()
}

override fun onStop() {
    super.onStop()
    apiClient.destroyService()
}
```

---

## API Reference

### Lifecycle

| Method | Description |
|---|---|
| `initService()` | Binds to the Payzone Transaction Service. Call in `onStart`. |
| `destroyService()` | Unbinds from the service and cancels pending operations. Call in `onStop`. |
| `fetchConfigData()` | Fetches environment config from Payzone Config Server. Called automatically on connect. |
| `isTransactionReady()` | Checks whether a token and cashier ID exist and the service is ready. |

---

### Device & Session

#### `registerDevice`
```kotlin
val obj = JSONObject().apply {
    put("barcode", "267693243349691")
    put("deviceId", "1545D2053")
    put("tId", "49691")
}
apiClient.registerDevice(obj)
```

#### `getToken`
```kotlin
apiClient.getToken("49691")
```

#### `getTokenBySerialNumber`
```kotlin
apiClient.getTokenBySerialNumber("1545D2053")
```

#### `startSession`
```kotlin
val obj = JSONObject().apply { put("pin", 1234) }
apiClient.startSession(obj)
```

#### `storeCashierId`
```kotlin
apiClient.storeCashierId("1234")
```

#### `storeMerchantId`
```kotlin
apiClient.storeMerchantId("MID-001")
```

---

### Transactions

#### `initTransaction`

Client app (with `clientRef`):
```kotlin
val obj = JSONObject().apply {
    put("clientRef", "294decdf-0d8d-4bc5-9921-7460ab737fba")
    put("transactionGuid", "bfd0f250-66ce-11eb-863b-a5942ff6aec7")
    put("transactionAmount", 1000)
    put("barcode", "63385450042016567880")
}
apiClient.initTransaction(obj)
```

Standard Payzone:
```kotlin
val obj = JSONObject().apply {
    put("transactionSource", "0")
    put("productId", "24382")
    put("transactionGuid", "bfd0f250-66ce-11eb-863b-a5942ff6aec7")
    put("transactionAmount", 1000)
    put("barcode", "63385450042016567880")
}
apiClient.initTransaction(obj)
```

#### `completeTransaction`
```kotlin
val obj = JSONObject().apply {
    put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7")
    put("utrn", "1100883828292828")
    put("responseCode", "00")  // "00" = success, "05" = failure
}
apiClient.completeTransaction(obj)
```

#### `markTransactionSuccess`
```kotlin
val obj = JSONObject().apply { put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7") }
apiClient.markTransactionSuccess(obj)
```

#### `markTransactionFailed`
```kotlin
val obj = JSONObject().apply { put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7") }
apiClient.markTransactionFailed(obj)
```

#### `markReceiptPrinted`
```kotlin
val obj = JSONObject().apply { put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7") }
apiClient.markReceiptPrinted(obj)
```

#### `getTransactionByNumber`
```kotlin
val obj = JSONObject().apply { put("transactionNumber", "TXN-001") }
apiClient.getTransactionByNumber(obj)
```

#### `validateBarcode`
```kotlin
val obj = JSONObject().apply { put("barcode", "63385450042016567880") }
apiClient.validateBarcode(obj)
```

---

### Basket

#### `openBasket`
```kotlin
apiClient.openBasket(basketId)
```

#### `closeBasket`
```kotlin
apiClient.closeBasket(basketId)
```

---

### Talexus

Register broadcast receivers to track hardware status:

```kotlin
// Key inserted/removed
val keyReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isInserted = intent.extras?.getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED)
    }
}
registerReceiver(keyReceiver, IntentFilter(MessageConstants.ACTION_KEY_INSERTED))

// Box connected/disconnected
val boxReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = intent.extras?.getBoolean(MessageConstants.RESP_TALEXUS_BOX_STATUS)
    }
}
registerReceiver(boxReceiver, IntentFilter(MessageConstants.ACTION_TALEXUS_BOX_STATUS))
```

| Method | Description |
|---|---|
| `initTalexus()` | Initialise Talexus services. |
| `stopTalexus()` | Stop Talexus services. Call when closing the app. |
| `isKeyInserted()` | Check whether a Talexus key is currently inserted. |
| `isBoxConnected()` | Check whether the Talexus box is connected. |
| `readKey()` | Read the inserted Talexus key data. |

#### `readKey` response
```json
{
  "success": true,
  "keyImage": "333346574081DDBD...",
  "variants": [
    { "id": 12, "name": "EDF Add Credit", "uiFlow": "talexus.addCredit", "maxAmount": 8000 },
    { "id": 11, "name": "EDF RTI", "uiFlow": "talexus.resetKey" },
    { "id": 13, "name": "EDF Display Balance", "uiFlow": "talexus.displayBalance", "balance": 17500 }
  ]
}
```

#### `addCredit` — POL network
```kotlin
val obj = JSONObject().apply {
    put("amount", "600")
    put("productId", "68128")
    put("keyImage", "555560321131...")
    put("basketId", basketId)
    put("fadCode", fadCode)
    put("nodeId", nodeId)
}
apiClient.addCredit(obj)
```

#### `pzAddCredit` — Payzone network
```kotlin
val obj = JSONObject().apply {
    put("amount", "600")
    put("productId", "68128")
    put("keyImage", "555560321131...")
}
apiClient.pzAddCredit(obj)
```

#### `rti` — POL network
```kotlin
val obj = JSONObject().apply {
    put("rtiReference", "05318140")
    put("productId", "68129")
    put("keyImage", "555560321131...")
    put("basketId", basketId)
}
apiClient.rti(obj)
```

#### `pzRti` — Payzone network
```kotlin
val obj = JSONObject().apply {
    put("rtiReference", "05318140")
    put("productId", "68129")
    put("keyImage", "555560321131...")
}
apiClient.pzRti(obj)
```

#### `reversal`
```kotlin
val obj = JSONObject().apply {
    put("productId", "68129")
    put("keyImage", "555560321131...")
}
apiClient.reversal(obj)
```

#### `getMerchantCredit`
```kotlin
apiClient.getMerchantCredit()
```

---

### Quantum

| Method | Description |
|---|---|
| `sale(jsonParams)` | Initiate a Quantum sale. |
| `securityKeys(jsonParams)` | Send Quantum security keys. |
| `localSecretCode(jsonParams)` | Send Quantum local secret code. |
| `nspHotcard(jsonParams)` | Quantum NSP hotcard operation. |
| `csRegional(jsonParams)` | Quantum CS regional operation. |
| `quantumTransactionComplete(jsonParams)` | Mark a Quantum transaction as complete. |
| `quantumRtiTransaction(jsonParams)` | Quantum RTI transaction. |

---

### ePay

| Method | Description |
|---|---|
| `epayVariants(jsonParams)` | Retrieve available ePay product variants. |
| `epayPurchase(jsonParams)` | Initiate an ePay purchase. |
| `epayReversal(jsonParams)` | Reverse an ePay transaction. |

---

### Keypad

| Method | Description |
|---|---|
| `validateKeypadCode(jsonParams)` | Validate a keypad-entered code. |
| `keypadPurchase(jsonParams)` | Initiate a keypad purchase. |

---

## Response Format

All responses are delivered asynchronously to your `Handler`. The `response` field in `MessageResponseHandler` contains the decompressed JSON string after `super.handleMessage(msg)` is called.

Example response shape (most methods):
```json
{
  "success": true,
  "transactionGuid": "e9d8ec0a-604f-4a1d-bc0f-dac6d1631817",
  "customerReceipt": "...",
  "status": "Completed"
}
```

To manually decompress a response string:
```kotlin
val json = ApiClient.decompressData(compressedString)
```

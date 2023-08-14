# Payzone Transaction Service API Client For Android

This is the client library for interacting with the Payzone Transaction Service component.

## Defined Methods

- initService
- fetchConfigData
- isTransactionReady
- destroyService
- registerDevice
- getToken
- getTokenBySerialNumber
- startSession
- storeCashierId
- initTransaction
- completeTransaction
- markTransactionSuccess
- markTransactionFailed
- markReceiptPrinted
- readKey
- addCredit
- rti
- isKeyInserted

<br/>

## Usage

<details>
  <summary>  Add the ResponseHandler class for receiving the asynchronous reply from PT Service. </summary>

    public class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String response;
            switch (msg.what) {
                case MessageConstants.MSG_REGISTER_DEVICE:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_REGISTER_DEVICE));
                    System.out.println("## Register Device Response = "+response);
                    break;
                case MessageConstants.MSG_INIT_TRANSACTION:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_INIT_TRANSACTION));
                    System.out.println("## Transaction Initialised Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_SUCCESS:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_SUCCESS));
                    System.out.println("## Marked Successful Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_FAILED:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_FAILED));
                    System.out.println("## Marked Failed Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_RECEIPT_PRINTED:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED));
                    System.out.println("## Marked Receipt Printed Response = "+response);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

  <b>NOTE:</b>
  
  You will notice a `ApiClient.decompressData` method wrapped around the response. This is because we only send compress string response data.
  Android Bundle/Parcel has a size limit and to mitigate against that we have to compress our responses. Therefore, you have to decompress the response data for all message responses coming back from the Payzone Transaction Service.    
</details>
<hr/>

<details>
  <summary> initService - Initialise the ApiClient class in your activity onStart method</summary>
  <br>

    The ApiClient class constructor params are:

    - Your activity context.
    - Reply Messenger created using your Response handler.


    @Override
    protected void onStart() {
        super.onStart();
        responseHandler = new ResponseHandler();
        replyMessenger = new Messenger(responseHandler);
        apiClient = new ApiClient(getApplicationContext(), replyMessenger);
        apiClient.initService(); // Connects your app with the Payzone Transaction Service Component
    }


</details>
<hr/>

<details>
  <summary> fetchConfigData - Fetch environment params from Payzone Config Server App</summary>
  <br>

    The ApiClient class constructor params are:

    - Your activity context.
    - Reply Messenger created using your Response handler.


    @Override
    protected void onStart() {
        super.onStart();
        responseHandler = new ResponseHandler();
        replyMessenger = new Messenger(responseHandler);
        apiClient = new ApiClient(getApplicationContext(), replyMessenger);
        apiClient.initService(); // Connects your app with the Payzone Transaction Service Component
        apiClient.fetchConfigData(); // Fetch environment params
    }


</details>
<hr/>

<details>
  <summary>isTransactionReady - Checks if Payzone Transaction Service (PTS) is ready to start transacting (It will check if an API Token and Cashier ID exists). </summary>
  <br>

    boolean success =  apiClient.isTransactionReady();
    System.out.println("## Is Transaction Ready check sent to service queue: "+success);

  <b>Note:</b> 
  The call is Async and a response will be sent via the response handler.
</details>
<hr/>

<details>
  <summary>registerDevice - Register a device on Payzone Network </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("barcode", "267693243349691");
    obj.put("deviceId", "1545D2053");
    obj.put("tId", "49691");
    boolean success =  apiClient.registerDevice(obj);
    System.out.println("## Device Registration sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>getToken - Get Token </summary>
  <br>

    String tId = "49691";
    boolean success =  apiClient.getToken(tId);
    System.out.println("## Get Token Request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>getTokenBySerialNumber - Get Token by Serial Number</summary>
  <br>

    String serialNumber = "1545D2053";
    boolean success =  apiClient.getTokenBySerialNumber(serialNumber);
    System.out.println("## Get Token By Serial Number Request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>startSession - Login / Start a new session </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("pin", 1234);
    boolean success =  apiClient.startSession(obj);
    System.out.println("## Get Session Request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>storeCashierId - Manually send your cashier ID to the Payzone Transaction Service before you can start transacting. Mostly for pre-registered terminals/devices </summary>
  <br>


    String cashierId = "1234";
    boolean success =  apiClient.storeCashierId(cashierId);
    System.out.println("## Get Store Cashier sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>initTransaction - Initialise a transaction on Payzone Network </summary>
  <br>
  
  Client App use case (For client apps with associated clientRef):


    JSONObject obj = new JSONObject();
    obj.put("clientRef", "294decdf-0d8d-4bc5-9921-7460ab737fba");
    obj.put("transactionGuid", "bfd0f250-66ce-11eb-863b-a5942ff6aec7");
    obj.put("transactionAmount", 1000);
    obj.put("barcode", "63385450042016567880");
    boolean success =  apiClient.initTransaction(obj);
    System.out.println("## Transaction init sent to service queue: "+success);

  Standard Payzone use case:
  
    JSONObject obj = new JSONObject();
    obj.put("transactionSource", "0");
    obj.put("productId", "24382");
    obj.put("transactionGuid", "bfd0f250-66ce-11eb-863b-a5942ff6aec7");
    obj.put("transactionAmount", 1000);
    obj.put("barcode", "63385450042016567880");
    boolean success =  apiClient.initTransaction(obj);
    System.out.println("## Transaction init sent to service queue: "+success);
      
</details>
<hr/>

<details>
  <summary>completeTransaction - i.e. Update ongoing transaction with extra transaction attributes </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7"); // i.e. Your transactionGuid
    obj.put("utrn", "1100883828292828"); // or this could be ticketNumber, extra_json_info object etc..
    obj.put("responseCode", "00"); // "00" for successful topup or "05" for failure.
    obj.put("smartMeterErrorText", "Something went wrong"); // Should in case it is a faulure
    boolean success =  apiClient.completeTransaction(obj);
    System.out.println("## Complete transaction request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>markTransactionSuccess - Update transaction as success on Payzone Transaction Network </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7");
    boolean success =  apiClient.markTransactionSuccess(obj);
    System.out.println("## Mark transaction successful request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>markTransactionFailed - Update transaction as failed on Payzone Transaction Network  </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7");
    boolean success =  apiClient.markTransactionFailed(obj);
    System.out.println("## Mark transaction failed request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>markReceiptPrinted - Update transaction receipt printed timestamp on Payzone Transaction Network  </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7");
    boolean success =  apiClient.markReceiptPrinted(obj);
    System.out.println("## Transaction receipt printed request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>readKey - Read talexus key </summary>
  <br>


    boolean success =  apiClient.readKey();
    System.out.println("## Read talexus key: " + success);


</details>
<hr/>


<details>
  <summary>addCredit - Talexus add credit using Payzone Transaction Network  </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("amount", "600");
    obj.put("productId", "68128");
    obj.put("keyImage", "555560321131866500366BE0400500550001D21C0000005AC000F779A2A1E350990B840084008400440004000400040004000400040044008400040004000400101101000F0D0000000000000000000000000000000000000000000080000098000000000000C04C000000000000000000000000000000000000000000003A94");
    boolean success =  apiClient.addCredit(obj);
    System.out.println("## Talexus add credit: "+success);


</details>
<hr/>

<details>
  <summary>rti - Talexus RTI using Payzone Transaction Network  </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("rtiReference", "05318140");
    obj.put("productId", "68129");
    obj.put("keyImage", "555560321131866500366BE0400500550001D21C0000005AC000F779A2A1E350990B840084008400440004000400040004000400040044008400040004000400101101000F0D0000000000000000000000000000000000000000000080000098000000000000C04C000000000000000000000000000000000000000000003A94");
    boolean success =  apiClient.rti(obj);
    System.out.println("## Talexus add credit: "+success);


</details>
<hr/>

<details>
  <summary>isKeyInserted - Check if talexus key is inserted </summary>
  <br>

    boolean success =  apiClient.isKeyInserted();
    System.out.println("## Is talexus key inserted: " + success);

</details>
<hr/>

<details>
  <summary>destroyService - Important! Destroy the Service in your activity onStop method</summary>
  <br>


    @Override
    protected void onStop() {
        super.onStop();
        if(apiClient.destroyService()) {
            System.out.println("## Disconnected from Payzone Transaction service...");
        }
    }


</details>

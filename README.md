# Payzone Transaction Service API Client For Android

This is the client library for interacting with the Payzone Transaction Service component. 
Note :- If the project's target SDK is higher than API 29 and above use 0.1.8 

## Defined Methods

- initService
- initTalexus
- stopTalexus
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
- pzAddCredit
- pzRti
- isKeyInserted
- isBoxConnected
- reversal

- openBasket
- closeBasket
- addCredit
- rti

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
  <summary>Talexus - Handle the key status</summary>
  <br>

  Register a broadcast receiver to receive the Talexus key status(inserted/removed)

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isKeyInserted = intent.getExtras().getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED);
            //Perform the actions
        }
    };

    
    registerReceiver(mHandleMessageReceiver, new IntentFilter(MessageConstants.ACTION_KEY_INSERTED));

Register a broadcast receiver to receive the Talexus box status(connected/disconnected)

    private final BroadcastReceiver mHandleBoxStatusMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isBoxConnected = intent.getExtras().getBoolean(MessageConstants.RESP_TALEXUS_BOX_STATUS);
            //Perform the actions
        }
    };

    
    registerReceiver(mHandleBoxStatusMessageReceiver, new IntentFilter(MessageConstants.ACTION_TALEXUS_BOX_STATUS));



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
  <summary> initTalexus - Need to initialise the Talexus services for handling the talexus transactions</summary>
  <br>

    apiClient.initTalexus();

</details>
<hr/>

<details>
  <summary> stopTalexus - Recommended to close the talexus service while closing the app.</summary>
  <br>

    apiClient.stopTalexus();


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
    
    Response:-
    {
      "success": true,
      "keyImage": "333346574081DDBD0004926E120600AF000A47340000D3AC41E5FFAB4070C140990B440EC800840E040E040E040E040E040E040E040E440E040E440E040E040E22010100261E0D07000000000000395A000000000000000000000000800000180000000000005FE30000000000000000000000000000000000000000000056BC",
      "variants": [
        {
          "id": 12,
          "iin": "102",
          "maxAmount": 8000,
          "multipleOf": 100,
          "name": "EDF Add Credit",
          "uiFlow": "talexus.addCredit"
        },
        {
          "id": 11,
          "iin": "102",
          "name": "EDF RTI",
          "uiFlow": "talexus.resetKey"
        },
        {
          "balance": 17500,
          "id": 13,
          "iin": "102",
          "name": "EDF Display Balance",
          "uiFlow": "talexus.displayBalance"
        }
      ]
    }
        

</details>
<hr/>


<details>
  <summary>pzAddCredit - Talexus add credit using Payzone Transaction Network  </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("amount", "600");
    obj.put("productId", "68128");
    obj.put("keyImage", "555560321131866500366BE0400500550001D21C0000005AC000F779A2A1E350990B840084008400440004000400040004000400040044008400040004000400101101000F0D0000000000000000000000000000000000000000000080000098000000000000C04C000000000000000000000000000000000000000000003A94");
    boolean success =  apiClient.pzAddCredit(obj);
    System.out.println("## Talexus add credit: "+success);

    Response:-
    {
      "success": true,
      "transactionGuid": "e9d8ec0a-604f-4a1d-bc0f-dac6d1631817",
      "customerReceipt": "<style>\n.receipt_preview {width: 100%;height: 100%;margin: 100px 0;}.receipt_preview.paper-bus {width: 100%;margin: 0 160px 100px 0;border-right: dashed 2px black;}@media only screen and (min-width : 575px){.receipt_preview td {width: 100%;font-size: 22pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}@media only screen and (max-width : 575px) and (orientation:portrait){.receipt_preview td {width: 100%;font-size: 19pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}.receipt_preview .receipt_line {border-bottom: 3px dashed #000;}.receipt_preview .receipt_half_height {width: 100%;height: 7px;}.receipt_preview .receipt_preview_image {width: 100%;}<\/style>\n<table class=\"receipt_preview\">\n<tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">Talexus EDF<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER TYPE:<\/td><td width=\"50%\"   align=\"right\">SML<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER NUMBER:<\/td><td width=\"50%\"   align=\"right\">F08D014657<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">KEY NUMBER:<\/td><td width=\"50%\"   align=\"right\">0004DDBD<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CRN:<\/td><td width=\"50%\"   align=\"right\">671070084911<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">SUPPLIER ID:<\/td><td width=\"50%\"   align=\"right\">4<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">HOST ID:<\/td><td width=\"50%\"   align=\"right\">4<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">Payment<\/td><td width=\"50%\"   align=\"right\">&pound;3.00<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">MOP<\/td><td width=\"50%\"   align=\"right\">CASH<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CREDIT:<\/td><td width=\"50%\"   align=\"right\">&pound;188.00<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">CUSTOMER COPY<\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">We're here for your<\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">energy top-ups this winter.<\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">Visit<\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">storelocator.payzone.co.uk<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">M423857<\/td><td width=\"50%\"   align=\"right\">C0002<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">T35112029<\/td><td width=\"50%\"   align=\"right\">R0050<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">DATE 17\/08\/23<\/td><td width=\"50%"   align=\"left\">DATE 17\/08\/23<\/td><td width=\"50%\"   align=\"right\">10:48<\/td><\/tr><\/table><br><br><br><br>\n",
      "status": "Completed"
    } 

</details>
<hr/>

<details>
  <summary>pzRti - Talexus RTI using Payzone Transaction Network  </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("rtiReference", "05318140");
    obj.put("productId", "68129");
    obj.put("keyImage", "555560321131866500366BE0400500550001D21C0000005AC000F779A2A1E350990B840084008400440004000400040004000400040044008400040004000400101101000F0D0000000000000000000000000000000000000000000080000098000000000000C04C000000000000000000000000000000000000000000003A94");
    boolean success =  apiClient.pzRti(obj);
    System.out.println("## Talexus add credit: "+success);

    Response:-
    {
      "success": true,
      "transactionGuid": "aa204f2a-9eb0-43d1-a7e2-e00de5372eb8",
      "customerReceipt": "<style>\n.receipt_preview {width: 100%;height: 100%;margin: 100px 0;}.receipt_preview.paper-bus {width: 100%;margin: 0 160px 100px 0;border-right: dashed 2px black;}@media only screen and (min-width : 575px){.receipt_preview td {width: 100%;font-size: 22pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}@media only screen and (max-width : 575px) and (orientation:portrait){.receipt_preview td {width: 100%;font-size: 19pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}.receipt_preview .receipt_line {border-bottom: 3px dashed #000;}.receipt_preview .receipt_half_height {width: 100%;height: 7px;}.receipt_preview .receipt_preview_image {width: 100%;}<\/style>\n<table class=\"receipt_preview\">\n<tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">Talexus EDF<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER TYPE:<\/td><td width=\"50%\"   align=\"right\">SE<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER NUMBER:<\/td><td width=\"50%\"   align=\"right\">S95A062911<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">KEY NUMBER:<\/td><td width=\"50%\"   align=\"right\">002D7DBC<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CRN:<\/td><td width=\"50%\"   align=\"right\">671069359233        <\/td><\/tr><tr><td width=\"50%\"   align=\"left\">SUPPLIER ID:<\/td><td width=\"50%\"   align=\"right\">0<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">HOST ID:<\/td><td width=\"50%\"   align=\"right\">0<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">RTI APPLIED<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">Customer ID<\/td><td width=\"50%\"   align=\"right\"><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CREDIT:<\/td><td width=\"50%\"   align=\"right\">&pound;30.00<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">CUSTOMER COPY<\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">M423857<\/td><td width=\"50%\"   align=\"right\">C0002<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">T35112029<\/td><td width=\"50%\"   align=\"right\">R0031<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">DATE 17\/08\/23<\/td><td width=\"50%\"   align=\"right\">10:48<\/td><\/tr><\/table><br><br><br><br>\n",
      "status": "Completed"
    }

</details>
<hr/>

<details>
  <summary>isKeyInserted - Check if talexus key is inserted </summary>
  <br>

    boolean success =  apiClient.isKeyInserted();
    System.out.println("## Is talexus key inserted: " + success);

     Response:-
    {"success":true,"isKeyInserted":true/false}
}
     
</details>
<details>
  <summary>isBoxConnected - Check if talexus box is connected </summary>
  <br>

    boolean success =  apiClient.isBoxConnected();
    System.out.println("## Is talexus key inserted: " + success);

     Response:-
    {"success":true,"isBoxConnected":true/false}

</details>
<details>
  <summary>reversal - Reverse the last transaction </summary>
  <br>

    JSONObject obj = new JSONObject();
    obj.put("productId", "68129");
    obj.put("keyImage", "555560321131866500366BE0400500550001D21C0000005AC000F779A2A1E350990B840084008400440004000400040004000400040044008400040004000400101101000F0D0000000000000000000000000000000000000000000080000098000000000000C04C000000000000000000000000000000000000000000003A94");
    boolean success =  apiClient.reversal(obj);
    System.out.println("## Talexus reversal: "+success);

    Response:-
    {
      "success": true,
      "transactionGuid": "aa204f2a-9eb0-43d1-a7e2-e00de5372eb8",
      "customerReceipt": "<style>\n.receipt_preview {width: 100%;height: 100%;margin: 100px 0;}.receipt_preview.paper-bus {width: 100%;margin: 0 160px 100px 0;border-right: dashed 2px black;}@media only screen and (min-width : 575px){.receipt_preview td {width: 100%;font-size: 22pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}@media only screen and (max-width : 575px) and (orientation:portrait){.receipt_preview td {width: 100%;font-size: 19pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}.receipt_preview .receipt_line {border-bottom: 3px dashed #000;}.receipt_preview .receipt_half_height {width: 100%;height: 7px;}.receipt_preview .receipt_preview_image {width: 100%;}<\/style>\n<table class=\"receipt_preview\">\n<tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">Talexus EDF<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER TYPE:<\/td><td width=\"50%\"   align=\"right\">SE<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER NUMBER:<\/td><td width=\"50%\"   align=\"right\">S95A062911<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">KEY NUMBER:<\/td><td width=\"50%\"   align=\"right\">002D7DBC<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CRN:<\/td><td width=\"50%\"   align=\"right\">671069359233        <\/td><\/tr><tr><td width=\"50%\"   align=\"left\">SUPPLIER ID:<\/td><td width=\"50%\"   align=\"right\">0<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">HOST ID:<\/td><td width=\"50%\"   align=\"right\">0<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">RTI APPLIED<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">Customer ID<\/td><td width=\"50%\"   align=\"right\"><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CREDIT:<\/td><td width=\"50%\"   align=\"right\">&pound;30.00<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">CUSTOMER COPY<\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">M423857<\/td><td width=\"50%\"   align=\"right\">C0002<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">T35112029<\/td><td width=\"50%\"   align=\"right\">R0031<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">DATE 17\/08\/23<\/td><td width=\"50%\"   align=\"right\">10:48<\/td><\/tr><\/table><br><br><br><br>\n",
      "status": "Completed"
    }
     
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
<details>
  <summary>openBasket - To create a basket entry</summary>
  <br>

    boolean success =  apiClient.openBasket(basketId);
    Response:-
    {
      "success": true,
      "id": "1222"
    }

</details>

<details>
  <summary>openBasket - To close the basket</summary>
  <br>

    The response id received for opening the basket
    boolean success =  apiClient.openBasket(id);

</details>

<details>
  <summary>addCredit - Talexus add credit using POL Transaction Network  </summary>
  <br>

    JSONObject obj = new JSONObject();
    obj.put("amount", "600");
    obj.put("productId", "68128");
    obj.put("keyImage", "555560321131866500366BE0400500550001D21C0000005AC000F779A2A1E350990B840084008400440004000400040004000400040044008400040004000400101101000F0D0000000000000000000000000000000000000000000080000098000000000000C04C000000000000000000000000000000000000000000003A94");
    obj.put("basketId", basketId);
    obj.put("fadCode", fadCode);
    obj.put("nodeId", nodeId);
    obj.put("stockUnitIdentifier", stockUnitIdentifier);
    obj.put("smartId", smartId);
    obj.put("deviceId", deviceId);
    obj.put("deviceType", deviceType);
    obj.put("itemId", itemId);
    boolean success =  apiClient.addCredit(obj);
    System.out.println("## Talexus add credit: "+success);

    Response:-
    {
      "success": true,
      "transactionGuid": "e9d8ec0a-604f-4a1d-bc0f-dac6d1631817",
      "customerReceipt": "<style>\n.receipt_preview {width: 100%;height: 100%;margin: 100px 0;}.receipt_preview.paper-bus {width: 100%;margin: 0 160px 100px 0;border-right: dashed 2px black;}@media only screen and (min-width : 575px){.receipt_preview td {width: 100%;font-size: 22pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}@media only screen and (max-width : 575px) and (orientation:portrait){.receipt_preview td {width: 100%;font-size: 19pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}.receipt_preview .receipt_line {border-bottom: 3px dashed #000;}.receipt_preview .receipt_half_height {width: 100%;height: 7px;}.receipt_preview .receipt_preview_image {width: 100%;}<\/style>\n<table class=\"receipt_preview\">\n<tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">Talexus EDF<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER TYPE:<\/td><td width=\"50%\"   align=\"right\">SML<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER NUMBER:<\/td><td width=\"50%\"   align=\"right\">F08D014657<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">KEY NUMBER:<\/td><td width=\"50%\"   align=\"right\">0004DDBD<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CRN:<\/td><td width=\"50%\"   align=\"right\">671070084911<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">SUPPLIER ID:<\/td><td width=\"50%\"   align=\"right\">4<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">HOST ID:<\/td><td width=\"50%\"   align=\"right\">4<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">Payment<\/td><td width=\"50%\"   align=\"right\">&pound;3.00<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">MOP<\/td><td width=\"50%\"   align=\"right\">CASH<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CREDIT:<\/td><td width=\"50%\"   align=\"right\">&pound;188.00<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\"><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">CUSTOMER COPY<\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">We're here for your<\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">energy top-ups this winter.<\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">Visit<\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">storelocator.payzone.co.uk<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">M423857<\/td><td width=\"50%\"   align=\"right\">C0002<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">T35112029<\/td><td width=\"50%\"   align=\"right\">R0050<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">DATE 17\/08\/23<\/td><td width=\"50%"   align=\"left\">DATE 17\/08\/23<\/td><td width=\"50%\"   align=\"right\">10:48<\/td><\/tr><\/table><br><br><br><br>\n",
      "status": "Completed",
      "value": "0",
      "quantity": "1"
    } 

</details>
<hr/>

<details>
  <summary>rti - Talexus RTI using POL Transaction Network  </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("rtiReference", "05318140");
    obj.put("productId", "68129");
    obj.put("keyImage", "555560321131866500366BE0400500550001D21C0000005AC000F779A2A1E350990B840084008400440004000400040004000400040044008400040004000400101101000F0D0000000000000000000000000000000000000000000080000098000000000000C04C000000000000000000000000000000000000000000003A94");
    obj.put("basketId", basketId);
    obj.put("fadCode", fadCode);
    obj.put("nodeId", nodeId);
    obj.put("stockUnitIdentifier", stockUnitIdentifier);
    obj.put("smartId", smartId);
    obj.put("deviceId", deviceId);
    obj.put("deviceType", deviceType);
    obj.put("itemId", itemId);
    boolean success =  apiClient.rti(obj);
    System.out.println("## Talexus add credit: "+success);

    Response:-
    {
      "success": true,
      "transactionGuid": "aa204f2a-9eb0-43d1-a7e2-e00de5372eb8",
      "customerReceipt": "<style>\n.receipt_preview {width: 100%;height: 100%;margin: 100px 0;}.receipt_preview.paper-bus {width: 100%;margin: 0 160px 100px 0;border-right: dashed 2px black;}@media only screen and (min-width : 575px){.receipt_preview td {width: 100%;font-size: 22pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}@media only screen and (max-width : 575px) and (orientation:portrait){.receipt_preview td {width: 100%;font-size: 19pt;font-family: arial;-webkit-font-smoothing: none;padding: 0;}}.receipt_preview .receipt_line {border-bottom: 3px dashed #000;}.receipt_preview .receipt_half_height {width: 100%;height: 7px;}.receipt_preview .receipt_preview_image {width: 100%;}<\/style>\n<table class=\"receipt_preview\">\n<tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\"><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">Talexus EDF<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER TYPE:<\/td><td width=\"50%\"   align=\"right\">SE<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">METER NUMBER:<\/td><td width=\"50%\"   align=\"right\">S95A062911<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">KEY NUMBER:<\/td><td width=\"50%\"   align=\"right\">002D7DBC<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CRN:<\/td><td width=\"50%\"   align=\"right\">671069359233        <\/td><\/tr><tr><td width=\"50%\"   align=\"left\">SUPPLIER ID:<\/td><td width=\"50%\"   align=\"right\">0<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">HOST ID:<\/td><td width=\"50%\"   align=\"right\">0<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"left\">RTI APPLIED<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">Customer ID<\/td><td width=\"50%\"   align=\"right\"><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">CREDIT:<\/td><td width=\"50%\"   align=\"right\">&pound;30.00<\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td colspan=\"2\" width=\"100%\"   align=\"center\">CUSTOMER COPY<\/td><\/tr><tr><td colspan=\"2\"><hr><\/td><\/tr><tr><td class=\"receipt_half_height\" colspan=\"2\"><div class=\"receipt_half_height\"><div><\/td><\/tr><tr><td width=\"50%\"   align=\"left\">M423857<\/td><td width=\"50%\"   align=\"right\">C0002<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">T35112029<\/td><td width=\"50%\"   align=\"right\">R0031<\/td><\/tr><tr><td width=\"50%\"   align=\"left\">DATE 17\/08\/23<\/td><td width=\"50%\"   align=\"right\">10:48<\/td><\/tr><\/table><br><br><br><br>\n",
      "status": "Completed",
      "value": "0",
      "quantity": "1"
    }

</details>
<hr/>

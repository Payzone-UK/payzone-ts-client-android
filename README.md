# Payzone Transaction Service API Client For Android

This is the client library for interacting with the Payzone Transaction Service component.

## Defined Methods

- initService
- destroyService
- registerDevice
- getToken
- startSession
- initTransaction
- completeTransaction
- markTransactionSuccess
- markTransactionFailed
- markReceiptPrinted

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
                    response = msg.getData().getString(MessageConstants.RESP_REGISTER_DEVICE);
                    System.out.println("## Register Device Response = "+response);
                    break;
                case MessageConstants.MSG_INIT_TRANSACTION:
                    response = msg.getData().getString(MessageConstants.RESP_INIT_TRANSACTION);
                    System.out.println("## Transaction Initialised Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_SUCCESS:
                    response = msg.getData().getString(MessageConstants.RESP_MARK_TRANS_SUCCESS);
                    System.out.println("## Marked Successful Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_FAILED:
                    response = msg.getData().getString(MessageConstants.RESP_MARK_TRANS_FAILED);
                    System.out.println("## Marked Failed Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_RECEIPT_PRINTED:
                    response = msg.getData().getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED);
                    System.out.println("## Marked Receipt Printed Response = "+response);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

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


    JSONObject obj = new JSONObject();
    obj.put("tId", 49691);
    boolean success =  apiClient.getToken(obj);
    System.out.println("## Get Token Request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>startSession - Login / Start a new session </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("pin", 0000);
    boolean success =  apiClient.startSession(obj);
    System.out.println("## Get Session Request sent to service queue: "+success);


</details>
<hr/>

<details>
  <summary>initTransaction - Initialise a transaction on Payzone Network </summary>
  <br>


    JSONObject obj = new JSONObject();
    obj.put("transactionSource", 0);
    obj.put("transactionGuid", "bfd0f250-66ce-11eb-863b-a5942ff6aec7");
    obj.put("productId", "3789");
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
    obj.put("id", "bfd0f250-66ce-11eb-863b-a5942ff6aec7");
    obj.put("utrn", "1100883828292828"); // or this could be ticketNumber etc..
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

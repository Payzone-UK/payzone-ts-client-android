package com.payzone.transaction.client;

public class MessageConstants {
    // Request Codes
    public static final int MSG_CONFIG_SETUP = 0;
    public static final int MSG_REGISTER_DEVICE = 1;
    public static final int MSG_INIT_TRANSACTION = 2;
    public static final int MSG_COMPLETE_TRANS = 3;
    public static final int MSG_MARK_TRANS_SUCCESS = 4;
    public static final int MSG_MARK_TRANS_FAILED = 5;
    public static final int MSG_MARK_RECEIPT_PRINTED = 6;
    public static final int MSG_GET_TOKEN = 7;
    public static final int MSG_START_SESSION = 8;
    public static final int MSG_STORE_CID = 9;
    public static final int MSG_IS_TRANSACTION_READY = 10;
    public static final int MSG_GET_TOKEN_BY_SERIAL_NUMBER = 11;
    public static final int MSG_TALEXUS_READ_KEY = 12;
    public static final int MSG_TALEXUS_ADD_CREDIT = 13;
    public static final int MSG_TALEXUS_RTI = 14;
    public static final int MSG_TALEXUS_IS_KEY_INSERTED = 15;
    public static final int MSG_TALEXUS_REVERSE_CREDIT = 16;
    public static final int MSG_QUANTUM_SALE = 17;
    public static final int MSG_QUANTUM_SECURITY_KEYS = 18;
    public static final int MSG_QUANTUM_LOCAL_SECRET_CODE = 19;
    public static final int MSG_QUANTUM_NSP_HOT_CARD = 20;
    public static final int MSG_QUANTUM_CS_REGIONAL = 21;
    public static final int MSG_QUANTUM_TRANSACTION_COMPLETE = 22;
    public static final int MSG_QUANTUM_RTI = 23;

    // Parameter KEYS FOR
    public static final String RESP_CONFIG_SETUP = "configSetup";
    public static final String RESP_REGISTER_DEVICE = "registerDevice";
    public static final String RESP_INIT_TRANSACTION = "initTransaction";
    public static final String RESP_COMPLETE_TRANS = "completeTransaction";
    public static final String RESP_MARK_TRANS_SUCCESS = "markTransSuccess";
    public static final String RESP_MARK_TRANS_FAILED = "markTransFailed";
    public static final String RESP_MARK_RECEIPT_PRINTED = "markReceiptPrinted";
    public static final String RESP_GET_TOKEN = "getToken";
    public static final String RESP_GET_TOKEN_BY_SERIAL_NUMBER = "getTokenBySerialNumber";
    public static final String RESP_START_SESSION = "startSession";
    public static final String RESP_STORE_CID = "storeCid";
    public static final String RESP_IS_TRANSACTION_READY = "isTransactionReady";
    public static final String RESP_TALEXUS_READ_KEY = "readKey";
    public static final String RESP_TALEXUS_ADD_CREDIT = "addCredit";
    public static final String RESP_TALEXUS_RTI = "rti";
    public static final String RESP_TALEXUS_IS_KEY_INSERTED = "isKeyInserted";
    public static final String RESP_TALEXUS_REVERSE_CREDIT = "reversal";
    public static final String RESP_TALEXUS_BOX_STATUS = "talexusBoxConnected";
    // Intent actions to receive the broadcasts
    public static final String ACTION_KEY_INSERTED = "talexus.key.inserted";
    public static final String RESP_QUANTUM_SALE = "sale";
    public static final String RESP_QUANTUM_SECURITY_KEYS = "securityKeys";
    public static final String RESP_QUANTUM_LOCAL_SECRET_CODE = "localSecretCode";
    public static final String RESP_QUANTUM_NSP_HOT_CARD = "nspHotcard";
    public static final String RESP_QUANTUM_CS_REGIONAL = "csRegional";
    public static final String RESP_QUANTUM_TRANSACTION_COMPLETE = "quantumTransactionComplete";
    public static final String RESP_QUANTUM_RTI = "quantumRti";
}

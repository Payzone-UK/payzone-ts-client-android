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
}

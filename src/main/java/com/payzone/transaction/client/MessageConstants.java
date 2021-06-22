package com.payzone.transaction.client;

public class MessageConstants {
    // Request Codes
    public static final int MSG_REGISTER_DEVICE = 1;
    public static final int MSG_INIT_TRANSACTION = 2;
    public static final int MSG_COMPLETE_TRANS = 3;
    public static final int MSG_MARK_TRANS_SUCCESS = 4;
    public static final int MSG_MARK_TRANS_FAILED = 5;
    public static final int MSG_MARK_RECEIPT_PRINTED = 6;

    // Parameter KEYS FOR
    public static final String RESP_REGISTER_DEVICE = "registerDevice";
    public static final String RESP_INIT_TRANSACTION = "initTransaction";
    public static final String RESP_COMPLETE_TRANS = "completeTransaction";
    public static final String RESP_MARK_TRANS_SUCCESS = "markTransSuccess";
    public static final String RESP_MARK_TRANS_FAILED = "markTransFailed";
    public static final String RESP_MARK_RECEIPT_PRINTED = "markReceiptPrinted";
    public static final String PARAM_SPLITTER_REGEX = "--@--";
}

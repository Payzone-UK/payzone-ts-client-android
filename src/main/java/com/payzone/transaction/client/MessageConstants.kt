package com.payzone.transaction.client

object MessageConstants {
    // Request Codes
    const val MSG_CONFIG_SETUP = 0
    const val MSG_REGISTER_DEVICE = 1
    const val MSG_INIT_TRANSACTION = 2
    const val MSG_COMPLETE_TRANS = 3
    const val MSG_MARK_TRANS_SUCCESS = 4
    const val MSG_MARK_TRANS_FAILED = 5
    const val MSG_MARK_RECEIPT_PRINTED = 6
    const val MSG_GET_TOKEN = 7
    const val MSG_START_SESSION = 8
    const val MSG_STORE_CID = 9
    const val MSG_IS_TRANSACTION_READY = 10
    const val MSG_GET_TOKEN_BY_SERIAL_NUMBER = 11
    const val MSG_TALEXUS_READ_KEY = 12
    const val MSG_TALEXUS_ADD_CREDIT = 13
    const val MSG_TALEXUS_RTI = 14
    const val MSG_TALEXUS_IS_KEY_INSERTED = 15
    const val MSG_TALEXUS_REVERSE_CREDIT = 16
    const val MSG_TALEXUS_BOX_CONNECTED = 24
    const val MSG_QUANTUM_SALE = 17
    const val MSG_QUANTUM_SECURITY_KEYS = 18
    const val MSG_QUANTUM_LOCAL_SECRET_CODE = 19
    const val MSG_QUANTUM_NSP_HOT_CARD = 20
    const val MSG_QUANTUM_CS_REGIONAL = 21
    const val MSG_QUANTUM_TRANSACTION_COMPLETE = 22
    const val MSG_QUANTUM_RTI = 23
    const val MSG_OPEN_BASKET = 25
    const val MSG_CLOSE_BASKET = 26
    const val MSG_INIT_TALEXUS = 27
    const val MSG_STOP_TALEXUS = 28
    const val PZ_MSG_TALEXUS_ADD_CREDIT = 29
    const val PZ_MSG_TALEXUS_RTI = 30
    const val MSG_VALIDATE_KEYPAD_CODE = 31
    const val MSG_KEYPAD_PURCHASE = 32
    const val MSG_EPAY_VARIANTS = 33
    const val MSG_EPAY_PURCHASE = 34
    const val MSG_STORE_MID = 35
    const val MSG_EPAY_REVERSE = 36
    const val MSG_GET_TRANSACTION = 37
    const val MSG_VALIDATE_BARCODE = 38
    const val MSG_MERCHANT_CREDIT = 39

    // Response Keys
    const val RESP_CONFIG_SETUP = "configSetup"
    const val RESP_REGISTER_DEVICE = "registerDevice"
    const val RESP_INIT_TRANSACTION = "initTransaction"
    const val RESP_COMPLETE_TRANS = "completeTransaction"
    const val RESP_MARK_TRANS_SUCCESS = "markTransSuccess"
    const val RESP_MARK_TRANS_FAILED = "markTransFailed"
    const val RESP_MARK_RECEIPT_PRINTED = "markReceiptPrinted"
    const val RESP_GET_TOKEN = "getToken"
    const val RESP_GET_TOKEN_BY_SERIAL_NUMBER = "getTokenBySerialNumber"
    const val RESP_START_SESSION = "startSession"
    const val RESP_STORE_CID = "storeCid"
    const val RESP_IS_TRANSACTION_READY = "isTransactionReady"
    const val RESP_TALEXUS_READ_KEY = "readKey"
    const val RESP_TALEXUS_ADD_CREDIT = "addCredit"
    const val RESP_TALEXUS_RTI = "rti"
    const val RESP_TALEXUS_IS_KEY_INSERTED = "isKeyInserted"
    const val RESP_TALEXUS_REVERSE_CREDIT = "reversal"
    const val RESP_TALEXUS_BOX_STATUS = "talexusBoxConnected"
    const val RESP_QUANTUM_SALE = "sale"
    const val RESP_QUANTUM_SECURITY_KEYS = "securityKeys"
    const val RESP_QUANTUM_LOCAL_SECRET_CODE = "localSecretCode"
    const val RESP_QUANTUM_NSP_HOT_CARD = "nspHotcard"
    const val RESP_QUANTUM_CS_REGIONAL = "csRegional"
    const val RESP_QUANTUM_TRANSACTION_COMPLETE = "quantumTransactionComplete"
    const val RESP_QUANTUM_RTI = "quantumRti"
    const val RESP_OPEN_BASKET = "openBasket"
    const val RESP_CLOSE_BASKET = "closeBasket"
    const val RESP_INIT_TALEXUS = "initTalexus"
    const val RESP_STOP_TALEXUS = "stopTalexus"
    const val RESP_VALIDATE_KEYPAD_CODE = "validateKeypadCode"
    const val RESP_KEYPAD_PURCHASE = "keypadPurchase"
    const val RESP_EPAY_VARIANTS = "epayVariants"
    const val RESP_EPAY_PURCHASE = "epayPurchase"
    const val RESP_STORE_MID = "storeMid"
    const val RESP_EPAY_REVERSE = "epayReverse"
    const val RESP_TRANSACTION_BY_NUMBER = "getTransactionByTransactionNumber"
    const val RESP_VALIDATE_BARCODE = "validateBarcode"
    const val RESP_MERCHANT_CREDIT = "getMerchantCredit"
    /** Bundle key present when a message could not be delivered to the service. */
    const val RESP_SEND_FAILURE_REASON = "sendFailureReason"

    // Internal IPC bundle routing keys
    /** Bundle key whose value names the response key to expect in the reply. */
    const val BUNDLE_RESPONSE_KEY = "responseKey"
    /** Bundle key whose value is the calling app's package name. */
    const val BUNDLE_PACKAGE_NAME = "packageName"

    // Intent actions
    const val ACTION_KEY_INSERTED = "talexus.key.inserted"
    const val ACTION_TALEXUS_BOX_STATUS = "talexus.box.connected"
}

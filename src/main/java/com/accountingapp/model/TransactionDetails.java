package com.accountingapp.model;

import com.accountingapp.enums.TransactionEntryType;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

// Capture Double Entry Txn Pairs
@Data
public class TransactionDetails implements Serializable {

    private String transactionDetailsId;

    private String transactionId;

    private long accountId;

    private Timestamp createdOn;

    private int sequenceNo;

    private TransactionEntryType entryType;

}

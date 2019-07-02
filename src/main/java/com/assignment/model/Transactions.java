package com.assignment.model;

import com.assignment.enums.TransactionServiceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Transactions implements Serializable {

    private String transactionId;

    private long senderAccountId;

    private long receiverAccountId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp transactionDate;

    private BigDecimal amount;

    @JsonIgnore
    private TransactionServiceType serviceType;
}

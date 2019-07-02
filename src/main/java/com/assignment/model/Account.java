package com.assignment.model;

import com.assignment.enums.Status;
import com.assignment.utils.AmountUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    private long accountId;

    private long userId;

    @JsonSerialize(using = AmountUtil.AmountSerializer.class)
    private BigDecimal balance;

    private String currencyCode;

    @JsonIgnore
    private Status status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp modifiedOn;

    public Account(long accountId, long userId, BigDecimal balance, String currencyCode) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }
}

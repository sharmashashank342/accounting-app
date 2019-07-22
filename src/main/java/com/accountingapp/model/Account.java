package com.accountingapp.model;

import com.accountingapp.enums.Status;
import com.accountingapp.utils.AmountUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    private long accountId;

    private long userId;

    private BigDecimal balance;

    private String currencyCode;

    @JsonIgnore
    private Status status;

    private Timestamp createdOn;

    private Timestamp modifiedOn;
}

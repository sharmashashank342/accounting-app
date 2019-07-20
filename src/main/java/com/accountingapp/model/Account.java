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

    @JsonSerialize(using = AmountUtil.AmountSerializer.class)
    private BigDecimal balance;

    private String currencyCode;

    @JsonIgnore
    private Status status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp modifiedOn;
}

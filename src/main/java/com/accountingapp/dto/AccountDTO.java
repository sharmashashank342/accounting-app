package com.accountingapp.dto;

import com.accountingapp.utils.AmountUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class AccountDTO {

    private long accountId;
    private long userId;

    @JsonSerialize(using = AmountUtil.AmountSerializer.class)
    private BigDecimal balance;
    private String currencyCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp modifiedOn;
}

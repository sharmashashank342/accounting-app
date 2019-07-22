package com.accountingapp.dto;

import lombok.Data;

@Data
public class CreateAccountParams {

    private long userId;

    private String currencyCode;
}

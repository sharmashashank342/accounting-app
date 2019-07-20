package com.accountingapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionRequest {

	@JsonProperty(required = true)
	private BigDecimal amount;

	@JsonProperty(required = true)
	private Long senderAccountId;

	@JsonProperty(required = true)
	private Long receiverAccountId;
}

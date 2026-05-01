package com.sentinelai.transactionservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.sentinelai.transactionservice.model.enums.MerchantCategory;
import com.sentinelai.transactionservice.model.enums.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

	@NotNull
	private String transactionId;
	
	@NotNull
	@Positive
	private BigDecimal transactionAmount;
	
	@NotNull
	private String currency;
	
	@NotNull
    private String transactionCountry;
	
	private TransactionType transactionType;
	
	private MerchantCategory merchantCategory;
	
	@NotNull
	private long customerId;
	
    private String merchantId;
    
    private String receiverId;
	
    private LocalDateTime requestTime;
    
	@NotNull
    private Map<String, String> metadata;
}

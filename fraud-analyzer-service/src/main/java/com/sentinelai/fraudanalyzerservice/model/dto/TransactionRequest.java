package com.sentinelai.fraudanalyzerservice.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.sentinelai.fraudanalyzerservice.model.enums.MerchantCategory;
import com.sentinelai.fraudanalyzerservice.model.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

	private String transactionId;
	
	private BigDecimal transactionAmount;
	
	private String currency;
	
    private String transactionCountry;
	
	private TransactionType transactionType;
	
	private MerchantCategory merchantCategory;
	
	private long customerId;
	
    private String merchantId;
    
    private String receiverId;
	
    private LocalDateTime requestTime;
    
    private Map<String, String> metadata;
}

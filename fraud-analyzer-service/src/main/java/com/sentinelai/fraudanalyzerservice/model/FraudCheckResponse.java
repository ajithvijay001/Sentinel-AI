package com.sentinelai.fraudanalyzerservice.model;

import java.math.BigDecimal;

import com.sentinelai.fraudanalyzerservice.model.enums.FraudVerdict;

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
public class FraudCheckResponse {

	private BigDecimal riskScore;
	
	private FraudVerdict verdict;
	
	private String reason;
	
	private String transactionId;
}

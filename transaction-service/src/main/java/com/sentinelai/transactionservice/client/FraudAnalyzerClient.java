package com.sentinelai.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.sentinelai.transactionservice.model.TransactionRequest;
import com.sentinelai.transactionservice.model.dto.FraudCheckResponse;

@FeignClient(name="fraud-analyzer-service")
public interface FraudAnalyzerClient {
	
	@PostMapping("/api/v1/fraud-analysis/check")
	FraudCheckResponse checkTransaction(TransactionRequest request);
}

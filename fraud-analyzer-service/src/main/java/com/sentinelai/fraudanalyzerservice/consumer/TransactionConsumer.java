package com.sentinelai.fraudanalyzerservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sentinelai.fraudanalyzerservice.model.FraudCheckResponse;
import com.sentinelai.fraudanalyzerservice.model.dto.TransactionRequest;
import com.sentinelai.fraudanalyzerservice.service.FraudAnalysisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {
	private final FraudAnalysisService fraudAnalysisService;

	private final KafkaTemplate<String, FraudCheckResponse> kafkaTemplate;
	@KafkaListener(
	        topics = "transaction-events", 
	        groupId = "fraud-analyzer-group"
	    )
	    public void consume(TransactionRequest request) {
	        log.info("Received transaction for fraud analysis: {}", request.getTransactionId());
	        
	        FraudCheckResponse result = fraudAnalysisService.analyzeWithAI(request);
	        
	        result.setTransactionId(request.getTransactionId());
	        kafkaTemplate.send("fraud-results", result.getTransactionId(), result);
	        log.info("Sent result back for: {}", result.getTransactionId());
	        
	    }
}

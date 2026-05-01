package com.sentinelai.fraudanalyzerservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
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

	@KafkaListener(
	        topics = "transaction-events", 
	        groupId = "fraud-analyzer-group"
	    )
	    public void consume(TransactionRequest request) {
	        log.info("Received transaction for fraud analysis: {}", request.getTransactionId());
	        
	        FraudCheckResponse result = fraudAnalysisService.analyzeWithAI(request);
	        
	        log.info("Analysis complete for {}. Verdict: {}", request.getTransactionId(), result.getVerdict());
	        //send back the response to the transaction service to proceed with next step based on the verdict.
	    }
}

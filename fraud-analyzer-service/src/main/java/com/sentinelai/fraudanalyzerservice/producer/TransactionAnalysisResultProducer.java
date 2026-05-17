package com.sentinelai.fraudanalyzerservice.producer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sentinelai.fraudanalyzerservice.model.FraudCheckResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAnalysisResultProducer {
	
	private static final String TOPIC = "fraud-results";
	private final KafkaTemplate<String, FraudCheckResponse> kafkaTemplate;

	public void sendTransactionAnalysisResult(FraudCheckResponse result) {
		kafkaTemplate.send(TOPIC, result.getTransactionId(), result);
        log.info("Sent result back for: {}", result.getTransactionId());
		
	}
}

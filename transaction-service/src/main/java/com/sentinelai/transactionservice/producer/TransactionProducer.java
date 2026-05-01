package com.sentinelai.transactionservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sentinelai.transactionservice.model.TransactionRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {

	private final KafkaTemplate<String, TransactionRequest> kafkaTemplate;
    private static final String TOPIC = "transaction-events";

    public void sendTransactionForAnalysis(TransactionRequest request) {
        log.info("Sending transaction {} to Kafka for analysis", request.getTransactionId());
        kafkaTemplate.send(TOPIC, request.getTransactionId(), request);
    }
}

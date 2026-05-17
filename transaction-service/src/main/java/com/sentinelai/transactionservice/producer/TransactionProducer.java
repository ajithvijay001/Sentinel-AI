package com.sentinelai.transactionservice.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sentinelai.transactionservice.model.TransactionRequest;
import com.sentinelai.transactionservice.model.entity.Transaction;
import com.sentinelai.transactionservice.model.enums.FraudVerdict;
import com.sentinelai.transactionservice.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {

	private final KafkaTemplate<String, TransactionRequest> kafkaTemplate;
    private static final String TOPIC = "transaction-events";
    
    @Autowired
    TransactionRepository transactionRepo;

    public void sendTransactionForAnalysis(TransactionRequest request) {
        
    	 log.info("Saving Transaction details in DataBase");
        Transaction transaction = Transaction.builder()
        		.transactionId(request.getTransactionId())
        		.transactionAmount(request.getTransactionAmount())
                .customerId(request.getCustomerId())
                .transactionCountry(request.getTransactionCountry())
                .currency(request.getCurrency())
                .merchantCategory(request.getMerchantCategory())
                .transactionType(request.getTransactionType())
                .requestTime(request.getRequestTime())
                .status(FraudVerdict.PENDING)
                .receiverId(request.getReceiverId())
                .merchantId(request.getMerchantId())
                .metadata(request.getMetadata())
        		.build();
        
        transactionRepo.save(transaction);
        
        log.info("Sending transaction {} to Kafka for analysis", request.getTransactionId());
        kafkaTemplate.send(TOPIC, request.getTransactionId(), request);
    }
}

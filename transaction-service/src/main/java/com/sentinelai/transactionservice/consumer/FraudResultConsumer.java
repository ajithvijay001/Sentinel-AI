package com.sentinelai.transactionservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.sentinelai.transactionservice.model.dto.FraudCheckResponse;
import com.sentinelai.transactionservice.model.entity.Transaction;
import com.sentinelai.transactionservice.model.enums.FraudVerdict;
import com.sentinelai.transactionservice.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudResultConsumer {
	
	@Autowired
	TransactionRepository transactionRepo;
	
	@KafkaListener(topics ="fraud-results", groupId="transaction-group")
	
	public void handleResult(FraudCheckResponse result) {
		log.info("Received fraud analysis results for {} and the verdict is {}", result.getTransactionId(), result.getVerdict());
		
		transactionRepo.findById(result.getTransactionId()).ifPresentOrElse(transaction -> {
            transaction.setStatus(result.getVerdict());
            transactionRepo.save(transaction);
            log.info("Successfully updated transaction {} status to {}", transaction.getTransactionId(), transaction.getStatus());
        }, () -> {
            log.error("Transaction ID {} not found in database!", result.getTransactionId());
        });
		
	}
}

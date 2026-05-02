package com.sentinelai.transactionservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.sentinelai.transactionservice.model.dto.FraudCheckResponse;
import com.sentinelai.transactionservice.model.enums.FraudVerdict;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FraudResultConsumer {
	@KafkaListener(topics ="fraud-results", groupId="transaction-group")
	public void handleResult(FraudCheckResponse result) {
		log.info("Received fraud analysis results for {} and the verdict is {}", result.getTransactionId(), result.getVerdict());
		
		//do db actions instead of logging
		if(result.getVerdict() == FraudVerdict.APPROVED) {
			log.info("Finalizing transaction..");
		}else if(result.getVerdict() == FraudVerdict.REJECTED) {
			log.info("Blocking transaction. Notifying user");
		}else {
			log.info("Manual review needed, confirming with the user for the transaction {}", result.getTransactionId());
		}
	}
}

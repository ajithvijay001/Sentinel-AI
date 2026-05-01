package com.sentinelai.transactionservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sentinelai.transactionservice.client.FraudAnalyzerClient;
import com.sentinelai.transactionservice.model.TransactionRequest;
import com.sentinelai.transactionservice.producer.TransactionProducer;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final FraudAnalyzerClient fraudClient;
    private final TransactionProducer transactionProducer;
    
//Transaction
//    ↓
//Rule Engine
//    ↓
//AI Fraud Model
//    ↓
//Risk Score
//    ↓
//Decision Engine // ask for additional verification instead of flagging now

    @PostMapping
    public ResponseEntity<String> initiateTransaction(@RequestBody TransactionRequest request) {
    	
    	    transactionProducer.sendTransactionForAnalysis(request);
    	    
    	    return ResponseEntity.accepted()
    	        .body("Transaction received. Processing with ID: " + request.getTransactionId());

    }
}
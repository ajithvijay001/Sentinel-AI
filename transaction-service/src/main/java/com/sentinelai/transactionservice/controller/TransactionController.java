package com.sentinelai.transactionservice.controller;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sentinelai.transactionservice.client.FraudAnalyzerClient;
import com.sentinelai.transactionservice.model.TransactionRequest;
import com.sentinelai.transactionservice.model.dto.FraudCheckResponse;
import com.sentinelai.transactionservice.model.enums.FraudVerdict;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final FraudAnalyzerClient fraudClient;
    
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

        FraudCheckResponse response = fraudClient.checkTransaction(request);
        
        if (response.getVerdict() == FraudVerdict.REJECTED) {
            return ResponseEntity.status(HttpStatus.SC_FORBIDDEN)
                .body("Transaction Blocked: " + response.getReason());
        }

        return ResponseEntity.ok("Transaction Successful! AI Score: " + response.getRiskScore() +", " + response.getReason());
    }
}
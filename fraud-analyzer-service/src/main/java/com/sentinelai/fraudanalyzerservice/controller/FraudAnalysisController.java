package com.sentinelai.fraudanalyzerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sentinelai.fraudanalyzerservice.model.FraudCheckResponse;
import com.sentinelai.fraudanalyzerservice.model.dto.TransactionRequest;
import com.sentinelai.fraudanalyzerservice.service.FraudAnalysisService;


@RestController
@RequestMapping("/api/v1/fraud-analysis")
public class FraudAnalysisController {

	@Autowired
	FraudAnalysisService fraudanalyzerservice;
	
	@PostMapping("/check")
    public ResponseEntity<FraudCheckResponse> checkTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(fraudanalyzerservice.analyzeWithAI(request));
    }
}

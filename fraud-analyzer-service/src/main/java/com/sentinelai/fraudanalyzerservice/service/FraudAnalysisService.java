package com.sentinelai.fraudanalyzerservice.service;

import java.math.BigDecimal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.sentinelai.fraudanalyzerservice.model.FraudCheckResponse;
import com.sentinelai.fraudanalyzerservice.model.dto.TransactionRequest;
import com.sentinelai.fraudanalyzerservice.model.enums.FraudVerdict;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FraudAnalysisService {
	
	private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
	
    @Value("classpath:/prompts/fraud-analysis.st")
    private Resource fraudPromptResource;
    
    public FraudAnalysisService(ChatClient.Builder builder, ObjectMapper objectMapper) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.objectMapper = objectMapper;
    }
	public FraudCheckResponse analyzeWithAI(TransactionRequest request) {
		
		String simulatedAccountOrigin = "JAPAN";// need to fetch from the DB in future.

		try {
		String jsonRequest = objectMapper.writeValueAsString(request);
		
		var converter = new BeanOutputConverter<>(FraudCheckResponse.class);
		// handle rate limit checks in future
		return chatClient.prompt()
				.system(sp->sp.text(fraudPromptResource)
						.param("format", converter.getFormat())
						.param("accountOrigin", simulatedAccountOrigin))// remove it here and get it from the DB later.
				
				.user(u -> u.text("Please analyze this transaction: {jsonInput}")
						.param("jsonInput", jsonRequest))
				.call()
				.entity(converter);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return getFallbackResponse("System encountered an error during AI analysis. Manual review is required.", request.getTransactionId());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return getFallbackResponse("System encountered an error while processing request to json", request.getTransactionId());
		}
	}
	
	private FraudCheckResponse getFallbackResponse(String message, String transactionId) {
	    return new FraudCheckResponse(
	        new BigDecimal("0.5"),      
	        FraudVerdict.FLAGGED, 
	        message,
	        transactionId
	    );
	}

}

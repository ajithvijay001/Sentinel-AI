package com.sentinelai.fraudanalyzerservice.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.sentinelai.fraudanalyzerservice.model.FraudCheckResponse;
import com.sentinelai.fraudanalyzerservice.model.dto.TransactionRequest;
import com.sentinelai.fraudanalyzerservice.model.enums.FraudVerdict;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class FraudAnalysisService {
	
	private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
	
    @Value("classpath:/prompts/fraud-analysis.st")
    private Resource fraudPromptResource;
    
    public FraudAnalysisService(ChatClient.Builder builder, ObjectMapper objectMapper, StringRedisTemplate redisTemplate) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }
    
    private static final String CACHE_PREFIX = "fraud-payload-hash:";
    private static final String VELOCITY_PREFIX = "velocity:customer:";
    
	public FraudCheckResponse analyzeWithAI(TransactionRequest request) {
		
		String simulatedAccountOrigin = "JAPAN";// need to fetch from the DB in future.
		
		//rate limiting the transaction requests
		if (isVelocityLimitExceeded(request.getCustomerId())) {
            log.warn("VELOCITY ALERT: Customer {} exceeded transaction limits! Bypassing cache to reject.", 
                    request.getCustomerId());
            
            return FraudCheckResponse.builder()
                    .transactionId(request.getTransactionId())
                    .verdict(FraudVerdict.REJECTED)
                    .riskScore(new BigDecimal("0.5"))
                    .reason("HIGH VELOCITY: More than 3 transactions attempted within 1 minute.")
                    .build();
        }
		
		String payloadHash = generatePayloadHash(request);
        String cacheKey = CACHE_PREFIX + payloadHash;

		try {
			String cachedResponse = redisTemplate.opsForValue().get(cacheKey);
			
			if(cachedResponse != null) {
				log.info("CACHE HIT: Found existing AI verdict in Redis for transaction {}", request.getTransactionId());
				FraudCheckResponse response = objectMapper.readValue(cachedResponse, FraudCheckResponse.class);
				response.setTransactionId(request.getTransactionId());
				return response;
			}
			
			log.info("CACHE MISS: Analyzing transaction {} via Gemini API...", request.getTransactionId());
			
			String jsonRequest = objectMapper.writeValueAsString(request);
		
			var converter = new BeanOutputConverter<>(FraudCheckResponse.class);

			FraudCheckResponse realAiResponse = chatClient.prompt()
					.system(sp->sp.text(fraudPromptResource)
							.param("format", converter.getFormat())
							.param("accountOrigin", simulatedAccountOrigin))
					
					.user(u -> u.text("Please analyze this transaction: {jsonInput}")
							.param("jsonInput", jsonRequest))
					.call()
					.entity(converter);
			if(realAiResponse != null) {
				String responseJson = objectMapper.writeValueAsString(realAiResponse);
				redisTemplate.opsForValue().set(cacheKey, responseJson, Duration.ofMinutes(5));
			}
			return realAiResponse;
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
	
	private String generatePayloadHash(TransactionRequest request) {
        Map<String, String> metadata = request.getMetadata() != null ? request.getMetadata() : Map.of();
        
        String rawKeyComponents = String.format("%s:%s:%s:%s:%s",
                request.getCustomerId(),
                request.getMerchantId(),
                request.getTransactionAmount().toPlainString(),
                metadata.getOrDefault("device_id", "unknown"),
                metadata.getOrDefault("ip_address", "unknown")
        );

        return DigestUtils.md5DigestAsHex(rawKeyComponents.getBytes(StandardCharsets.UTF_8));
    }
	
	private boolean isVelocityLimitExceeded(long customerId) {
        String redisKey = VELOCITY_PREFIX + customerId;
        long now = Instant.now().toEpochMilli();
        long oneMinuteAgo = now - 60000; 
        
        
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, oneMinuteAgo);

        redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);

        Long transactionCount = redisTemplate.opsForZSet().count(redisKey, oneMinuteAgo, now);

        redisTemplate.expire(redisKey, Duration.ofMinutes(2));

        return transactionCount != null && transactionCount > 3;
    }

}

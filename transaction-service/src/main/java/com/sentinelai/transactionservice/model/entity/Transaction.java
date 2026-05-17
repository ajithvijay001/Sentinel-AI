package com.sentinelai.transactionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.sentinelai.transactionservice.model.enums.FraudVerdict;
import com.sentinelai.transactionservice.model.enums.MerchantCategory;
import com.sentinelai.transactionservice.model.enums.TransactionType;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal transactionAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "transaction_country", nullable = false, length = 50)
    private String transactionCountry;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "merchant_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantCategory merchantCategory;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FraudVerdict status; 

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "merchant_id", length = 50)
    private String merchantId;

    @Column(name = "receiver_id", length = 50)
    private String receiverId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, String> metadata;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = FraudVerdict.PENDING;
        }
    }
}

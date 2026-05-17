package com.sentinelai.transactionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sentinelai.transactionservice.model.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}

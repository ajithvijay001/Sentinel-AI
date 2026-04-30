package com.sentinelai.transactionservice.model;

import java.time.LocalDateTime;

public class UserDetails {
	
	long customerId;
	long accountId;
	String userName;
	String userBranch;
	LocalDateTime lastLogin;
	String accountOrigin; //or location or locality of the account
}

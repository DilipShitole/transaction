package com.transaction.model;


import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class TransactionModel {
	@MongoId
	private String transaction_id;
	private String account;
	private String to_account;
	private Float transaction_amount;
	private String date;

}

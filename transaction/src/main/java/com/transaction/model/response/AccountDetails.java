package com.transaction.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetails {
private String account_no;
	private Float balance;
	private String account_type;
	private String date;
	
}

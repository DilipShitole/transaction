package com.transaction.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
private String id;
	private String firstname;
	private String lastname;
	private String mobile;
	private String email;
	private String account;
	

}

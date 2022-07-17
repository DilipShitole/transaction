package com.transaction.model.response;

import java.util.List;

import com.transaction.model.TransactionModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountSummary {
  private AccountDetails accountDetails;
  private Customer customer;
  private List<TransactionModel> transactionModel;
}
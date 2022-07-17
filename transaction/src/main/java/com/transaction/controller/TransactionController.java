package com.transaction.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.transaction.model.TransactionModel;
import com.transaction.model.response.AccountSummary;
import com.transaction.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	TransactionService transactionService;

	@GetMapping("/getAllTransactions")
	private ResponseEntity<List<TransactionModel>> getAllTransactions()
			throws FileNotFoundException, DocumentException {
		List<TransactionModel> Transactionlist = transactionService.getAllTransactions();
		return new ResponseEntity<List<TransactionModel>>(Transactionlist, HttpStatus.OK);

	}

	@PostMapping("/saveTransactions")
	private ResponseEntity<TransactionModel> saveTransaction(@RequestBody TransactionModel transactionModel) {
		TransactionModel transactionModel1 = transactionService.saveTransaction(transactionModel);
		return new ResponseEntity<TransactionModel>(transactionModel1, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{transaction_id}")
	private ResponseEntity<String> deleteById(@PathVariable String transaction_id) {
		String message = transactionService.deleteById(transaction_id);
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

	@PutMapping("/update/{transaction_id}")
	private ResponseEntity<TransactionModel> updateById(@PathVariable String transaction_id,
			@RequestBody TransactionModel transactionModel) {
		TransactionModel transactionModel2 = transactionService.updateById(transaction_id, transactionModel);
		return new ResponseEntity<TransactionModel>(transactionModel2, HttpStatus.OK);
	}

	// get transactions by account no
	@GetMapping("/getTransactionsbyacc/{account}")
	private ResponseEntity<AccountSummary> getTransactionsbyacc(@PathVariable String account) {
		AccountSummary accountSummary = transactionService.getTrnactionDetails(account);
		return new ResponseEntity<AccountSummary>(accountSummary, HttpStatus.OK);

	}

	@PostMapping("/transfer/{account}/{amount}/{to_account}")
	public ResponseEntity transferFund(@PathVariable String account, @PathVariable Float amount,
			@PathVariable String to_account) {
		// step 1 fetch from account details and balance to validate amount
		// step 2 fetch to account details and add balance into to account details
		// step 3 generate account transaction and save into transaction table
		// step 4 update account table with latest balance
		transactionService.transferFund(account, amount, to_account);
		return null;

	}

}

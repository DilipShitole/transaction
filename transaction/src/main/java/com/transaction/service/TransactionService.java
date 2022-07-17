package com.transaction.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.transaction.model.TransactionModel;
import com.transaction.model.response.AccountSummary;
import com.transaction.repository.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	RestTemplate restTemplate;

	@Value("${account_details_byid}")
	private String account_summary_url;

	@Value("${update_account_balance}")
	private String update_account_balance;

	public TransactionModel saveTransaction(TransactionModel transactionModel) {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:MM:ss");
		String requiredDate = df.format(new Date(System.currentTimeMillis()));
		transactionModel.setDate(requiredDate);
		transactionRepository.save(transactionModel);
		return transactionModel;
	}

//	public List<TransactionModel> getAllTransactions() {
//		List<TransactionModel> transactionlist = transactionRepository.findAll();
//		return transactionlist;
//	}

	public List<TransactionModel> getAllTransactions() throws FileNotFoundException, DocumentException {
		List<TransactionModel> Transactionlist = transactionRepository.findAll();
		convertIntoPdf(Transactionlist);
		return Transactionlist;

	}

	private void convertIntoPdf(List<TransactionModel> Transactionlist)
			throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("account.pdf"));
		document.open();
		// document.add(new Paragraph(Transactionlist.toString()));
		Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
		fontTiltle.setSize(22);

		// Creating paragraph
		Paragraph paragraph = new Paragraph("Accont Transactions", fontTiltle);

		// Aligning the paragraph in document
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);

		// Adding the created paragraph in document
		document.add(paragraph);

		// Creating a table of 3 columns
		PdfPTable table = new PdfPTable(5);

		// Setting width of table, its columns and spacing
		table.setWidthPercentage(100f);
		table.setWidths(new int[] { 2, 3, 3, 3, 3 });
		table.setSpacingBefore(5);

		// Create Table Cells for table header
		PdfPCell cell = new PdfPCell();

		// Setting the background color and padding
		cell.setBackgroundColor(CMYKColor.ORANGE);
		cell.setPadding(5);

		// Creating font
		// Setting font style and size
		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
		font.setColor(CMYKColor.BLUE);

		// Adding headings in the created table cell/ header
		// Adding Cell to table
		cell.setPhrase(new Phrase("Transaction_id", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Account", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("To_account", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Transaction_amount", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Date", font));
		table.addCell(cell);

		// Iterating over the list of students
		for (TransactionModel trans : Transactionlist) {

			table.addCell(String.valueOf(trans.getTransaction_id()));
			table.addCell(String.valueOf(trans.getAccount()));

			table.addCell(String.valueOf(trans.getTo_account()));
			table.addCell(String.valueOf(trans.getTransaction_amount()));
			table.addCell(String.valueOf(trans.getDate()));

		}
		// Adding the created table to document
		document.add(table);

		// Closing the document
		document.close();
		writer.close();

	}

	public String deleteById(String transaction_id) {
		transactionRepository.deleteById(transaction_id);
		return "Transaction Deleted Successfully";
	}

	public TransactionModel updateById(String transaction_id, TransactionModel transactionModel) {
		Optional<TransactionModel> transactionModel1 = transactionRepository.findById(transaction_id);
		TransactionModel transactionModel2 = transactionModel1.get();

		transactionModel2.setTransaction_id(transactionModel.getTransaction_id());
		transactionModel2.setAccount(transactionModel.getAccount());
		transactionModel2.setTo_account(transactionModel.getTo_account());
		transactionModel2.setTransaction_amount(transactionModel.getTransaction_amount());
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:MM:ss");
		String requiredDate = df.format(new Date(System.currentTimeMillis()));
		transactionModel2.setDate(requiredDate);
		transactionRepository.save(transactionModel2);

		return transactionModel2;
	}

	public AccountSummary getTrnactionDetails(String account) {
		HttpHeaders header = new HttpHeaders();
		HttpEntity<AccountSummary> entity = new HttpEntity<AccountSummary>(header);
		Map<String, String> vars = new HashMap<>();
		vars.put("account", account);
		ResponseEntity<AccountSummary> accSummary = restTemplate.exchange(account_summary_url, HttpMethod.GET, entity,
				AccountSummary.class, vars);
		Optional<List<TransactionModel>> TRS = transactionRepository.getTransactionsByAccount(account);
		List<TransactionModel> tranList = TRS.get();
		AccountSummary accSummary1 = accSummary.getBody();
		accSummary1.setTransactionModel(tranList);
		return accSummary1;

	}

	public void transferFund(String account, Float amount, String to_account) {
		// step 1 fetch from account details and balance to validate amount
		// step 2 fetch to account details and add balance into to account details
		// step 3 generate account transaction and save into transaction table
		// step 4 update account table with latest balance
		AccountSummary accSum = getTrnactionDetails(account);
		Float from_account_balance = accSum.getAccountDetails().getBalance();
		AccountSummary accSum1 = getTrnactionDetails(to_account);
		Float to_account_balance = accSum1.getAccountDetails().getBalance();
		Float balance_update = accSum1.getAccountDetails().getBalance() + amount;
		if (from_account_balance > amount) {
			ResponseEntity<String> response = updateBalanceInAccountService(to_account, balance_update);
			System.out.println("get status code value" + response.getStatusCodeValue());
			if (response.getStatusCodeValue() == 200) {
				updateBalanceInAccountService(account, from_account_balance - amount);

				// update transaction with new transaction details
				TransactionModel transactionModel = new TransactionModel();
				transactionModel.setAccount(account);
				transactionModel.setTo_account(to_account);
				transactionModel.setTransaction_amount(amount);
				transactionModel.setAccount(account);
				String transaction_id = Double.toString(Math.random());
				transactionModel.setTransaction_id(transaction_id);
				transactionRepository.save(transactionModel);
			}
		}
	}

	public ResponseEntity<String> updateBalanceInAccountService(String account_id, Float balance) {
		HttpHeaders header = new HttpHeaders();
		HttpEntity<AccountSummary> entity = new HttpEntity<AccountSummary>(header);
		Map<String, Object> vars = new HashMap<>();
		vars.put("account_id", account_id);
		vars.put("balance", balance);
		ResponseEntity<String> accSummary = restTemplate.exchange(update_account_balance, HttpMethod.PUT, entity,
				String.class, vars);
		return accSummary;
	}
}

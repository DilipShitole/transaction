package com.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.transaction.model.TransactionModel;
@Repository
public interface TransactionRepository extends MongoRepository<TransactionModel, String>{

	Optional<List<TransactionModel>> getTransactionsByAccount(String account);

}

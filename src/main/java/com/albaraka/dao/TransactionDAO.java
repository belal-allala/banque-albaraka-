package com.albaraka.dao;

import com.albaraka.entities.*;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(int id);
    List<Transaction> findAll();
    List<Transaction> findByCompteId(int compteId);
    Transaction update(Transaction transaction);
    void deleteById(int id);
}
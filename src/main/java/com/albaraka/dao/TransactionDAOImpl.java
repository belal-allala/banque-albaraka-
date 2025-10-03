package com.albaraka.dao;

import com.albaraka.entities.Transaction;
import com.albaraka.entities.TypeTransaction;
import com.albaraka.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO Transaction (date, montant, type, lieu, idCompte) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(transaction.date()));
            pstmt.setBigDecimal(2, transaction.montant());
            pstmt.setString(3, transaction.type().name()); 
            pstmt.setString(4, transaction.lieu());
            pstmt.setInt(5, transaction.idCompte());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return new Transaction(generatedId, transaction.date(), transaction.montant(), transaction.type(), transaction.lieu(), transaction.idCompte());
                } else {
                    throw new SQLException("La création de la transaction a échoué, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Transaction> findById(int id) {
        String sql = "SELECT * FROM Transaction WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByCompteId(int compteId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE idCompte = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, compteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    @Override
    public Transaction update(Transaction transaction) {
        // Les transactions sont généralement immuables, une méthode update a peu de sens.
        // On pourrait la laisser vide ou lancer une exception.
        throw new UnsupportedOperationException("Les transactions ne peuvent pas être mises à jour.");
    }
    
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM Transaction WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Transaction fromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getBigDecimal("montant"),
                TypeTransaction.valueOf(rs.getString("type")), 
                rs.getString("lieu"),
                rs.getInt("idCompte")
        );
    }
}
package com.albaraka.dao;

import com.albaraka.entities.Compte;
import com.albaraka.entities.CompteCourant;
import com.albaraka.entities.CompteEpargne;
import com.albaraka.utils.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteDAOImpl implements CompteDAO {

    @Override
    public Compte save(Compte compte) {
        String sql = "INSERT INTO Compte (numero, solde, idClient, typeCompte, decouvertAutorise, tauxInteret) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, compte.getNumero());
            pstmt.setBigDecimal(2, compte.getSolde());
            pstmt.setInt(3, compte.getIdClient());

            if (compte instanceof CompteCourant cc) {
                pstmt.setString(4, "COURANT");
                pstmt.setBigDecimal(5, cc.getDecouvertAutorise());
                pstmt.setNull(6, Types.DECIMAL);
            } else if (compte instanceof CompteEpargne ce) {
                pstmt.setString(4, "EPARGNE");
                pstmt.setNull(5, Types.DECIMAL);
                pstmt.setBigDecimal(6, ce.getTauxInteret());
            }

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // int generatedId = generatedKeys.getInt(1);
                   
                    return compte; 
                } else {
                    throw new SQLException("La création du compte a échoué, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Compte> findById(int id) {
        String sql = "SELECT * FROM Compte WHERE id = ?";
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
    public List<Compte> findAll() {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM Compte";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                comptes.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comptes;
    }
    
    @Override
    public List<Compte> findByClientId(int clientId) {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM Compte WHERE idClient = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comptes.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comptes;
    }

    @Override
    public Compte update(Compte compte) {
        String sql = "UPDATE Compte SET solde = ? WHERE id = ?";
         try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setBigDecimal(1, compte.getSolde());
             pstmt.setInt(2, compte.getId());
             pstmt.executeUpdate();
         } catch (SQLException e) {
             e.printStackTrace();
         }
        return compte;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM Compte WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Compte fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String numero = rs.getString("numero");
        BigDecimal solde = rs.getBigDecimal("solde");
        int idClient = rs.getInt("idClient");
        String typeCompte = rs.getString("typeCompte");

        if ("COURANT".equalsIgnoreCase(typeCompte)) {
            BigDecimal decouvert = rs.getBigDecimal("decouvertAutorise");
            return new CompteCourant(id, numero, solde, idClient, decouvert);
        } else if ("EPARGNE".equalsIgnoreCase(typeCompte)) {
            BigDecimal taux = rs.getBigDecimal("tauxInteret");
            return new CompteEpargne(id, numero, solde, idClient, taux);
        } else {
            throw new IllegalStateException("Type de compte inconnu dans la base de données: " + typeCompte);
        }
    }
}
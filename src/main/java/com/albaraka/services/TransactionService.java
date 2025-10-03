package com.albaraka.services;

import com.albaraka.dao.*;
import com.albaraka.entities.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final CompteDAO compteDAO;

    public TransactionService(TransactionDAO transactionDAO, CompteDAO compteDAO) {
        this.transactionDAO = transactionDAO;
        this.compteDAO = compteDAO;
    }

    public Transaction enregistrerTransaction(Transaction transaction) {
        Optional<Compte> compteOpt = compteDAO.findById(transaction.idCompte());

        if (compteOpt.isEmpty()) {
            System.err.println("Erreur : Le compte associé à la transaction n'existe pas.");
            return null;
        }

        Compte compte = compteOpt.get();
        BigDecimal montantTransaction = transaction.montant();
        BigDecimal soldeActuel = compte.getSolde();
        BigDecimal nouveauSolde;

        if (transaction.type() == TypeTransaction.VERSEMENT) {
            nouveauSolde = soldeActuel.add(montantTransaction);
        } else if (transaction.type() == TypeTransaction.RETRAIT) {
            nouveauSolde = soldeActuel.subtract(montantTransaction);

            BigDecimal limite = BigDecimal.ZERO;
            if (compte instanceof CompteCourant cc) {
                limite = cc.getDecouvertAutorise().negate();
            }
            if (nouveauSolde.compareTo(limite) < 0) {
                System.err.println("Erreur : Solde insuffisant pour effectuer le retrait.");
                return null;
            }
        } else {
            nouveauSolde = soldeActuel.subtract(montantTransaction);
        }

        compteDAO.update(creerCompteAvecNouveauSolde(compte, nouveauSolde));

        return transactionDAO.save(transaction);
    }

    public List<Transaction> listerTransactionsParCompte(int compteId) {
        return transactionDAO.findByCompteId(compteId);
    }

    public Map<TypeTransaction, List<Transaction>> regrouperTransactionsParType(int compteId) {
        return transactionDAO.findByCompteId(compteId)
                .stream()
                .collect(Collectors.groupingBy(Transaction::type));
    }

    public BigDecimal calculerTotalTransactionsParCompte(int compteId) {
        return transactionDAO.findByCompteId(compteId)
                .stream()
                .map(t -> t.type() == TypeTransaction.VERSEMENT ? t.montant() : t.montant().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Compte creerCompteAvecNouveauSolde(Compte original, BigDecimal nouveauSolde) {
        if (original instanceof CompteCourant cc) {
            return new CompteCourant(cc.getId(), cc.getNumero(), nouveauSolde, cc.getIdClient(), cc.getDecouvertAutorise());
        }
        if (original instanceof CompteEpargne ce) {
            return new CompteEpargne(ce.getId(), ce.getNumero(), nouveauSolde, ce.getIdClient(), ce.getTauxInteret());
        }
        throw new IllegalStateException("Type de compte non supporté");
    }

}
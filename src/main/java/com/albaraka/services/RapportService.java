package com.albaraka.services;

import com.albaraka.dao.ClientDAO;
import com.albaraka.dao.CompteDAO;
import com.albaraka.dao.TransactionDAO;
import com.albaraka.entities.Client;
import com.albaraka.entities.Compte;
import com.albaraka.entities.Transaction;
import com.albaraka.entities.TypeTransaction;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RapportService {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;
    private final TransactionDAO transactionDAO;

    public RapportService(ClientDAO clientDAO, CompteDAO compteDAO, TransactionDAO transactionDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
        this.transactionDAO = transactionDAO;
    }

    public List<Client> genererTop5ClientsParSolde() {
        Map<Integer, BigDecimal> soldesParClientId = compteDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        Compte::getIdClient,
                        Collectors.mapping(Compte::getSolde, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return clientDAO.findAll().stream()
                .sorted(Comparator.comparing((Client client) -> soldesParClientId.getOrDefault(client.id(), BigDecimal.ZERO)).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public void produireRapportMensuel(int annee, int mois) {
        List<Transaction> transactionsDuMois = transactionDAO.findAll().stream()
                .filter(t -> t.date().getYear() == annee && t.date().getMonthValue() == mois)
                .collect(Collectors.toList());

        System.out.println("--- Rapport pour " + mois + "/" + annee + " ---");
        System.out.println("Nombre total de transactions : " + transactionsDuMois.size());
        Map<TypeTransaction, Long> countParType = transactionsDuMois.stream()
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
        System.out.println("Transactions par type : " + countParType);
        BigDecimal volumeTotal = transactionsDuMois.stream()
                .map(Transaction::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Volume total des transactions : " + volumeTotal + " €");
    }

    public List<Transaction> detecterTransactionsSuspectesParMontant(BigDecimal seuilMontant) {
        return transactionDAO.findAll().stream()
                .filter(t -> t.montant().compareTo(seuilMontant) > 0)
                .collect(Collectors.toList());
    }

    public List<List<Transaction>> detecterTransactionsSuspectesParFrequence(long intervalleSecondes) {
        Map<Integer, List<Transaction>> transactionsParCompte = transactionDAO.findAll().stream()
                .sorted(Comparator.comparing(Transaction::date))
                .collect(Collectors.groupingBy(Transaction::idCompte));

        List<List<Transaction>> toutesLesAnomalies = new ArrayList<>();
        for (List<Transaction> transactions : transactionsParCompte.values()) {
            if (transactions.size() < 2) continue;
            for (int i = 0; i < transactions.size() - 1; i++) {
                Transaction t1 = transactions.get(i);
                Transaction t2 = transactions.get(i + 1);
                if (Duration.between(t1.date(), t2.date()).getSeconds() < intervalleSecondes) {
                    toutesLesAnomalies.add(List.of(t1, t2));
                }
            }
        }
        return toutesLesAnomalies;
    }

    public List<Compte> identifierComptesInactifs(LocalDateTime dateLimite) {
        Map<Integer, LocalDateTime> dernieresTransactions = transactionDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        Transaction::idCompte,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Transaction::date)),
                                opt -> opt.map(Transaction::date).orElse(LocalDateTime.MIN)
                        )
                ));

        return compteDAO.findAll().stream()
                .filter(compte -> dernieresTransactions.getOrDefault(compte.getId(), LocalDateTime.MIN).isBefore(dateLimite))
                .collect(Collectors.toList());
    }

}
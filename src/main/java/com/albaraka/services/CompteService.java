package com.albaraka.services;

import com.albaraka.dao.CompteDAO;
import com.albaraka.entities.Compte;
import com.albaraka.entities.CompteCourant;
import com.albaraka.entities.CompteEpargne;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class CompteService {

    private final CompteDAO compteDAO;

    public CompteService(CompteDAO compteDAO) {
        this.compteDAO = compteDAO;
    }

    public Compte creerCompte(Compte compte) {
        if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("Erreur : Le solde initial ne peut pas être négatif.");
            return null; 
        }
        return compteDAO.save(compte);
    }

    public void mettreAJourSolde(int compteId, BigDecimal nouveauSolde) {
        Optional<Compte> compteOpt = compteDAO.findById(compteId);
        
        if (compteOpt.isPresent()) {
            Compte compte = compteOpt.get();
            Compte compteMisAJour;
            if (compte instanceof CompteCourant cc) {
                compteMisAJour = new CompteCourant(cc.getId(), cc.getNumero(), nouveauSolde, cc.getIdClient(), cc.getDecouvertAutorise());
            } else if (compte instanceof CompteEpargne ce) {
                 compteMisAJour = new CompteEpargne(ce.getId(), ce.getNumero(), nouveauSolde, ce.getIdClient(), ce.getTauxInteret());
            } else {
                return;
            }
            compteDAO.update(compteMisAJour);
        }
    }

    public List<Compte> rechercherComptesParClient(int clientId) {
        return compteDAO.findByClientId(clientId);
    }
 
    public Optional<Compte> trouverCompteAvecSoldeMaximum() {
        return compteDAO.findAll()
                .stream()
                .max(Comparator.comparing(Compte::getSolde));
    }

    public Optional<Compte> trouverCompteAvecSoldeMinimum() {
        return compteDAO.findAll()
                .stream()
                .min(Comparator.comparing(Compte::getSolde));
    }

}
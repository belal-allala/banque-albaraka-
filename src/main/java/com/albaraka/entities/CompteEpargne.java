package com.albaraka.entities;


import java.math.BigDecimal;

public final class CompteEpargne extends Compte {

    private BigDecimal tauxInteret;

    public CompteEpargne(int id, String numero, BigDecimal solde, int idClient, BigDecimal tauxInteret) {
        super(id, numero, solde, idClient);
        this.tauxInteret = tauxInteret;
    }

    // Getter
    public BigDecimal getTauxInteret() {
        return tauxInteret;
    }
}
package com.albaraka.entities;

import java.math.BigDecimal;

public final class CompteCourant extends Compte {

    private BigDecimal decouvertAutorise;

    public CompteCourant(int id, String numero, BigDecimal solde, int idClient, BigDecimal decouvertAutorise) {
        super(id, numero, solde, idClient);
        this.decouvertAutorise = decouvertAutorise;
    }

    // Getter
    public BigDecimal getDecouvertAutorise() {
        return decouvertAutorise;
    }
}
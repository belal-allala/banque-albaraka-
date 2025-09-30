package com.albaraka.entities;


import java.math.BigDecimal;

public sealed abstract class Compte permits CompteCourant, CompteEpargne {

    protected int id;
    protected String numero;
    protected BigDecimal solde;
    protected int idClient;

    public Compte(int id, String numero, BigDecimal solde, int idClient) {
        this.id = id;
        this.numero = numero;
        this.solde = solde;
        this.idClient = idClient;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public int getIdClient() {
        return idClient;
    }
}

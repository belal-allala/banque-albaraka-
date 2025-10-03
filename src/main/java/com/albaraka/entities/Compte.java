package com.albaraka.entities;

import java.math.BigDecimal;

// import java.math.Double;

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

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }   

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", solde=" + solde +
                ", idClient=" + idClient +
                '}';
    }
}

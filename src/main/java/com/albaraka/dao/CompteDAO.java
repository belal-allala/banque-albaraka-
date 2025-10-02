package com.albaraka.dao;

import com.albaraka.entities.*;
import java.util.List;
import java.util.Optional;  

public interface CompteDAO {
    Compte save(Compte compte);
    Optional<Compte> findById(int id);
    List<Compte> findAll();
    List<Compte> findByClientId(int clientId);
    Compte update(Compte compte);
    void deleteById(int id);
}
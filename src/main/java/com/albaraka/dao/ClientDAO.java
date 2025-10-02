package com.albaraka.dao;

import com.albaraka.entities.*;
import java.util.List;
import java.util.Optional;

public interface ClientDAO {
    Client save(Client client);
    Optional<Client> findById(int id);
    List<Client> findAll();
    Client update(Client client);
    void deleteById(int id);
}
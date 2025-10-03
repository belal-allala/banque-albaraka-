package com.albaraka.services;

import com.albaraka.dao.ClientDAO;
import com.albaraka.entities.Client;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public Client createClient(Client client) {
        return clientDAO.save(client);
    }

    public Optional<Client> getClientById(int id) {
        return clientDAO.findById(id);
    }

    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }

    public Client updateClient(Client client) {
        return clientDAO.update(client);
    }

    public boolean deleteClient(int id) {
        return clientDAO.delete(id);
    }
}
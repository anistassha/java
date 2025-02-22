package org.example.Services;

import org.example.DataAccessObjects.CustomerDAO;
import org.example.Interfaces.DAO;
import org.example.Interfaces.Service;
import org.example.Entities.Customer;

import java.util.List;

public class CustomerService implements Service<Customer> {
    DAO daoService = new CustomerDAO();

    @Override
    public Customer findEntity(int id) {
        Customer entity = (Customer) this.daoService.findById(id);
        return entity;
    }

    @Override
    public void saveEntity(Customer entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Customer entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Customer entity) {
        daoService.update(entity);
    }

    @Override
    public List<Customer> findAllEntities() {
        return daoService.findAll();
    }

    public void setDaoService(DAO daoService) {
        this.daoService = daoService;
    }
    public DAO getDaoService() {
        return daoService;
    }
}



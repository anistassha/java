package org.example.Services;

import org.example.DataAccessObjects.PurchaseDAO;
import org.example.Interfaces.Service;
import org.example.Entities.Purchase;

import java.util.List;

public class PurchaseService implements Service<Purchase> {

    PurchaseDAO daoService = new PurchaseDAO();
    @Override
    public Purchase findEntity(int id) {
        Purchase entity = (Purchase) daoService.findById(id);
        if (entity != null) {
            entity.setCustomer(null);
            entity.setEmployee(null);
            entity.setProduct(null);
        }
        return entity;
    }

    @Override
    public void saveEntity(Purchase entity) { daoService.save(entity); }

    @Override
    public void deleteEntity(Purchase entity) { daoService.delete(entity); }

    @Override
    public void updateEntity(Purchase entity) { daoService.update(entity); }

    @Override
    public List<Purchase> findAllEntities() {
        return daoService.findAll();
    }

    public List<Purchase> findRecordsByCustomerId(int userId) {
        return daoService.findByCustomerId(userId);
    }
}

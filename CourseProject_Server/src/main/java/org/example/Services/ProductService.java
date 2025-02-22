package org.example.Services;

import org.example.DataAccessObjects.ProductDAO;
import org.example.Interfaces.DAO;
import org.example.Interfaces.Service;
import org.example.Entities.Product;

import java.util.List;

public class ProductService implements Service<Product> {
    DAO daoService = new ProductDAO();

    @Override
    public Product findEntity(int id) {
        Product entity = (Product) this.daoService.findById(id);
        return entity;
    }

    @Override
    public void saveEntity(Product entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Product entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Product entity) {
        daoService.update(entity);
    }

    @Override
    public List<Product> findAllEntities() {
        return daoService.findAll();
    }
}

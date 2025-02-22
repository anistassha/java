package org.example.Services;

import org.example.DataAccessObjects.EmployeeDAO;
import org.example.Interfaces.DAO;
import org.example.Interfaces.Service;
import org.example.Entities.Employee;

import java.util.List;

public class EmployeeService  implements Service<Employee> {
    DAO daoService = new EmployeeDAO();

    @Override
    public Employee findEntity(int id) {
        Employee entity = (Employee) this.daoService.findById(id);
        return entity;
    }

    @Override
    public void saveEntity(Employee entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Employee entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Employee entity) {
        daoService.update(entity);
    }

    @Override
    public List<Employee> findAllEntities() {
        return daoService.findAll();
    }
}



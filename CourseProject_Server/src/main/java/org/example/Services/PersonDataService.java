package org.example.Services;

import org.example.DataAccessObjects.EmployeeDAO;
import org.example.DataAccessObjects.PersonDataDAO;
import org.example.Entities.PersonData;
import org.example.Interfaces.DAO;
import org.example.Interfaces.Service;

import java.util.List;

public class PersonDataService  implements Service<PersonData> {
    DAO daoService = new PersonDataDAO();

    @Override
    public PersonData findEntity(int id) {
        PersonData entity = (PersonData) this.daoService.findById(id);
        return entity;
    }

    @Override
    public void saveEntity(PersonData entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(PersonData entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(PersonData entity) {
        daoService.update(entity);
    }

    @Override
    public List<PersonData> findAllEntities() {
        return daoService.findAll();
    }
}



package org.example.DataAccessObjects;

import org.example.Entities.Purchase;
import org.example.Interfaces.DAO;
import org.example.Database.HibernateSessionFactory;
import org.example.Entities.Customer;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class PurchaseDAO implements DAO {
    @Override
    public void save(Object obj) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(obj);
        tx1.commit();
        session.close();
    }

    @Override
    public void update(Object obj) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.saveOrUpdate(obj);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Object obj) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(obj);
        tx1.commit();
        session.close();
    }

    @Override
    public Object findById(int id) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Purchase purchase = session.get(Purchase.class, id);
        session.close();
        return purchase;
    }

    @Override
    public List findAll() {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        List<Object> purchase = (List<Object>)session.createQuery("From Purchase").list();
        session.close();
        return purchase;
    }

    public List<Purchase> findByCustomerId(int customerId) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            String hql = "FROM Purchase WHERE customer.customerId = :customerId";
            TypedQuery<Purchase> query = session.createQuery(hql, Purchase.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}


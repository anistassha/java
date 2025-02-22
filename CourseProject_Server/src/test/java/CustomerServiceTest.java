import static org.mockito.Mockito.*;
import org.example.DataAccessObjects.CustomerDAO;
import org.example.Entities.Customer;
import org.example.Interfaces.DAO;
import org.example.Services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    private DAO daoMock;
    private CustomerService service;

    @BeforeEach
    void setup() {
        daoMock = mock(CustomerDAO.class);
        service = new CustomerService();
        service.setDaoService(daoMock);
    }

    @Test
    void testFindById() {
        int id = 1;
        Customer expected = new Customer(id, "John", "Doe", "Male", "123456789", "john@example.com");

        when(daoMock.findById(id)).thenReturn(expected);

        Customer result = service.findEntity(id);

        assertEquals(expected, result);
        verify(daoMock, times(1)).findById(id);
    }

    @Test
    void testSave() {
        Customer customer = new Customer(1, "John", "Doe", "Male", "123456789", "john@example.com");

        service.saveEntity(customer);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(daoMock, times(1)).save(captor.capture());

        assertEquals(customer, captor.getValue());
    }

    @Test
    void testDelete() {
        Customer customer = new Customer(1, "John", "Doe", "Male", "123456789", "john@example.com");

        service.deleteEntity(customer);

        verify(daoMock, times(1)).delete(customer);
    }

    @Test
    void testUpdate() {
        Customer customer = new Customer(1, "John", "Doe", "Male", "123456789", "john@example.com");

        service.updateEntity(customer);

        verify(daoMock, times(1)).update(customer);
    }

    @Test
    void testFindAll() {
        List<Customer> expected = Arrays.asList(
                new Customer(1, "John", "Doe", "Male", "123456789", "john@example.com"),
                new Customer(2, "Jane", "Smith", "Female", "987654321", "jane@example.com")
        );

        when(daoMock.findAll()).thenReturn(expected);

        List<Customer> result = service.findAllEntities();

        assertEquals(expected, result);
        verify(daoMock, times(1)).findAll();
    }
}

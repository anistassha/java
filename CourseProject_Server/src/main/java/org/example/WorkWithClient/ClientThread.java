package org.example.WorkWithClient;

import com.google.gson.Gson;
import org.example.Enums.ResponseStatus;
import org.example.Entities.*;
import org.example.Communication.Request;
import org.example.Communication.Response;
import org.example.Services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ClientThread implements Runnable {
    private Socket clientSocket;
    private Request request;
    private Response response;
    private Gson gson;
    private BufferedReader in;
    private PrintWriter out;

    private EmployeeService employeeService = new EmployeeService();
    private ProductService productService = new ProductService();
    private CustomerService customerService = new CustomerService();
    private PurchaseService purchaseService = new PurchaseService();
    private PersonDataService personDataService = new PersonDataService();

    public ClientThread(Socket clientSocket) throws IOException {
        response = new Response();
        request = new Request();
        this.clientSocket = clientSocket;
        gson = new Gson();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                String message = in.readLine();
                if (message == null) {
                    break;
                }

                request = gson.fromJson(message, Request.class);
                if (request == null || request.getRequestType() == null) {
                    continue;
                }
                switch (request.getRequestType()) {
                    case LOGIN: {
                        Employee employee = gson.fromJson(request.getRequestMessage(), Employee.class);
                        Employee existingEmployee = employeeService.findAllEntities().stream()
                                .filter(x -> x.getLogin().equalsIgnoreCase(employee.getLogin()))
                                .findFirst()
                                .orElse(null);

                        if (existingEmployee != null) {
                            if (PasswordUtils.checkPassword(employee.getPassword(), existingEmployee.getPassword())) {
                                response = new Response(ResponseStatus.OK, "Авторизация успешна!", gson.toJson(existingEmployee));
                            } else {
                                response = new Response(ResponseStatus.ERROR, "Неверный пароль!", "");
                            }
                        } else {
                            response = new Response(ResponseStatus.ERROR, "Неверный логин!", "");
                        }
                        break;
                    }
                    case REGISTER: {
                        System.out.println("Received employee data: " + request.getRequestMessage());
                        Employee employee = gson.fromJson(request.getRequestMessage(), Employee.class);
                        if (employeeService.findAllEntities().stream().noneMatch(x -> x.getLogin().toLowerCase().equals(employee.getLogin().toLowerCase()))) {
                            personDataService.saveEntity(employee.getPerson());
                            employee.setPerson(employee.getPerson());
                            employeeService.saveEntity(employee);
                            Employee returnEmployee;
                            returnEmployee = employeeService.findEntity(employee.getEmployeeId());
                            response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(returnEmployee));
                        } else {
                            response = new Response(ResponseStatus.ERROR, "Такой пользователь уже существует!", "");
                        }
                        break;
                    }
                    case GET_ALL_PRODUCTS: {
                        List<Product> products = productService.findAllEntities();
                        if (products.isEmpty()) {
                            response = new Response(ResponseStatus.ERROR, "Список продуктов пуст!", "");
                        } else {
                            System.out.println("Отправка данных продуктов: " + gson.toJson(products));
                            response = new Response(ResponseStatus.OK, "Продукты успешно загружены!", gson.toJson(products));
                        }
                        break;
                    }
                    case ADD_PRODUCT: {
                        Product newProduct = gson.fromJson(request.getRequestMessage(), Product.class);
                        productService.saveEntity(newProduct);
                        response = new Response(ResponseStatus.OK, "Продукт успешно добавлен!", "");
                        break;
                    }
                    case UPDATE_PRODUCT: {
                        Product newProduct = gson.fromJson(request.getRequestMessage(), Product.class);
                        productService.updateEntity(newProduct);
                        response = new Response(ResponseStatus.OK, "Продукт успешно обновлен!", "");
                        break;
                    }
                    case DELETE_PRODUCT: {
                        Product newProduct = gson.fromJson(request.getRequestMessage(), Product.class);
                        productService.deleteEntity(newProduct);
                        response = new Response(ResponseStatus.OK, "Продукт успешно удален!", "");
                        break;
                    }
                    case CHECK_PRODUCT_EXISTS: {
                        Integer id = gson.fromJson(request.getRequestMessage(), Integer.class);
                        Product product = productService.findEntity(id);
                        if (product != null) {
                            response = new Response(ResponseStatus.OK, "Продукт найден!", "");
                        } else {
                            response = new Response(ResponseStatus.ERROR, "Продукт с ID " + id + " не найден!", "");
                        }
                        break;
                    }
                    case SEARCH_PRODUCTS: {
                        String query = request.getRequestMessage();
                        List<Product> products = productService.findAllEntities();
                        List<Product> filteredProducts = products.stream()
                                .filter(product -> product.getProductName().toLowerCase().contains(query.toLowerCase())
                                        || product.getProductCategory().toLowerCase().contains(query.toLowerCase())
                                        || product.getManufacturer().toLowerCase().contains(query.toLowerCase())
                                        || String.valueOf(product.getPrice()).contains(query)
                                        || String.valueOf(product.getStockQuantity()).contains(query))
                                .toList();
                        response = new Response(ResponseStatus.OK, "Результаты поиска", gson.toJson(filteredProducts));
                        break;
                    }
                    case GET_ALL_CUSTOMERS: {
                        List<Customer> customers = customerService.findAllEntities();
                        if (customers.isEmpty()) {
                            response = new Response(ResponseStatus.ERROR, "Список покупателей пуст!", "");
                        } else {
                            System.out.println("Отправка данных покупателей: " + gson.toJson(customers));
                            response = new Response(ResponseStatus.OK, "Покупатели успешно загружены!", gson.toJson(customers));
                        }
                        break;
                    }
                    case GET_LAST_CUSTOMER_ID: {
                        try {
                            List<Customer> customers = customerService.findAllEntities();
                            if (customers.isEmpty()) {
                                response = new Response(ResponseStatus.ERROR, "Список покупателей пуст!", "");
                            } else {
                                int lastCustomerId = 0;
                                for (Customer customer : customers) {
                                    if (customer.getCustomerId() > lastCustomerId) {
                                        lastCustomerId = customer.getCustomerId();
                                    }
                                }
                                response = new Response(ResponseStatus.OK, "Последний ID покупателя успешно загружен!", String.valueOf(lastCustomerId));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            response = new Response(ResponseStatus.ERROR, "Ошибка получения последнего ID покупателя!", "");
                        }
                        break;
                    }
                    case ADD_CUSTOMER: {
                        try {
                            Customer newCustomer = gson.fromJson(request.getRequestMessage(), Customer.class);
                            customerService.saveEntity(newCustomer);
                            response = new Response(ResponseStatus.OK, "Покупатель успешно добавлен!", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                            response = new Response(ResponseStatus.ERROR, "Ошибка добавления покупателя!", "");
                        }
                        break;
                    }
                    case UPDATE_CUSTOMER: {
                        Customer updateCustomer = gson.fromJson(request.getRequestMessage(), Customer.class);
                        customerService.updateEntity(updateCustomer);
                        response = new Response(ResponseStatus.OK, "Данные покупателя успешно обновлены!", "");
                        break;
                    }
                    case DELETE_CUSTOMER: {
                        Customer customer = gson.fromJson(request.getRequestMessage(), Customer.class);
                        customerService.deleteEntity(customer);
                        response = new Response(ResponseStatus.OK, "Покупатель успешно удален!", "");
                        break;
                    }
                    case SEARCH_CUSTOMERS_ID: {
                        String query = request.getRequestMessage();
                        List<Customer> customers = customerService.findAllEntities();
                        List<Customer> filteredCustomers = customers.stream()
                                .filter(customer -> String.valueOf(customer.getCustomerId()).contains(query))
                                .toList();
                        response = new Response(ResponseStatus.OK, "Результаты поиска", gson.toJson(filteredCustomers));
                        break;
                    }
                    case SEARCH_CUSTOMERS_SURNAME: {
                        String query = request.getRequestMessage();
                        List<Customer> customers = customerService.findAllEntities();
                        List<Customer> filteredCustomers = customers.stream()
                                .filter(customer -> customer.getLastName().toLowerCase().contains(query.toLowerCase()))
                                .toList();
                        response = new Response(ResponseStatus.OK, "Результаты поиска", gson.toJson(filteredCustomers));
                        break;
                    }
                    case GET_PRODUCT: {
                        Product product = gson.fromJson(request.getRequestMessage(), Product.class);
                        product = productService.findEntity(product.getProductId());
                        response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(product));
                        break;
                    }
                    case GET_CUSTOMER: {
                        Customer customer = gson.fromJson(request.getRequestMessage(), Customer.class);
                        customer = customerService.findEntity(customer.getCustomerId());
                        response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(customer));
                        break;
                    }
                    case ADD_PURCHASE: {
                        Purchase purchase = gson.fromJson(request.getRequestMessage(), Purchase.class);
                        purchaseService.saveEntity(purchase);
                        response = new Response(ResponseStatus.OK, "Данные о покупке успешно сохранены!", "");
                        break;
                    }
                    case SEARCH_PURCHASE_BY_CUSTOMER_ID: {
                        String query = request.getRequestMessage();
                        List<Purchase> purchases = purchaseService.findAllEntities();
                        List<Purchase> filteredPurchases = purchases.stream()
                                .filter(purchase -> String.valueOf(purchase.getCustomer().getCustomerId()).contains(query))
                                .toList();
                        response = new Response(ResponseStatus.OK, "Результаты поиска", gson.toJson(filteredPurchases));
                        break;
                    }
                    case SEARCH_PURCHASE_BY_EMPLOYEE_ID: {
                        String query = request.getRequestMessage();
                        List<Purchase> purchases = purchaseService.findAllEntities();
                        List<Purchase> filteredPurchases = purchases.stream()
                                .filter(purchase -> String.valueOf(purchase.getEmployee().getEmployeeId()).contains(query))
                                .toList();
                        response = new Response(ResponseStatus.OK, "Результаты поиска", gson.toJson(filteredPurchases));
                        break;
                    }
                    case GET_ALL_EMPLOYEES: {
                        List<Employee> employees = employeeService.findAllEntities();
                        if (employees.isEmpty()) {
                            response = new Response(ResponseStatus.ERROR, "Список сотрудников пуст!", "");
                        } else {
                            System.out.println("Отправка данных сотрудников: " + gson.toJson(employees));
                            response = new Response(ResponseStatus.OK, "Сотрудники успешно загружены!", gson.toJson(employees));
                        }
                        break;
                    }
                    case UPDATE_EMPLOYEE: {
                        Employee updateEmployee = gson.fromJson(request.getRequestMessage(), Employee.class);
                        personDataService.updateEntity(updateEmployee.getPerson());

                        employeeService.updateEntity(updateEmployee);
                        response = new Response(ResponseStatus.OK, "Данные сотрудника успешно обновлены!", "");
                        break;
                    }
                    case GET_ALL_PURCHASES: {
                        List<Purchase> purchases = purchaseService.findAllEntities();
                        if (purchases.isEmpty()) {
                            response = new Response(ResponseStatus.ERROR, "Список покупок пуст!", "");
                        } else {
                            System.out.println("Отправка данных покупок: " + gson.toJson(purchases));
                            response = new Response(ResponseStatus.OK, "Покупки успешно загружены!", gson.toJson(purchases));
                        }
                        break;
                    }
                    case GET_LOW_STOCK_ITEMS: {
                        List<Product> allProducts = productService.findAllEntities();
                        List<Product> lowStockProducts = new ArrayList<>();

                        for (Product product : allProducts) {
                            if (product.getStockQuantity() < 10) {
                                lowStockProducts.add(product);
                            }
                        }

                        if (lowStockProducts.isEmpty()) {
                            response = new Response(ResponseStatus.OK, "Все товары в достаточном количестве.", "[]");
                        } else {
                            System.out.println("Отправка товаров с низким количеством: " + gson.toJson(lowStockProducts));
                            response = new Response(ResponseStatus.OK, "Товары с низким количеством загружены!", gson.toJson(lowStockProducts));
                        }
                        break;
                    }
                }
                out.println(gson.toJson(response));
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("Клиент " + clientSocket.getInetAddress() + clientSocket.getPort() + " закрыл соединение");
        } finally {
            try {
                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println("Клиент отключен");
                e.printStackTrace();
            }
        }
    }
}

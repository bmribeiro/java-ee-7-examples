package pt.brunoribeiro.examples.soap.service;

import pt.brunoribeiro.examples.soap.model.Customer;

import javax.ejb.Stateless;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Stateless
public class CustomerService {

    private static final Map<Long, Customer> customers =
            new ConcurrentHashMap<>();

    private static final AtomicLong sequence = new AtomicLong();

    static {
        Customer customer1 = new Customer(
                sequence.incrementAndGet(),
                "Bruno",
                "bruno@example.com"
        );

        Customer customer2 = new Customer(
                sequence.incrementAndGet(),
                "Maria",
                "maria@example.com"
        );

        customers.put(customer1.getId(), customer1);
        customers.put(customer2.getId(), customer2);
    }

    public Customer findById(Long id) {
        return customers.get(id);
    }

    public Customer create(String name, String email) {

        Long id = sequence.incrementAndGet();

        Customer customer = new Customer(
                id,
                name,
                email
        );

        customers.put(id, customer);

        return customer;
    }
}
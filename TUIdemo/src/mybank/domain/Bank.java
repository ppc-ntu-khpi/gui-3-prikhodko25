package com.mybank.domain;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private static final Bank instance = new Bank();
    private final List<Customer> customers = new ArrayList<>();

    private Bank() {
    }

    public static Bank getBank() {
        return instance;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public int getNumberOfCustomers() {
        return customers.size();
    }

    public Customer getCustomer(int index) {
        return customers.get(index);
    }
}

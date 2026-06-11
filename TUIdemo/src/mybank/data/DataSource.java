package com.mybank.data;

import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataSource {
    private final String path;

    public DataSource(String path) {
        this.path = path;
    }

    public void loadData() throws IOException {
        Bank bank = Bank.getBank();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            int customerCount = Integer.parseInt(reader.readLine().trim());

            for (int i = 0; i < customerCount; i++) {
                reader.readLine();

                String[] customerFields = reader.readLine().split("\t");
                String firstName = customerFields[0].trim();
                String lastName = customerFields[1].trim();
                int accountCount = Integer.parseInt(customerFields[2].trim());

                Customer customer = new Customer(firstName, lastName);

                for (int j = 0; j < accountCount; j++) {
                    String[] accountFields = reader.readLine().split("\t");
                    String type = accountFields[0].trim();
                    double balance = Double.parseDouble(accountFields[1].trim());
                    double extra = Double.parseDouble(accountFields[2].trim());

                    Account account = "S".equals(type)
                        ? new SavingsAccount(balance, extra)
                        : new CheckingAccount(balance, extra);

                    customer.addAccount(account);
                }

                bank.addCustomer(customer);
            }
        }
    }
}

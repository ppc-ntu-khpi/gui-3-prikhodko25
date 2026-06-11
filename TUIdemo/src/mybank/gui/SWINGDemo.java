package com.mybank.gui;

import com.mybank.data.DataSource;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.Account;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Locale;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SWINGDemo {

    private final JEditorPane log;
    private final JButton show;
    private final JButton report;
    private final JComboBox<String> clients;

    public SWINGDemo() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(500, 300));
        log.setEditable(false);

        show   = new JButton("Show");
        report = new JButton("Report");

        clients = new JComboBox<>();
        Bank bank = Bank.getBank();
        for (int i = 0; i < bank.getNumberOfCustomers(); i++) {
            Customer c = bank.getCustomer(i);
            clients.addItem(c.getLastName() + ", " + c.getFirstName());
        }
    }

    private void launchFrame() {
        JFrame frame = new JFrame("MyBank Clients");
        frame.setLayout(new BorderLayout());

        JPanel cpane = new JPanel();
        cpane.setLayout(new GridLayout(1, 3));
        cpane.add(clients);
        cpane.add(show);
        cpane.add(report);
        frame.add(cpane, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(log);
        frame.add(scroll, BorderLayout.CENTER);

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bank bank = Bank.getBank();
                Customer cust = bank.getCustomer(clients.getSelectedIndex());

                StringBuilder sb = new StringBuilder();
                sb.append("<br>&nbsp;<b><span style=\"font-size:1.6em;\">")
                  .append(cust.getLastName()).append(", ").append(cust.getFirstName())
                  .append("</span></b><br><hr>");

                for (int i = 0; i < cust.getNumberOfAccounts(); i++) {
                    Account acc = cust.getAccount(i);
                    String accType = acc instanceof CheckingAccount ? "Checking" : "Savings";
                    String color   = acc.getBalance() >= 0 ? "green" : "red";

                    sb.append("&nbsp;<b>Account #").append(i + 1).append("</b><br>")
                      .append("&nbsp;&nbsp;<b>Type:</b> ").append(accType).append("<br>")
                      .append("&nbsp;&nbsp;<b>Balance:</b> <span style=\"color:")
                      .append(color).append(";\"><b>$")
                      .append(String.format("%.2f", acc.getBalance()))
                      .append("</b></span><br><br>");
                }

                log.setText(sb.toString());
            }
        });

        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bank bank = Bank.getBank();

                StringBuilder sb = new StringBuilder();
                sb.append("<br>&nbsp;<b><span style=\"font-size:1.4em;\">Customer Report</span></b><br><hr>")
                  .append("<table border='0' cellpadding='4' width='100%'>")
                  .append("<tr style=\"background:#ddd;\">")
                  .append("<th>#</th><th>Name</th><th>Account Type</th><th>Balance</th>")
                  .append("</tr>");

                for (int i = 0; i < bank.getNumberOfCustomers(); i++) {
                    Customer cust = bank.getCustomer(i);
                    String fullName = cust.getLastName() + ", " + cust.getFirstName();
                    String bg = (i % 2 == 0) ? "#f9f9f9" : "#ffffff";

                    for (int j = 0; j < cust.getNumberOfAccounts(); j++) {
                        Account acc = cust.getAccount(j);
                        String accType = acc instanceof CheckingAccount ? "Checking" : "Savings";
                        String color   = acc.getBalance() >= 0 ? "green" : "red";

                        sb.append("<tr style=\"background:").append(bg).append(";\">")
                          .append("<td>").append(i).append("</td>")
                          .append("<td>").append(j == 0 ? fullName : "").append("</td>")
                          .append("<td>").append(accType).append("</td>")
                          .append("<td><span style=\"color:").append(color).append(";\"><b>$")
                          .append(String.format("%.2f", acc.getBalance()))
                          .append("</b></span></td>")
                          .append("</tr>");
                    }
                }

                sb.append("</table>");
                log.setText(sb.toString());
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(520, 400));
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);

        DataSource ds = new DataSource("data/test.dat");
        ds.loadData();

        SWINGDemo demo = new SWINGDemo();
        demo.launchFrame();
    }
}

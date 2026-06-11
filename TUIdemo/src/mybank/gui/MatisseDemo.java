package com.mybank.gui;

import com.mybank.data.DataSource;
import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import java.awt.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;

public class MatisseDemo extends JFrame {

    private JComboBox<String> cmbCustomers;
    private JButton           btnShow;
    private JButton           btnReport;
    private JButton           btnAbout;
    private JEditorPane       epLog;
    private JScrollPane       scrollLog;
    private JLabel            lblSelect;
    private JPanel            pnlTop;
    private JPanel            pnlButtons;

    public MatisseDemo() {
        initComponents();
        populateCustomers();
    }

    private void initComponents() {
        setTitle("MyBank — Client Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        lblSelect    = new JLabel("Select customer:");
        cmbCustomers = new JComboBox<>();
        cmbCustomers.setPreferredSize(new Dimension(200, 26));

        btnShow   = new JButton("Show");
        btnReport = new JButton("Report");
        btnAbout  = new JButton("About");

        epLog = new JEditorPane("text/html", "");
        epLog.setEditable(false);
        epLog.setBackground(new Color(252, 252, 252));
        epLog.setBorder(new EmptyBorder(6, 8, 6, 8));
        scrollLog = new JScrollPane(epLog);
        scrollLog.setPreferredSize(new Dimension(520, 300));
        scrollLog.setBorder(new TitledBorder("Details"));

        pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnlTop.setBorder(new TitledBorder("Customer"));
        pnlTop.add(lblSelect);
        pnlTop.add(cmbCustomers);

        pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        pnlButtons.add(btnShow);
        pnlButtons.add(btnReport);
        pnlButtons.add(btnAbout);

        JPanel content = new JPanel(new BorderLayout(6, 6));
        content.setBorder(new EmptyBorder(8, 8, 8, 8));
        content.add(pnlTop,      BorderLayout.NORTH);
        content.add(scrollLog,   BorderLayout.CENTER);
        content.add(pnlButtons,  BorderLayout.SOUTH);
        setContentPane(content);

        btnShow.addActionListener(e -> {
            int idx = cmbCustomers.getSelectedIndex();
            if (idx < 0) return;
            Customer cust = Bank.getBank().getCustomer(idx);

            StringBuilder sb = new StringBuilder();
            sb.append("<html><body style='font-family:Arial,sans-serif;font-size:12px;'>")
              .append("<h2 style='margin:4px 0;'>")
              .append(cust.getLastName()).append(", ").append(cust.getFirstName())
              .append("</h2><hr>")
              .append("<table cellpadding='5' cellspacing='0' width='100%' ")
              .append("style='border-collapse:collapse;'>")
              .append("<tr style='background:#e8eaf6;'>")
              .append("<th align='left'>#</th>")
              .append("<th align='left'>Account Type</th>")
              .append("<th align='right'>Balance</th></tr>");

            for (int i = 0; i < cust.getNumberOfAccounts(); i++) {
                Account acc  = cust.getAccount(i);
                String  type = acc instanceof CheckingAccount ? "Checking" : "Savings";
                String  color = acc.getBalance() >= 0 ? "#2e7d32" : "#c62828";
                String  bg    = i % 2 == 0 ? "#ffffff" : "#f5f5f5";

                sb.append("<tr style='background:").append(bg).append(";'>")
                  .append("<td>").append(i + 1).append("</td>")
                  .append("<td>").append(type).append("</td>")
                  .append("<td align='right'><b><span style='color:")
                  .append(color).append(";'>$")
                  .append(String.format("%.2f", acc.getBalance()))
                  .append("</span></b></td></tr>");
            }

            sb.append("</table></body></html>");
            epLog.setText(sb.toString());
            epLog.setCaretPosition(0);
        });

        btnReport.addActionListener(e -> {
            Bank bank = Bank.getBank();

            StringBuilder sb = new StringBuilder();
            sb.append("<html><body style='font-family:Arial,sans-serif;font-size:12px;'>")
              .append("<h2 style='margin:4px 0;'>Customer Report</h2><hr>")
              .append("<table cellpadding='5' cellspacing='0' width='100%' ")
              .append("style='border-collapse:collapse;'>")
              .append("<tr style='background:#e8eaf6;'>")
              .append("<th align='left'>#</th>")
              .append("<th align='left'>Name</th>")
              .append("<th align='left'>Account Type</th>")
              .append("<th align='right'>Balance</th></tr>");

            for (int i = 0; i < bank.getNumberOfCustomers(); i++) {
                Customer cust     = bank.getCustomer(i);
                String   fullName = cust.getLastName() + ", " + cust.getFirstName();

                for (int j = 0; j < cust.getNumberOfAccounts(); j++) {
                    Account acc   = cust.getAccount(j);
                    String  type  = acc instanceof CheckingAccount ? "Checking" : "Savings";
                    String  color = acc.getBalance() >= 0 ? "#2e7d32" : "#c62828";
                    String  bg    = i % 2 == 0 ? "#ffffff" : "#f5f5f5";

                    sb.append("<tr style='background:").append(bg).append(";'>")
                      .append("<td>").append(i).append("</td>")
                      .append("<td>").append(j == 0 ? fullName : "").append("</td>")
                      .append("<td>").append(type).append("</td>")
                      .append("<td align='right'><b><span style='color:")
                      .append(color).append(";'>$")
                      .append(String.format("%.2f", acc.getBalance()))
                      .append("</span></b></td></tr>");
                }
            }

            sb.append("</table></body></html>");
            epLog.setText(sb.toString());
            epLog.setCaretPosition(0);
        });

        btnAbout.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "<html><b>MyBank Client Manager</b><br>" +
                "Lab 4 — Matisse GUI<br><br>" +
                "Reads customer data from <i>test.dat</i><br>" +
                "and displays accounts via Swing.<br><br>" +
                "&#169; 2026 Yarik</html>",
                "About",
                JOptionPane.INFORMATION_MESSAGE
            )
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void populateCustomers() {
        Bank bank = Bank.getBank();
        cmbCustomers.removeAllItems();
        for (int i = 0; i < bank.getNumberOfCustomers(); i++) {
            Customer c = bank.getCustomer(i);
            cmbCustomers.addItem(c.getLastName() + ", " + c.getFirstName());
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);

        DataSource ds = new DataSource("data/test.dat");
        ds.loadData();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new MatisseDemo().setVisible(true));
    }
}

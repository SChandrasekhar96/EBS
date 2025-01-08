package com.Banking;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class adminMainMenu extends JFrame {
    private JButton viewDetailsButton, logoutButton, withdrawButton, depositButton, internalButton, externalButton, viewTransactionButton, updateDetailsButton;
    private String loggedInUsername, adminUsername;

    public adminMainMenu(String loggedInUsername, String adminUsername) {
        this.loggedInUsername = loggedInUsername;
        this.adminUsername = adminUsername;

        setTitle("Admin Main Menu");
        setSize(300, 500); // Increased size for the new button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        viewDetailsButton = new JButton("View Account Details");
        viewDetailsButton.setBounds(50, 30, 200, 30);
        add(viewDetailsButton);

        withdrawButton = new JButton("Withdraw Funds");
        withdrawButton.setBounds(50, 80, 200, 30);
        add(withdrawButton);

        depositButton = new JButton("Deposit Funds");
        depositButton.setBounds(50, 130, 200, 30);
        add(depositButton);

        internalButton = new JButton("Internal Fund Transfer");
        internalButton.setBounds(50, 180, 200, 30);
        add(internalButton);

        externalButton = new JButton("External Fund Transfer");
        externalButton.setBounds(50, 230, 200, 30);
        add(externalButton);

        viewTransactionButton = new JButton("View Transaction History");
        viewTransactionButton.setBounds(50, 280, 200, 30);
        add(viewTransactionButton);

        updateDetailsButton = new JButton("Update User Details");
        updateDetailsButton.setBounds(50, 330, 200, 30);
        add(updateDetailsButton);

        logoutButton = new JButton("Log Out");
        logoutButton.setBounds(50, 380, 200, 30);
        add(logoutButton);

        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAccountDetails();
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Withdraw(loggedInUsername);
            }
        });

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Deposit(loggedInUsername);
            }
        });

        internalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InternalTransfer(loggedInUsername);
            }
        });

        externalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ExternalTransfer(loggedInUsername);
            }
        });

        viewTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountNo = fetchAccNumber(loggedInUsername);
                if (accountNo != null && !accountNo.isEmpty()) {
                    new viewTransactions(accountNo);
                } else {
                    JOptionPane.showMessageDialog(adminMainMenu.this, "Unable to retrieve the account number", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	new UpdateUserDetails(fetchAccNumber(loggedInUsername));
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(adminMainMenu.this, "Logged Out Successfully");
                new adminAccountPage(adminUsername).setVisible(true);
                dispose();
            }
        });

        setVisible(true);
    }

    private void viewAccountDetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT a.AccountNumber, u.FullName, u.Email, u.Phone, a.Balance " +
                         "FROM Users u " +
                         "JOIN Accounts a ON u.UserID = a.UserID " +
                         "WHERE u.Username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loggedInUsername);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String accountno = rs.getString("AccountNumber");
                String fullName = rs.getString("FullName");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                double balance = rs.getDouble("Balance");

                JOptionPane.showMessageDialog(this, "Account Number : " + accountno + "\nFull Name : " + fullName + "\nEmail Address : " + email + "\nPhone : " + phone + "\nAccount Type : Savings" + "\nAccount Balance : " + balance, "Account Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No account details found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String fetchAccNumber(String username) {
        String accNo = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT AccountNumber FROM Accounts WHERE UserID = (SELECT UserID FROM Users WHERE Username = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                accNo = rs.getString("AccountNumber");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
            "Error: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        }
        return accNo;
    }
}

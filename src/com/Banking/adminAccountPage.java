package com.Banking;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class adminAccountPage extends JFrame {
    private JLabel accountNumberLabel;
    private JTextField accountNumberField;
    private JButton submitButton, logoutButton,createButton;
    private String adminUsername; // Store admin's username

    public adminAccountPage(String adminUsername) {
        this.adminUsername = adminUsername; // Initialize admin's username
        setTitle("Admin Account Page");
        setSize(300, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        accountNumberLabel = new JLabel("Enter Account Number:");
        accountNumberLabel.setBounds(50, 30, 200, 20);
        add(accountNumberLabel);

        accountNumberField = new JTextField();
        accountNumberField.setBounds(50, 60, 200, 25);
        add(accountNumberField);

        submitButton = new JButton("Submit");
        submitButton.setBounds(50, 100, 90, 30);
        add(submitButton);

        logoutButton = new JButton("Log Out");
        logoutButton.setBounds(160, 100, 90, 30);
        add(logoutButton);
        
        createButton = new JButton("Create Account ?");
        createButton.setBounds(70, 140, 150, 30);
        add(createButton);

        // Submit button logic
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountNumber = accountNumberField.getText().trim();
                if (accountNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(adminAccountPage.this,
                            "Account number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String username = fetchUsernameFromAccountNumber(accountNumber);
                    if (username != null) {
                        new adminMainMenu(username, adminUsername); // Pass both usernames
                        dispose(); // Close this page
                    } else {
                        JOptionPane.showMessageDialog(adminAccountPage.this,
                                "Invalid account number. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Logout button logic
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(adminAccountPage.this, "Logged Out Successfully");
                new LoginPage().setVisible(true); // Redirect to admin login page
                dispose(); // Close this page
            }
        });
        
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateAccount(); // Redirect to admin login page // Close this page
            }
        });

        setVisible(true);
    }

    private String fetchUsernameFromAccountNumber(String accountNumber) {
        String username = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "SELECT u.Username FROM Users u " +
                         "JOIN Accounts a ON u.UserID = a.UserID " +
                         "WHERE a.AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                username = rs.getString("Username");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return username;
    }
}

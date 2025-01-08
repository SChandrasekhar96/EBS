package com.Banking;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateUserDetails extends JFrame {
    private JTextField nameField, emailField, phoneField, usernameField;
    private JPasswordField passwordField;
    private JButton searchButton, updateButton;
    private JLabel statusLabel;
    private String accountNumber;

    public UpdateUserDetails(String accountNumber) {
        this.accountNumber = accountNumber;

        setTitle("Update User Details");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setBounds(50, 30, 120, 30);
        add(accountLabel);

        JLabel accountValueLabel = new JLabel(accountNumber);
        accountValueLabel.setBounds(180, 30, 200, 30);
        add(accountValueLabel);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 80, 100, 30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 80, 200, 30);
        nameField.setEnabled(false); // Initially disabled
        add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 130, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 130, 200, 30);
        emailField.setEnabled(false);
        add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 180, 100, 30);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 180, 200, 30);
        phoneField.setEnabled(false);
        add(phoneField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 230, 100, 30);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 230, 200, 30);
        usernameField.setEnabled(false);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 280, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 280, 200, 30);
        passwordField.setEnabled(false);
        add(passwordField);

        searchButton = new JButton("Search");
        searchButton.setBounds(50, 330, 100, 30);
        add(searchButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(250, 330, 100, 30);
        updateButton.setEnabled(false); // Initially disabled
        add(updateButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(50, 370, 300, 30);
        add(statusLabel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchUserDetails();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUserDetails();
            }
        });

        setVisible(true);
    }

    private void searchUserDetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "SELECT u.FullName, u.Email, u.Phone, u.Username, u.PasswordHash " +
                         "FROM Users u JOIN Accounts a ON u.UserID = a.UserID WHERE a.AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("FullName"));
                emailField.setText(rs.getString("Email"));
                phoneField.setText(rs.getString("Phone"));
                usernameField.setText(rs.getString("Username"));
                passwordField.setText(rs.getString("PasswordHash"));

                nameField.setEnabled(true);
                emailField.setEnabled(true);
                phoneField.setEnabled(true);
                usernameField.setEnabled(true);
                passwordField.setEnabled(true);
                updateButton.setEnabled(true);

                statusLabel.setText("User found. Update details and click Update.");
            } else {
                JOptionPane.showMessageDialog(this, "No user found for the given account number.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("No user found.");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateUserDetails() {
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "UPDATE Users u " +
                         "JOIN Accounts a ON u.UserID = a.UserID " +
                         "SET u.FullName = ?, u.Email = ?, u.Phone = ?, u.Username = ?, u.PasswordHash = ? " +
                         "WHERE a.AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, username);
            stmt.setString(5, password);
            stmt.setString(6, accountNumber);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "User details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No user found for the given account number.", "Update Failed", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Update failed.");
            }

            stmt.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

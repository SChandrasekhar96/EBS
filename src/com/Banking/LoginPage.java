package com.Banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;

    public LoginPage() {
        setTitle("Login Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        

        JLabel welcomeLabel = new JLabel("Welecome to EBS Bank!"); 
        welcomeLabel.setBounds(125,30,250,30);
        mainPanel.add(welcomeLabel);
        
        JButton userLoginButton = new JButton("User Login");
        userLoginButton.setBounds(100, 100, 200, 30);
        mainPanel.add(userLoginButton);

        JButton adminLoginButton = new JButton("Admin Login");
        adminLoginButton.setBounds(100, 150, 200, 30);
        mainPanel.add(adminLoginButton);

        userLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserLoginPanel();
            }
        });

        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAdminLoginPanel();
            }
        });

        add(mainPanel, "mainPanel");

        JPanel userLoginPanel = createUserLoginPanel();
        add(userLoginPanel, "userLoginPanel");

        JPanel adminLoginPanel = createAdminLoginPanel();
        add(adminLoginPanel, "adminLoginPanel");

        setVisible(true);
    }

    private JPanel createUserLoginPanel() {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 50, 100, 25);
        userPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 150, 25);
        userPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 100, 25);
        userPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 150, 25);
        userPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 160, 100, 30);
        userPanel.add(loginButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(220, 160, 100, 30);
        userPanel.add(backButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processUserLogin();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) getContentPane().getLayout();
                cl.show(getContentPane(), "mainPanel");
            }
        });

        return userPanel;
    }

    private JPanel createAdminLoginPanel() {
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(null);

        JLabel adminUsernameLabel = new JLabel("Admin Username:");
        adminUsernameLabel.setBounds(50, 50, 150, 25);
        adminPanel.add(adminUsernameLabel);

        adminUsernameField = new JTextField();
        adminUsernameField.setBounds(200, 50, 150, 25);
        adminPanel.add(adminUsernameField);

        JLabel adminPasswordLabel = new JLabel("Admin Password:");
        adminPasswordLabel.setBounds(50, 100, 150, 25);
        adminPanel.add(adminPasswordLabel);

        adminPasswordField = new JPasswordField();
        adminPasswordField.setBounds(200, 100, 150, 25);
        adminPanel.add(adminPasswordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 160, 100, 30);
        adminPanel.add(loginButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(220, 160, 100, 30);
        adminPanel.add(backButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processAdminLogin();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) getContentPane().getLayout();
                cl.show(getContentPane(), "mainPanel");
            }
        });

        return adminPanel;
    }

    private void showUserLoginPanel() {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "userLoginPanel");
    }

    private void showAdminLoginPanel() {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "adminLoginPanel");
    }

    private void processUserLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "SELECT * FROM Users WHERE Username = ? AND PasswordHash = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "User Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new UserMainMenu(username);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void processAdminLogin() {
        String adminUsername = adminUsernameField.getText();
        String adminPassword = new String(adminPasswordField.getPassword());

        if (adminUsername.isEmpty() || adminPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Admin Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "SELECT * FROM AdminDetails WHERE AdminUsername = ? AND AdminPassword = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, adminUsername);
            stmt.setString(2, adminPassword);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Admin Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new adminAccountPage(adminUsername);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}


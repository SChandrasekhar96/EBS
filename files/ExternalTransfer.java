import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class ExternalTransfer extends JFrame {
    private JTextField receiverAccountField, bankNameField, amountField;
    private JLabel senderAccountLabel;
    private String loggedInUsername, senderAccount;

    public ExternalTransfer(String username) {
        this.loggedInUsername = username;

        setTitle("External Fund Transfer");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel senderLabel = new JLabel("Your Account Number:");
        senderLabel.setBounds(10, 20, 150, 25);
        add(senderLabel);

        senderAccountLabel = new JLabel();
        senderAccountLabel.setBounds(180, 20, 200, 25);
        add(senderAccountLabel);

        JLabel bankLabel = new JLabel("Recipient Bank Name:");
        bankLabel.setBounds(10, 60, 150, 25);
        add(bankLabel);

        bankNameField = new JTextField();
        bankNameField.setBounds(180, 60, 150, 25);
        add(bankNameField);

        JLabel receiverLabel = new JLabel("Recipient Account:");
        receiverLabel.setBounds(10, 100, 150, 25);
        add(receiverLabel);

        receiverAccountField = new JTextField();
        receiverAccountField.setBounds(180, 100, 150, 25);
        add(receiverAccountField);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(10, 140, 150, 25);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(180, 140, 150, 25);
        add(amountField);

        JButton transferButton = new JButton("Transfer");
        transferButton.setBounds(50, 200, 100, 30);
        add(transferButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(200, 200, 100, 30);
        add(backButton);

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processExternalTransfer();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        fetchSenderAccount();

        setVisible(true);
    }

    private void fetchSenderAccount() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "SELECT AccountNumber FROM Accounts WHERE UserID = (SELECT UserID FROM Users WHERE Username = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loggedInUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                senderAccount = rs.getString("AccountNumber");
                senderAccountLabel.setText(senderAccount);
            } else {
                JOptionPane.showMessageDialog(this, "Sender account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void processExternalTransfer() {
        String bankName = bankNameField.getText();
        String receiverAccount = receiverAccountField.getText();
        String amountText = amountField.getText();

        if (bankName.isEmpty() || receiverAccount.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String senderSql = "SELECT Balance FROM Accounts WHERE AccountNumber = ?";
            PreparedStatement senderStmt = conn.prepareStatement(senderSql);
            senderStmt.setString(1, senderAccount);
            ResultSet senderRs = senderStmt.executeQuery();

            if (senderRs.next()) {
                double senderBalance = senderRs.getDouble("Balance");

                if (senderBalance >= amount) {
                    String updateSenderSql = "UPDATE Accounts SET Balance = Balance - ? WHERE AccountNumber = ?";
                    PreparedStatement updateSenderStmt = conn.prepareStatement(updateSenderSql);
                    updateSenderStmt.setDouble(1, amount);
                    updateSenderStmt.setString(2, senderAccount);
                    updateSenderStmt.executeUpdate();

                    String updateUserSql = "UPDATE Users SET Balance = Balance - ? WHERE UserID = (SELECT UserID FROM Accounts WHERE AccountNumber = ?)";
                    PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSql);
                    updateUserStmt.setDouble(1, amount);
                    updateUserStmt.setString(2, senderAccount);
                    updateUserStmt.executeUpdate();

                    String logSql = "INSERT INTO Transactions (AccountNumber, ReceiverAccountNumber, TransactionType, Amount) VALUES (?, ?, 'Transfer', ?)";
                    PreparedStatement logStmt = conn.prepareStatement(logSql);
                    logStmt.setString(1, senderAccount);
                    logStmt.setString(2, receiverAccount);
                    logStmt.setDouble(3, amount);
                    logStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Transfer successful.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    updateSenderStmt.close();
                    logStmt.close();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            senderRs.close();
            senderStmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

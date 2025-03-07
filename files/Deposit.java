import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
public class Deposit extends JFrame{
    private JTextField amountText;
    private JButton depositButton,cancelButton;
    private String loggedInUsername;

    public Deposit(String username){
        this.loggedInUsername = username;
        setTitle("Deposit Funds");
        setSize(300,200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        

        JLabel amountLabel = new JLabel("Enter the Amount : ");
        amountLabel.setBounds(10,20,100,25);
        add(amountLabel);

        amountText = new JTextField();
        amountText.setBounds(120,20,150,25);
        add(amountText);

        depositButton = new JButton("Deposit");
        depositButton.setBounds(10,70,120,30);
        add(depositButton);

        cancelButton = new JButton("Cancel Deposit");
        cancelButton.setBounds(150,70,120,30);
        add(cancelButton);

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                depositAmount();
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
        setVisible(true);
    }

    private void depositAmount(){
        String textAmount = amountText.getText();

        if(textAmount.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please Enter an Amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(textAmount);
            if(amount<=0){
                JOptionPane.showMessageDialog(this, "Amount must be more than Zero", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter Valid Amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "SELECT AccountNumber,Balance FROM Accounts WHERE UserID = (SELECT UserID FROM Users WHERE Username = ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loggedInUsername);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String accountNumber = rs.getString("AccountNumber");
                double currBalance = rs.getDouble("Balance");
                    String updatesql = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = ?";
                    PreparedStatement upstmt = conn.prepareStatement(updatesql);
                    upstmt.setDouble(1,amount);
                    upstmt.setString(2, accountNumber);
                    upstmt.executeUpdate();

                    String updateUserSql = "UPDATE Users SET Balance = Balance + ? WHERE UserID = (SELECT UserID FROM Accounts WHERE AccountNumber = ?)";
                    PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSql);
                    updateUserStmt.setDouble(1, amount);
                    updateUserStmt.setString(2, accountNumber);
                    updateUserStmt.executeUpdate();

                    String logTsql = "INSERT INTO Transactions (AccountNumber, ReceiverAccountNumber, TransactionType,Amount) VALUES(?,Null,'Deposit',?)";
                    PreparedStatement logstmt = conn.prepareStatement(logTsql);
                    logstmt.setString(1,accountNumber);
                    logstmt.setDouble(2,amount);
                    logstmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Deposit Successful. New balance :" +(currBalance+amount), "Suceess", JOptionPane.INFORMATION_MESSAGE);

                    upstmt.close();
                    logstmt.close();
                 

            }
            else{
                JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

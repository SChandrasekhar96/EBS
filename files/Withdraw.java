import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
public class Withdraw extends JFrame{
    private JTextField amountText;
    private JButton withdrawButton,cancelButton;
    private String loggedInUsername;

    public Withdraw(String username){
        this.loggedInUsername = username;
        setTitle("Withdraw Funds");
        setSize(300,200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        

        JLabel amountLabel = new JLabel("Enter the Amount : ");
        amountLabel.setBounds(10,20,100,25);
        add(amountLabel);

        amountText = new JTextField();
        amountText.setBounds(120,20,150,25);
        add(amountText);

        withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(10,70,120,30);
        add(withdrawButton);

        cancelButton = new JButton("Cancel Withdraw");
        cancelButton.setBounds(150,70,120,30);
        add(cancelButton);

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                withdrawAmount();
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

    private void withdrawAmount(){
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
                if(amount>currBalance){
                    JOptionPane.showMessageDialog(this, "Insufficient Balance.", "Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    String updatesql = "UPDATE Accounts SET Balance = Balance - ? WHERE AccountNumber = ?";
                    PreparedStatement upstmt = conn.prepareStatement(updatesql);
                    upstmt.setDouble(1,amount);
                    upstmt.setString(2, accountNumber);
                    upstmt.executeUpdate();

                    String updateUserSql = "UPDATE Users SET Balance = Balance - ? WHERE UserID = (SELECT UserID FROM Accounts WHERE AccountNumber = ?)";
                    PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSql);
                    updateUserStmt.setDouble(1, amount);
                    updateUserStmt.setString(2, accountNumber);
                    updateUserStmt.executeUpdate();

                    String logTsql = "INSERT INTO Transactions (AccountNumber, ReceiverAccountNumber, TransactionType,Amount) VALUES(?,Null,'Withdrawal',?)";
                    PreparedStatement logstmt = conn.prepareStatement(logTsql);
                    logstmt.setString(1,accountNumber);
                    logstmt.setDouble(2,amount);
                    logstmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Withdrawl Successful. New balance :" +(currBalance-amount), "Suceess", JOptionPane.INFORMATION_MESSAGE);

                    upstmt.close();
                    logstmt.close();
                } 

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

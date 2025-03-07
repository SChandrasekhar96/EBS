import javax.swing.*;
import java.sql.*;
import java.awt.event.*;
public class InternalTransfer extends JFrame{
    private JTextField receiverAccount,amountText;
    private JLabel senderAccountLabel;
    private String loggedInUsername,senderAccount;
    public InternalTransfer(String username){
        this.loggedInUsername=username;
        setTitle("Internal fund Transfer");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel senderLabel = new JLabel("Sender Account No :");
        senderLabel.setBounds(10,20,150,25); 
        add(senderLabel);

        senderAccountLabel = new JLabel();
        senderAccountLabel.setBounds(180,20,200,25);
        add(senderAccountLabel);

        JLabel recieverLabel = new JLabel("Recipient Account : ");
        recieverLabel.setBounds(10,60,150,25);
        add(recieverLabel);

        receiverAccount = new JTextField();
        receiverAccount.setBounds(180,60,150,25);
        add(receiverAccount);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(10, 100, 150, 25);
        add(amountLabel);

        amountText = new JTextField();
        amountText.setBounds(180, 100, 150, 25);
        add(amountText);

        JButton transferButton = new JButton("Transfer");
        transferButton.setBounds(50, 160, 100, 30);
        add(transferButton);

        JButton backButton = new JButton("Cancel");
        backButton.setBounds(200, 160, 100, 30);
        add(backButton);

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                inttransferProcess();
            }
        });
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });  
        fetchSenderAccount();
        setVisible(true);      
    }
    private void fetchSenderAccount(){
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
    private void inttransferProcess(){
        String receiverAcc = receiverAccount.getText();
        String textAmount = amountText.getText();
        if(receiverAcc.isEmpty()||textAmount.isEmpty()){
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(textAmount);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sendsql = "SELECT Balance FROM Accounts WHERE AccountNumber = ?";
            PreparedStatement sendstmt = conn.prepareStatement(sendsql);
            sendstmt.setString(1, senderAccount);
            ResultSet senderRs = sendstmt.executeQuery();
            if(senderRs.next()){
                double senderbalance = senderRs.getDouble("Balance");

                String recievesql = "SELECT Balance FROM Accounts WHERE AccountNumber = ?";
                PreparedStatement receivestmt = conn.prepareStatement(recievesql);
                receivestmt.setString(1, receiverAcc);
                ResultSet receiveRs = receivestmt.executeQuery();
                if(receiveRs.next()){
                    if(senderbalance>=amount){
                        String updateSendersql = "UPDATE Accounts SET Balance = Balance - ? WHERE AccountNumber = ?";
                        PreparedStatement upsendstmt = conn.prepareStatement(updateSendersql);
                        upsendstmt.setDouble(1, amount);
                        upsendstmt.setString(2, senderAccount);
                        upsendstmt.executeUpdate();

                        String updateSenderUserSql = "UPDATE Users SET Balance = Balance - ? WHERE UserID = (SELECT UserID FROM Accounts WHERE AccountNumber = ?)";
                        PreparedStatement upSenderUserStmt = conn.prepareStatement(updateSenderUserSql);
                        upSenderUserStmt.setDouble(1, amount);
                        upSenderUserStmt.setString(2, senderAccount);
                        upSenderUserStmt.executeUpdate();

                        String updatereceivesql = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = ?";
                        PreparedStatement uprecstmt = conn.prepareStatement(updatereceivesql);
                        uprecstmt.setDouble(1, amount);
                        uprecstmt.setString(2, receiverAcc);
                        uprecstmt.executeUpdate();

                        String updateReceiverUserSql = "UPDATE Users SET Balance = Balance + ? WHERE UserID = (SELECT UserID FROM Accounts WHERE AccountNumber = ?)";
                        PreparedStatement upReceiverUserStmt = conn.prepareStatement(updateReceiverUserSql);
                        upReceiverUserStmt.setDouble(1, amount);
                        upReceiverUserStmt.setString(2, receiverAcc);
                        upReceiverUserStmt.executeUpdate();

                        String logsql = "INSERT INTO Transactions(AccountNumber,ReceiverAccountNumber,TransactionType,Amount) VALUES (?,?,'Transfer',?)";
                        PreparedStatement logstmt = conn.prepareStatement(logsql);
                        logstmt.setString(1, senderAccount);
                        logstmt.setString(2, receiverAcc);
                        logstmt.setDouble(3, amount);
                        logstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Funds Transfered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        upsendstmt.close();
                        uprecstmt.close();
                        logstmt.close();

                        dispose();
                    }else{
                        JOptionPane.showMessageDialog(this, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    receiveRs.close();
                    receivestmt.close();
                }else{
                    JOptionPane.showMessageDialog(this, "Recipient account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            senderRs.close();
            sendstmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

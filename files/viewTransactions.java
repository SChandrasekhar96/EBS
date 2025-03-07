import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

import java.awt.BorderLayout;
import java.sql.*;
public class viewTransactions extends JFrame {
    private JTable transactionTable;
    private JButton backButton;
    private String accountNumber;
    public viewTransactions(String accountNumber){
        this.accountNumber = accountNumber;
        setTitle("Transaction History");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"Transaction ID","Type","Amount","Reciever Account","Timestamp"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        transactionTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane,BorderLayout.CENTER);

        backButton = new JButton("Back to Main Menu");
        add(backButton,BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        loadTransactions(accountNumber);
        setVisible(true);
    }
    private void loadTransactions(String accountNumber){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/OnlineBanking";
            String dbUsername = "root";
            String dbPassword = "Chandu@96";
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "SELECT TransactionID, TransactionType, Amount, ReceiverAccountNumber, Timestamp " +
                         "FROM Transactions WHERE AccountNumber = ? ORDER BY Timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
            while(rs.next()){
                int transactionID = rs.getInt("TransactionID");
                String transactiontype = rs.getString("TransactionType");
                double amount = rs.getDouble("Amount");
                String receiverAcc = rs.getString("ReceiverAccountNumber");
                String timeSt = rs.getString("Timestamp");
                model.addRow(new Object[]{transactionID,transactiontype,amount,receiverAcc,timeSt});
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

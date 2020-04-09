package Advisor.Client.UserPage.PinReset;

import Advisor.Database.DataBaseHandler;
import Advisor.Mail.MailUtil;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


/**
 *
 * @author Ashwin
 */
public class PinResetJSwing extends javax.swing.JFrame {
    String UserPIN;
    int UserID;
    String Email;
    DataBaseHandler DBH;
    public PinResetJSwing(String pin, int ID, String email) {
        UserPIN = pin;
        UserID = ID;
        Email = email;
        DBH = DataBaseHandler.getInstance();
        initComponents();
    }
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1.setText("Submit");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Re-Send");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Enter your PIN : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1)
                                .addGap(40, 40, 40))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jButton2)
                                .addGap(26, 26, 26)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 28, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addGap(97, 97, 97))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(29, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String EnteredPIN = jTextField1.getText();
        if(EnteredPIN.equals(UserPIN)){
            DBH.execUpdate("update tax_revenue set tax_paid="+true+" where customers_id="+UserID+";");
            super.dispose();
        }else{
            jLabel2.setText("Please Click on the Re-Send button if you dont know your pin");
        }

    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        String newPin = SecurityNumberGenerator();
        DBH.execUpdate("update cust set security_pin = '"+newPin+"' where customers_id="+UserID+";");
        ResultSet rs = DBH.execQuery("select security_pin from cust where customers_id="+UserID+";");
        try {
            if(rs.next()){
                UserPIN = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        new Thread(()->{
            MailUtil.sendMail(Email,"Your New Pin is : "+newPin);
        }).start();
    }

    private String SecurityNumberGenerator() {
        int length = 5;
        int left = 48;
        int right = 90;
        Random r = new Random();
        String random = r.ints(left, right).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        System.out.println(random);
        return random;
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
}

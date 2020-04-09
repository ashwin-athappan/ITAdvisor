package Advisor.Database;

import javax.swing.*;
import java.sql.*;

public class DataBaseHandler {
    private final String url = "jdbc:mysql://localhost:3306/itadvisor";
    private final String userName = "root";
    private final String password = "1379";
    private Connection myConnection = null;
    private Statement myStatement= null;
    private static DataBaseHandler handler;

    private DataBaseHandler(){
        createConnection();
        new Thread(() -> {
            setUpCustomersTable();
            setUpTax_RevenueTable();
            setUpPasswordsTable();
            setUpPasswordDisplayTable();
            setUpCustomer_RequestTable();
            setUpBillsTable();
        }).start();
    }

    public static DataBaseHandler getInstance(){
        if(handler == null){
            handler = new DataBaseHandler();
        }
        return handler;
    }

    public void createConnection(){
        try {
            myConnection = DriverManager.getConnection(url, userName, password);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void setUpCustomersTable(){
        String Table_Name = "client";
        try{
            myStatement = myConnection.createStatement();
            DatabaseMetaData dbm = myConnection.getMetaData();
            ResultSet rs = dbm.getTables(null,null,Table_Name.toUpperCase(),null);

            if(rs.next()){
                System.out.println("Table "+Table_Name+" already exists.");
            }else{
                String query = "CREATE TABLE `itadvisor`.`cust` (\n" +
                        "  `customers_id` INT NOT NULL AUTO_INCREMENT,\n" +
                        "  `customers_name` VARCHAR(200) NULL,\n" +
                        "  `email` VARCHAR(200) NULL,\n" +
                        "  `income` DOUBLE NULL,\n" +
                        "  `account_type` INT NULL,\n" +
                        "  `security_pin` VARCHAR(45) NULL,\n" +
                        "  PRIMARY KEY (`customers_id`),\n" +
                        "  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE);";
                myStatement.executeUpdate(query);
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void setUpTax_RevenueTable(){
        String Table_Name = "tax_revenue";
        try{
            myStatement = myConnection.createStatement();
            DatabaseMetaData dbm = myConnection.getMetaData();
            ResultSet rs = dbm.getTables(null,null,Table_Name.toUpperCase(),null);

            if(rs.next()){
                System.out.println("Table "+Table_Name+" already exists.");
            }else{
                String query = "CREATE TABLE `itadvisor`.`tax_revenue` (\n" +
                               "  `customers_id` INT NOT NULL,\n" +
                               "  `income` DOUBLE NULL,\n" +
                               "  `tax` DOUBLE NULL,\n" +
                               "  `tax_paid` BOOLEAN NULL,\n" +
                               "  `profit_margin` DOUBLE NULL,\n" +
                               "  `warnings` INT NULL,\n" +
                               "  PRIMARY KEY (`customers_id`),\n" +
                               "  CONSTRAINT `customers_id`\n" +
                               "    FOREIGN KEY (`customers_id`)\n" +
                               "    REFERENCES `itadvisor`.`cust` (`customers_id`)\n" +
                               "    ON DELETE NO ACTION\n" +
                               "    ON UPDATE NO ACTION);\n";
                myStatement.executeUpdate(query);
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void setUpPasswordsTable(){
        String Table_Name = "passwords";
        try{
            myStatement = myConnection.createStatement();
            DatabaseMetaData dbm = myConnection.getMetaData();
            ResultSet rs = dbm.getTables(null,null,Table_Name.toUpperCase(),null);

            if(rs.next()){
                System.out.println("Table "+Table_Name+" already exists.");
            }else{
                String query = "CREATE TABLE `itadvisor`.`passwords` (\n" +
                        "  `customer_id` INT NOT NULL,\n" +
                        "  `password` VARCHAR(200) NULL,\n" +
                        "  PRIMARY KEY (`customer_id`),\n" +
                        "  CONSTRAINT `customer_id`\n" +
                        "    FOREIGN KEY (`customer_id`)\n" +
                        "    REFERENCES `itadvisor`.`cust` (`customers_id`)\n" +
                        "    ON DELETE NO ACTION\n" +
                        "    ON UPDATE NO ACTION);";
                myStatement.executeUpdate(query);
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void setUpPasswordDisplayTable(){
        String Table_Name = "password_display";
        try{
            myStatement = myConnection.createStatement();
            DatabaseMetaData dbm = myConnection.getMetaData();
            ResultSet rs = dbm.getTables(null,null,Table_Name.toUpperCase(),null);

            if(rs.next()){
                System.out.println("Table "+Table_Name+" already exists.");
            }else{
                String query = "CREATE TABLE `itadvisor`.`password_display` (\n" +
                               "  `id` INT NOT NULL,\n" +
                               "  `trials` INT NOT NULL,\n" +
                               "  PRIMARY KEY (`id`),\n" +
                               "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,\n" +
                               "  CONSTRAINT `id`\n" +
                               "    FOREIGN KEY (`id`)\n" +
                               "    REFERENCES `itadvisor`.`cust` (`customers_id`)\n" +
                               "    ON DELETE NO ACTION\n" +
                               "    ON UPDATE NO ACTION);";
                myStatement.executeUpdate(query);
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void setUpCustomer_RequestTable(){
        String Table_Name = "cust_request";
        try{
            myStatement = myConnection.createStatement();
            DatabaseMetaData dbm = myConnection.getMetaData();
            ResultSet rs = dbm.getTables(null,null,Table_Name.toUpperCase(),null);

            if(rs.next()){
                System.out.println("Table "+Table_Name+" already exists.");
            }else{
                String query = "CREATE TABLE `itadvisor`.`cust_request` (\n" +
                        "  `customers_name` VARCHAR(200) NOT NULL,\n" +
                        "  `email` VARCHAR(200) NOT NULL,\n" +
                        "  `income` DOUBLE NULL,\n" +
                        "  `account_type` INT NULL,\n" +
                        "  `password` VARCHAR(200) NULL,\n" +
                        "  UNIQUE INDEX `email_UNIQUE` (`email` ASC));";
                myStatement.executeUpdate(query);
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void setUpBillsTable(){
        String Table_Name = "bills";
        try{
            myStatement = myConnection.createStatement();
            DatabaseMetaData dbm = myConnection.getMetaData();
            ResultSet rs = dbm.getTables(null,null,Table_Name.toUpperCase(),null);

            if(rs.next()){
                System.out.println("Table "+Table_Name+" already exists.");
            }else{
                String query = "CREATE TABLE `itadvisor`.`bills` (\n" +
                               "  `cust_id` INT NOT NULL,\n" +
                               "  `image` VARCHAR(300) NOT NULL,\n" +
                               "  INDEX `id_idx` (`cust_id` ASC) VISIBLE,\n" +
                               "  UNIQUE INDEX `image_UNIQUE` (`image` ASC) VISIBLE,\n" +
                               "  CONSTRAINT `cust_id`\n" +
                               "    FOREIGN KEY (`cust_id`)\n" +
                               "    REFERENCES `itadvisor`.`cust` (`customers_id`)\n" +
                               "    ON DELETE NO ACTION\n" +
                               "    ON UPDATE NO ACTION);";
                myStatement.executeUpdate(query);
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public ResultSet execQuery(String query){
        ResultSet rs = null;
        try {
            Statement queryStatement = myConnection.createStatement();
            rs = queryStatement.executeQuery(query);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error :"+e.getMessage(),"Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }

    public boolean execAction(String query){
        try {
            Statement actionStatement = myConnection.createStatement();
            actionStatement.execute(query);
            return true;
        } catch (SQLException e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Error "+e.getMessage(),"Error Occurred",JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean execUpdate(String query){
        try {
            Statement actionStatement = myConnection.createStatement();
            actionStatement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Error "+e.getMessage(),"Error Occurred",JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public ResultSet execSpecialQuery(int par,String key) {
        String q = "";
        PreparedStatement s = null;
        switch (par) {
            case 1:
                q = "select * from cust where customers_name like ?";
                break;
            case 2:
                q = "select * from cust where email like ?";
                break;
            case 3:
                q = "select * from cust where income like ?";
                break;
            case 4:
                q = "select * from tax_revenue where warnings like ?";
                break;
            case 5:
                q = "select * from tax_revenue where tax like ?";
                break;
            default:
        }
        try {
            s = myConnection.prepareStatement(q);
            s.setString(1, "%" + key + "%");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!q.isEmpty()) {
            try {
                assert s != null;
                return s.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ResultSet SelectPreppedStatement(String tableName, int key){
        String q = "Select * from "+tableName+" where customers_id = ?";
        PreparedStatement ps = null;
        try {
            ps = myConnection.prepareStatement(q);
            ps.setInt(1,key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            assert ps != null;
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

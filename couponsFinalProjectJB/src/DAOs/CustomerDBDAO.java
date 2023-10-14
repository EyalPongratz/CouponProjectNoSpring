package DAOs;

import Connections.ConnectionPool;
import javaBeans.Coupon;
import javaBeans.Customer;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDBDAO implements CustomersDAO{
    private ConnectionPool connectionPool;

    public CustomerDBDAO() throws SQLException {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public boolean isCustomerExists(String email, String password) throws SQLException {

        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT email, password FROM customers WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            boolean result = rs.next();
            return result;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void addCustomer(Customer customer) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "insert into customers(first_name, last_name, email, password) values(?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getLastName());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getPassword());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                customer.setId(id);
            }
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "UPDATE customers " +
                    "SET first_name = ?, last_name = ?, email = ?, password = ? " +
                    "WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getLastName());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getPassword());
            statement.setInt(5, customer.getId());
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void deleteCustomer(int customerID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "DELETE FROM customers WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public ArrayList<Customer> getAllCustomers() throws SQLException {
        Connection connection = connectionPool.getConnection();
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();

        try {
            String sql = "select * from customers";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                String firstName = rs.getString(2);
                String lastName = rs.getString(3);
                String email = rs.getString(4);
                String password = rs.getString(5);
                ArrayList<Coupon> coupons = couponsDBDAO.getCustomerCoupons(id, connection);

                customers.add(new Customer(id, firstName, lastName, email, password, coupons));
            }
            return customers;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public Customer getOneCustomer(int customerID) throws SQLException {
        Connection connection = connectionPool.getConnection();
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();

        try {
            String sql = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            ResultSet rs = statement.executeQuery();

            Customer customer = null;
            if (rs.next()) {
                int id = rs.getInt(1);
                String firstName = rs.getString(2);
                String lastName = rs.getString(3);
                String email = rs.getString(4);
                String password = rs.getString(5);
                ArrayList<Coupon> coupons = couponsDBDAO.getCustomerCoupons(id, connection);

                customer = new Customer(id, firstName, lastName, email, password, coupons);
            }
            return customer;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    public int getIDByEmail(String email) throws SQLException{
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT id FROM customers WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return rs.getInt(1);
            else return -1;

        } finally {
            connectionPool.restoreConnection(connection);
        }

    }

    public boolean emailAlreadyExists(String email) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT email FROM customers WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            return rs.next();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    public boolean alreadyPurchased(int customerID, int coupon_ID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT * FROM customers_vs_coupons WHERE customer_id = ? and coupon_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            statement.setInt(2, coupon_ID);
            ResultSet rs = statement.executeQuery();

            return rs.next();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    public int getID(String email, String password) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "select id from customers where email = ? and password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else
                return -1;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }
}

package DAOs;

import Connections.ConnectionPool;
import javaBeans.Category;
import javaBeans.Company;
import javaBeans.Coupon;

import java.sql.*;
import java.util.ArrayList;

public class CompaniesDBDAO implements CompaniesDAO{
    private ConnectionPool connectionPool;

    public CompaniesDBDAO() throws SQLException {
        this.connectionPool = ConnectionPool.getInstance();
    }

    /**
     * checks if company exists in database based on email and password
     * @param email of the company
     * @param password of the company
     * @return true if company exists in database
     * @throws SQLException in case of sql issues
     */
    @Override
    public boolean isCompanyExists(String email, String password) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "select email, password from companies where email = ? and password = ?";
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

    /**
     * adds a company to the database
     * @param company to be added
     * @throws SQLException in case of sql issues
     */
    @Override
    public void addCompany(Company company) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "insert into companies(name, email, password) values(?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, company.getName());
            statement.setString(2, company.getEmail());
            statement.setString(3, company.getPassword());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                company.setId(id);
            }
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * updates a company
     * @param company to be updated
     * @throws SQLException in case of sql issues
     */
    @Override
    public void updateCompany(Company company) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "UPDATE companies " +
                    "SET name = ?, email = ?, password = ? " +
                    "WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, company.getName());
            statement.setString(2, company.getEmail());
            statement.setString(3, company.getPassword());
            statement.setInt(4, company.getId());
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * deletes a company
     * @param companyID of the company to be deleted
     * @throws SQLException in case of sql issues
     */
    @Override
    public void deleteCompany(int companyID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "DELETE FROM companies WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, companyID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * @return an array list of all companies
     * @throws SQLException in case of sql issues
     */
    @Override
    public ArrayList<Company> getAllCompanies() throws SQLException {
        Connection connection = connectionPool.getConnection();
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();

        try {
            String sql = "select * from companies";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<Company> companies = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                String password = rs.getString(4);
                ArrayList<Coupon> coupons = couponsDBDAO.getCompanyCoupons(id, connection);

                companies.add(new Company(id, name, email, password, coupons));
            }
            return companies;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * @param companyID of the company requested
     * @return a company
     * @throws SQLException in case of sql issues
     */
    @Override
    public Company getOneCompany(int companyID) throws SQLException {
        Connection connection = connectionPool.getConnection();
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();

        try {
            String sql = "SELECT * FROM companies WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, companyID);
            ResultSet rs = statement.executeQuery();

            Company company = null;
            if (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                String password = rs.getString(4);
                ArrayList<Coupon> coupons = couponsDBDAO.getCompanyCoupons(id, connection);

                company = new Company(id, name, email, password, coupons);
            }
            return company;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * used to retrieve an id associated with a company name
     * @param name of the company
     * @return the company id
     * @throws SQLException in case of sql issues
     */
    public int getIDByName(String name) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT id FROM companies WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return rs.getInt(1);
            else return -1;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * checks if a company name is already in the database
     * @param name of the company
     * @return true if the company name is in the database
     * @throws SQLException in case of sql issues
     */
    public boolean nameAlreadyExists(String name) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT name FROM companies WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return true;
            else return false;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * checks if a company email already exists in the database
     * @param email of the company
     * @return true if the email exists in the database
     * @throws SQLException in case of sql issues
     */
    public boolean emailAlreadyExists(String email) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT email FROM companies WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return true;
            else return false;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves the unique identifier (ID) associated with a user based on their email and password.
     *
     * This method queries the database to retrieve the unique identifier (ID) of a user (e.g., a company) based on
     * their provided email and password. If a matching user is found, the method returns their ID; otherwise, it returns -1.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return The unique identifier (ID) of the user, or -1 if no matching user is found.
     * @throws SQLException If there is an issue with the database operation.
     */
    public int getID(String email, String password) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "select id from companies where email = ? and password = ?";
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

package DAOs;

import Connections.ConnectionPool;
import javaBeans.Category;
import javaBeans.Coupon;
import java.sql.*;
import java.util.ArrayList;

public class CouponsDBDAO implements CouponsDAO{
    private ConnectionPool connectionPool;

    public CouponsDBDAO() throws SQLException {
        this.connectionPool = ConnectionPool.getInstance();
    }

    /**
     * Adds a new coupon to the database.
     *
     * @param coupon The coupon object to be added.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public void addCoupon(Coupon coupon) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "insert into coupons" +
                    "(company_id, category_id, title, description, start_date, end_date, amount, price, image) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?);";

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, coupon.getCompanyID());
            statement.setInt(2, findCategoryID(coupon.getCategory()));
            statement.setString(3, coupon.getTitle());
            statement.setString(4, coupon.getDescription());
            statement.setDate(5, coupon.getStartDate());
            statement.setDate(6, coupon.getEndDate());
            statement.setInt(7, coupon.getAmount());
            statement.setDouble(8, coupon.getPrice());
            statement.setString(9, coupon.getImage());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                coupon.setId(id);
            }
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Finds and returns the ID associated with a given Category.
     *
     * @param category The Category enum value for which to find the ID.
     * @return The ID of the specified Category, or 0 if not found.
     */
    public int findCategoryID(Category category) {
        for (int i = 0; i < Category.values().length; i++) {
            if(category == Category.values()[i])
                return i+1;
        }
        return 0;
    }

    /**
     * Updates an existing coupon in the database with the provided coupon data.
     *
     * @param coupon The coupon object containing the updated information.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public void updateCoupon(Coupon coupon) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "UPDATE coupons " +
                    "SET company_id = ?, category_id = ?, title = ?, " +
                    "description = ?, start_date = ?, end_date = ?, " +
                    "amount = ?, price = ?, image = ? " +
                    "WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, coupon.getCompanyID());
            statement.setInt(2, findCategoryID(coupon.getCategory()));
            statement.setString(3, coupon.getTitle());
            statement.setString(4, coupon.getDescription());
            statement.setDate(5, coupon.getStartDate());
            statement.setDate(6, coupon.getEndDate());
            statement.setInt(7, coupon.getAmount());
            statement.setDouble(8, coupon.getPrice());
            statement.setString(9, coupon.getImage());
            statement.setInt(10, coupon.getId());
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Deletes a coupon from the database based on its unique identifier (coupon ID).
     *
     * @param couponID The unique identifier of the coupon to be deleted.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public void deleteCoupon(int couponID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "DELETE FROM coupons WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, couponID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves a list of all coupons from the database.
     *
     * @return An ArrayList containing all the coupons stored in the database.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public ArrayList<Coupon> getAllCoupons() throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "select * from coupons";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<Coupon> coupons = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                int companyID = rs.getInt(2);
                Category category = Category.values()[rs.getInt(3)-1];
                String title = rs.getString(4);
                String description = rs.getString(5);
                Date startDate = rs.getDate(6);
                Date endDate = rs.getDate(7);
                int amount = rs.getInt(8);
                double price = rs.getDouble(9);
                String image = rs.getString(10);

                coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount, price, image));
            }
            return coupons;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves a single coupon from the database based on its unique identifier (coupon ID).
     *
     * @param couponID The unique identifier of the coupon to retrieve.
     * @return The Coupon object representing the retrieved coupon, or null if not found.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public Coupon getOneCoupon(int couponID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT * FROM coupons WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, couponID);
            ResultSet rs = statement.executeQuery();

            Coupon coupon = null;
            if (rs.next()) {
                int id = rs.getInt(1);
                int companyID = rs.getInt(2);
                Category category = Category.values()[rs.getInt(3)-1];
                String title = rs.getString(4);
                String description = rs.getString(5);
                Date startDate = rs.getDate(6);
                Date endDate = rs.getDate(7);
                int amount = rs.getInt(8);
                double price = rs.getDouble(9);
                String image = rs.getString(10);

                coupon = new Coupon(id, companyID, category, title, description, startDate, endDate, amount, price, image);
            }
            return coupon;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Records the purchase of a coupon by a customer in the system.
     *
     * This method inserts a record into the database to indicate that a customer has purchased a specific coupon.
     *
     * @param customerID The unique identifier (ID) of the customer making the purchase.
     * @param couponID   The unique identifier (ID) of the coupon being purchased.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public void addCouponPurchase(int customerID, int couponID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "insert into customers_vs_coupons(customer_id, coupon_id) values(?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            statement.setInt(2, couponID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Deletes a record of a coupon purchase by a customer from the system.
     *
     * This method removes the record that indicates a customer's purchase of a specific coupon from the database.
     *
     * @param customerID The unique identifier (ID) of the customer whose purchase record is being deleted.
     * @param couponID   The unique identifier (ID) of the coupon for which the purchase record is being deleted.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public void deleteCouponPurchase(int customerID, int couponID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "DELETE FROM customers_vs_coupons WHERE customer_id = ? AND coupon_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            statement.setInt(2, couponID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Deletes all purchase records associated with a specific coupon from the system.
     *
     * This method removes all purchase records that indicate customers' purchases of a specific coupon from the database.
     *
     * @param couponID The unique identifier (ID) of the coupon for which purchase records are being deleted.
     * @throws SQLException If there is an issue with the database operation.
     */
    public void deletePurchaseByCouponID(int couponID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "DELETE FROM customers_vs_coupons WHERE coupon_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, couponID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Deletes all purchase records associated with a specific customer from the system.
     *
     * This method removes all purchase records that indicate a specific customer's purchases of coupons from the database.
     *
     * @param customerID The unique identifier (ID) of the customer for whom purchase records are being deleted.
     * @throws SQLException If there is an issue with the database operation.
     */
    public void deletePurchaseByCustomerID(int customerID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "DELETE FROM customers_vs_coupons WHERE customer_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Deletes all coupons associated with a specific company from the system.
     *
     * This method removes all coupons that belong to a specific company from the database.
     *
     * @param companyID The unique identifier (ID) of the company for which coupons are being deleted.
     * @throws SQLException If there is an issue with the database operation.
     */
    public void deleteCouponsByCompanyID(int companyID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "DELETE FROM coupons WHERE company_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, companyID);
            statement.execute();
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Checks if a specific coupon is still in stock.
     *
     * This method queries the database to determine whether a specific coupon is still available in stock by checking its
     * current quantity. If the coupon's quantity is greater than zero, it is considered to be in stock; otherwise, it is
     * considered out of stock.
     *
     * @param couponID The unique identifier (ID) of the coupon to check.
     * @return True if the coupon is still in stock; otherwise, false.
     * @throws SQLException If there is an issue with the database operation.
     */
    public boolean stillInStock(int couponID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT amount FROM coupons WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, couponID);
            ResultSet rs = statement.executeQuery();

            if(rs.next())
                return rs.getInt(1) > 0;
            else
                return false;

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Checks if a coupon with the same title exists for a specific company.
     *
     * This method queries the database to determine if a coupon with the same title exists for a particular company.
     *
     * @param coupon The coupon object representing the title and company for which existence is checked.
     * @return True if a coupon with the same title exists for the specified company; otherwise, false.
     * @throws SQLException If there is an issue with the database operation.
     */
    public boolean titleInCompanyExists(Coupon coupon) throws SQLException{
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT id FROM coupons WHERE title = ? and company_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, coupon.getTitle());
            statement.setInt(2, coupon.getCompanyID());
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return true;
            else return false;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Checks if the end date of a specific coupon has already expired.
     *
     * This method queries the database to determine if the end date of a specific coupon has passed, indicating that the
     * coupon has expired.
     *
     * @param coupon The coupon object containing the coupon's ID and end date to check for expiration.
     * @return True if the coupon's end date has expired; otherwise, false.
     * @throws SQLException If there is an issue with the database operation.
     */
    public boolean dateExpired(Coupon coupon) throws SQLException {
        long currentTimeMillis = System.currentTimeMillis();
        java.sql.Date sqlDate = new java.sql.Date(currentTimeMillis);

        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT end_date FROM coupons WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, coupon.getId());
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return rs.getDate(1).before(sqlDate);
            else
                 return false;

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves all coupons associated with a specific company from the database.
     *
     * This method queries the database to fetch all coupons that belong to a particular company based on the provided
     * company ID.
     *
     * @param companyID The unique identifier (ID) of the company for which coupons are being retrieved.
     * @param connection A database connection used for executing the SQL query.
     * @return An ArrayList containing all coupons associated with the specified company.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCompanyCoupons(int companyID, Connection connection) throws SQLException {
        String sql = "SELECT * FROM coupons WHERE company_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, companyID);
        ResultSet rs = statement.executeQuery();

        ArrayList<Coupon> coupons = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            Category category = Category.values()[rs.getInt(3)-1];
            String title = rs.getString(4);
            String description = rs.getString(5);
            Date startDate = rs.getDate(6);
            Date endDate = rs.getDate(7);
            int amount = rs.getInt(8);
            double price = rs.getDouble(9);
            String image = rs.getString(10);

            coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount,price, image));
        }

        return coupons;
    }

    /**
     * Retrieves all coupons associated with a specific company by its unique identifier (ID).
     *
     * This method fetches all coupons that belong to a particular company based on the provided company ID.
     *
     * @param companyID The unique identifier (ID) of the company for which coupons are being retrieved.
     * @return An ArrayList containing all coupons associated with the specified company.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCompanyCouponsByID(int companyID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            return getCompanyCoupons(companyID, connection);
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves all coupons of a specific category associated with a company by its unique identifier (ID).
     *
     * This method fetches all coupons that belong to a particular category and a specific company based on the provided
     * company ID and category.
     *
     * @param companyID The unique identifier (ID) of the company for which coupons are being retrieved.
     * @param category The category of coupons to retrieve.
     * @return An ArrayList containing all coupons of the specified category associated with the specified company.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCompanyCouponsByCategory(int companyID, Category category) throws SQLException {
        Connection connection = connectionPool.getConnection();
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();

        try {
            String sql = "SELECT * FROM coupons WHERE company_id = ? and category_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, companyID);
            statement.setInt(2, couponsDBDAO.findCategoryID(category));
            ResultSet rs = statement.executeQuery();

            ArrayList<Coupon> coupons = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(4);
                String description = rs.getString(5);
                Date startDate = rs.getDate(6);
                Date endDate = rs.getDate(7);
                int amount = rs.getInt(8);
                double price = rs.getDouble(9);
                String image = rs.getString(10);

                coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount,price, image));
            }

            return coupons;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves all coupons associated with a company by its unique identifier (ID) with a price not exceeding a specified maximum.
     *
     * This method fetches all coupons that belong to a specific company and have a price less than or equal to the specified
     * maximum price.
     *
     * @param companyID The unique identifier (ID) of the company for which coupons are being retrieved.
     * @param maxPrice The maximum price limit for coupons to retrieve.
     * @return An ArrayList containing all coupons associated with the specified company within the specified price range.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCompanyCouponsUpToPrice(int companyID, double maxPrice) throws SQLException {
        Connection connection = connectionPool.getConnection();
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();

        try {
            String sql = "SELECT * FROM coupons WHERE company_id = ? and price <= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, companyID);
            statement.setDouble(2, maxPrice);
            ResultSet rs = statement.executeQuery();

            ArrayList<Coupon> coupons = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                Category category = Category.values()[rs.getInt(3)-1];
                String title = rs.getString(4);
                String description = rs.getString(5);
                Date startDate = rs.getDate(6);
                Date endDate = rs.getDate(7);
                int amount = rs.getInt(8);
                double price = rs.getDouble(9);
                String image = rs.getString(10);

                coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount,price, image));
            }

            return coupons;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves all coupons associated with a customer by their unique identifier (ID).
     *
     * This method fetches all coupons that have been purchased by a specific customer based on the provided customer ID.
     *
     * @param customerID The unique identifier (ID) of the customer for whom coupons are being retrieved.
     * @param connection A database connection used for retrieving coupon data.
     * @return An ArrayList containing all coupons associated with the specified customer.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCustomerCoupons(int customerID, Connection connection) throws SQLException {
        String sql = "SELECT * FROM customers_vs_coupons" +
                " JOIN coupons ON coupon_id = coupons.id" +
                " WHERE customer_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, customerID);
        ResultSet rs = statement.executeQuery();

        ArrayList<Coupon> coupons = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(3);
            int companyID = rs.getInt(4);
            Category category = Category.values()[rs.getInt(5)-1];
            String title = rs.getString(6);
            String description = rs.getString(7);
            Date startDate = rs.getDate(8);
            Date endDate = rs.getDate(9);
            int amount = rs.getInt(10);
            double price = rs.getDouble(11);
            String image = rs.getString(12);

            coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount,price, image));
        }

        return coupons;
    }

    /**
     * Retrieves all coupons associated with a customer by their unique identifier (ID).
     *
     * This method fetches all coupons that have been purchased by a specific customer based on the provided customer ID.
     *
     * @param customerID The unique identifier (ID) of the customer for whom coupons are being retrieved.
     * @return An ArrayList containing all coupons associated with the specified customer.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCustomerCouponsByID(int customerID) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            return getCustomerCoupons(customerID, connection);
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves coupons of a specific category associated with a customer by their unique identifier (ID).
     *
     * This method fetches all coupons of a specified category that have been purchased by a specific customer based on the provided customer ID.
     *
     * @param customerID The unique identifier (ID) of the customer for whom coupons are being retrieved.
     * @param category The category of coupons to retrieve.
     * @return An ArrayList containing coupons of the specified category associated with the specified customer.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCustomerCouponsByCategory(int customerID, Category category) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT * FROM customers_vs_coupons" +
                    " JOIN coupons ON coupon_id = coupons.id" +
                    " WHERE customer_id = ? and category_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            statement.setInt(2, findCategoryID(category));
            ResultSet rs = statement.executeQuery();

            ArrayList<Coupon> coupons = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(3);
                int companyID = rs.getInt(4);
                String title = rs.getString(6);
                String description = rs.getString(7);
                Date startDate = rs.getDate(8);
                Date endDate = rs.getDate(9);
                int amount = rs.getInt(10);
                double price = rs.getDouble(11);
                String image = rs.getString(12);

                coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount,price, image));
            }

            return coupons;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    /**
     * Retrieves coupons purchased by a customer up to a specified maximum price.
     *
     * This method retrieves coupons that have been purchased by a specific customer and have a price less than or equal to the provided maximum price.
     *
     * @param customerID The unique identifier (ID) of the customer for whom coupons are being retrieved.
     * @param maxPrice The maximum price limit for the coupons to be retrieved.
     * @return An ArrayList containing coupons purchased by the specified customer up to the maximum price.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCustomerCouponsUpToPrice(int customerID, double maxPrice) throws SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            String sql = "SELECT * FROM customers_vs_coupons" +
                    " JOIN coupons ON coupon_id = coupons.id" +
                    " WHERE customer_id = ? and price <= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerID);
            statement.setDouble(2, maxPrice);
            ResultSet rs = statement.executeQuery();

            ArrayList<Coupon> coupons = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(3);
                int companyID = rs.getInt(4);
                Category category = Category.values()[rs.getInt(5)-1];
                String title = rs.getString(6);
                String description = rs.getString(7);
                Date startDate = rs.getDate(8);
                Date endDate = rs.getDate(9);
                int amount = rs.getInt(10);
                double price = rs.getDouble(11);
                String image = rs.getString(12);

                coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount,price, image));
            }

            return coupons;
        } finally {
            connectionPool.restoreConnection(connection);
        }
    }
}

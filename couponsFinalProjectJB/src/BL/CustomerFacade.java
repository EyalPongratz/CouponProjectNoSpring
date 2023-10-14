package BL;

import Exceptions.*;
import javaBeans.Category;
import javaBeans.Coupon;
import javaBeans.Customer;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerFacade extends ClientFacade{
    private int customerID;

    public CustomerFacade() throws SQLException {
    }

    /**
     * Gets the unique identifier (ID) of the customer associated with this object.
     *
     * @return The unique identifier (ID) of the customer.
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Logs in a customer with the specified email and password.
     *
     * This method attempts to authenticate a customer by their email and password.
     * If successful, it sets the customer ID associated with the customer.
     *
     * @param email    The email address of the customer.
     * @param password The password of the customer.
     * @return True if the login is successful; false otherwise.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public boolean login(String email, String password) throws SQLException {
        customerID = customerDBDAO.getID(email, password);
        if (customerID == -1)
            return false;
        else
            return true;
    }

    /**
     * Purchases a coupon for the logged-in customer.
     *
     * This method allows a logged-in customer to purchase a coupon. It performs checks to ensure the purchase is valid:
     * - Checks if the customer has already purchased the coupon and throws an AlreadyPurchasedException if true.
     * - Checks if the coupon is still in stock and throws an OutOfStockException if not.
     * - Checks if the coupon's expiration date has passed and throws a DateExpiredException if true.
     *
     * If all checks pass, the method adds the purchase to the database, updates the coupon's amount, and records the purchase.
     *
     * @param coupon The Coupon object to be purchased.
     * @throws AlreadyPurchasedException If the customer has already purchased the coupon.
     * @throws OutOfStockException If the coupon is out of stock.
     * @throws DateExpiredException If the coupon's expiration date has passed.
     * @throws Exception If there is an issue with the database operation.
     */
    public void purchaseCoupon(Coupon coupon) throws Exception {
        if (customerDBDAO.alreadyPurchased(customerID, coupon.getId()))
            throw new AlreadyPurchasedException();
        else if (!couponsDBDAO.stillInStock(coupon.getId()))
            throw new OutOfStockException();
        else if (couponsDBDAO.dateExpired(coupon))
            throw new DateExpiredException();
        else {
            couponsDBDAO.addCouponPurchase(customerID, coupon.getId());
            coupon.setAmount(coupon.getAmount()-1);
            couponsDBDAO.updateCoupon(coupon);
        }
    }

    /**
     * Retrieves a list of coupons associated with the logged-in customer.
     *
     * This method retrieves and returns a list of coupons that are associated with the customer currently logged in.
     *
     * @return An ArrayList containing coupons associated with the logged-in customer.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCustomerCoupons() throws SQLException {
        return couponsDBDAO.getCustomerCouponsByID(customerID);
    }

    /**
     * Retrieves a list of coupons associated with the logged-in customer and a specific category.
     *
     * This method retrieves and returns a list of coupons that are associated with the customer currently logged in
     * and belong to the specified category.
     *
     * @param category The category of coupons to retrieve.
     * @return An ArrayList containing coupons associated with the logged-in customer and the specified category.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCustomerCoupons(Category category) throws SQLException {
        return couponsDBDAO.getCustomerCouponsByCategory(customerID, category);
    }

    /**
     * Retrieves a list of coupons associated with the logged-in customer up to a specified maximum price.
     *
     * This method retrieves and returns a list of coupons that are associated with the customer currently logged in
     * and have a price up to the specified maximum price.
     *
     * @param maxPrice The maximum price for the coupons to retrieve.
     * @return An ArrayList containing coupons associated with the logged-in customer up to the specified maximum price.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCustomerCoupons(double maxPrice) throws SQLException {
        return couponsDBDAO.getCustomerCouponsUpToPrice(customerID, maxPrice);
    }

    /**
     * Retrieves details of the logged-in customer from the system.
     *
     * This method retrieves and returns detailed information about the customer that is currently logged in.
     *
     * @return The Customer object representing the details of the logged-in customer.
     * @throws SQLException If there is an issue with the database operation.
     */
    public Customer getCustomerDetails() throws SQLException {
        return customerDBDAO.getOneCustomer(customerID);
    }
}

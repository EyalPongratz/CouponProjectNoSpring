package BL;

import Exceptions.AlreadyExistsException;
import Exceptions.FieldNotMutableException;
import Exceptions.NoSuchCompanyException;
import Exceptions.NoSuchCouponException;
import javaBeans.Category;
import javaBeans.Company;
import javaBeans.Coupon;

import java.sql.SQLException;
import java.util.ArrayList;

public class CompanyFacade extends ClientFacade{

    private int companyID;
    public CompanyFacade() throws SQLException {
    }

    /**
     * Gets the unique identifier (ID) of the company associated with this object.
     *
     * @return The unique identifier (ID) of the company.
     */
    public int getCompanyID() {
        return companyID;
    }

    /**
     * Logs in a user with the specified email and password.
     *
     * This method attempts to authenticate a user by their email and password.
     * If successful, it sets the company ID associated with the user.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return True if the login is successful; false otherwise.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public boolean login(String email, String password) throws SQLException {
        companyID = companiesDBDAO.getID(email, password);
        if (companyID == -1)
            return false;
        else
            return true;
    }

    /**
     * Adds a new coupon to the system for the associated company.
     *
     * This method checks if a coupon with the same title already exists for the associated company.
     * If a coupon with the same title exists, it throws an AlreadyExistsException specifying the title and company.
     * Otherwise, it adds the new coupon to the database.
     *
     * @param coupon The Coupon object to be added.
     * @throws AlreadyExistsException If a coupon with the same title exists for this company.
     * @throws Exception If there is an issue with the database operation.
     */
    public void addCoupon(Coupon coupon) throws Exception {
        if (couponsDBDAO.titleInCompanyExists(coupon))
            throw new AlreadyExistsException(coupon.getTitle(), "title, for this company");
        else
            couponsDBDAO.addCoupon(coupon);
    }

    /**
     * Updates an existing coupon's information in the system.
     *
     * This method first checks if the coupon with the specified ID exists in the system.
     * If the coupon does not exist, it throws a NoSuchCouponException.
     * If the provided coupon's associated company ID differs from the existing one, it throws a FieldNotMutableException.
     * Otherwise, it updates the coupon's information in the database.
     *
     * @param coupon The updated Coupon object with the new information.
     * @throws NoSuchCouponException   If the coupon with the specified ID does not exist.
     * @throws FieldNotMutableException If the associated company ID is changed (immutable field).
     * @throws Exception               If there is an issue with the database operation.
     */
    public void updateCoupon(Coupon coupon) throws Exception {
        Coupon copy = couponsDBDAO.getOneCoupon(coupon.getId());
        if (copy == null)
            throw new NoSuchCouponException(coupon.getId());
        else if (coupon.getCompanyID() != copy.getCompanyID())
            throw new FieldNotMutableException("company_id");
        else
            couponsDBDAO.updateCoupon(coupon);
    }

    /**
     * Deletes a coupon from the system, including its associated purchases.
     *
     * This method first checks if the coupon with the specified ID exists in the system.
     * If the coupon does not exist, it throws a NoSuchCouponException.
     * Otherwise, it proceeds to delete all purchases associated with the coupon.
     * Finally, it deletes the coupon itself from the database.
     *
     * @param couponID The unique identifier (ID) of the coupon to be deleted.
     * @throws NoSuchCouponException If the coupon with the specified ID does not exist.
     * @throws Exception If there is an issue with the database operation.
     */
    public void deleteCoupon(int couponID) throws Exception {
        Coupon copy = couponsDBDAO.getOneCoupon(couponID);
        if (copy == null)
            throw new NoSuchCouponException(couponID);
        else {
            couponsDBDAO.deletePurchaseByCouponID(couponID);
            couponsDBDAO.deleteCoupon(couponID);
        }
    }

    /**
     * Retrieves a list of coupons associated with the current company.
     *
     * This method retrieves and returns a list of coupons that are associated with the company currently logged in.
     *
     * @return An ArrayList containing coupons associated with the current company.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCompanyCoupons() throws SQLException {
        return couponsDBDAO.getCompanyCouponsByID(companyID);
    }

    /**
     * Retrieves a list of coupons associated with the current company and a specific category.
     *
     * This method retrieves and returns a list of coupons that are associated with the company currently logged in
     * and belong to the specified category.
     *
     * @param category The category of coupons to retrieve.
     * @return An ArrayList containing coupons associated with the current company and the specified category.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCompanyCoupons(Category category) throws SQLException {
        return couponsDBDAO.getCompanyCouponsByCategory(companyID, category);
    }

    /**
     * Retrieves a list of coupons associated with the current company up to a specified maximum price.
     *
     * This method retrieves and returns a list of coupons that are associated with the company currently logged in
     * and have a price up to the specified maximum price.
     *
     * @param maxPrice The maximum price for the coupons to retrieve.
     * @return An ArrayList containing coupons associated with the current company up to the specified maximum price.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Coupon> getCompanyCoupons(double maxPrice) throws SQLException {
        return couponsDBDAO.getCompanyCouponsUpToPrice(companyID, maxPrice);
    }

    /**
     * Retrieves details of the current company from the system.
     *
     * This method retrieves and returns detailed information about the company that is currently logged in.
     *
     * @return The Company object representing the details of the current company.
     * @throws SQLException If there is an issue with the database operation.
     */
    public Company getCompanyDetails() throws SQLException {
        return companiesDBDAO.getOneCompany(companyID);
    }
}

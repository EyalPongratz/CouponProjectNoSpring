package BL;

import Exceptions.AlreadyExistsException;
import Exceptions.FieldNotMutableException;
import Exceptions.NoSuchCompanyException;
import Exceptions.NoSuchCustomerException;
import javaBeans.Company;
import javaBeans.Coupon;
import javaBeans.Customer;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminFacade extends ClientFacade{
    public AdminFacade() throws SQLException {
    }

    /**
     * Logs in a user with the specified email and password.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return True if the login is successful; false otherwise.
     * @throws SQLException If there is an issue with the database operation.
     */
    @Override
    public boolean login(String email, String password) throws SQLException {
        return email.equals("admin@admin.com") && password.equals("admin");
    }

    /**
     * Adds a new company to the system.
     *
     * This method checks if a company with the same name or email already exists in the system.
     * If a company with the same name exists, it throws an AlreadyExistsException with the name of the existing company.
     * If a company with the same email exists, it throws an AlreadyExistsException with the email of the existing company.
     * Otherwise, it adds the new company to the database.
     *
     * @param company The Company object to be added.
     * @throws AlreadyExistsException If a company with the same name or email already exists.
     * @throws Exception If there is an issue with the database operation.
     */
    public void addCompany(Company company) throws Exception {
        if (companiesDBDAO.nameAlreadyExists(company.getName()))
            throw new AlreadyExistsException(company.getName(), "name");
        else if (companiesDBDAO.emailAlreadyExists(company.getEmail()))
            throw new AlreadyExistsException(company.getEmail(), "email");
        else {
            companiesDBDAO.addCompany(company);
        }
    }

    /**
     * Updates an existing company's information in the system.
     *
     * This method first checks if the company with the specified ID exists in the system.
     * If the company does not exist, it throws a NoSuchCompanyException.
     * If the provided company's name or email differs from the existing one, it throws a FieldNotMutableException.
     * Otherwise, it updates the company's information in the database.
     *
     * @param company The updated Company object with the new information.
     * @throws NoSuchCompanyException If the company with the specified ID does not exist.
     * @throws FieldNotMutableException If the company's name or email is changed (immutable fields).
     * @throws Exception If there is an issue with the database operation.
     */
    public void updateCompany(Company company) throws Exception {
        Company copy = companiesDBDAO.getOneCompany(company.getId());
        if (copy == null)
            throw new NoSuchCompanyException(company.getId());
        else if(!company.getName().equals(copy.getName()))
            throw new FieldNotMutableException("company name");
        else if(!company.getEmail().equals(copy.getEmail()))
            throw new FieldNotMutableException("company email");
        else
            companiesDBDAO.updateCompany(company);
    }

    /**
     * Deletes a company from the system, including its associated coupons and purchases.
     *
     * This method first checks if the company with the specified ID exists in the system.
     * If the company does not exist, it throws a NoSuchCompanyException.
     * Otherwise, it proceeds to delete all coupons associated with the company and their purchases.
     * Finally, it deletes the company itself from the database.
     *
     * @param companyID The unique identifier (ID) of the company to be deleted.
     * @throws NoSuchCompanyException If the company with the specified ID does not exist.
     * @throws Exception If there is an issue with the database operation.
     */
    public void deleteCompany(int companyID) throws Exception{
        Company company = companiesDBDAO.getOneCompany(companyID);
        if (company == null)
            throw new NoSuchCompanyException(companyID);

        ArrayList<Coupon> coupons = couponsDBDAO.getCompanyCouponsByID(companyID);

        for (Coupon coupon: coupons) {
            couponsDBDAO.deletePurchaseByCouponID(coupon.getId());
        }
        couponsDBDAO.deleteCouponsByCompanyID(companyID);
        companiesDBDAO.deleteCompany(companyID);
    }

    /**
     * Retrieves a list of all companies registered in the system.
     *
     * This method retrieves and returns a list of all companies currently registered in the system.
     *
     * @return An ArrayList containing all registered companies.
     * @throws Exception If there is an issue with the database operation.
     */
    public ArrayList<Company> getAllCompanies() throws SQLException {
        return companiesDBDAO.getAllCompanies();
    }

    /**
     * Retrieves a single company from the system based on its unique identifier (company ID).
     *
     * This method retrieves and returns a single company from the system based on the provided company ID.
     * If no company with the specified ID is found, it throws a NoSuchCompanyException.
     *
     * @param companyID The unique identifier (ID) of the company to retrieve.
     * @return The Company object representing the retrieved company.
     * @throws NoSuchCompanyException If the company with the specified ID does not exist.
     * @throws Exception If there is an issue with the database operation.
     */
    public Company getOneCompany(int companyID) throws Exception {
        Company company = companiesDBDAO.getOneCompany(companyID);
        if (company == null)
            throw new NoSuchCompanyException(companyID);
        else
            return company;
    }

    /**
     * Adds a new customer to the system.
     *
     * This method checks if a customer with the same email address already exists in the system.
     * If a customer with the same email exists, it throws an AlreadyExistsException with the email of the existing customer.
     * Otherwise, it adds the new customer to the database.
     *
     * @param customer The Customer object to be added.
     * @throws AlreadyExistsException If a customer with the same email already exists.
     * @throws Exception If there is an issue with the database operation.
     */
    public void addCustomer(Customer customer) throws Exception {
        if (customerDBDAO.emailAlreadyExists(customer.getEmail()))
            throw new AlreadyExistsException(customer.getEmail(), "email");
        else
            customerDBDAO.addCustomer(customer);
    }

    /**
     * Updates an existing customer's information in the system.
     *
     * This method first checks if the customer with the specified ID exists in the system.
     * If the customer does not exist, it throws a NoSuchCustomerException.
     * Otherwise, it updates the customer's information in the database.
     *
     * @param customer The updated Customer object with the new information.
     * @throws NoSuchCustomerException If the customer with the specified ID does not exist.
     * @throws Exception If there is an issue with the database operation.
     */
    public void updateCustomer(Customer customer) throws Exception {
        Customer copy = customerDBDAO.getOneCustomer(customer.getId());
        if (copy == null)
            throw new NoSuchCustomerException(customer.getId());
        else
            customerDBDAO.updateCustomer(customer);
    }

    /**
     * Deletes a customer from the system, including their associated coupon purchases.
     *
     * This method first checks if the customer with the specified ID exists in the system.
     * If the customer does not exist, it throws a NoSuchCustomerException.
     * Otherwise, it proceeds to delete all coupon purchases associated with the customer.
     * Finally, it deletes the customer itself from the database.
     *
     * @param customerID The unique identifier (ID) of the customer to be deleted.
     * @throws NoSuchCustomerException If the customer with the specified ID does not exist.
     * @throws Exception If there is an issue with the database operation.
     */
    public void deleteCustomer(int customerID) throws Exception{
        Customer copy = customerDBDAO.getOneCustomer(customerID);
        if (copy == null)
            throw new NoSuchCustomerException(customerID);
        else {
            couponsDBDAO.deletePurchaseByCustomerID(customerID);
            customerDBDAO.deleteCustomer(customerID);
        }
    }

    /**
     * Retrieves a list of all customers registered in the system.
     *
     * This method retrieves and returns a list of all customers currently registered in the system.
     *
     * @return An ArrayList containing all registered customers.
     * @throws SQLException If there is an issue with the database operation.
     */
    public ArrayList<Customer> getAllCustomers() throws SQLException {
        return customerDBDAO.getAllCustomers();
    }

    /**
     * Retrieves a single customer from the system based on their unique identifier (customer ID).
     *
     * This method retrieves and returns a single customer from the system based on the provided customer ID.
     * If no customer with the specified ID is found, it throws a NoSuchCustomerException.
     *
     * @param customerID The unique identifier (ID) of the customer to retrieve.
     * @return The Customer object representing the retrieved customer.
     * @throws NoSuchCustomerException If the customer with the specified ID does not exist.
     * @throws Exception If there is an issue with the database operation.
     */
    public Customer getOneCustomer(int customerID) throws Exception {
        Customer customer = customerDBDAO.getOneCustomer(customerID);
        if (customer == null)
            throw new NoSuchCustomerException(customerID);
        else
            return customer;
    }
}

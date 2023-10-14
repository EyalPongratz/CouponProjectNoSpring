package BL;

import DAOs.CompaniesDBDAO;
import DAOs.CouponsDBDAO;
import DAOs.CustomerDBDAO;

import java.sql.SQLException;

public abstract class ClientFacade {
    protected CompaniesDBDAO companiesDBDAO;
    protected CouponsDBDAO couponsDBDAO;
    protected CustomerDBDAO customerDBDAO;

    protected ClientFacade() throws SQLException {
        this.companiesDBDAO = new CompaniesDBDAO();
        this.couponsDBDAO = new CouponsDBDAO();
        this.customerDBDAO = new CustomerDBDAO();
    }

    public abstract boolean login(String email, String password) throws SQLException;
}

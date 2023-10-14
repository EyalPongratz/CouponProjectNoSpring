package BL;

import Connections.ConnectionPool;
import Exceptions.InvalidCredentialsException;

/**
 * a class used to manage login and return the relevant facade
 */
public class LoginManger {
    private static LoginManger instance;

    private LoginManger() {
    }

    public static LoginManger getInstance() {
        if (instance == null)
            instance = new LoginManger();
        return instance;
    }

    /**
     * Logs in a user with the specified email, password, and client type.
     *
     * This method allows a user to log in based on their email, password, and client type. Depending on the client type,
     * it creates and returns an appropriate facade object (AdminFacade, CompanyFacade, or CustomerFacade) if the login is successful.
     * If the login fails, it throws an InvalidCredentialsException.
     *
     * @param email      The email address of the user.
     * @param password   The password of the user.
     * @param clientType The type of client (e.g., Administrator, Company, Customer).
     * @return A ClientFacade object representing the logged-in user.
     * @throws InvalidCredentialsException If the login credentials are invalid.
     * @throws Exception                  If there is an issue with the login process.
     */
    public ClientFacade login(String email, String password, ClientType clientType) throws Exception {
        if (clientType.equals(ClientType.ADMINISTRATOR)) {
            AdminFacade facade = new AdminFacade();
            if (facade.login(email, password))
                return facade;
            else
                throw new InvalidCredentialsException();
        } else if (clientType.equals(ClientType.COMPANY)) {
            CompanyFacade facade = new CompanyFacade();
            if (facade.login(email, password))
                return facade;
            else
                throw new InvalidCredentialsException();
        } else if (clientType.equals(ClientType.CUSTOMER)) {
            CustomerFacade facade = new CustomerFacade();
            if (facade.login(email, password))
                return facade;
            else
                throw new InvalidCredentialsException();
        } else
            throw new InvalidCredentialsException();
    }
}

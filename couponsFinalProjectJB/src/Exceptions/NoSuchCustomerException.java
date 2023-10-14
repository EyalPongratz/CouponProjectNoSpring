package Exceptions;

public class NoSuchCustomerException extends Exception{
    public NoSuchCustomerException(int id) {
        super("No customer exists under id: " + id);
    }
}

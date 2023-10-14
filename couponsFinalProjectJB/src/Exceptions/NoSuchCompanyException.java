package Exceptions;

public class NoSuchCompanyException extends Exception{
    public NoSuchCompanyException(int id) {
        super("No company exists under id: " + id);
    }
}

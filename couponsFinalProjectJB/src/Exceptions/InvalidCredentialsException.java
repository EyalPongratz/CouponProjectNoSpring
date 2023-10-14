package Exceptions;

public class InvalidCredentialsException extends Exception{
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}

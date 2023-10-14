package Exceptions;

public class AlreadyExistsException extends Exception{
    public AlreadyExistsException(String value, String columnName) {
        super("the value: " + value + ", for column: " + columnName + ", already exists in database");
    }
}

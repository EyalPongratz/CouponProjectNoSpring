package Exceptions;

public class FieldNotMutableException extends Exception{
    public FieldNotMutableException(String fieldName) {
        super("The field: '" + fieldName + "' cannot be changed");
    }
}

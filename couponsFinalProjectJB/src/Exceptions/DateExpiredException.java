package Exceptions;

public class DateExpiredException extends Exception{
    public DateExpiredException() {
        super("The coupon you requested has expired");
    }
}

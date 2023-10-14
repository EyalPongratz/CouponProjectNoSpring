package Exceptions;

public class NoSuchCouponException extends Exception{
    public NoSuchCouponException(int id) {
        super("No coupon under id: " + id + " exists in database");
    }
}

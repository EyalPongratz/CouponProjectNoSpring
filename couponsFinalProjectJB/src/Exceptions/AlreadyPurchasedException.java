package Exceptions;

public class AlreadyPurchasedException extends Exception{
    public AlreadyPurchasedException() {
        super("This coupon has already been purchased by this customer");
    }
}

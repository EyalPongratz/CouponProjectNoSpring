package Exceptions;

public class OutOfStockException extends Exception{
    public OutOfStockException() {
        super("The coupon you requested is out of stock");
    }
}

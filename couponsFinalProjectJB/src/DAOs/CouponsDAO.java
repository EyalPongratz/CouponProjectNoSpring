package DAOs;

import javaBeans.Coupon;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CouponsDAO {
    void addCoupon(Coupon coupon) throws SQLException;
    void updateCoupon(Coupon coupon) throws SQLException;
    void deleteCoupon(int couponID) throws SQLException;
    ArrayList<Coupon> getAllCoupons() throws SQLException;
    Coupon getOneCoupon(int couponID) throws SQLException;
    void addCouponPurchase(int customerID, int couponID) throws SQLException;
    void deleteCouponPurchase(int customerID, int couponID) throws SQLException;
}

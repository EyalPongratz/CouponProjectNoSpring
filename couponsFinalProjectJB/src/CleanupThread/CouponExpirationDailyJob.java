package CleanupThread;

import DAOs.CouponsDBDAO;
import javaBeans.Coupon;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class CouponExpirationDailyJob implements Runnable{
    private CouponsDBDAO couponsDBDAO;
    private boolean quit = false;
    private final int DELAY = 1000*3600*24;


    public CouponExpirationDailyJob() throws Exception {
        couponsDBDAO = new CouponsDBDAO();
    }

    /**
     * Periodically checks and deletes expired coupons from the system.
     *
     * This method runs in a loop and periodically checks for expired coupons in the system. It retrieves all coupons
     * from the database, iterates through them, and deletes any coupons that have expired. The deletion process also
     * includes removing purchase records associated with expired coupons.
     *
     * @throws RuntimeException If there is an issue with the database operation or if the thread is interrupted.
     */
    @Override
    public void run() {
        while (!quit) {
            try {
                ArrayList<Coupon> coupons = couponsDBDAO.getAllCoupons();

                for (Coupon coupon: coupons) {
                    if (couponsDBDAO.dateExpired(coupon)) {
                        couponsDBDAO.deletePurchaseByCouponID(coupon.getId());
                        couponsDBDAO.deleteCoupon(coupon.getId());
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                sleep(DELAY);
            } catch (InterruptedException ignored) {}
        }
    }

    public void stop() {
        quit = true;
    }
}

package tests;

import BL.*;
import CleanupThread.CouponExpirationDailyJob;
import DAOs.CompaniesDBDAO;
import DAOs.CouponsDBDAO;
import DAOs.CustomerDBDAO;
import Exceptions.AlreadyExistsException;
import javaBeans.Category;
import javaBeans.Company;
import javaBeans.Coupon;
import javaBeans.Customer;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.sql.SQLException;
import java.util.ArrayList;

public class program {
    public static void main(String[] args) throws Exception {

        Test.testAll();
    }
}

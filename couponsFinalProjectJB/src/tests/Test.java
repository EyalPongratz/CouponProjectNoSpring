package tests;

import BL.*;
import CleanupThread.CouponExpirationDailyJob;
import DAOs.CouponsDBDAO;
import javaBeans.Category;
import javaBeans.Company;
import javaBeans.Coupon;
import javaBeans.Customer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Test {
    public static void testAll() {

        try {
            // Initialize the thread
            CouponExpirationDailyJob job = new CouponExpirationDailyJob();
            Thread thread = new Thread(job);
            thread.start();

            //logs in the admin
            LoginManger manger = LoginManger.getInstance();
            AdminFacade adminFacade = (AdminFacade) manger.login("admin@admin.com", "admin", ClientType.ADMINISTRATOR);

            //tests the facades
            testAdminFacade(adminFacade);
            testCompanyFacade(adminFacade);
            testCustomerFacade(adminFacade);

            job.stop();
            thread.interrupt();

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void testAdminFacade(AdminFacade adminFacade) throws Exception {
        Company company = new Company("toyota", "toyota@gmail.com", "bla", new ArrayList<>());
        adminFacade.addCompany(company);
        company.setPassword("yadda");
        adminFacade.updateCompany(company);
        adminFacade.deleteCompany(company.getId());

        ArrayList<Company> companies = adminFacade.getAllCompanies();
        System.out.println("showing all companies:\n");
        for (Company c: companies) {
            System.out.println(c + "\n");
        }
        System.out.println("showing one company\n");
        System.out.println(adminFacade.getOneCompany(2) + "\n");

        Customer customer = new Customer("Bill", "Black", "billblack@gmail.com", "billy", new ArrayList<>());
        adminFacade.addCustomer(customer);
        customer.setPassword("bob");
        adminFacade.updateCustomer(customer);
        adminFacade.deleteCustomer(customer.getId());

        System.out.println("showing all customers:\n");
        ArrayList<Customer> customers = adminFacade.getAllCustomers();
        for (Customer c: customers) {
            System.out.println(c + "\n");
        }
        System.out.println("showing one customer\n");
        System.out.println(adminFacade.getOneCustomer(1) + "\n");
    }

    public static void testCompanyFacade(AdminFacade adminFacade) throws Exception {
        Company company = new Company("toyota", "toyota@gmail.com", "bla", new ArrayList<>());
        adminFacade.addCompany(company);
        LoginManger manger = LoginManger.getInstance();
        CompanyFacade companyFacade = (CompanyFacade) manger.login(company.getEmail(), company.getPassword(), ClientType.COMPANY);
        Coupon coupon1 = new Coupon(company.getId(), Category.ELECTRICITY, "Electric car", "Vroom", generateDate("13/09/2023"), generateDate("19/09/2023"), 100, 10_000,"img");
        Coupon coupon2 = new Coupon(company.getId(), Category.VACATION, "car ride", "trip to the country", generateDate("13/09/2023"), generateDate("19/09/2023"), 100, 5_000,"img");
        companyFacade.addCoupon(coupon1);
        companyFacade.addCoupon(coupon2);
        coupon1.setAmount(200);
        companyFacade.updateCoupon(coupon1);

        ArrayList<Coupon> coupons = companyFacade.getCompanyCoupons();
        System.out.println("showing all company coupons\n");
        for (Coupon c: coupons) {
            System.out.println(c + "\n");
        }

        ArrayList<Coupon> priceCoupons = companyFacade.getCompanyCoupons(6000);
        System.out.println("showing all price coupons:\n");
        for (Coupon c: priceCoupons) {
            System.out.println(c + "\n");
        }

        ArrayList<Coupon> categoryCoupons = companyFacade.getCompanyCoupons(Category.ELECTRICITY);
        System.out.println("showing all category coupons:\n");
        for (Coupon c: categoryCoupons) {
            System.out.println(c + "\n");
        }

        System.out.println("showing company details:\n");
        System.out.println(companyFacade.getCompanyDetails() + "\n");

        companyFacade.deleteCoupon(coupon1.getId());
        adminFacade.deleteCompany(company.getId());
    }

    public static void testCustomerFacade(AdminFacade adminFacade) throws Exception {
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
        Coupon coupon1 = new Coupon(2, Category.FOOD, "double pizza", "yum", Test.generateDate("13/09/2023"), Test.generateDate("15/09/2024"), 100, 1000,"img");
        couponsDBDAO.addCoupon(coupon1);
        Coupon coupon2 = new Coupon(2, Category.VACATION, "trip", "to italy", Test.generateDate("13/09/2023"), Test.generateDate("15/09/2024"), 100, 10,"img");
        couponsDBDAO.addCoupon(coupon2);

        Customer customer = new Customer("Bruce", "Willis", "@gmail.com", "boom", new ArrayList<>());
        adminFacade.addCustomer(customer);

        LoginManger manger = LoginManger.getInstance();
        CustomerFacade customerFacade = (CustomerFacade) manger.login(customer.getEmail(), customer.getPassword(), ClientType.CUSTOMER);
        customerFacade.purchaseCoupon(coupon1);
        customerFacade.purchaseCoupon(coupon2);

        ArrayList<Coupon> coupons = customerFacade.getCustomerCoupons();
        System.out.println("showing all customer coupons:\n");
        for (Coupon c: coupons) {
            System.out.println(c + "\n");
        }

        ArrayList<Coupon> priceCoupons = customerFacade.getCustomerCoupons(100);
        System.out.println("showing all price coupons:\n");
        for (Coupon c: priceCoupons) {
            System.out.println(c + "\n");
        }

        ArrayList<Coupon> categoryCoupons = customerFacade.getCustomerCoupons(Category.FOOD);
        System.out.println("showing all category coupons:\n");
        for (Coupon c: categoryCoupons) {
            System.out.println(c + "\n");
        }

        System.out.println("showing customer details:\n");
        System.out.println(customerFacade.getCustomerDetails());

        adminFacade.deleteCustomer(customer.getId());
        couponsDBDAO.deleteCoupon(coupon1.getId());
        couponsDBDAO.deleteCoupon(coupon2.getId());
    }

    public static java.sql.Date generateDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date utilDate = null;
        java.sql.Date sqlDate = null;

        try {
            utilDate = dateFormat.parse(dateString);
            sqlDate = new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return sqlDate;
    }
}

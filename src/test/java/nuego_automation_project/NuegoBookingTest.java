package nuego_automation_project;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;

public class NuegoBookingTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private LoginPage loginPage;
    private HomePage homePage;
    private BusBookingPage bookingPage;
    private Filters filtersPage;
    private SelectSeatPoints seatPointsPage;
    private Review_Booking_Page reviewBookingPage;
    private Payment_Mode paymentModePage;

    // ✅ ExtentReports variables
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeSuite(alwaysRun = true)
    public void startReport() {
        // ✅ Initialize ExtentReports
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("NueGo Web Automation Report");
        sparkReporter.config().setReportName("NueGo Booking Flow Test Results");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Tester", "Sumedh Sonawane");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Browser", "Chrome");
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--allow-insecure-localhost");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Initialize page objects
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingPage = new BusBookingPage(driver);
        filtersPage = new Filters(driver);
        seatPointsPage = new SelectSeatPoints(driver);
        reviewBookingPage = new Review_Booking_Page(driver);
        paymentModePage = new Payment_Mode(driver);

        System.out.println("✅ Browser launched and setup completed");
    }

    // ---------------------- TEST CASES -----------------------------

    @Test(priority = 1, description = "Login to application using mobile and OTP")
    public void testLogin() {
        test = extent.createTest("Login Test", "Login to the application using mobile and OTP");
        try {
            driver.get("https://greencell-nuego-web.web.app/");
            test.info("Navigated to application URL");

            loginPage.login("7385109680", "1234");
            wait.until(ExpectedConditions.urlContains("home"));
            test.pass("✅ Login successful and navigated to Home Page");
        } catch (Exception e) {
            test.fail("❌ Login failed: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 2, description = "Handle homepage popups and search bus")
    public void testHomePageActions() {
        test = extent.createTest("Home Page Test", "Handle popups and search buses");
        try {
            homePage.closePopupIfPresent();
            homePage.searchBus("Agra", "Bassi");
            test.pass("✅ Bus search performed successfully (Agra → Bassi)");
        } catch (Exception e) {
            test.fail("❌ Home Page actions failed: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 3, description = "Apply filters: Timer, Boarding & Dropping points")
    public void testFilters() {
        test = extent.createTest("Filter Test", "Apply filters and verify results");
        try {
            String timer = filtersPage.selectTimer();
            test.info("⏰ Timer Applied: " + timer);

            filtersPage.clickBoardingPoint();
            String boardingSelected = filtersPage.selectBoardingCheckbox();
            test.info("🚌 Boarding Point selected: " + boardingSelected);

            filtersPage.clickDroppingPoint();
            String droppingSelected = filtersPage.selectDroppingCheckboxAndReset();
            test.info("📍 Dropping Point selected: " + droppingSelected);

            test.pass("✅ Filter test completed successfully");
        } catch (Exception e) {
            test.fail("❌ Filter test failed: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 4, description = "Select seat and move to payment page")
    public void testSeatSelectionAndBooking() {
        test = extent.createTest("Seat Selection Test", "Select seat and proceed to booking");
        try {
            bookingPage.scrollDownSmall();
            bookingPage.clickSeat();

            seatPointsPage.selectSeats("5B","6C","2D","7D");
            seatPointsPage.selectPickupPointByName("Eidgah Bus Stan...");
            seatPointsPage.selectDropPointByName("Bassi Chowk");
            seatPointsPage.clickBookAndPay();

            test.pass("Seat selection and booking flow completed successfully");
        } catch (Exception e) {
            test.fail("❌ Seat selection failed: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 5, description = "Complete review booking flow (Coupon + Wallet + Proceed)")
    public void testReviewBookingPage() {
        test = extent.createTest("Review Booking Test", "Apply coupon, use wallet, and proceed");
        try {
            reviewBookingPage.scrollToReviewSection();
            reviewBookingPage.clickCouponButton();
            reviewBookingPage.scrollCouponModal();
            reviewBookingPage.clickApplyCoupon();
            reviewBookingPage.clickAssuranceCheckbox();
            reviewBookingPage.clickWalletApply();
            reviewBookingPage.clickProceedToBook();

            test.pass("✅ Review Booking Page flow completed successfully");
        } catch (Exception e) {
            test.fail("❌ Review Booking flow failed: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 6, description = "Perform actions on Payment Mode Page (Steps 1–6)")
    public void testPaymentModePage() {
        test = extent.createTest("Payment Page Test", "Perform payment flow (NetBanking → Charged → Submit)");
        try {
            // Step 1–6 Execution
            paymentModePage.selectNetBanking();
            Thread.sleep(1000);

            paymentModePage.selectAxisBank();
            Thread.sleep(1000);

            paymentModePage.clickProceedToPay();
            Thread.sleep(2000);

            paymentModePage.clickTxnDropdown();
            Thread.sleep(1000);

            paymentModePage.selectChargedOption();
            Thread.sleep(1000);

            paymentModePage.clickSubmitButton();
            Thread.sleep(1000);

            test.pass("✅ Payment Mode flow completed successfully (Steps 1–6)");
        } catch (Exception e) {
            test.fail("❌ Payment Mode flow failed: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    // ---------------------- REPORTING -----------------------------

    @AfterMethod(alwaysRun = true)
    public void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "❌ Test Case Failed: " + result.getName());
            test.log(Status.FAIL, "Error: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "✅ Test Case Passed: " + result.getName());
        } else {
            test.log(Status.SKIP, "⚠️ Test Case Skipped: " + result.getName());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🧹 Browser closed after test execution");
        }
    }

    @AfterSuite(alwaysRun = true)
    public void endReport() {
        extent.flush();
        System.out.println("📊 Extent Report generated at: test-output/ExtentReport.html");
    }
}

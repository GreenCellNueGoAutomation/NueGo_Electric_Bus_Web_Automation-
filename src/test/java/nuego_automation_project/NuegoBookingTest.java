package nuego_automation_project;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.BaseTest;
import utils.ExtentReportManager;
import utils.ExtentListener;

import java.io.ByteArrayInputStream;
import java.time.Duration;

@Epic("NueGo Web Automation")
@Feature("Complete Booking Flow")
@Listeners(ExtentListener.class)
public class NuegoBookingTest extends BaseTest {

    private WebDriverWait wait;
    private static ExtentReports extent = ExtentReportManager.getReport();
    private static ExtentTest test;

    private LoginPage loginPage;
    private HomePage homePage;
    private BusBookingPage bookingPage;
    private Filters filtersPage;
    private SelectSeatPoints seatPointsPage;
    private Review_Booking_Page reviewBookingPage;
    private Payment_Mode paymentModePage;

    // ---------------------- SETUP -----------------------------
    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Initialize Page Objects
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingPage = new BusBookingPage(driver);
        filtersPage = new Filters(driver);
        seatPointsPage = new SelectSeatPoints(driver);
        reviewBookingPage = new Review_Booking_Page(driver);
        paymentModePage = new Payment_Mode(driver);

        System.out.println("✅ Browser launched and setup completed");
    }

    // ---------------------- TEST CASE 1: LOGIN -----------------------------
    @Test(priority = 1, description = "Login to application using mobile and OTP")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLogin() {
        test = extent.createTest("Login Test", "Login using mobile and OTP");
        try {
            Allure.step("Open NueGo website");
            driver.get("https://greencell-nuego-web.web.app/");
            System.out.println("🌐 Opened NueGo Website");

            Allure.step("Enter mobile number and OTP");
            loginPage.login("7385109680", "1234");
            System.out.println("📱 Mobile number and OTP entered");

            test.log(Status.PASS, "✅ Login successful and redirected to Home page");
            Allure.step("Login successful - Home page loaded");
        } catch (Exception e) {
            handleFailure("Login failed", e);
        }
    }

    // ---------------------- TEST CASE 2: HOME PAGE -----------------------------
    @Test(priority = 2, description = "Handle homepage popups and search bus")
    @Severity(SeverityLevel.NORMAL)
    @Story("Home Page Flow")
    public void testHomePageActions() {
        test = extent.createTest("Home Page Test", "Handle popups and search buses");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Enter Source']")));
            Thread.sleep(1500);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Enter Destination']")));
            Thread.sleep(1500);

            Allure.step("Close popup if present");
            homePage.closePopupIfPresent();
            System.out.println("🪟 Popup closed successfully (if visible)");

            Allure.step("Search bus from Agra to Bassi");
            homePage.searchBus("Agra", "Bassi");
            System.out.println("Searched for buses: Agra → Bassi");

            test.log(Status.PASS, "✅ Bus search performed successfully (Agra → Bassi)");
            Allure.step("Bus search successful");
        } catch (Exception e) {
            handleFailure("Home Page test failed", e);
        }
    }

    // ---------------------- TEST CASE 3: FILTERS -----------------------------
    @Test(priority = 3, dependsOnMethods = {"testHomePageActions"}, description = "Apply filters: Timer, Boarding & Dropping points")
    @Severity(SeverityLevel.MINOR)
    @Story("Bus Filtering")
    public void testFilters() {
        test = extent.createTest("Filter Test", "Apply filters and verify results");
        try {
            Allure.step("Select Timer");
            String timer = filtersPage.selectTimer();
            Allure.step("Timer selected: " + timer);

            Allure.step("Select Boarding Point");
            filtersPage.clickBoardingPoint();
            String boarding = filtersPage.selectBoardingCheckbox();
            Allure.step("Boarding Point: " + boarding);

            Allure.step("Select Dropping Point");
            filtersPage.clickDroppingPoint();
            String dropping = filtersPage.selectDroppingCheckboxAndReset();
            Allure.step("Dropping Point: " + dropping);

            test.log(Status.PASS, "✅ Filters applied successfully");
        } catch (Exception e) {
            handleFailure("Filter test failed", e);
        }
    }

    // ---------------------- TEST CASE 4: BUS BOOKING PAGE ACTIONS -----------------------------
    @Test(priority = 4, dependsOnMethods = {"testFilters"}, description = "Scroll and click seat on Bus Booking page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Bus Seat Visibility and Click Flow")
    public void testBusBookingPageActions() {
        test = extent.createTest("Bus Booking Page Test", "Scroll and click on seat");
        try {
            Allure.step("Scroll slightly to view seat area");
            bookingPage.scrollDownSmall();
            System.out.println("🧭 Page scrolled down slightly");

            Allure.step("Click on a seat button");
            bookingPage.clickSeat();
            System.out.println("💺 Seat clicked successfully on bus booking page");

            test.log(Status.PASS, "✅ BusBookingPage actions executed successfully");
            Allure.step("Bus booking page flow completed successfully");
        } catch (Exception e) {
            handleFailure("Bus Booking Page test failed", e);
        }
    }

    // ---------------------- TEST CASE 5: SELECT SEATS -----------------------------
    @Test(priority = 5, description = "Select seat, pickup & drop points and click Book & Pay")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Seat Selection & Booking")
    public void testSelectSeatAndProceedToPay() {
        test = extent.createTest("Seat Selection Test", "Select seats and proceed to payment");
        try {
            Allure.step("Select preferred seats");
            seatPointsPage.selectSeats("5B", "6C", "2D", "7D");

            Allure.step("Select pickup point");
            seatPointsPage.selectPickupPoint();

            Allure.step("Select drop point");
            seatPointsPage.selectDropPoint();

            Allure.step("Click Book & Pay button");
            seatPointsPage.clickBookAndPay();

            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("review") || currentUrl.contains("payment"),
                    "Expected to navigate to review or payment page.");

            test.log(Status.PASS, "✅ Seat selection and Book & Pay successful");
            Allure.step("Seat selection and payment navigation successful");
        } catch (Exception e) {
            handleFailure("Seat selection and payment test failed", e);
        }
    }

    // ---------------------- TEST CASE 6: REVIEW BOOKING -----------------------------
    @Test(priority = 6, description = "Verify Review Booking full flow actions (coupon + passenger + wallet + proceed)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Review Booking Page Actions")
    @Description("Execute complete Review Booking sequence including coupon apply, passenger selection, assurance, wallet apply, and proceed to booking.")
    public void testReviewBookingFlow() {
        test = extent.createTest("Review Booking Flow", "Full booking flow on review page");
        try {
            Allure.step("Execute full review booking flow sequence");
            reviewBookingPage.clickApplyCoupon();  // ✅ Handles all steps internally

            test.log(Status.PASS, "✅ Review Booking full flow completed successfully");
            Allure.step("Review Booking flow executed successfully");
        } catch (Exception e) {
            handleFailure("Review Booking flow failed", e);
        }
    }

    // ---------------------- TEST CASE 7: PAYMENT FLOW -----------------------------
    @Test(priority = 7, dependsOnMethods = {"testReviewBookingFlow"}, description = "Complete payment flow using NetBanking - Axis Bank")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Flow")
    @Description("Verify user can complete payment using Net Banking (Axis Bank) and submit transaction as CHARGED.")
    public void testPaymentFlow() {
        test = extent.createTest("Payment Flow Test", "Complete payment using NetBanking (Axis Bank)");
        try {
            Allure.step("Select Net Banking option");
            paymentModePage.selectNetBanking();

            Allure.step("Select Axis Bank");
            paymentModePage.selectAxisBank();

            Allure.step("Click Proceed to Pay");
            paymentModePage.clickProceedToPay();

            Allure.step("Select Transaction Status");
            paymentModePage.clickTxnDropdown();
            paymentModePage.selectChargedOption();

            Allure.step("Submit transaction");
            paymentModePage.clickSubmitButton();

            test.log(Status.PASS, "✅ Payment flow completed successfully with 'CHARGED' status");
            Allure.step("Payment flow completed successfully");
        } catch (Exception e) {
            handleFailure("Payment Flow failed", e);
        }
    }

    // ---------------------- SCREENSHOT & FAILURE HANDLER -----------------------------
    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Step("Handle failure and capture screenshot")
    public void handleFailure(String message, Exception e) {
        try {
            if (test != null) {
                test.log(Status.FAIL, message + " ❌");
                test.log(Status.FAIL, e.getMessage());
            }

            System.out.println("❌ " + message + ": " + e.getMessage());

            // Attach screenshot in Allure
            Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(takeScreenshot()));

            // Mark test as failed
            Assert.fail(message + ": " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("⚠️ Error while handling failure: " + ex.getMessage());
        }
    }

    // ---------------------- CLEANUP -----------------------------
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🧹 Browser closed successfully");
        }
        extent.flush();
        System.out.println("📊 Extent report flushed successfully");
    }
}

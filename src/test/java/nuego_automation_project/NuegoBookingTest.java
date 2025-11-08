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
import utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.Base64;

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
       // options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");

        // 🧩 Detect Jenkins environment for full resolution
        String jenkinsEnv = System.getenv("JENKINS_HOME");
        if (jenkinsEnv != null) {
            System.out.println("🧠 Detected Jenkins environment — using headless full HD mode");
            // options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            System.out.println("💻 Local execution — starting Chrome in visible maximized mode");
            options.addArguments("--start-maximized");
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            System.out.println("⚠️ Maximize failed, setting manual resolution.");
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingPage = new BusBookingPage(driver);
        filtersPage = new Filters(driver);
        seatPointsPage = new SelectSeatPoints(driver);
        reviewBookingPage = new Review_Booking_Page(driver);
        paymentModePage = new Payment_Mode(driver);

        System.out.println("✅ Browser launched successfully (" +
                (jenkinsEnv != null ? "Jenkins Headless Mode" : "Visible Mode") + ")");
    }

    // ---------------------- TEST CASE 1: LOGIN -----------------------------
    @Test(priority = 1, description = "Login to application using mobile and OTP")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLogin() {
        test = extent.createTest("Login Test", "Login using mobile and OTP");
        try {
            driver.get("https://greencell-nuego-web.web.app/");
            System.out.println("Opened NueGo Website");

            loginPage.login("7385109680", "1234");
            test.log(Status.PASS, "Login successful and redirected to Home page ✅");
        } catch (Exception e) {
            handleFailure("Login failed", e);
        }
    }

    // ---------------------- TEST CASE 2: HOME PAGE -----------------------------
    @Test(priority = 2, dependsOnMethods = {"testLogin"}, description = "Handle homepage popups and search bus")
    @Severity(SeverityLevel.NORMAL)
    @Story("Home Page Flow")
    public void testHomePageActions() {
        test = extent.createTest("Home Page Test", "Handle popups and search buses");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Enter Source']")));
            homePage.closePopupIfPresent();
            homePage.searchBus("Agra", "Bassi");
            test.log(Status.PASS, "Bus search performed successfully (Agra → Bassi) ✅");
        } catch (Exception e) {
            handleFailure("Home Page test failed", e);
        }
    }

    // ---------------------- TEST CASE 3: FILTERS -----------------------------
    @Test(priority = 3, dependsOnMethods = {"testHomePageActions"}, description = "Apply filters")
    @Severity(SeverityLevel.MINOR)
    @Story("Bus Filtering")
    public void testFilters() {
        test = extent.createTest("Filter Test", "Apply filters and verify results");
        try {
            filtersPage.selectTimer();
            filtersPage.clickBoardingPoint();
            filtersPage.selectBoardingCheckbox();
            filtersPage.clickDroppingPoint();
            filtersPage.selectDroppingCheckboxAndReset();
            test.log(Status.PASS, "Filters applied successfully ✅");
        } catch (Exception e) {
            handleFailure("Filter test failed", e);
        }
    }

    // ---------------------- TEST CASE 4: BUS BOOKING -----------------------------
    @Test(priority = 4, dependsOnMethods = {"testFilters"}, description = "Scroll and click seat on Bus Booking page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Bus Seat Visibility and Click Flow")
    public void testBusBookingPageActions() {
        test = extent.createTest("Bus Booking Page Test", "Scroll and click on seat");
        try {
            bookingPage.scrollDownSmall();
            bookingPage.clickSeat();
            test.log(Status.PASS, "BusBookingPage actions executed successfully ✅");
        } catch (Exception e) {
            handleFailure("Bus Booking Page test failed", e);
        }
    }

    // ---------------------- TEST CASE 5: SELECT SEATS -----------------------------
    @Test(priority = 5, dependsOnMethods = {"testBusBookingPageActions"},description = "Select seat, pickup & drop points and click Book & Pay")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Seat Selection & Booking")
    public void testSelectSeatAndProceedToPay() {
        test = extent.createTest("Seat Selection Test", "Select seats and proceed to payment");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id*='seat']")));
            Thread.sleep(1000);

            seatPointsPage.selectSeats("5B", "6C","2D","7D");	
            seatPointsPage.selectPickupPoint();
            seatPointsPage.selectDropPoint();
            seatPointsPage.clickBookAndPay();

            // ✅ Handle Discount Alert popup
            reviewBookingPage.handleDiscountPopup();

            WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            boolean navigated = false;

            try {
                WebElement reviewOrPaymentElement = pageWait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(),'Review Booking') or contains(text(),'Payment')]")
                ));
                navigated = reviewOrPaymentElement.isDisplayed();
            } catch (TimeoutException te) {
                System.out.println("⚠️ Timeout: Review or Payment page element not found within 20s");
            }

            if (navigated) {
                test.log(Status.PASS, "Seat selection and Book & Pay successful ✅");
            } else {
                String screenshotBase64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                test.log(Status.FAIL, "Did not reach Review or Payment page ❌")
                    .addScreenCaptureFromBase64String(screenshotBase64, "Navigation_Failure");
                Assert.fail("Expected to navigate to Review or Payment page, but element not found.");
            }

        } catch (Exception e) {
            handleFailure("Seat selection and payment test failed", e);
        }
    }

    // ---------------------- TEST CASE 6: REVIEW BOOKING -----------------------------
    @Test(priority = 6,dependsOnMethods={"testSelectSeatAndProceedToPay"} ,description = "Verify Review Booking flow actions")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Review Booking and Coupon Flow")
    public void testReviewBookingFlow() {
        test = extent.createTest("Review Booking Flow", "Full booking flow on review page");
        try {
            reviewBookingPage.clickApplyCoupon();
            test.log(Status.PASS, "Review Booking flow completed successfully ✅");
        } catch (Exception e) {
            handleFailure("Review Booking flow failed", e);
        }
    }

    // ---------------------- TEST CASE 7: PAYMENT FLOW -----------------------------
    @Test(priority = 7, dependsOnMethods = {"testReviewBookingFlow"}, description = "Complete payment flow using NetBanking - Axis Bank")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Flow")
    public void testPaymentFlow() {
        test = extent.createTest("Payment Flow Test", "Complete payment using NetBanking (Axis Bank)");
        try {
            paymentModePage.selectNetBanking();
            paymentModePage.selectAxisBank();
            paymentModePage.clickProceedToPay();
            paymentModePage.clickTxnDropdown();
            paymentModePage.selectChargedOption();
            paymentModePage.clickSubmitButton();
            test.log(Status.PASS, "Payment flow completed successfully with 'CHARGED' status ✅");
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
            String webError = CommonUtils.captureErrorMessage(driver);
            String screenshotBase64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

            if (test != null) {
                test.log(Status.FAIL, message + " ❌");
                if (webError != null)
                    test.log(Status.FAIL, "Web error message: " + webError);
                test.log(Status.FAIL, e.getMessage())
                    .addScreenCaptureFromBase64String(screenshotBase64, "Error Screenshot");
            }

            System.out.println("❌ " + message + ": " + e.getMessage());
            Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(takeScreenshot()));
            Assert.fail(message + ": " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("Error while handling failure: " + ex.getMessage());
        }
    }

    // ---------------------- CLEANUP -----------------------------
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed successfully");
        }
        extent.flush();
        System.out.println("Extent report flushed successfully");
    }
}

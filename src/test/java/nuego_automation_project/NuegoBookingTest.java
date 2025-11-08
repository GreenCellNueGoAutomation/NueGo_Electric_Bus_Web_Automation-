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
import utils.*;

import java.io.*;
import java.nio.file.*;
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
    
    private static final String REPORT_DIR = System.getProperty("user.dir") +"/target/ExtentReports";
            


    private static final String SCREENSHOT_DIR = REPORT_DIR + "/screenshots";

    // ---------------------- SETUP -----------------------------
    @BeforeClass(alwaysRun = true)
    public void setUp() {
        // Handle IOException inside method
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create screenshot directory: " + e.getMessage());
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--disable-notifications",
                "--ignore-certificate-errors",
                "--disable-blink-features=AutomationControlled",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--remote-allow-origins=*"
        );

        String jenkinsEnv = System.getenv("JENKINS_HOME");
        if (jenkinsEnv != null) {
            System.out.println("🧠 Jenkins detected — running Chrome in non-headless mode");
            options.addArguments("--start-maximized");  // visible Chrome in Jenkins
        } else {
            System.out.println("💻 Local execution — Chrome visible mode");
            options.addArguments("--start-maximized");
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingPage = new BusBookingPage(driver);
        filtersPage = new Filters(driver);
        seatPointsPage = new SelectSeatPoints(driver);
        reviewBookingPage = new Review_Booking_Page(driver);
        paymentModePage = new Payment_Mode(driver);
    }

    // ---------------------- LOGIN TEST -----------------------------
    @Test(priority = 1, description = "Login to application using mobile and OTP")
    @Severity(SeverityLevel.CRITICAL)
    public void testLogin() {
        test = extent.createTest("Login Test", "Login using mobile and OTP");
        try {
            driver.get("https://greencell-nuego-web.web.app/");
            loginPage.login("7385109680", "1234");
            test.log(Status.PASS, "Login successful ✅");
        } catch (Exception e) {
            handleFailure("Login failed", e);
        }
    }

    // ---------------------- HOME PAGE TEST -----------------------------
    @Test(priority = 2, dependsOnMethods = {"testLogin"}, description = "Handle homepage popups and search bus")
    @Severity(SeverityLevel.NORMAL)
    public void testHomePageActions() {
        test = extent.createTest("Home Page Test", "Handle popups and search buses");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Enter Source']")));
            homePage.closePopupIfPresent();
            homePage.searchBus("Agra", "Bassi");
            test.log(Status.PASS, "Bus search performed successfully ✅");
        } catch (Exception e) {
            handleFailure("Home Page test failed", e);
        }
    }

    // ---------------------- FILTER TEST -----------------------------
    @Test(priority = 3, dependsOnMethods = {"testHomePageActions"}, description = "Apply filters")
    @Severity(SeverityLevel.MINOR)
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

    // ---------------------- BUS BOOKING -----------------------------
    @Test(priority = 4, dependsOnMethods = {"testFilters"}, description = "Scroll and click seat")
    @Severity(SeverityLevel.CRITICAL)
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

    // ---------------------- SELECT SEATS -----------------------------
    @Test(priority = 5, dependsOnMethods = {"testBusBookingPageActions"}, description = "Select seat & proceed to pay")
    @Severity(SeverityLevel.CRITICAL)
    public void testSelectSeatAndProceedToPay() {
        test = extent.createTest("Seat Selection Test", "Select seats and proceed to payment");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id*='seat']")));
            Thread.sleep(1000);
            seatPointsPage.selectSeats("5B", "6C","2D","7D");	
            seatPointsPage.selectPickupPoint();
            seatPointsPage.selectDropPoint();
            seatPointsPage.clickBookAndPay();
            reviewBookingPage.handleDiscountPopup();

            WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            boolean navigated = false;
            try {
                WebElement reviewOrPaymentElement = pageWait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(),'Review Booking') or contains(text(),'Payment')]")
                ));
                navigated = reviewOrPaymentElement.isDisplayed();
            } catch (TimeoutException te) {
                System.out.println("⚠️ Timeout: Review or Payment page element not found");
            }

            if (navigated) {
                test.log(Status.PASS, "Seat selection and Book & Pay successful ✅");
            } else {
                takeScreenshotFile("SeatSelectionFailure.png");
                test.log(Status.FAIL, "Did not reach Review or Payment page ❌");
                Assert.fail("Expected to navigate to Review or Payment page, but element not found.");
            }

        } catch (Exception e) {
            handleFailure("Seat selection and payment test failed", e);
        }
    }

    // ---------------------- REVIEW BOOKING -----------------------------
    @Test(priority = 6, dependsOnMethods={"testSelectSeatAndProceedToPay"}, description = "Verify Review Booking flow actions")
    @Severity(SeverityLevel.CRITICAL)
    public void testReviewBookingFlow() {
        test = extent.createTest("Review Booking Flow", "Full booking flow on review page");
        try {
            reviewBookingPage.clickApplyCoupon();
            test.log(Status.PASS, "Review Booking flow completed successfully ✅");
        } catch (Exception e) {
            handleFailure("Review Booking flow failed", e);
        }
    }

    // ---------------------- PAYMENT FLOW -----------------------------
    @Test(priority = 7, dependsOnMethods = {"testReviewBookingFlow"}, description = "Complete payment flow using NetBanking")
    @Severity(SeverityLevel.CRITICAL)
    public void testPaymentFlow() {
        test = extent.createTest("Payment Flow Test", "Complete payment using NetBanking (Axis Bank)");
        try {
            paymentModePage.selectNetBanking();
            paymentModePage.selectAxisBank();
            paymentModePage.clickProceedToPay();
            paymentModePage.clickTxnDropdown();
            paymentModePage.selectChargedOption();
            paymentModePage.clickSubmitButton();
            test.log(Status.PASS, "Payment flow completed successfully ✅");
        } catch (Exception e) {
            handleFailure("Payment Flow failed", e);
        }
    }

    // ---------------------- FAILURE HANDLER -----------------------------
    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public void takeScreenshotFile(String fileName) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = Paths.get(SCREENSHOT_DIR, fileName);
            Files.copy(srcFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot saved: " + dest.toString());
        } catch (IOException e) {
            System.out.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    @Step("Handle failure and capture screenshot")
    public void handleFailure(String message, Exception e) {
        try {
            String screenshotName = "Error_" + System.currentTimeMillis() + ".png";
            takeScreenshotFile(screenshotName);

            if (test != null) {
                test.log(Status.FAIL, message + " ❌")
                    .addScreenCaptureFromPath(SCREENSHOT_DIR + "/" + screenshotName);
                test.log(Status.FAIL, e.getMessage());
            }

            Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(takeScreenshot()));
            Assert.fail(message + ": " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("Error in handleFailure: " + ex.getMessage());
        }
    }

    // ---------------------- TEARDOWN & EMAIL REPORT -----------------------------
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed successfully");
        }

        extent.flush();
        System.out.println("Extent report flushed successfully");

        // Email the ExtentReport
        String reportPath = REPORT_DIR + "target/ExtentReports";
        EmailUtils.sendEmail(
        	    "sumedhsonwane19@gmail.com,sumedhsonwane18@gmail.com",  // recipients
        	    "NueGo Automation Test Report",                          // subject
        	    "Please find the attached automation test report.",     // body  ✅ comma added
        	    reportPath                                              // attachment path
        	);
        System.out.println("Email sent with report: " + reportPath);
    }
}

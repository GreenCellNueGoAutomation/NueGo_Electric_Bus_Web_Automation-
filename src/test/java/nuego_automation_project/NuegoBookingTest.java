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

import java.io.ByteArrayInputStream;
import java.time.Duration;

import nuego_automation_project.SendReportEmail;

@Epic("NueGo Web Automation")
@Feature("Complete Booking Flow")
@Listeners(ExtentListener.class)
public class NuegoBookingTest extends BaseTest {

    // 🔹 Base URL separated as a constant
    private static final String BASE_URL = "https://greencell-nuego-web.web.app/";

    private WebDriverWait wait;
    private static ExtentReports extent = ExtentReportManager.getReport();
    private static ExtentTest test;

    // Page Objects
    private LoginPage loginPage;
    private Wallet walletpage;
  //  private MyBookings MyBookingsPage;
    private HomePage homePage;
    private BusBookingPage bookingPage;
    private Filters filtersPage;
    private SelectSeatPoints seatPointsPage;
    private Review_Booking_Page reviewBookingPage;
    private Payment_Mode paymentModePage;
    private Ticket_Confirmation ticketConfirmationPage;
    private Reschedule_Booking rescheduleBookingPage;
    private CancelBooking cancelBookingPage;
    // private DiscoverJourneyPage discoverJourneyPage;

    // ---------------------- SETUP -----------------------------
    @BeforeClass(alwaysRun = true)
    public void setUp() {
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
     // Force desktop user-agent to avoid mobile layout
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36");

        

        // 🔹 Read "headless" flag from system property (default: false)
        String headless = System.getProperty("headless", "false");
        if (headless.equalsIgnoreCase("true")) {
            // ✅ Jenkins / CI → headless
            options.addArguments("--headless=new");     // or "--headless" for older Chrome
            options.addArguments("--window-size=1920,1080");
            System.out.println("⚙️ Running in HEADLESS mode (System property headless=true)");
        } else {
            // ✅ Local → normal browser
            System.out.println("⚙️ Running in NORMAL (NON-headless) mode (headless flag not set)");
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        // Initialize Page Objects
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingPage = new BusBookingPage(driver);
        filtersPage = new Filters(driver);
        seatPointsPage = new SelectSeatPoints(driver);
        reviewBookingPage = new Review_Booking_Page(driver);
        paymentModePage = new Payment_Mode(driver);
        ticketConfirmationPage = new Ticket_Confirmation(driver);
        rescheduleBookingPage = new Reschedule_Booking(driver);
        cancelBookingPage = new CancelBooking(driver);
        walletpage = new Wallet(driver);
      //  MyBookingsPage = new MyBookings(driver);
        // discoverJourneyPage = new DiscoverJourneyPage(driver);

        // 🔹 Launch URL BEFORE any tests
        driver.get(BASE_URL);
        System.out.println("✅ Launched application URL: " + BASE_URL);
    }

    // ---------------------- TESTS -----------------------------

    @Test(priority = 1, description = "Login to application using mobile and OTP", retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLogin() {
        test = extent.createTest("Login Test", "Login using mobile number and OTP");
        try {
            // URL is already opened in @BeforeClass
            loginPage.login("7385109680", "1234");
            test.log(Status.PASS, "✅ Login successful and redirected to Home page");
        } catch (Exception e) {
            handleFailure("Login failed", e);
        }
    }

    @Test(
            priority = 2,
            dependsOnMethods = {"testLogin"},
            description = "Open wallet and add money",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Severity(SeverityLevel.CRITICAL)
    @Story("Wallet Operations")
    public void testAddMoneyToWallet() {
        test = extent.createTest("Wallet Test", "Open wallet and add money to account");
        try {
            walletpage.openWallet();
            test.log(Status.INFO, "Wallet icon clicked successfully");

            walletpage.addMoney();
            test.log(Status.INFO, "Amount entered and Add Money clicked, navigating to payment");

            paymentModePage.completeNetBankingAxisFlow();
            test.log(Status.INFO, "NetBanking Axis payment flow completed");

            try {
                WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(30));
                localWait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//p[contains(.,'Payment Successful') or contains(.,'Money added successfully') or contains(.,'Wallet recharged successfully')]")
                        ),
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//*[contains(.,'Money added to wallet') or contains(.,'Wallet updated')]")
                        )
                ));
                test.log(Status.INFO, "Wallet / payment success message displayed");
            } catch (Exception waitEx) {
                test.log(Status.WARNING,
                        "Could not explicitly verify wallet success message, proceeding to navigate home: " + waitEx.getMessage());
            }

            test.log(Status.PASS, "Money added to wallet flow executed");

            walletpage.goBackToHomePage();
            test.log(Status.INFO, "Navigated back to Home page after wallet top-up");

        } catch (Exception e) {
            handleFailure("Wallet operation failed", e);
        }
    }

    // 🔹 NEW TEST: MyBookings + Header + Offer, priority = 3
   
  /*  @Test(
            priority = 3,
            dependsOnMethods = {"testLogin"},
            description = "My Bookings (Completed, Upcoming, Cancelled)",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Severity(SeverityLevel.NORMAL)
    @Story("My Bookings & Header Verification")
    public void testMyBookingsAndHeaderFlow() {

        test = extent.createTest(
                "My Bookings & Header Flow",
                "Verify top header, offers page and My Bookings (Completed / Upcoming / Cancelled trips)"
        );

        try {

        MyBookingsPage.runMyBookingsFullFlow();


        } catch (Exception e) {
            handleFailure("My Bookings & Header flow failed", e);
        }
    }*/

    @Test(priority = 4, dependsOnMethods = {"testLogin"}, description = "Handle homepage popups and search bus", retryAnalyzer = RetryAnalyzer.class)
    public void testHomePageActions() {
        test = extent.createTest("Home Page Test", "Handle popups and search buses");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Enter Source']")));
            homePage.closePopupIfPresent();
            homePage.searchBus("Agra", "Bassi");
            test.log(Status.PASS, "✅ Bus search performed successfully (Agra → Bassi)");
        } catch (Exception e) {
            handleFailure("Home Page test failed", e);
        }
    }

    @Test(priority = 5, dependsOnMethods = {"testHomePageActions"}, description = "Apply filters", retryAnalyzer = RetryAnalyzer.class)
    public void testFilters() {
        test = extent.createTest("Filter Test", "Apply filters and verify results");
        try {
            filtersPage.selectTimer();
            filtersPage.clickBoardingPoint();
            filtersPage.selectBoardingCheckbox();
            filtersPage.clickDroppingPoint();
            filtersPage.selectDroppingCheckboxAndReset();
            test.log(Status.PASS, "✅ Filters applied successfully");
        } catch (Exception e) {
            handleFailure("Filter test failed", e);
        }
    }

    @Test(priority = 6, dependsOnMethods = {"testFilters"}, description = "Scroll and click seat on Bus Booking page", retryAnalyzer = RetryAnalyzer.class)
    public void testBusBookingPageActions() {
        test = extent.createTest("Bus Booking Page Test", "Scroll and click on seat");
        try {
            bookingPage.scrollDownSmall();
            bookingPage.clickSeat();
            test.log(Status.PASS, "✅ BusBookingPage actions executed successfully");
        } catch (Exception e) {
            handleFailure("Bus Booking Page test failed", e);
        }
    }

    @Test(
            priority = 7,
            dependsOnMethods = {"testBusBookingPageActions"},
            description = "Select seat, pickup & drop points and click Book & Pay",
            retryAnalyzer = RetryAnalyzer.class
    )
    public void testSelectSeatAndProceedToPay() {
        test = extent.createTest("Seat Selection Test", "Select seats and proceed to payment");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id*='seat']")));

            seatPointsPage.selectSeats();
            Thread.sleep(3000);
            seatPointsPage.selectPickupPoint();
            Thread.sleep(1500);
            seatPointsPage.selectDropPoint();
            Thread.sleep(1500);

            seatPointsPage.clickBookAndPay();

            reviewBookingPage.handleDiscountPopup();

            WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement reviewOrPaymentElement = pageWait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(),'Review Booking') or contains(text(),'Payment')]")
                    )
            );

            Assert.assertTrue(reviewOrPaymentElement.isDisplayed(), "❌ Review or Payment page not reached");
            test.log(Status.PASS, "✅ Seat selection and Book & Pay successful");
        } catch (Exception e) {
            handleFailure("Seat selection and payment test failed", e);
        }
    }

    @Test(
            priority = 8,
            dependsOnMethods = {"testSelectSeatAndProceedToPay"},
            description = "Review Booking: handle popup, coupon (if any), assurance/wallet/miles (if any), GST & Proceed",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Severity(SeverityLevel.CRITICAL)
    @Story("Review Booking")
    public void testReviewBookingFlow() {

        test = extent.createTest(
                "Review Booking Flow",
                "Handle discount popup, optional coupon, optional assurance/wallet/miles, GST validation and Proceed & Book"
        );

        try {
            System.out.println("➡️ Starting Review Booking flow...");

            reviewBookingPage.scrollToReviewSection();
            test.log(Status.INFO, "✅ Scrolled to Review Booking section");

            reviewBookingPage.completeReviewBookingFlow();
            test.log(Status.INFO, "✅ Review Booking flow executed (with dynamic checks)");

            test.pass("Review Booking flow executed successfully");
            test.log(Status.PASS, "✅ Review Booking page flow passed");

        } catch (Exception e) {
            test.fail("❌ Review Booking flow failed – " + e.getMessage());
            test.log(Status.FAIL, "❌ Review Booking flow failed with exception: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Review Booking flow failed", e);
        }
    }

    @Test(
            priority = 9,
            dependsOnMethods = {"testReviewBookingFlow"},
            description = "Complete payment flow using NetBanking - Axis Bank",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Flow")
    public void testPaymentFlow() {
        test = extent.createTest(
                "Payment Flow Test",
                "Complete payment using NetBanking (Axis Bank)"
        );

        try {
            System.out.println("🧭 Navigating through payment section...");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'netbanking')]")
            ));
            System.out.println("✅ Payment page with NetBanking options loaded");

            paymentModePage.completeNetBankingAxisFlow();

            test.log(Status.PASS,
                    "Payment flow completed successfully (NetBanking → Axis Bank → Proceed to Pay → CHARGED → Submit)");

        } catch (Exception e) {
            handleFailure("Payment Flow failed", e);
        } finally {
            try {
                driver.switchTo().defaultContent();
            } catch (Exception ignored) {}
        }
    }

    @Test(
            priority = 10,
            dependsOnMethods = {"testPaymentFlow"},
            description = "Verify Ticket Confirmation page actions (Whatsapp, SMS, Fare, E-ticket, Copy Link, Download)",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Severity(SeverityLevel.NORMAL)
    @Story("Ticket Confirmation")
    public void testTicketConfirmationFlow() {

        test = extent.createTest(
                "Ticket Confirmation Test",
                "Verify confirmation page, Whatsapp, SMS, Fare details, E-ticket, Copy Link & Download"
        );

        try {
            ticketConfirmationPage.verifyOnConfirmationPage();
            String currentUrl = ticketConfirmationPage.getCurrentUrl();
            System.out.println("Current URL on Confirmation page → " + currentUrl);
            Assert.assertTrue(
                    currentUrl.contains("confirmation"),
                    "Not on confirmation page!"
            );
            test.log(Status.INFO, "✅ Landed on Ticket Confirmation page");

            ticketConfirmationPage.dismissNotificationIfPresent();
            test.log(Status.INFO, "Handled notification popup (if present)");

            ticketConfirmationPage.clickWhatsappOnceAndVerifySuccess();
            ticketConfirmationPage.clickWhatsappOnceAndVerifySuccess();
            test.log(Status.INFO, "✅ Whatsapp ticket share verified (2 times)");

            ticketConfirmationPage.clickSmsAndVerifySuccess();
            test.log(Status.INFO, "✅ SMS ticket share verified");

            ticketConfirmationPage.openAndCloseFareDetails();
            test.log(Status.INFO, "✅ Fare details opened & closed");

            ticketConfirmationPage.clickETicketOption();
            test.log(Status.INFO, "✅ E-Ticket option clicked");

            ticketConfirmationPage.clickCopyLink();
            test.log(Status.INFO, "✅ Copy Link button clicked");

            ticketConfirmationPage.clickDownloadAndVerifyTicketInNewTab();
            test.log(Status.INFO, "✅ Download Ticket clicked and new tab handled");

            test.pass("Ticket Confirmation flow executed successfully");
            test.log(Status.PASS, "✅ Ticket Confirmation page flow executed successfully");

        } catch (Exception e) {
            handleFailure("Ticket Confirmation flow failed", e);
        }
    }

    @Test(
            priority = 11,
            dependsOnMethods = {"testTicketConfirmationFlow"},
            description = "Verify Reschedule Booking flow from confirmation page",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Severity(SeverityLevel.NORMAL)
    @Story("Reschedule Booking")
    public void testRescheduleBookingFlow() {
        test = extent.createTest(
                "Reschedule Booking Test",
                "Verify change booking, reschedule tab, calendar future date, View Coaches, seat selection, payment & confirmation"
        );

        try {
            System.out.println("===== 🔁 Starting Reschedule Booking flow =====");

            ticketConfirmationPage.verifyOnConfirmationPage();
            String currentUrl = ticketConfirmationPage.getCurrentUrl();
            System.out.println("Current URL on Confirmation page → " + currentUrl);
            Assert.assertTrue(
                    currentUrl.contains("confirmation"),
                    "Not on confirmation page! Cannot start reschedule."
            );
            test.log(Status.INFO, "✅ Confirmed we are on Ticket Confirmation page before reschedule");

            System.out.println(">>> Step 1: Click Change booking");
            rescheduleBookingPage.clickChangeBooking();

            System.out.println(">>> Step 2: Click Reschedule tab");
            try {
                rescheduleBookingPage.clickRescheduleTab();
            } catch (Exception e) {
                System.out.println("Primary Reschedule tab locator failed, using alternate...");
                rescheduleBookingPage.clickRescheduleTabAlt1();
            }

            System.out.println(">>> Step 3: Open calendar");
            rescheduleBookingPage.openCalendar();

            System.out.println(">>> Step 4: Select future date automatically");
            rescheduleBookingPage.selectFutureDateAutomatically();

            System.out.println(">>> Step 5: Click View Coaches");
            rescheduleBookingPage.clickViewCoaches();

            System.out.println(">>> Step 6: Seat selection");
            bookingPage.clickSeat();

            seatPointsPage.selectSeats();
            Thread.sleep(1000);
            seatPointsPage.selectPickupPoint();
            Thread.sleep(1000);
            seatPointsPage.selectDropPoint();
            Thread.sleep(1000);

            seatPointsPage.clickBookAndPay();
            Thread.sleep(1000);
            System.out.println("Clicked Book & Pay");

            reviewBookingPage.scrollToReviewSection();
            Thread.sleep(2000);
            reviewBookingPage.getTotalFareAmount();
            reviewBookingPage.clickProceedToBookButton();
            reviewBookingPage.handleBookingPopupIfPresent();
            reviewBookingPage.getDiscountAlertNoThanksButtonLocator();
            reviewBookingPage.clickProceedToBookButton();
            

            paymentModePage.completeNetBankingAxisFlow();

            ticketConfirmationPage.completeConfirmationActionsFlow();

            test.log(Status.PASS, "✅ Reschedule Booking flow executed successfully");
            System.out.println("===== ✅ Reschedule Booking flow completed =====");

        } catch (Exception e) {
            handleFailure("Reschedule Booking flow failed", e);
        }
    }

    @Test(
            priority = 12,
            dependsOnMethods = {"testRescheduleBookingFlow"},
            description = "Cancel Booking flow: Cancel → Checkbox → Continue → Refund → Go Home"
    )
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cancel Booking")
    public void testCancelBookingFlow() {
        test = extent.createTest(
                "Cancel Booking Flow",
                "Execute Cancel Booking: Cancel tab → select reason → Continue → Refund → Go Home"
        );

        System.out.println(" Click Change booking");
        rescheduleBookingPage.clickChangeBooking();

        try {
            System.out.println("===== 🔁 Starting Cancel Booking flow =====");

            cancelBookingPage.clickCancelButton();
            test.log(Status.INFO, "✅ Clicked Cancel button");

            cancelBookingPage.selectTravelCheckbox();
            test.log(Status.INFO, "✅ Selected checkbox for reason");

            cancelBookingPage.clickContinueButton();
            test.log(Status.INFO, "✅ Clicked Continue button");

            cancelBookingPage.clickRefundButton();
            test.log(Status.INFO, "✅ Clicked Refund button");

            cancelBookingPage.clickGoHomeButton();
            test.log(Status.INFO, "✅ Clicked Go to Home button");

            test.pass("✅ Cancel Booking flow executed successfully");
            System.out.println("===== ✅ Cancel Booking flow completed =====");

        } catch (Exception e) {
            handleFailure("Cancel Booking flow failed", e);
        }
    }

    // ---------------------- FAILURE HANDLER -----------------------------
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

            Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(takeScreenshot()));
            Assert.fail(message + ": " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("⚠️ Error while handling failure: " + ex.getMessage());
        }
    }

    // ---------------------- EMAIL TRIGGER -----------------------------
    @AfterSuite(alwaysRun = true)
    public void sendReportEmail() {
        try {
            System.out.println("📧 Triggering report email after suite completion...");
            SendReportEmail.main(null);
            System.out.println("✅ Report email triggered successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to send automation report email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

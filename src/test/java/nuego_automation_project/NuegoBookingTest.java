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
import utils.ExcelUtils;



import java.io.ByteArrayInputStream;
import java.time.Duration;

// ✅ Email utility import
import nuego_automation_project.SendReportEmail;

@Epic("NueGo Web Automation")
@Feature("Complete Booking Flow")
@Listeners(ExtentListener.class)
public class NuegoBookingTest extends BaseTest {

    private WebDriverWait wait;
    private static ExtentReports extent = ExtentReportManager.getReport();
    private static ExtentTest test;
 // Page Objects
    private LoginPage loginPage;
    private  Wallet walletpage;
    private HomePage homePage;
    private BusBookingPage bookingPage;
    private Filters filtersPage;
    private SelectSeatPoints seatPointsPage;
    private Review_Booking_Page reviewBookingPage;
    private Payment_Mode paymentModePage;
    private Ticket_Confirmation ticketConfirmationPage;
    private Reschedule_Booking rescheduleBookingPage;
    private CancelBooking cancelBookingPage; // ✅ Use correct type & lowercase variable

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
        cancelBookingPage = new CancelBooking(driver); // ✅ Fixed
        walletpage = new Wallet(driver);
    }

    @Test(priority = 1, description = "Login to application using mobile and OTP", retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLogin() {
        test = extent.createTest("Login Test", "Login using mobile number and OTP");
        try {
            driver.get("https://greencell-nuego-web.web.app/");
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
            // 1️⃣ Open wallet icon
            walletpage.openWallet();
            test.log(Status.INFO, "Wallet icon clicked successfully");

            // 2️⃣ Enter amount & click Add Money (lands on payment page)
            walletpage.addMoney();
            test.log(Status.INFO, "Amount entered and Add Money clicked, navigating to payment");

            // 3️⃣ Complete payment using reused method
            paymentModePage.completeNetBankingAxisFlow();
            test.log(Status.INFO, "NetBanking Axis payment flow completed");

            // 4️⃣ OPTIONAL: Try to verify payment/wallet success message
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

            // 5️⃣ Go back to Home page AFTER payment success / success attempt
            walletpage.goBackToHomePage();
            test.log(Status.INFO, "Navigated back to Home page after wallet top-up");

        } catch (Exception e) {
            handleFailure("Wallet operation failed", e);
        }
    }


    
    @Test(priority = 3, dependsOnMethods = {"testLogin"}, description = "Handle homepage popups and search bus", retryAnalyzer = RetryAnalyzer.class)
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

    @Test(priority = 4, dependsOnMethods = {"testHomePageActions"}, description = "Apply filters", retryAnalyzer = RetryAnalyzer.class)
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

    @Test(priority = 5, dependsOnMethods = {"testFilters"}, description = "Scroll and click seat on Bus Booking page", retryAnalyzer = RetryAnalyzer.class)
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
            priority = 6,
            dependsOnMethods = {"testBusBookingPageActions"},
            description = "Select seat, pickup & drop points and click Book & Pay",
            retryAnalyzer = RetryAnalyzer.class
    )
    public void testSelectSeatAndProceedToPay() {
        test = extent.createTest("Seat Selection Test", "Select seats and proceed to payment");
        try {
            // Wait for seat layout to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id*='seat']")));

            // ✅ Auto-select ANY available seat
            seatPointsPage.selectSeats();

            // ✅ Pickup & Drop selection
            seatPointsPage.selectPickupPoint();
            Thread.sleep(1500);
            seatPointsPage.selectDropPoint();
            Thread.sleep(1500);

            // ✅ Book & Pay
            seatPointsPage.clickBookAndPay();

            // Discount popup (if any)
            reviewBookingPage.handleDiscountPopup();

            // ✅ Verify we reached Review Booking or Payment page
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

    // ✅ Review Booking page test
    @Test(
            priority = 7,
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

            // 1️⃣ Ensure Review Booking section is visible
            reviewBookingPage.scrollToReviewSection();
            test.log(Status.INFO, "✅ Scrolled to Review Booking section");

            // 2️⃣ Run the complete flow (internally skips sections not present – safe for reschedule)
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
            priority = 8,
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

    // ✅ Ticket Confirmation page test
    @Test(
            priority = 9,
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
            // 1️⃣ Ensure we’re on confirmation page
            ticketConfirmationPage.verifyOnConfirmationPage();
            String currentUrl = ticketConfirmationPage.getCurrentUrl();
            System.out.println("Current URL on Confirmation page → " + currentUrl);
            Assert.assertTrue(
                    currentUrl.contains("confirmation"),
                    "Not on confirmation page!"
            );
            test.log(Status.INFO, "✅ Landed on Ticket Confirmation page");

            // 2️⃣ Dismiss notification popup if present
            ticketConfirmationPage.dismissNotificationIfPresent();
            test.log(Status.INFO, "Handled notification popup (if present)");

            // 3️⃣ Click on Whatsapp (twice) & verify success message
            ticketConfirmationPage.clickWhatsappOnceAndVerifySuccess();
            ticketConfirmationPage.clickWhatsappOnceAndVerifySuccess();
            test.log(Status.INFO, "✅ Whatsapp ticket share verified (2 times)");

            // 4️⃣ Click on SMS & verify success message
            ticketConfirmationPage.clickSmsAndVerifySuccess();
            test.log(Status.INFO, "✅ SMS ticket share verified");

            // 5️⃣ Open & close Fare Details
            ticketConfirmationPage.openAndCloseFareDetails();
            test.log(Status.INFO, "✅ Fare details opened & closed");

            // 6️⃣ Click E-Ticket option
            ticketConfirmationPage.clickETicketOption();
            test.log(Status.INFO, "✅ E-Ticket option clicked");

            // 7️⃣ Click Copy Link button
            ticketConfirmationPage.clickCopyLink();
            test.log(Status.INFO, "✅ Copy Link button clicked");

            // 8️⃣ Click on Download Ticket (opens in new tab) & verify new tab
            ticketConfirmationPage.clickDownloadAndVerifyTicketInNewTab();
            test.log(Status.INFO, "✅ Download Ticket clicked and new tab handled");

            test.pass("Ticket Confirmation flow executed successfully");
            test.log(Status.PASS, "✅ Ticket Confirmation page flow executed successfully");

        } catch (Exception e) {
            handleFailure("Ticket Confirmation flow failed", e);
        }
    }

    @Test(
            priority = 10,
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

            // 0️⃣ Ensure we are on Ticket Confirmation page
            ticketConfirmationPage.verifyOnConfirmationPage();
            String currentUrl = ticketConfirmationPage.getCurrentUrl();
            System.out.println("Current URL on Confirmation page → " + currentUrl);
            Assert.assertTrue(
                    currentUrl.contains("confirmation"),
                    "Not on confirmation page! Cannot start reschedule."
            );
            test.log(Status.INFO, "✅ Confirmed we are on Ticket Confirmation page before reschedule");

            // 1️⃣ Click on Change booking from confirmation page
            System.out.println(">>> Step 1: Click Change booking");
            rescheduleBookingPage.clickChangeBooking();

            // 2️⃣ Click on Reschedule tab (with fallback handled inside page object)
            System.out.println(">>> Step 2: Click Reschedule tab");
            try {
                rescheduleBookingPage.clickRescheduleTab();
            } catch (Exception e) {
                System.out.println("Primary Reschedule tab locator failed, using alternate...");
                rescheduleBookingPage.clickRescheduleTabAlt1();
            }

            // 3️⃣ Open calendar
            System.out.println(">>> Step 3: Open calendar");
            rescheduleBookingPage.openCalendar();

            // 4️⃣ Select automatic future date
            System.out.println(">>> Step 4: Select future date automatically");
            rescheduleBookingPage.selectFutureDateAutomatically();

            // 5️⃣ Click on View Coaches
            System.out.println(">>> Step 5: Click View Coaches");
            rescheduleBookingPage.clickViewCoaches();

            // 6️⃣ Seat selection (for rescheduled bus)
            System.out.println(">>> Step 6: Seat selection");
            bookingPage.clickSeat();

            // ✅ Pickup & Drop selection
            seatPointsPage.selectSeats();
            Thread.sleep(1000);
            seatPointsPage.selectPickupPoint();
            Thread.sleep(1000);
            seatPointsPage.selectDropPoint();
            Thread.sleep(1000);

            // ✅ Book & Pay
            seatPointsPage.clickBookAndPay();
            Thread.sleep(1000);
            System.out.println("Clicked Book & Pay");

            // 7️⃣ Review booking actions for rescheduled journey
            reviewBookingPage.scrollToReviewSection();
            Thread.sleep(2000);
            // For reschedule, coupon/assurance may not exist – we do minimal steps:
            reviewBookingPage.clickWalletApply();
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
            priority = 11,
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
        
        // reused method
        System.out.println(" Click Change booking");
        rescheduleBookingPage.clickChangeBooking();

        
        try {
            System.out.println("===== 🔁 Starting Cancel Booking flow =====");

            // 1️⃣ Click Cancel button
            cancelBookingPage.clickCancelButton();
            test.log(Status.INFO, "✅ Clicked Cancel button");

            // 2️⃣ Select checkbox "My travel plans have changed"
            cancelBookingPage.selectTravelCheckbox();
            test.log(Status.INFO, "✅ Selected checkbox for reason");

            // 3️⃣ Click Continue button
            cancelBookingPage.clickContinueButton();
            test.log(Status.INFO, "✅ Clicked Continue button");

            // 4️⃣ Click Refund button
            cancelBookingPage.clickRefundButton();
            test.log(Status.INFO, "✅ Clicked Refund button");

            // 5️⃣ Click Go to Home button
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

    // ---------------------- CLEANUP -----------------------------
    /*@AfterClass(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver != null) {
                test.log(Status.INFO, "Closing browser...");
                driver.quit();
                System.out.println("Browser closed successfully");
            }
            extent.flush();
            System.out.println("Extent report flushed successfully");
        } catch (Exception e) {
            System.out.println("⚠️ Error during teardown: " + e.getMessage());
        }
    }*/

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

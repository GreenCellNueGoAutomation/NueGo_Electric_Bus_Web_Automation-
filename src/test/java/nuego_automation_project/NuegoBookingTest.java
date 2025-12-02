package nuego_automation_project;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.qameta.allure.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.*;

import java.io.ByteArrayInputStream;
import java.time.Duration;

@Epic("NueGo Web Automation")
@Feature("Complete Booking Flow")
@Listeners(ExtentListener.class)
public class NuegoBookingTest extends BaseTest {

    private static final String BASE_URL = "https://greencell-nuego-web.web.app/";
    private static ExtentReports extent = ExtentReportManager.getReport();
    private static ExtentTest test;
    private WebDriverWait wait;

    // Page Objects
    private LoginPage loginPage;
    private Wallet walletpage;
    private HomePage homePage;
    private BusBookingPage bookingPage;
    private Filters filtersPage;
    private SelectSeatPoints seatPointsPage;
    private Review_Booking_Page reviewBookingPage;
    private Payment_Mode paymentModePage;
    private Ticket_Confirmation ticketConfirmationPage;
    private Reschedule_Booking rescheduleBookingPage;
    private CancelBooking cancelBookingPage;

    // ---------------------- SETUP -----------------------------
    @BeforeClass(alwaysRun = true)
    public void setUpTest() {
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

        // Launch application URL
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
            loginPage.login("7385109680", "1234");
            test.log(Status.PASS, "✅ Login successful and redirected to Home page");
        } catch (Exception e) {
            handleFailure("Login failed", e);
        }
    }

    @Test(priority = 2, dependsOnMethods = {"testLogin"}, description = "Open wallet and add money", retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Wallet Operations")
    public void testAddMoneyToWallet() {
        test = extent.createTest("Wallet Test", "Open wallet and add money to account");
        try {
            walletpage.openWallet();
            walletpage.addMoney();
            paymentModePage.completeNetBankingAxisFlow();
            walletpage.goBackToHomePage();
            test.log(Status.PASS, "✅ Money added to wallet and navigated back to Home page");
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

    @Test(priority = 6, dependsOnMethods = {"testBusBookingPageActions"}, description = "Select seat, pickup & drop points and click Book & Pay", retryAnalyzer = RetryAnalyzer.class)
    public void testSelectSeatAndProceedToPay() {
        test = extent.createTest("Seat Selection Test", "Select seats and proceed to payment");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id*='seat']")));

            seatPointsPage.selectSeats();
            Thread.sleep(2000);
            seatPointsPage.selectPickupPoint();
            Thread.sleep(1000);
            seatPointsPage.selectDropPoint();
            Thread.sleep(1000);
            seatPointsPage.clickBookAndPay();

            reviewBookingPage.handleDiscountPopup();
            WebElement reviewOrPaymentElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'Review Booking') or contains(text(),'Payment')]")
            ));
            Assert.assertTrue(reviewOrPaymentElement.isDisplayed(), "❌ Review or Payment page not reached");
            test.log(Status.PASS, "✅ Seat selection and Book & Pay successful");
        } catch (Exception e) {
            handleFailure("Seat selection and payment test failed", e);
        }
    }

    @Test(priority = 7, dependsOnMethods = {"testSelectSeatAndProceedToPay"}, description = "Review Booking flow", retryAnalyzer = RetryAnalyzer.class)
    public void testReviewBookingFlow() {
        test = extent.createTest("Review Booking Flow", "Handle discount popup, coupon, assurance/wallet/miles, GST, and proceed");
        try {
            reviewBookingPage.scrollToReviewSection();
            reviewBookingPage.completeReviewBookingFlow();
            test.log(Status.PASS, "✅ Review Booking flow executed successfully");
        } catch (Exception e) {
            handleFailure("Review Booking flow failed", e);
        }
    }

    @Test(priority = 8, dependsOnMethods = {"testReviewBookingFlow"}, description = "Payment flow using NetBanking - Axis Bank", retryAnalyzer = RetryAnalyzer.class)
    public void testPaymentFlow() {
        test = extent.createTest("Payment Flow Test", "Complete payment using NetBanking (Axis Bank)");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'netbanking')]")
            ));
            paymentModePage.completeNetBankingAxisFlow();
            test.log(Status.PASS, "✅ Payment flow completed successfully");
        } catch (Exception e) {
            handleFailure("Payment Flow failed", e);
        }
    }

    @Test(priority = 9, dependsOnMethods = {"testPaymentFlow"}, description = "Ticket Confirmation flow", retryAnalyzer = RetryAnalyzer.class)
    public void testTicketConfirmationFlow() {
        test = extent.createTest("Ticket Confirmation Test", "Verify confirmation page, Whatsapp, SMS, Fare details, E-ticket, Copy Link & Download");
        try {
            ticketConfirmationPage.verifyOnConfirmationPage();
            ticketConfirmationPage.dismissNotificationIfPresent();
            ticketConfirmationPage.clickWhatsappOnceAndVerifySuccess();
            ticketConfirmationPage.clickSmsAndVerifySuccess();
            ticketConfirmationPage.openAndCloseFareDetails();
            ticketConfirmationPage.clickETicketOption();
            ticketConfirmationPage.clickCopyLink();
            ticketConfirmationPage.clickDownloadAndVerifyTicketInNewTab();
            test.log(Status.PASS, "✅ Ticket Confirmation flow executed successfully");
        } catch (Exception e) {
            handleFailure("Ticket Confirmation flow failed", e);
        }
    }

    @Test(priority = 10, dependsOnMethods = {"testTicketConfirmationFlow"}, description = "Reschedule Booking flow", retryAnalyzer = RetryAnalyzer.class)
    public void testRescheduleBookingFlow() {
        test = extent.createTest("Reschedule Booking Test", "Reschedule booking from confirmation page");
        try {
            ticketConfirmationPage.verifyOnConfirmationPage();
            rescheduleBookingPage.clickChangeBooking();
            rescheduleBookingPage.clickRescheduleTab();
            rescheduleBookingPage.openCalendar();
            rescheduleBookingPage.selectFutureDateAutomatically();
            rescheduleBookingPage.clickViewCoaches();
            bookingPage.clickSeat();
            seatPointsPage.selectSeats();
            seatPointsPage.selectPickupPoint();
            seatPointsPage.selectDropPoint();
            seatPointsPage.clickBookAndPay();
            reviewBookingPage.scrollToReviewSection();
            reviewBookingPage.clickProceedToBookButton();
            reviewBookingPage.handleBookingPopupIfPresent();
            paymentModePage.completeNetBankingAxisFlow();
            ticketConfirmationPage.completeConfirmationActionsFlow();
            test.log(Status.PASS, "✅ Reschedule Booking flow executed successfully");
        } catch (Exception e) {
            handleFailure("Reschedule Booking flow failed", e);
        }
    }

    @Test(priority = 11, dependsOnMethods = {"testRescheduleBookingFlow"}, description = "Cancel Booking flow", retryAnalyzer = RetryAnalyzer.class)
    public void testCancelBookingFlow() {
        test = extent.createTest("Cancel Booking Flow", "Cancel booking and refund");
        try {
            rescheduleBookingPage.clickChangeBooking();
            cancelBookingPage.clickCancelButton();
            cancelBookingPage.selectTravelCheckbox();
            cancelBookingPage.clickContinueButton();
            cancelBookingPage.clickRefundButton();
            cancelBookingPage.clickGoHomeButton();
            test.log(Status.PASS, "✅ Cancel Booking flow executed successfully");
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
                if (webError != null) test.log(Status.FAIL, "Web error message: " + webError);
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
  /*  @AfterSuite(alwaysRun = true)
    public void sendReportEmail() {
        try {
            System.out.println("📧 Triggering report email after suite completion...");
         //   SendReportEmail.main(null);
            System.out.println("✅ Report email triggered successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to send automation report email: " + e.getMessage());
            e.printStackTrace();
        }*/
    }



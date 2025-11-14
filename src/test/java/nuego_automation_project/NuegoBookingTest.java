package nuego_automation_project;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.*;

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
    private HomePage homePage;
    private BusBookingPage bookingPage;
    private Filters filtersPage;
    private SelectSeatPoints seatPointsPage;
    private Review_Booking_Page reviewBookingPage;
    private Payment_Mode paymentModePage;
    private NueGoTicketPage ticketPage;

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
                "--remote-allow-origins=*",
                "--start-maximized"
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
        ticketPage = new NueGoTicketPage(driver);
    }

    // ---------------------- TEST CASES -----------------------------
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

    @Test(priority = 2, dependsOnMethods = {"testLogin"}, description = "Handle homepage popups and search bus", retryAnalyzer = RetryAnalyzer.class)
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

    @Test(priority = 3, dependsOnMethods = {"testHomePageActions"}, description = "Apply filters", retryAnalyzer = RetryAnalyzer.class)
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

    @Test(priority = 4, dependsOnMethods = {"testFilters"}, description = "Scroll and click seat on Bus Booking page", retryAnalyzer = RetryAnalyzer.class)
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

    @Test(priority = 5, dependsOnMethods = {"testBusBookingPageActions"}, description = "Select seat, pickup & drop points and click Book & Pay", retryAnalyzer = RetryAnalyzer.class)
    public void testSelectSeatAndProceedToPay() {
        test = extent.createTest("Seat Selection Test", "Select seats and proceed to payment");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id*='seat']")));
            seatPointsPage.selectSeats("5B", "6C", "2D", "7D", "9B", "2A");
            seatPointsPage.selectPickupPoint();
            Thread.sleep(1500);
            seatPointsPage.selectDropPoint();
            Thread.sleep(1500);
            seatPointsPage.clickBookAndPay();
            reviewBookingPage.handleDiscountPopup();

            WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement reviewOrPaymentElement = pageWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'Review Booking') or contains(text(),'Payment')]")
            ));

            Assert.assertTrue(reviewOrPaymentElement.isDisplayed(), "❌ Review or Payment page not reached");
            test.log(Status.PASS, "✅ Seat selection and Book & Pay successful");
        } catch (Exception e) {
            handleFailure("Seat selection and payment test failed", e);
        }
    }

    @Test(priority = 6, dependsOnMethods = {"testSelectSeatAndProceedToPay"}, description = "Verify Review Booking flow actions", retryAnalyzer = RetryAnalyzer.class)
    public void testReviewBookingFlow() {
        test = extent.createTest("Review Booking Flow", "Apply coupon and verify review booking actions");
        try {
            reviewBookingPage.clickApplyCoupon();
            test.log(Status.PASS, "✅ Review Booking flow completed successfully");
        } catch (Exception e) {
            handleFailure("Review Booking flow failed", e);
        }
    }

    // ✅ UPDATED PAYMENT FLOW TEST
    @Test(priority = 7, dependsOnMethods = {"testReviewBookingFlow"}, description = "Complete payment flow using NetBanking - Axis Bank", retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Flow")
    public void testPaymentFlow() {
        test = extent.createTest("Payment Flow Test", "Complete payment using NetBanking (Axis Bank)");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

            // Wait for Payment section to load
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'netbanking')]")));
            
            WebElement netBanking = driver.findElement(By.xpath("(//article[contains(text(),'NetBanking')])[2]"));
            Actions actions = new Actions(driver);
            actions.moveToElement(netBanking).click().perform();
            
            WebElement axisBank = driver.findElement(By.xpath("//article[normalize-space()='Axis Bank']"));
            Actions actions1 = new Actions(driver);
            actions.moveToElement(axisBank).click().perform();
            
    /*        WebElement axisBank2 = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/article[1]"));
            Actions actions2 = new Actions(driver);
            actions.moveToElement(axisBank2).click().perform();*/
            
            
            WebElement axisBank3 = driver.findElement(By.xpath("//img[contains(@src,'jp_boxedlayout_tick.png')]"));

             Actions actions3 = new Actions(driver);
            actions.moveToElement(axisBank3).click().perform();
            
            
            WebElement proceedtopay = driver.findElement(By.xpath("(//article[normalize-space(text())='Proceed to Pay'])[1]"));
            Actions actions4 = new Actions(driver);
            actions.moveToElement(proceedtopay).click().perform();
            
            WebElement proceedtopay1 = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[4]/div[1]/div[1]/div[2]"));
            Actions actions5 = new Actions(driver);
            actions.moveToElement(proceedtopay1).click().perform();
            
           
           
           WebElement txtoption = driver.findElement(By.xpath("//div[@id='txnStateDropdownText']"));
           Actions actions6 = new Actions(driver);
           actions.moveToElement(txtoption).click().perform();
          
           WebElement clkcharge = driver.findElement(By.xpath("//span[normalize-space(text())='CHARGED']"));
           Actions actions7 = new Actions(driver);
           actions.moveToElement(clkcharge).click().perform();
           
           
           WebElement submitbtn = driver.findElement(By.xpath("//button[@id='submitButton']"));
           Actions actions8 = new Actions(driver);
           actions.moveToElement(submitbtn).click().perform();
        
            System.out.println("🧭 Navigating through payment section...");
            paymentModePage.selectNetBankingAndAxisBank();
            Thread.sleep(2000);
            paymentModePage.completePaymentFlow();
            Thread.sleep(2000);
            paymentModePage.clickNetBanking();
            Thread.sleep(2000);
            paymentModePage.clickAxisBank();
            Thread.sleep(2000);
            paymentModePage.clickAxisBankTick();
            Thread.sleep(2000);
            paymentModePage.clickProceedToPay();
            Thread.sleep(2000);
            paymentModePage.clickProceedToclk();
            paymentModePage.clickDropdownText();
            Thread.sleep(2000);
            paymentModePage.clickCharged();
            Thread.sleep(2000);
            paymentModePage.clickSubmitButton();
           
           
            

            test.log(Status.PASS, "✅ Payment flow completed successfully (NetBanking → Axis Bank)");
        } catch (Exception e) {
            handleFailure("Payment Flow failed", e);
        } finally {
            try {
                driver.switchTo().defaultContent();
            } catch (Exception ignored) {}
        }
    }

    @Test(priority = 8, dependsOnMethods = {"testPaymentFlow"}, description = "Verify ticket confirmation page actions", retryAnalyzer = RetryAnalyzer.class)
    public void testTicketPageFlow() {
        test = extent.createTest("Ticket Page Flow", "Validate post-booking actions on Ticket Confirmation page");
        try {
            ticketPage.clickDontAllowIfVisible();
            ticketPage.clickWhatsapp();
            Thread.sleep(3000);
            ticketPage.clickTextMessage();
            Thread.sleep(3000);
            ticketPage.scrollDown();
            Thread.sleep(3000);
            ticketPage.clickFareDetail();
            Thread.sleep(3000);
            ticketPage.closeFareDetail();
            Thread.sleep(3000);
            ticketPage.scrollDown2();
            ticketPage.clickShareOption();
            Thread.sleep(3000);
            ticketPage.closeShareScreen();
            Thread.sleep(3000);
            ticketPage.clickETicket();
            ticketPage.clickCopyLink();
            ticketPage.clickDownloadTicket();
            Thread.sleep(3000);
            ticketPage.clickChangeBooking();
            Thread.sleep(3000);
            test.log(Status.PASS, "✅ Ticket page interactions executed successfully");
        } catch (Exception e) {
            handleFailure("Ticket Page flow failed", e);
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
    @AfterClass(alwaysRun = true)
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

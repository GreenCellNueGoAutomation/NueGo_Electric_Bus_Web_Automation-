package nuego_automation_project;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ✅ Allure Metadata for the suite
@Epic("NueGo Web Automation")
@Feature("Complete Booking Flow")
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

    // ✅ ExtentReports setup
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeSuite(alwaysRun = true)
    public void initializeReports() {
        // Create folder with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = "test-output/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

        // 🎨 Beautiful dark theme customization
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("🚀 NueGo Automation Test Report");
        sparkReporter.config().setReportName("NueGo Web Automation - Regression Results");
        sparkReporter.config().setEncoding("utf-8");
        sparkReporter.config().setCss(
            ".badge-primary { background-color: #00bfa5; } " +
            ".nav-wrapper { background-color: #212121; } " +
            ".card { border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.3); } " +
            "body { background-color: #121212; color: #e0e0e0; font-family: 'Segoe UI'; }"
        );

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Tester", "Sumedh Sonawane");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Project", "NueGo Web Automation");

        // ✅ Initialize Allure Listener
        org.testng.TestNG testng = new org.testng.TestNG();
        testng.addListener(new AllureTestNg());

        System.out.println("📘 Extent and Allure Reports initialized successfully.");
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
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    @Description("Verify user can successfully login using valid mobile number and OTP.")
    public void testLogin() {
        test = extent.createTest("Login Test", "Login to the application using mobile and OTP");
        try {
            Allure.step("Open NueGo website");
            driver.get("https://greencell-nuego-web.web.app/");

            Allure.step("Enter mobile number and OTP");
            loginPage.login("7385109680", "1234");

            Allure.step("Verify redirection to Home Page");
            wait.until(ExpectedConditions.urlContains("home"));
            test.pass("✅ Login successful and navigated to Home Page");
            Allure.step("Login successful");
        } catch (Exception e) {
            attachScreenshotToAllure();
            test.fail("❌ Login failed: " + e.getMessage());
            Allure.addAttachment("Failure Reason", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 2, dependsOnMethods = {"testLogin"}, description = "Handle homepage popups and search bus")
    @Severity(SeverityLevel.NORMAL)
    @Story("Home Page Flow")
    @Description("Close popups, search for a bus route, and verify the results page.")
    public void testHomePageActions() {
        test = extent.createTest("Home Page Test", "Handle popups and search buses");
        try {
            Allure.step("Close popup if present");
            homePage.closePopupIfPresent();

            Allure.step("Search bus from Agra to Bassi");
            homePage.searchBus("Agra", "Bassi");

            test.pass("✅ Bus search performed successfully (Agra → Bassi)");
            Allure.step("Bus search completed successfully");
        } catch (Exception e) {
            attachScreenshotToAllure();
            test.fail("❌ Home Page actions failed: " + e.getMessage());
            Allure.addAttachment("Failure Reason", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 3, dependsOnMethods = {"testLogin"}, description = "Apply filters: Timer, Boarding & Dropping points")
    @Severity(SeverityLevel.MINOR)
    @Story("Bus Filtering")
    @Description("Verify timer, boarding, and dropping point filters work correctly.")
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

            test.pass("✅ Filters applied successfully");
        } catch (Exception e) {
            attachScreenshotToAllure();
            test.fail("❌ Filter test failed: " + e.getMessage());
            Allure.addAttachment("Failure Reason", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    // ---------------------- REPORTING -----------------------------

    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public void attachScreenshotToAllure() {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Failed Step Screenshot", new ByteArrayInputStream(screenshot));
        } catch (Exception ignored) {
        }
    }

    @AfterMethod(alwaysRun = true)
    public void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "❌ Test Case Failed: " + result.getName());
            test.log(Status.FAIL, "Error: " + result.getThrowable());
            attachScreenshotToAllure();
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
        System.out.println("📊 Extent Report generated successfully!");
    }
}

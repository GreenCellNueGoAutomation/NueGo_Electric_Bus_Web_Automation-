package nuego_automation_project;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class NuegoBookingTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private HomePage homePage;
    private BusBookingPage bookingPage;
    private Filters filtersPage;
    private SelectSeatPoints seatPointsPage;

    @BeforeClass
    public void setUp() {
        // Setup ChromeDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--ignore-certificate-errors"); // Ignore SSL warnings
        options.addArguments("--allow-insecure-localhost");  // Allow insecure localhost
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Initialize page objects
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingPage = new BusBookingPage(driver);
        filtersPage = new Filters(driver);
        seatPointsPage = new SelectSeatPoints(driver);
    }

    /*
    @Test(priority = 1, description = "Login to application using mobile and OTP")
    public void testLogin() throws InterruptedException {
        driver.get("https://greencell-nuego-web.web.app/");
        System.out.println("Navigated to application URL");

        loginPage.login("7385109680", "1234"); // Replace with valid test OTP
        System.out.println("Login successful (mobile + OTP entered)");

        // Small wait to stabilize UI after login
        Thread.sleep(2000);
    }
    */

    @Test(priority = 1, description = "Handle homepage popups and search bus")
    public void testHomePageActions() {
        driver.get("https://greencell-nuego-web.web.app/"); // Open URL here
        homePage.closePopupIfPresent();
        homePage.searchBus("Agra", "Bassi");
        System.out.println("Searched for buses from Agra to Bassi");
    }

    @Test(priority = 2, description = "Apply filters: Timer, Boarding & Dropping points")
    public void testFilters() {
        String timer = filtersPage.selectTimer();
        Assert.assertNotNull(timer, "Timer filter not applied!");
        System.out.println("Timer Applied: " + timer);

        filtersPage.clickBoardingPoint();
        String boardingSelected = filtersPage.selectBoardingCheckbox();
        Assert.assertNotNull(boardingSelected, "Boarding point not selected!");
        System.out.println("Boarding Point selected: " + boardingSelected);

        filtersPage.clickDroppingPoint();
        String droppingSelected = filtersPage.selectDroppingCheckboxAndReset();
        Assert.assertNotNull(droppingSelected, "Dropping point not selected!");
        System.out.println("Dropping Point selected: " + droppingSelected);
    }

    @Test(priority = 3, description = "Select seat and complete booking")
    public void testSeatSelectionAndBooking() {
        bookingPage.scrollDownSmall();
        bookingPage.clickSeat();

        seatPointsPage.selectSeats("1A");
        seatPointsPage.selectPickupPointByName("Eidgah Bus Stan...");
        seatPointsPage.selectDropPointByName("Bassi Chowk");
        seatPointsPage.clickBookAndPay();

        System.out.println("Booking flow completed successfully");
    }

    //@AfterClass(alwaysRun = true)
    //public void tearDown() {
    //    if (driver != null) {
    //        driver.quit();
    //        System.out.println("Browser closed");
    //    }
    //}
}

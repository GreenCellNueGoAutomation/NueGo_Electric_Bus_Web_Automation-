package nuego_automation_project;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import nuego_automation_project.base.BasePage;

public class HomePage{

    WebDriver driver;
    WebDriverWait wait;

    public static final String BASE_URL = "https://greencell-nuego-web.web.app/";

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openHomePage() {
        driver.get(BASE_URL);
        System.out.println("Opened Nuego Booking Website: " + BASE_URL);
    }

    public void closePopupIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.elementToBeClickable(By.id("moe-dontallow_button"))).click();
            System.out.println("Popup closed successfully.");
        } catch (Exception e) {
            System.out.println("Popup not displayed, continuing...");
        }
    }

    public void searchBus(String sourceCity, String destCity) {
        try {
            System.out.println("Entering source: " + sourceCity);
            WebElement source = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Enter Source']")));
            source.clear();
            source.sendKeys(sourceCity);
            Thread.sleep(1500);

            System.out.println("Entering destination: " + destCity);
            WebElement dest = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Enter Destination']")));
            dest.clear();
            dest.sendKeys(destCity);
            Thread.sleep(500);

            System.out.println("Select seat type...");
            WebElement seat = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[normalize-space()='Seater']")));
            seat.click();
            System.out.println("Seater selected");
            Thread.sleep(500);

            seat = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[normalize-space()='Sleeper']")));
            seat.click();
            System.out.println("Sleeper selected");

            // Click calendar
            WebElement calendarIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@alt='Calendar']")));
            calendarIcon.click();
            System.out.println("Calendar icon clicked.");

            // Select future date → tomorrow instead of today
            WebElement todayDate = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(@class,'react-calendar__tile--now')]")));
            WebElement tomorrowDate = todayDate.findElement(By.xpath("following-sibling::button[1]"));
            wait.until(ExpectedConditions.elementToBeClickable(tomorrowDate)).click();
            System.out.println("Future date (tomorrow) selected.");

            // Click search
            System.out.println("Clicking Search button...");
            WebElement search = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='homepage-d-search-button']")));
            search.click();
            System.out.println("Search button clicked successfully.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to search bus: " + e.getMessage(), e);
        }
    }
}

package nuego_automation_project;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BusBookingPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Constructor
public BusBookingPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
}

// Scroll down a little with wait
public void scrollDownSmall() {
    System.out.println("Scrolling down slightly...");
((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
    waitForPageAction();
}

// Click seat with extra waits and post-click scroll
public void clickSeat() {
    By seatLocator = By.xpath("//div[@class='col-8']//div[2]//div[1]//div[1]//div[2]//div[2]//div[1]//div[1]//div[2]//button[1]//p[1]");

System.out.println("Waiting for seat to be visible...");
WebElement seat = wait.until(ExpectedConditions.visibilityOfElementLocated(seatLocator));

System.out.println("Scrolling seat into view...");
((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", seat);
waitForPageAction();

System.out.println("Waiting for seat to be clickable...");
wait.until(ExpectedConditions.elementToBeClickable(seat)).click();
System.out.println("Seat clicked successfully!");
waitForPageAction();

// Extra scenario: small scroll after clicking seat
System.out.println("Scrolling slightly after seat click...");
((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 700);");
    waitForPageAction();
}

// Helper wait after each action
private void waitForPageAction() {
    try {
        Thread.sleep(2000); // 2 seconds delay after each step
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class SelectSeatPoints {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---------------- Constructor ---------------- //
    public SelectSeatPoints(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ---------------- Actions ---------------- //

    // Overloaded method to accept multiple seat numbers
    public void selectSeats(String... seatNumbers) {
        selectSeats(Arrays.asList(seatNumbers));
    }

    // Main method that works with a List of seat numbers
    public void selectSeats(List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            By seatLocator = By.xpath("//div[5]//div[1]//div[1]//img[1]");
            WebElement seat = wait.until(ExpectedConditions.presenceOfElementLocated(seatLocator));
            wait.until(ExpectedConditions.visibilityOf(seat));
            wait.until(ExpectedConditions.elementToBeClickable(seat));

            scrollIntoCenterView(seat);
            safeClick(seat);

            System.out.println("Selected seat: " + seatNumber);
            pause(1500);
        }
    }

    // ---------------- Pickup Methods ---------------- //

    public void selectPickupPoint() {
        selectPickupPointByName("Eidgah Bus Stan...");
    }

    public void selectPickupPointByName(String pickupName) {
        By pickupLocator = By.xpath("//div[normalize-space()='" + pickupName + "']");
        WebElement pickup = wait.until(ExpectedConditions.presenceOfElementLocated(pickupLocator));
        wait.until(ExpectedConditions.visibilityOf(pickup));
        wait.until(ExpectedConditions.elementToBeClickable(pickup));

        scrollIntoCenterView(pickup);
        safeClick(pickup);

        System.out.println("Pickup point selected: " + pickupName);
        pause(2000);
    }

    // ---------------- Drop Methods ---------------- //

    public void selectDropPoint() {
        selectDropPointByName("Bassi Chowk");
    }

    public void selectDropPointByName(String dropName) {
        By dropLocator = By.xpath("//div[@class='main_place'][normalize-space()='" + dropName + "']");
        WebElement drop = wait.until(ExpectedConditions.presenceOfElementLocated(dropLocator));
        wait.until(ExpectedConditions.visibilityOf(drop));
        wait.until(ExpectedConditions.elementToBeClickable(drop));

        scrollIntoCenterView(drop);
        safeClick(drop);

        System.out.println("Drop point selected: " + dropName);
        pause(2000);
    }

    // ---------------- Book & Pay ---------------- //

    public void clickBookAndPay() {
        By bookPayLocator = By.xpath("//button[normalize-space()='Book & Pay']");
        WebElement bookPay = wait.until(ExpectedConditions.presenceOfElementLocated(bookPayLocator));
        wait.until(ExpectedConditions.visibilityOf(bookPay));
        wait.until(ExpectedConditions.elementToBeClickable(bookPay));

        // Scroll button into the center of viewport
        scrollIntoCenterView(bookPay);

        safeClick(bookPay);
        System.out.println("Clicked Book & Pay");
        pause(1000);

        // Scroll up half the viewport after clicking to center next section
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollBy({top: -window.innerHeight/2, behavior: 'smooth'});"
        );
        pause(800);
    }

    // ---------------- Utility Methods ---------------- //

    // Scroll element to center of viewport
    private void scrollIntoCenterView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth', block:'center', inline:'center'});", el
        );
        pause(600);
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    private void safeClick(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}

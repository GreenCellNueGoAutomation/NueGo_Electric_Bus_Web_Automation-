package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

public class SelectSeatPoints {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---------------- Constructor ---------------- //
    public SelectSeatPoints(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ---------------- Main Seat Selection Logic ---------------- //

    public void selectSeats(String... seatNumbers) {
        selectSeats(Arrays.asList(seatNumbers));
    }

    public void selectSeats(List<String> seatNumbers) {
        int seatsSelected = 0;

        for (String seatNumber : seatNumbers) {
            String xpath = getSeatXPath(seatNumber);
            if (xpath == null) continue;

            try {
                By seatLocator = By.xpath(xpath);
                WebElement seat = wait.until(ExpectedConditions.presenceOfElementLocated(seatLocator));

                scrollIntoCenterView(seat);

                // Check if seat is available
                if (isSeatAvailable(seat)) {
                    safeClick(seat);
                    System.out.println("✅ Selected seat: " + seatNumber);
                    seatsSelected++;
                    pause(1200);

                    if (seatsSelected >= 1) break; // ✅ Select only ONE seat
                } else {
                    System.out.println("⚠️ Seat " + seatNumber + " is already booked, trying next...");
                }

            } catch (Exception e) {
                System.out.println("⚠️ Seat " + seatNumber + " not found or not clickable. Trying next...");
            }
        }

        // If not enough seats selected, pick from available automatically
        if (seatsSelected < 1) {
            System.out.println("🔁 Preferred seat not available. Selecting one available seat automatically...");
            selectAnyAvailableSeats(1 - seatsSelected); // ✅ Only 1 needed
        }
    }

    // ---------------- Helper: Check seat availability ---------------- //

    private boolean isSeatAvailable(WebElement seat) {
        try {
            String classAttr = seat.getAttribute("class");
            String aria = seat.getAttribute("aria-disabled");
            boolean disabled = "true".equalsIgnoreCase(aria);
            return !disabled && (classAttr == null || !classAttr.contains("booked"));
        } catch (Exception e) {
            return false;
        }
    }

    // ---------------- Helper: Auto-select from available ---------------- //

    private void selectAnyAvailableSeats(int neededSeats) {
        List<WebElement> availableSeats = driver.findElements(By.xpath("//img[contains(@src, 'seat') and not(contains(@class,'booked'))]"));
        int count = 0;

        for (WebElement seat : availableSeats) {
            try {
                scrollIntoCenterView(seat);
                safeClick(seat);
                count++;
                System.out.println("✅ Auto-selected available seat #" + count);
                pause(1000);
                if (count >= neededSeats) break; // ✅ Stop after selecting 1
            } catch (Exception ignored) {}
        }

        if (count < neededSeats) {
            System.out.println("❌ Unable to select a seat automatically.");
        }
    }

    // ---------------- Seat Mapping ---------------- //

    private String getSeatXPath(String seatNumber) {
        switch (seatNumber) {
            case "5B":
                return "//div[24]//div[1]//div[1]//img[1]";
            case "6C":
                return "//div[27]//div[1]//div[1]//img[1]";
            case "2D":
                return "//div[6]//div[1]//div[1]//img[1]";
            case "7D":
                return "//div[31]//div[1]//div[1]//img[1]";
            default:
                return null;
        }
    }

    // ---------------- Pickup & Drop Selection ---------------- //

    public void selectPickupPoint() {
        selectPickupPointByName("Eidgah Bus Stan...");
    }

    public void selectPickupPointByName(String pickupName) {
        By pickupLocator = By.xpath("//div[normalize-space()='" + pickupName + "']");
        WebElement pickup = wait.until(ExpectedConditions.presenceOfElementLocated(pickupLocator));
        scrollIntoCenterView(pickup);
        safeClick(pickup);
        System.out.println("📍 Pickup point selected: " + pickupName);
        pause(1500);
    }

    public void selectDropPoint() {
        selectDropPointByName("Bassi Chowk");
    }

    public void selectDropPointByName(String dropName) {
        By dropLocator = By.xpath("//div[@class='main_place'][normalize-space()='" + dropName + "']");
        WebElement drop = wait.until(ExpectedConditions.presenceOfElementLocated(dropLocator));
        scrollIntoCenterView(drop);
        safeClick(drop);
        System.out.println("📍 Drop point selected: " + dropName);
        pause(1500);
    }

    // ---------------- Book & Pay ---------------- //

    public void clickBookAndPay() {
        By bookPayLocator = By.xpath("//button[normalize-space()='Book & Pay']");
        WebElement bookPay = wait.until(ExpectedConditions.presenceOfElementLocated(bookPayLocator));
        scrollIntoCenterView(bookPay);
        safeClick(bookPay);
        System.out.println("💳 Clicked Book & Pay");
        pause(1000);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy({top: -window.innerHeight/2, behavior: 'smooth'});");
        pause(800);
    }

    // ---------------- Utility Methods ---------------- //

    private void scrollIntoCenterView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth', block:'center', inline:'center'});", el
        );
        pause(500);
    }

    private void safeClick(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}

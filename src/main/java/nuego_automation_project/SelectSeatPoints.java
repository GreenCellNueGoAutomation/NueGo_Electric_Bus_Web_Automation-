package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SelectSeatPoints {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---------------- Constructor ---------------- //
    public SelectSeatPoints(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ---------------- Main Seat Selection Logic ---------------- //

    // No-arg method used in your test – always select 3 seats
    public void selectSeats() {
        selectAnyAvailableSeats(3);   // always select 3 seats
    }

    // Kept same signature so existing tests compile, but now dynamic count
    public void selectSeats(String... seatNumbers) {
        int neededSeats = (seatNumbers == null || seatNumbers.length == 0)
                ? 3
                : seatNumbers.length;
        selectAnyAvailableSeats(neededSeats);
    }

    public void selectSeats(List<String> seatNumbers) {
        int neededSeats = (seatNumbers == null || seatNumbers.isEmpty())
                ? 3
                : seatNumbers.size();
        selectAnyAvailableSeats(neededSeats);
    }

    // ---------------- Helper: Check seat availability ---------------- //

    private boolean isSeatAvailable(WebElement seat) {
        try {
            String classAttr = seat.getAttribute("class");
            String aria = seat.getAttribute("aria-disabled");
            boolean disabled = "true".equalsIgnoreCase(aria);

            // adjust these keywords as per your actual UI
            boolean isBooked = classAttr != null && classAttr.contains("booked");
            boolean isSelected = classAttr != null && classAttr.contains("seat-selected");

            // we only want free, not-booked, not-selected seats
            return !disabled && !isBooked && !isSelected;
        } catch (Exception e) {
            return false;
        }
    }

    // ---------------- Helper: Auto-select from available ---------------- //

    /**
     * Uses generic available seat locator and clicks neededSeats seats.
     * We re-fetch seats each time to avoid stale elements after DOM updates.
     */
    private void selectAnyAvailableSeats(int neededSeats) {
        By availableSeatsLocator = By.xpath("//*[@class='position-relative  seat-available']");

        int count = 0;
        int safetyGuard = 0; // to prevent infinite loop if something goes wrong

        while (count < neededSeats && safetyGuard < 20) {
            safetyGuard++;

            List<WebElement> availableSeats = driver.findElements(availableSeatsLocator);
            if (availableSeats.isEmpty()) {
                System.out.println("❌ No available seats found using generic locator.");
                break;
            }

            boolean clickedThisRound = false;

            for (WebElement seat : availableSeats) {
                try {
                    if (!isSeatAvailable(seat)) {
                        continue;
                    }

                    scrollIntoCenterView(seat);
                    safeClick(seat);
                    count++;
                    clickedThisRound = true;
                    System.out.println("✅ Auto-selected available seat #" + count);
                    pause(1000);

                    if (count >= neededSeats) {
                        break;
                    }
                } catch (StaleElementReferenceException sere) {
                    // DOM changed, we'll retry in next loop iteration
                    System.out.println("⚠️ StaleElementReferenceException on seat, retrying...");
                } catch (Exception e) {
                    System.out.println("⚠️ Error clicking seat: " + e.getMessage());
                }
            }

            if (!clickedThisRound) {
                // couldn't click any seat in this pass, so no point looping forever
                System.out.println("❌ Could not click additional seats in this pass.");
                break;
            }
        }

        if (count < neededSeats) {
            System.out.println("❌ Unable to select required number of seats automatically. Selected: " + count);
        } else {
            System.out.println("✅ Successfully selected " + count + " seats.");
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

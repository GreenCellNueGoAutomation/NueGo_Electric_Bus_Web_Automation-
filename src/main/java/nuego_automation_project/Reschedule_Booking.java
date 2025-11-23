package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class Reschedule_Booking {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    // ---------- LOCATORS ----------

    // "Change booking" option on confirmation page
    private final By changeBookingOption = By.xpath("//p[normalize-space()='Change booking']");

    // Reschedule tab (local active tab)
    private final By rescheduleTab = By.xpath("//p[normalize-space()='Reschedule']");

    // ALT Reschedule tab (absolute or backup – adjust as needed)
    private final By rescheduleTabAlt1 = By.xpath(
            "/html[1]/body[1]/div[2]/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[2]"
    );

    // Calendar icon
    private final By calendarIcon = By.xpath(
            "//div[contains(@class,'padding-x-local')]//div[contains(@class,'position-relative')]//img[contains(@alt,'alt')]"
    );

    // Calendar dates – generic locator for date cells
    private final By calendarAllDates = By.xpath(
            "//div[contains(@class,'DayPicker-Day') or contains(@class,'day') or self::td]"
    );

    // "View Coaches" button
    private final By viewCoachesButton = By.xpath("//p[normalize-space()='View Coaches']");

    // ---------- CONSTRUCTOR ----------

    public Reschedule_Booking(WebDriver driver) {
        this.driver  = driver;
        this.wait    = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.actions = new Actions(driver);
    }

    // ---------- COMMON UTILITIES ----------

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Scroll element into view using JavaScript.
     */
    private WebElement scrollIntoView(By locator) {
        WebElement el = waitVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
        pause(800);
        return el;
    }

    /**
     * Click using Actions after scrolling into view.
     */
    private void clickWithActionsAfterScroll(By locator) {
        WebElement el = scrollIntoView(locator);
        try {
            actions.moveToElement(el).click().perform();
        } catch (Exception e) {
            // Fallback: JS click if Actions fails
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
        pause(1500);
    }

    // Try to get numeric day from the date cell (inner text like "15", "23")
    private Integer extractDayNumber(WebElement dateElement) {
        try {
            String text = dateElement.getText();
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            text = text.trim().replaceAll("[^0-9]", ""); // remove any non-digits
            if (text.isEmpty()) return null;
            return Integer.parseInt(text);
        } catch (Exception e) {
            return null;
        }
    }

    // ---------- PAGE ACTIONS ----------

    // 1) Click on "Change booking" (robust: clickable + scroll + Actions + JS fallback)
    public void clickChangeBooking() {
        try {
            System.out.println("🔍 Waiting for 'Change booking' to be clickable...");
            WebElement el = waitClickable(changeBookingOption);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", el);
            pause(800);

            try {
                actions.moveToElement(el).click().perform();
                System.out.println("✅ Clicked 'Change booking' via Actions");
            } catch (Exception inner) {
                System.out.println("⚠️ Actions click failed on 'Change booking', trying JS click: " + inner.getMessage());
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                System.out.println("⚡ JS clicked 'Change booking'");
            }

            pause(1500);
        } catch (TimeoutException te) {
            // Extra debug info if not clickable
            System.out.println("❌ Timeout waiting for 'Change booking' clickable: " + te.getMessage());
            debugChangeBookingPresence();
            throw te;
        } catch (Exception e) {
            System.out.println("❌ Error clicking 'Change booking': " + e.getMessage());
            debugChangeBookingPresence();
            throw e;
        }
    }

    // Debug helper – log presence/visibility of Change booking element
    private void debugChangeBookingPresence() {
        try {
            List<WebElement> list = driver.findElements(changeBookingOption);
            System.out.println("🔎 'Change booking' elements found: " + list.size());
            if (!list.isEmpty()) {
                WebElement el = list.get(0);
                System.out.println("    Text: " + el.getText());
                System.out.println("    Displayed: " + el.isDisplayed());
                System.out.println("    Enabled: " + el.isEnabled());
            }
        } catch (Exception e) {
            System.out.println("❌ Error checking 'Change booking' presence: " + e.getMessage());
        }
    }

    // 2) Click on Reschedule tab (primary locator)
    public void clickRescheduleTab() {
        clickWithActionsAfterScroll(rescheduleTab);
    }

    // 2b) Click on Reschedule tab (alternate abs path)
    public void clickRescheduleTabAlt1() {
        clickWithActionsAfterScroll(rescheduleTabAlt1);
    }

    // 3) Click calendar icon
    public void openCalendar() {
        clickWithActionsAfterScroll(calendarIcon);
        // small extra wait for calendar to fully render
        pause(1000);
    }

    /**
     * Selects an automatic future date in the calendar.
     * Uses scroll + Actions click on the chosen date.
     */
    public void selectFutureDateAutomatically() {
        // Calendar should already be open
        pause(1000); // wait to ensure calendar is rendered

        // small scroll just in case the grid is below
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 250);");
        pause(500);

        // wait that at least one date is visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(calendarAllDates));

        List<WebElement> dates = driver.findElements(calendarAllDates);
        if (dates == null || dates.isEmpty()) {
            throw new RuntimeException("No date elements found in calendar.");
        }

        LocalDate today = LocalDate.now();
        int todayDay = today.getDayOfMonth();

        WebElement firstEnabledDate = null;

        // Pass 1: look for a date strictly after today (future day in same month view)
        for (WebElement date : dates) {
            try {
                String classes = date.getAttribute("class");

                // Skip disabled/past/unavailable dates
                if (classes != null &&
                        (classes.toLowerCase().contains("disabled")
                         || classes.toLowerCase().contains("past")
                         || classes.toLowerCase().contains("unavailable"))) {
                    continue;
                }

                if (!date.isDisplayed() || !date.isEnabled()) {
                    continue;
                }

                // Remember first enabled date as fallback
                if (firstEnabledDate == null) {
                    firstEnabledDate = date;
                }

                Integer dayNum = extractDayNumber(date);
                if (dayNum == null) {
                    continue;
                }

                // Future date check: day number > today's date
                if (dayNum > todayDay) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({block: 'center'});", date);
                    pause(500);
                    try {
                        actions.moveToElement(date).click().perform();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", date);
                    }
                    pause(2000);
                    return;
                }

            } catch (StaleElementReferenceException ignored) {
                // Calendar DOM changed, ignore this element
            }
        }

        // Pass 2: if we did not find strictly future date, click first enabled date as fallback
        if (firstEnabledDate != null) {
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});", firstEnabledDate);
                pause(500);
                try {
                    actions.moveToElement(firstEnabledDate).click().perform();
                } catch (Exception e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstEnabledDate);
                }
                pause(2000);
                return;
            } catch (StaleElementReferenceException ignored) {
                // fall through
            }
        }

        throw new RuntimeException("No selectable future date found in calendar.");
    }

    // 4) Click on "View Coaches" button
    public void clickViewCoaches() {
        clickWithActionsAfterScroll(viewCoachesButton);
    }

    // ---------- HIGH-LEVEL FLOW ----------

    /**
     * Full reschedule flow:
     * 1. Click Change booking
     * 2. Click Reschedule tab
     * 3. Open calendar
     * 4. Auto-select future date
     * 5. Click View Coaches
     */
    public void performRescheduleBookingFlow() {
        clickChangeBooking();

        try {
            clickRescheduleTab();       // primary locator with scroll + Actions
        } catch (Exception e) {
            System.out.println("⚠️ Primary Reschedule tab click failed, trying ALT locator...");
            clickRescheduleTabAlt1();   // backup abs path if needed
        }

        openCalendar();
        selectFutureDateAutomatically();
        clickViewCoaches();
    }

}

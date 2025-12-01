// package nuego_automation_project;
//
// import org.openqa.selenium.*;
// import org.openqa.selenium.interactions.Actions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
//
// import java.time.Duration;
// import java.util.List;
//
// public class MyBookings {
//
//     // WebDriver instance to control the browser
//     private final WebDriver driver;
//     // Explicit wait to handle synchronization
//     private final WebDriverWait wait;
//     // Actions class for advanced user interactions (hover, click, etc.)
//     private final Actions actions;
//
//     // Base URL of the application (Home page)
//     private static final String BASE_URL = "https://greencell-nuego-web.web.app/";
//
//     // Constructor to initialize driver, wait and actions
//     public MyBookings(WebDriver driver) {
//         this.driver = driver;
//         this.wait = new WebDriverWait(driver, Duration.ofSeconds(25)); // max 25s wait
//         this.actions = new Actions(driver);
//     }
//
//     // ================== LOCATORS ================== //
//
//     // "My Bookings" dropdown on header / menu
//     private By myBookingsDropdown = By.xpath("//p[normalize-space()='My Bookings']");
//
//     // All booking cards (Completed / Upcoming / Cancelled lists)
//     private By bookingCards = By.xpath("//div[contains(@class,'booking-card-wrapper')]//div[contains(@class,'booking-card')]");
//
//     // Tab options inside My Bookings dropdown
//     private By completedTripsOption = By.xpath("//p[normalize-space()='Completed Trips']");
//     private By upcomingTripsOption = By.xpath("//p[normalize-space()='Upcoming Trips']");
//     private By cancelledTripsOption = By.xpath("//p[normalize-space()='Cancelled Trips']");
//
//     // Common "View Tickets" button locator for all sections
//     private By viewTicketsButton = By.cssSelector("div.viewTickets.ubuntu-500w-18s-28h");
//
//     // ================== COMMON METHODS ================== //
//
//     /**
//      * Scroll the page so that the given element comes into center view.
//      * Uses JavaScript scrollIntoView.
//      */
//     private void scrollIntoView(WebElement element) {
//         ((JavascriptExecutor) driver).executeScript(
//                 "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
//     }
//
//     /**
//      * Scroll to the element and then click it using Actions.
//      * This helps when normal click gets intercepted.
//      */
//     private void scrollIntoViewAndClick(WebElement element) {
//         scrollIntoView(element);
//         // Wait until element is clickable
//         wait.until(ExpectedConditions.elementToBeClickable(element));
//         // Use Actions to move and click
//         actions.moveToElement(element).click().perform();
//     }
//
//     /**
//      * Wait for a single element to become visible and return it.
//      */
//     private WebElement waitForVisible(By locator) {
//         return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
//     }
//
//     /**
//      * Wait for all elements matching locator to become visible and return list.
//      */
//     private List<WebElement> waitForVisibleElements(By locator) {
//         return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
//     }
//
//     /**
//      * Scroll down to the bottom of the page.
//      * Mostly used to view full ticket details.
//      */
//     private void scrollDownFullPage() {
//         ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
//     }
//
//     /**
//      * Navigate one step back in browser history.
//      * Used after opening a ticket to return to booking list.
//      */
//     private void navigateBack() {
//         try {
//             driver.navigate().back();
//             System.out.println("🔙 Clicked browser back.");
//         } catch (Exception e) {
//             System.out.println("⚠️ Failed to navigate back: " + e.getMessage());
//         }
//     }
//
//     // ================== FLOW METHODS ================== //
//
//     /**
//      * Open the "My Bookings" dropdown from header.
//      */
//     public void openMyBookingsDropdown() {
//         // Wait till My Bookings is visible
//         WebElement dropdown = waitForVisible(myBookingsDropdown);
//         // Scroll and click
//         scrollIntoViewAndClick(dropdown);
//         System.out.println("Clicked My Bookings dropdown.");
//     }
//
//     /**
//      * Print all booking cards (text) visible in current trips tab.
//      * This is only for logging/debugging purpose.
//      */
//     public void printAllAvailableBookingsInList() {
//         // Wait till at least one booking card is visible
//         List<WebElement> cards = waitForVisibleElements(bookingCards);
//         System.out.println("Total booking cards found: " + cards.size());
//         int index = 1;
//         for (WebElement card : cards) {
//             // Scroll each card into view before printing
//             scrollIntoView(card);
//             System.out.println("---- Booking Card #" + index + " ----");
//             System.out.println(card.getText());      // Print the card details
//             System.out.println("------------------------------");
//             index++;
//         }
//     }
//
//     /**
//      * Click first visible "View Tickets" button in current tab (Completed/Upcoming/Cancelled).
//      * Uses scroll + Actions click + JS click fallback for reliability.
//      */
//     public void clickFirstViewTickets() {
//         try {
//             // small wait after any tab switch / list load to stabilize UI
//             Thread.sleep(1500);
//
//             // Find all visible View Tickets buttons
//             List<WebElement> tickets = waitForVisibleElements(viewTicketsButton);
//             if (tickets.isEmpty()) {
//                 System.out.println("❌ No 'View Tickets' button found in current tab.");
//                 return;
//             }
//
//             // Pick the first one
//             WebElement first = tickets.get(0);
//
//             // Scroll into view
//             scrollIntoView(first);
//             Thread.sleep(800);
//
//             // Try Actions click first
//             try {
//                 wait.until(ExpectedConditions.elementToBeClickable(first));
//                 actions.moveToElement(first).click().perform();
//                 System.out.println("✅ 'View Tickets' clicked using Actions.");
//             } catch (Exception e1) {
//                 System.out.println("⚠️ Actions click failed, trying JS click: " + e1.getMessage());
//                 try {
//                     // JS fallback click
//                     ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
//                     System.out.println("✅ 'View Tickets' clicked using JavaScript.");
//                 } catch (Exception e2) {
//                     System.out.println("❌ Failed to click 'View Tickets' even with JS: " + e2.getMessage());
//                 }
//             }
//
//         } catch (TimeoutException te) {
//             System.out.println("⚠️ Timeout waiting for 'View Tickets' button: " + te.getMessage());
//         } catch (Exception e) {
//             System.out.println("❌ Unexpected error in clickFirstViewTickets: " + e.getMessage());
//         }
//     }
//
//     // ========== COMPLETED TRIPS FLOW ========== //
//
//     /**
//      * Flow:
//      * 1. Open My Bookings
//      * 2. Click "Completed Trips" tab
//      * 3. Print all booking cards (optional logging)
//      * 4. Click first View Tickets
//      * 5. Scroll ticket to bottom
//      * 6. Go back to booking list using browser back
//      */
//     public void handleCompletedTrips() {
//         openMyBookingsDropdown();
//         scrollIntoViewAndClick(waitForVisible(completedTripsOption));
//         System.out.println("Selected Completed Trips.");
//
//         // Optional – log all completed booking cards
//         printAllAvailableBookingsInList();
//
//         // View first ticket
//         clickFirstViewTickets();
//
//         // Scroll through the opened ticket details
//         scrollDownFullPage();
//
//         // Navigate back to Completed Trips list (NOT home)
//         navigateBack();
//     }
//
//     // ========== UPCOMING TRIPS FLOW ========== //
//
//     /**
//      * Flow:
//      * 1. Open My Bookings
//      * 2. Click "Upcoming Trips" tab
//      * 3. Click first View Tickets
//      * 4. Scroll ticket to bottom
//      * 5. Go back to upcoming list using browser back
//      */
//     public void handleUpcomingTrips() {
//         openMyBookingsDropdown();
//         scrollIntoViewAndClick(waitForVisible(upcomingTripsOption));
//         System.out.println("Selected Upcoming Trips.");
//
//         // View first upcoming trip ticket
//         clickFirstViewTickets();
//
//         // Scroll through ticket details
//         scrollDownFullPage();
//
//         // Navigate back to Upcoming Trips list
//         navigateBack();
//     }
//
//     // ========== CANCELLED TRIPS FLOW ========== //
//
//     /**
//      * Flow:
//      * 1. Open My Bookings
//      * 2. Click "Cancelled Trips" tab
//      * 3. Print all booking cards (optional logging)
//      * 4. Click first View Tickets
//      * 5. Scroll ticket to bottom
//      * 6. Go back to cancelled list using browser back
//      */
//     public void handleCancelledTrips() {
//         openMyBookingsDropdown();
//         scrollIntoViewAndClick(waitForVisible(cancelledTripsOption));
//         System.out.println("Selected Cancelled Trips.");
//
//         // Optional – log all cancelled booking cards
//         printAllAvailableBookingsInList();
//
//         // View first cancelled ticket
//         clickFirstViewTickets();
//
//         // Scroll through ticket details
//         scrollDownFullPage();
//
//         // Navigate back to Cancelled Trips list
//         navigateBack();
//     }
//
//     /**
//      * Final step: Navigate back to Home page from anywhere.
//      * Uses direct URL instead of browser back to ensure we land on home correctly.
//      */
//     public void goBackToHomePage() {
//         try {
//             // Open base URL directly
//             driver.navigate().to(BASE_URL);
//             System.out.println("✅ Navigated back to Home page via direct URL");
//
//             // Wait until My Bookings dropdown is again visible to confirm home is loaded
//             wait.until(ExpectedConditions.visibilityOfElementLocated(myBookingsDropdown));
//         } catch (Exception e) {
//             System.out.println("❌ Failed to navigate to Home via URL: " + e.getMessage());
//         }
//     }
//
//     // ========== MASTER FLOW (OPTIONAL) ========== //
//
//     /**
//      * Full My Bookings regression flow:
//      * 1. Go through Completed Trips
//      * 2. Go through Upcoming Trips
//      * 3. Go through Cancelled Trips
//      * 4. Return to Home page
//      *
//      * You can call this once from your test to cover all three tabs.
//      */
//     public void runMyBookingsFullFlow() {
//         // 1) Completed Trips
//         handleCompletedTrips();
//
//         // 2) Upcoming Trips
//         handleUpcomingTrips();
//
//         // 3) Cancelled Trips
//         handleCancelledTrips();
//
//         // 4) Finally go to Home Page
//         goBackToHomePage();
//     }
// }

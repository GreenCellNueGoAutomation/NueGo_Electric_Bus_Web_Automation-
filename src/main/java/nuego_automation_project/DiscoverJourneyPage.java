// package nuego_automation_project;
// import org.openqa.selenium.*;
// import org.openqa.selenium.interactions.Actions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
//
// import java.time.Duration;
// import java.util.List;
//
// public class DiscoverJourneyPage {
//
//     private WebDriver driver;
//     private WebDriverWait wait;
//     private Actions actions;
//     private JavascriptExecutor js;
//
//     public DiscoverJourneyPage(WebDriver driver) {
//         this.driver = driver;
//         this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//         this.actions = new Actions(driver);
//         this.js = (JavascriptExecutor) driver;
//     }
//
//     // ---------- LOCATORS ----------
//
//     // Discover journey section (if needed, adjust to real locator)
//     private By discoverJourneySection = By.xpath("//*[contains(normalize-space(),'Discover') and contains(normalize-space(),'journey')]");
// 
//     // Visakhapatnam image
//     private By visakhapatnamImage = By.xpath("//img[@alt='visakhapatnam image']");
//
//     // Div with text 'I'
//     private By iLetterDiv = By.xpath("//div[normalize-space()='I']");
//
//     // Common route link (all routes use same structure)
//     // Specific example: Bhopal to Indore buses
//     private By bhopalToIndoreRoute = By.xpath("//a[contains(@class,'allBusLink') and contains(@class,'available-routes')]//h3[normalize-space()='Bhopal to Indore buses']");
//
//     // Ul container under coachroute div
//     private By coachRouteList = By.xpath("//div[@class='coachroute']//ul");
//
//     // Second li → third span → first a  (Book Now button)
//     private By secondLiBookNow = By.xpath("//li[2]//span[3]//a[1]");
//
//     // Tabs
//     private By seeRouteTab   = By.xpath("//p[normalize-space()='See Route']");
//     private By photosTab     = By.xpath("//p[normalize-space()='Photos']");
//     private By policiesTab   = By.xpath("//p[normalize-space()='Policies']");
//     private By aboutTab      = By.xpath("//p[normalize-space()='About']");
//
//     // ---------- UTIL ----------
//
//     private void scrollIntoView(WebElement element) {
//         js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
//     }
//
//     private void safeClick(By locator, String log) {
//         try {
//             WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
//             scrollIntoView(el);
//             actions.moveToElement(el).click().perform();
//             System.out.println("✅ " + log + " (Actions click)");
//         } catch (Exception e) {
//             try {
//                 WebElement el = driver.findElement(locator);
//                 scrollIntoView(el);
//                 js.executeScript("arguments[0].click();", el);
//                 System.out.println("⚡ " + log + " (JS click fallback)");
//             } catch (Exception ex) {
//                 System.out.println("❌ Failed: " + log + " | " + ex.getMessage());
//             }
//         }
//     }
//
//     private void sleep(long ms) {
//         try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
//     }
//
//     // ---------- STEPS IMPLEMENTATION ----------
//
//     /** Scroll to Discover journey section (optional helper) */
//     public void scrollToDiscoverJourney() {
//         try {
//             WebElement section = wait.until(ExpectedConditions.visibilityOfElementLocated(discoverJourneySection));
//             scrollIntoView(section);
//             System.out.println("✅ Scrolled to Discover journey section");
//         } catch (Exception e) {
//             js.executeScript("window.scrollBy(0, 600);");
//             System.out.println("⚠️ Discover journey section not clearly located, scrolled down as fallback");
//         }
//     }
//
//     /** Scroll down and click Visakhapatnam image */
//     public void clickVisakhapatnamImage() {
//         try {
//             WebElement img = wait.until(ExpectedConditions.elementToBeClickable(visakhapatnamImage));
//             scrollIntoView(img);
//             actions.moveToElement(img).click().perform();
//             System.out.println("✅ Clicked Visakhapatnam image");
//         } catch (Exception e) {
//             System.out.println("❌ Unable to click Visakhapatnam image: " + e.getMessage());
//         }
//     }
//
//     /** Click the div with text 'I' using Actions class */
//     public void clickILetterDiv() {
//         try {
//             WebElement iDiv = wait.until(ExpectedConditions.elementToBeClickable(iLetterDiv));
//             scrollIntoView(iDiv);
//             actions.moveToElement(iDiv).click().perform();
//             System.out.println("✅ Clicked 'I' div using Actions");
//         } catch (Exception e) {
//             System.out.println("❌ Unable to click 'I' div: " + e.getMessage());
//         }
//     }
//
//     /** Click the route "Bhopal to Indore buses" (uses common route structure) */
//     public void clickBhopalToIndoreRoute() {
//         safeClick(bhopalToIndoreRoute, "Clicked 'Bhopal to Indore buses' route");
//     }
//
//     /** Print all UI text inside //div[@class='coachroute']//ul */
//     public void printAllCoachRouteUI() {
//         try {
//             WebElement ul = wait.until(ExpectedConditions.visibilityOfElementLocated(coachRouteList));
//             scrollIntoView(ul);
//
//             List<WebElement> liElements = ul.findElements(By.tagName("li"));
//             System.out.println("📋 ==== Coach Route UI (li elements) ====");
//             if (liElements.isEmpty()) {
//                 System.out.println("ℹ️ No <li> elements found under coachroute ul");
//             } else {
//                 int index = 1;
//                 for (WebElement li : liElements) {
//                     String text = li.getText().trim();
//                     System.out.println(index + ") " + text);
//                     index++;
//                 }
//             }
//             System.out.println("📋 ==== End of Coach Route UI ====");
//
//         } catch (Exception e) {
//             System.out.println("❌ Failed to read coachroute ul content: " + e.getMessage());
//         }
//     }
//
//     /** Scroll and click Book Now button: //li[2]//span[3]//a[1] */
//     public void clickSecondLiBookNow() {
//         safeClick(secondLiBookNow, "Clicked Book Now button in second li");
//     }
//
//     /** Generic toggle method: click → wait → click again */
//     private void toggleTabTwice(By tabLocator, String tabName) {
//         try {
//             // First click
//             WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(tabLocator));
//             scrollIntoView(tab);
//             actions.moveToElement(tab).click().perform();
//             System.out.println("✅ Clicked " + tabName + " tab (1st time)");
//             sleep(1000);
//
//             // Second click
//             tab = wait.until(ExpectedConditions.elementToBeClickable(tabLocator));
//             scrollIntoView(tab);
//             actions.moveToElement(tab).click().perform();
//             System.out.println("✅ Clicked " + tabName + " tab (2nd time)");
//             sleep(1000);
//         } catch (Exception e) {
//             System.out.println("❌ Error toggling " + tabName + " tab: " + e.getMessage());
//         }
//     }
//
//     public void toggleSeeRouteTab() {
//         toggleTabTwice(seeRouteTab, "See Route");
//     }
//
//     public void togglePhotosTab() {
//         toggleTabTwice(photosTab, "Photos");
//     }
//
//     public void togglePoliciesTab() {
//         toggleTabTwice(policiesTab, "Policies");
//     }
//
//     public void toggleAboutTab() {
//         toggleTabTwice(aboutTab, "About");
//     }
//
//     // ---------- FULL FLOW (optional helper) ----------
//
//     /**
//      * Complete flow:
//      * 1) Scroll to Discover journey
//      * 2) Click Visakhapatnam image
//      * 3) Click "I" div
//      * 4) Click Bhopal to Indore route
//      * 5) Print all UI in coachroute ul
//      * 6) Click Book Now
//      * 7) Toggle See Route, Photos, Policies, About
//      */
// //    public void executeDiscoverJourneyFlow() {
// //        scrollToDiscoverJourney();
// //        clickVisakhapatnamImage();
// //        clickILetterDiv();
// //        clickBhopalToIndoreRoute();
// //        printAllCoachRouteUI();
// //        clickSecondLiBookNow();
// //        toggleSeeRouteTab();
// //        togglePhotosTab();
// //        togglePoliciesTab();
// //        toggleAboutTab();
// //    }
// }

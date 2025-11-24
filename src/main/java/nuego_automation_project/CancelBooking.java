package nuego_automation_project;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CancelBooking {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;

    // ✅ Constructor
    public CancelBooking(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25)); // increased timeout
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
    }

    // Locators
    private By cancelButton = By.xpath("//div[contains(@class,'d-flex justify-content-center active-tab-local')]//img[@alt='alt']");
    private By travelCheckboxPrimary = By.xpath("//label[p[text()='My travel plans have changed']]/input[@type='checkbox']");
    private By travelCheckboxAlternative = By.xpath("//div[@class='grey-area py-4']//div[1]//img[1]");
    private By submitButton = By.xpath("//button[@class='submit-button']");

    // Generic scroll and click method with wait
    private void scrollAndClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        actions.moveToElement(element).click().perform();
    }

    // 1️⃣ Click Cancel button
    public void clickCancelButton() {
        scrollAndClick(cancelButton);
    }

    // 2️⃣ Select checkbox "My travel plans have changed"
    public void selectTravelCheckbox() {
        try {
            scrollAndClick(travelCheckboxPrimary);
        } catch (Exception e) {
            scrollAndClick(travelCheckboxAlternative);
        }
    }

    // 3️⃣ Click Continue button
    public void clickContinueButton() {
        scrollAndClick(submitButton);
    }

    // 4️⃣ Click Refund button
    public void clickRefundButton() {
        scrollAndClick(submitButton);
    }

    // 5️⃣ Click Go to Home button
    public void clickGoHomeButton() {
        scrollAndClick(submitButton);
    }
}

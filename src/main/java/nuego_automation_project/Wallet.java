package nuego_automation_project;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Wallet {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;

    // Constructor
    public Wallet(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
    }

    // Open Wallet icon (no extra header wait)
    public void openWallet() {
        try {
            WebElement walletIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[name()='path' and contains(@d,'M20 3.5H5C')]")
            ));
            js.executeScript("arguments[0].scrollIntoView(true);", walletIcon);
            Thread.sleep(1500);
            actions.moveToElement(walletIcon).click().perform();
            Thread.sleep(1500);
            System.out.println("✅ Wallet icon clicked successfully.");
        } catch (Exception e) {
            System.out.println("❌ Wallet icon click failed: " + e.getMessage());
        }
    }

    // Add Money to wallet and then let test handle payment + home navigation
    public void addMoney() {
        try {
            // ===== Amount input =====
            WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='common-input-wrapper d-flex align-items-center']/input")
            ));

            // Bring input nicely into view (center of screen)
            js.executeScript(
                    "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                    amountInput
            );
            Thread.sleep(1500);

            // Small offset in case header is overlapping
            js.executeScript("window.scrollBy(0, -80);");
            Thread.sleep(800);

            amountInput.clear();
            amountInput.sendKeys("10");
            Thread.sleep(1500);

            // ===== Click Add Money button =====
            boolean clicked = false;
            int attempts = 0;

            while (!clicked && attempts < 3) {
                try {
                    WebElement addMoneyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//p[@class='open-600w-18s-28h my-auto mb-0 white-color' and normalize-space()='Add money']")
                    ));

                    js.executeScript(
                            "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                            addMoneyBtn
                    );
                    Thread.sleep(1000);

                    actions.moveToElement(addMoneyBtn).click().perform();
                    Thread.sleep(1500);

                    clicked = true;
                } catch (Exception e) {
                    WebElement addMoneyBtnAlt = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//p[contains(normalize-space(),'Add money')]")
                    ));

                    js.executeScript(
                            "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                            addMoneyBtnAlt
                    );
                    Thread.sleep(1000);

                    actions.moveToElement(addMoneyBtnAlt).click().perform();
                    Thread.sleep(1500);

                    clicked = true;
                }
                attempts++;
            }

            if (!clicked) {
                System.out.println("❌ Failed to click Add Money button after 3 attempts.");
                return;
            } else {
                System.out.println("✅ Add Money button clicked successfully. Control will move to payment page.");
            }

            // Payment is handled by paymentModePage in the test

        } catch (Exception e) {
            System.out.println("❌ Wallet operation failed: " + e.getMessage());
        }
    }

    // ✅ ALWAYS navigate back to Home page via URL (call this AFTER payment success)
    public void goBackToHomePage() {
        try {
            driver.navigate().to("https://greencell-nuego-web.web.app/");
            System.out.println("✅ Navigated back to Home page via direct URL");
        } catch (Exception e) {
            System.out.println("❌ Failed to navigate to Home via URL: " + e.getMessage());
        }
    }
}

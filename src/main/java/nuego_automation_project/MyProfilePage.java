/*package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MyProfilePage {

    /**
     * Reusable method to update user profile with explicit waits.
     *
     * @param driver WebDriver instance
     * @param wait   WebDriverWait instance
     * @param actions Actions instance
     * @param js     JavascriptExecutor instance
     */
  /*  public static void updateMyProfile(WebDriver driver, WebDriverWait wait, Actions actions, JavascriptExecutor js) {
        try {
            // Click Profile Icon
            WebElement profileIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@alt='Profile']")));
            js.executeScript("arguments[0].scrollIntoView(true);", profileIcon);
            actions.moveToElement(profileIcon).click().perform();

            // Click "My Profile"
            WebElement myProfile = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[normalize-space()='My Profile']")));
            js.executeScript("arguments[0].scrollIntoView(true);", myProfile);
            actions.moveToElement(myProfile).click().perform();

            // Scroll to Eco-Contribution card
            WebElement ecoCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='profile-eco-contribution-card']//div//div[@class='d-flex justify-content-between align-items-center']")));
            js.executeScript("arguments[0].scrollIntoView(true);", ecoCard);

            // Navigate back
            driver.navigate().back();

            // Update Name
            WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='border-less-input w-100']")));
            wait.until(ExpectedConditions.elementToBeClickable(nameInput));
            js.executeScript("arguments[0].scrollIntoView(true);", nameInput);
            actions.moveToElement(nameInput).click().perform();
            nameInput.clear();
            nameInput.sendKeys("sumedh");

            // Update Age
            WebElement ageInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='common-input-wrap input-age false']//input")));
            js.executeScript("arguments[0].scrollIntoView(true);", ageInput);
            actions.moveToElement(ageInput).click().perform();
            ageInput.clear();
            ageInput.sendKeys("24");

            // Click Save Changes
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='open-600w-18s-24h teal-2-00A095-color outline-button mb-0 cursor-pointer']")));
            js.executeScript("arguments[0].scrollIntoView(true);", saveBtn);
            actions.moveToElement(saveBtn).click().perform();

            // Toggle GST Number checkbox
            WebElement gstCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='d-flex']//img[@alt='alt']")));
            js.executeScript("arguments[0].scrollIntoView(true);", gstCheckbox);
            actions.moveToElement(gstCheckbox).click().perform();
            wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
            js.executeScript("arguments[0].scrollIntoView(true);", saveBtn);
            actions.moveToElement(saveBtn).click().perform();
            wait.until(ExpectedConditions.elementToBeClickable(gstCheckbox));
            actions.moveToElement(gstCheckbox).click().perform();
            wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
            actions.moveToElement(saveBtn).click().perform();

            // Add new entry
            WebElement addNew = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[normalize-space()='Add New']")));
            js.executeScript("arguments[0].scrollIntoView(true);", addNew);
            actions.moveToElement(addNew).click().perform();

            WebElement newName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'px-3 px-lg-0')]//input[contains(@type,'text')]")));
            wait.until(ExpectedConditions.elementToBeClickable(newName));
            js.executeScript("arguments[0].scrollIntoView(true);", newName);
            actions.moveToElement(newName).click().perform();
            newName.sendKeys("sumedhs");

            WebElement newAge = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'px-3 px-lg-0')]//div[contains(@class,'mb-4')]//input[contains(@type,'number')]")));
            js.executeScript("arguments[0].scrollIntoView(true);", newAge);
            actions.moveToElement(newAge).click().perform();
            newAge.sendKeys("26");

            WebElement numberInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'w-100')]//input[contains(@type,'number')]")));
            js.executeScript("arguments[0].scrollIntoView(true);", numberInput);
            actions.moveToElement(numberInput).click().perform();
            numberInput.sendKeys("9999999999");

            WebElement saveNewBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='submit-button mt-4 cursor-pointer text-center open-600w-16s-24h']")));
            js.executeScript("arguments[0].scrollIntoView(true);", saveNewBtn);
            actions.moveToElement(saveNewBtn).click().perform();

            // Navigate back to Home
            driver.get("https://greencell-nuego-web.web.app/");
            System.out.println("✅ Profile updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} */

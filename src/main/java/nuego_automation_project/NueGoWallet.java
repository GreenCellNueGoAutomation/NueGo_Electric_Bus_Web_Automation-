// package nuego_automation_project;
//
//
// import org.openqa.selenium.*;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import java.time.Duration;
//
// public class NueGoWallet {
//
//
//
// 	  private WebDriver driver;
// 	    private WebDriverWait wait;
// 	    private JavascriptExecutor js;
//
// 	    // ---------- LOCATORS ----------
//
// 	    // Wallet icon (top navbar)
// 	    private By walletIcon = By.xpath("//body/div[@id='root']/div[@class='auth-modal']/div[@class='home-page']/div/div[@class='navbar-component d-flex justify-content-between align-items-center']/div[2]//*[name()='svg'][1]");
//
// 	    // Amount input
// 	    private By amountInput = By.xpath("//input[@placeholder='0']");
//
// 	    // Add Money button
// 	    private By addMoneyButton = By.xpath("//button[@class='submit-button cursor-pointer mt-3']");
//
//
// 	    // ---------- CONSTRUCTOR ----------
//
// 	    public NueGoWallet(WebDriver driver) {
// 	        this.driver = driver;
// 	        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
// 	        this.js = (JavascriptExecutor) driver;
// 	    }
//
//
// 	    // ---------- ACTION METHODS ----------
//
// 	    /**
// 	     * Clicks on the wallet icon from the navbar.
// 	     */
// 	    public void clickWalletIcon() {
// 	        WebElement wallet = wait.until(ExpectedConditions.elementToBeClickable(walletIcon));
// 	        wallet.click();
// 	    }
//
// 	    /**
// 	     * Scrolls down slightly to ensure wallet section and fields are visible.
// 	     */
// 	    public void scrollDownSlightly() {
// 	        js.executeScript("window.scrollBy(0, 300);");
// 	    }
//
// 	    /**
// 	     * Enters the wallet amount in the input box.
// 	     *
// 	     * @param amount Wallet amount to be added, as String (e.g., "500")
// 	     */
// 	    public void enterWalletAmount(String amount) {
// 	        WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput));
// 	        amountField.clear();
// 	        amountField.sendKeys(amount);
// 	    }
//
// 	    /**
// 	     * Clicks on the "Add Money" button.
// 	     */
// 	    public void clickAddMoney() {
// 	        WebElement addMoneyBtn = wait.until(ExpectedConditions.elementToBeClickable(addMoneyButton));
// 	        addMoneyBtn.click();
// 	    }
//
// 	    /**
// 	     * Full flow: open wallet, scroll, enter amount, and click Add Money.
// 	     *
// 	     * @param amount Wallet amount to be added
// 	     */
// 	    public void addMoneyToWallet(String amount) {
// 	        clickWalletIcon();
// 	        scrollDownSlightly();
// 	        enterWalletAmount(amount);
// 	        clickAddMoney();
// 	    }
// 	}

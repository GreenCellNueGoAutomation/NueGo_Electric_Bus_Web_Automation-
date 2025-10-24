package nuego_automation_project;

import nuego_automation_project.base.BasePage;
import nuego_automation_project.utils.ScreenshotUtil;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPage extends BasePage {

    private final Duration TIMEOUT = Duration.ofSeconds(30);

    // ---------------- WebElements ---------------- //
    @FindBy(xpath = "//img[@alt='Profile']")
    private WebElement clk;

    @FindBy(name = "random_mobile_number")
    private WebElement txtMobileNumber;

    @FindBy(css = ".teal-22BBB0-bg.submit-button.w-100.mb-3")
    private WebElement btnSendOtp;

    // Corrected OTP field locator (single input)
    @FindBy(xpath = "//input[@aria-label='Please enter OTP character 1']")
    private WebElement txtOtp;

    // ⚙️ Alternative for multiple OTP boxes (uncomment if needed)
    // @FindBy(css = "input.otp-input")
    // private List<WebElement> otpInputs;

    @FindBy(xpath = "//button[@class='teal-22BBB0-bg white-color open-600w-16s-24h p-3 w-100 submit-button mb-3']")
    private WebElement btnVerifyOtp;

    // ---------------- Constructor ---------------- //
    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ---------------- Actions ---------------- //
    public void login(String mobile, String otp) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        try {
            // Click profile icon
            wait.until(ExpectedConditions.elementToBeClickable(clk)).click();

            // Enter full mobile number
            wait.until(ExpectedConditions.visibilityOf(txtMobileNumber));
            txtMobileNumber.clear();
            txtMobileNumber.sendKeys(mobile);

            // Click "Send OTP"
            wait.until(ExpectedConditions.elementToBeClickable(btnSendOtp)).click();

            // Wait for OTP field
            wait.until(ExpectedConditions.visibilityOf(txtOtp));

            // Enter OTP (single input)
            txtOtp.sendKeys(otp);

            // ⚙️ Uncomment below if OTP fields are multiple boxes
            /*
            for (int i = 0; i < otp.length(); i++) {
                otpInputs.get(i).sendKeys(Character.toString(otp.charAt(i)));
            }
            */

            // Click Verify OTP
            wait.until(ExpectedConditions.elementToBeClickable(btnVerifyOtp)).click();

        } catch (Exception e) {
            takeScreenshot("LoginPage_Failure");
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}

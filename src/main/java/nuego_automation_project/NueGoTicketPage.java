package nuego_automation_project;




	import org.openqa.selenium.*;
	import org.openqa.selenium.support.ui.ExpectedConditions;
	import org.openqa.selenium.support.ui.WebDriverWait;
	import java.time.Duration;
	import java.util.ArrayList;

	public class NueGoTicketPage {

	    private WebDriver driver;
	    private WebDriverWait wait;

	    public NueGoTicketPage(WebDriver driver) {
	        this.driver = driver;
	        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    }

	    // Locators
	    private By dontAllowBtn = By.xpath("//button[@id='moe-dontallow_button']");
	    private By whatsappBtn = By.xpath("//p[normalize-space()='Whatsapp']");
	    private By textMessage = By.xpath("//div[@class='receive-by-ticket']//div[3]");
	    private By fareDetail = By.xpath("//img[@src='https://cdn.nuego.co.in/greencell/assets/images/CombinedShape.png']");
	    private By closeFare = By.xpath("//img[@class='cursor-pointer']");
	    private By shareOption = By.xpath("/html[1]/body[1]/div[2]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[3]/div[1]/div[1]");
	    private By downloadTicket = By.xpath("/html[1]/body[1]/div[2]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[3]/div[1]/div[2]/p[1]");
	    private By eTicket = By.xpath("//p[normalize-space()='E - ticket']");
	    private By copyLink = By.xpath("//button[contains(@class,'submit-button')]");
	    private By changeBooking = By.xpath("//p[normalize-space()='Change booking']");

	    // Methods

	    public void clickDontAllowIfVisible() {
	        try {
	            WebElement dontAllow = wait.until(ExpectedConditions.visibilityOfElementLocated(dontAllowBtn));
	            dontAllow.click();
	            System.out.println("Clicked 'Don't Allow' button.");
	        } catch (TimeoutException e) {
	            System.out.println("Notification popup not displayed.");
	        }
	    }

	    public void clickWhatsapp() {
	        wait.until(ExpectedConditions.elementToBeClickable(whatsappBtn)).click();
	        System.out.println("Clicked on WhatsApp.");
	       
	    }

	    public void clickTextMessage() {
	        wait.until(ExpectedConditions.elementToBeClickable(textMessage)).click();
	        System.out.println("Clicked on Text Message.");
	    }

	    public void scrollDown() {
	        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1500)");
	        System.out.println("Scrolled down the page.");
	    }

	    public void clickFareDetail() {
	        wait.until(ExpectedConditions.elementToBeClickable(fareDetail)).click();
	        System.out.println("Clicked on Fare Detail.");
	    }

	    public void closeFareDetail() {
	        wait.until(ExpectedConditions.elementToBeClickable(closeFare)).click();
	        System.out.println("Closed Fare Detail popup.");
	    }
	    
	    public void scrollDown2() {
	        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000)");
	        System.out.println("Scrolled down the page.");
	    }

	    public void clickShareOption() {
	        wait.until(ExpectedConditions.elementToBeClickable(shareOption)).click();
	        System.out.println("Clicked on Share option.");
	    }

	    public void closeShareScreen() {
	        driver.navigate().refresh(); // Or use ESC key / close button if available
	        System.out.println("Closed Share screen (auto refresh used).");
	    }

	    public void clickDownloadTicket() {
	        wait.until(ExpectedConditions.elementToBeClickable(downloadTicket)).click();
	        System.out.println("Clicked on Download Ticket.");

	        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
	        if (tabs.size() > 1) {
	            driver.switchTo().window(tabs.get(1));
	            System.out.println("Download Ticket URL: " + driver.getCurrentUrl());
	            driver.close();
	            driver.switchTo().window(tabs.get(0));
	        }
	    }

	    public void clickETicket() {
	        wait.until(ExpectedConditions.elementToBeClickable(eTicket)).click();
	        System.out.println("Clicked on E-Ticket.");
	    }

	    public void clickCopyLink() {
	        wait.until(ExpectedConditions.elementToBeClickable(copyLink)).click();
	        System.out.println("Clicked on Copy Link.");
	    }

	    public void clickChangeBooking() {
	        wait.until(ExpectedConditions.elementToBeClickable(changeBooking)).click();
	        System.out.println("Clicked on Change Booking.");
	    }
	}




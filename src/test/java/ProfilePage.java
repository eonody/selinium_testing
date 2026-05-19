import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

class ProfilePage extends PageBase {

    private By bioTextareaBy = By.xpath("//form[contains(@action,'profile')]//textarea[@name='bio']");
    private By realNameBy = By.xpath("//form[contains(@action,'profile')]//input[@name='realName']");
    private By locationBy = By.xpath("//form[contains(@action,'profile')]//input[@name='location']");
    private By submitBy = By.xpath("//form[contains(@action,'profile')]//button[@type='submit']");
    private By flashBy = By.xpath("//div[contains(@class,'flash')]");

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public ProfilePage open() {
        driver.get(TestConfig.getBaseUrl() + "/account/profile");
        return this;
    }

    public void fillBio(String text) {
        WebElement textarea = waitAndReturnElement(bioTextareaBy);
        textarea.clear();
        textarea.sendKeys(text);
    }

    public String getBioValue() {
        return waitAndReturnElement(bioTextareaBy).getAttribute("value");
    }

    public boolean hasBioTextarea() {
        return isElementPresent(bioTextareaBy);
    }

    public void fillRealName(String name) {
        WebElement input = waitAndReturnElement(realNameBy);
        input.clear();
        input.sendKeys(name);
    }

    public void fillLocation(String loc) {
        WebElement input = waitAndReturnElement(locationBy);
        input.clear();
        input.sendKeys(loc);
    }

    public void submitForm() {
        waitAndClickable(submitBy).click();
    }

    public String getFlashMessage() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(flashBy));
            return driver.findElement(flashBy).getText();
        } catch (TimeoutException e) {
            return "";
        }
    }
}


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


class LoginPage extends PageBase {

    private By usernameBy = By.xpath("//form[contains(@action,'login')]//input[@id='form3-username']");
    private By passwordBy = By.xpath("//form[contains(@action,'login')]//input[@id='form3-password']");
    private By submitBy = By.xpath("//form[contains(@action,'login')]//button[@type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        this.driver.get(TestConfig.getBaseUrl() + "/login");
        return this;
    }

    public void login(String username, String password) {
        waitAndReturnElement(usernameBy).clear();
        waitAndReturnElement(usernameBy).sendKeys(username);
        waitAndReturnElement(passwordBy).clear();
        waitAndReturnElement(passwordBy).sendKeys(password);
        WebElement submitBtn = wait.until(ExpectedConditions.presenceOfElementLocated(submitBy));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        try {
            waitAndClickable(submitBy).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        }
    }

    public boolean isLoginFormDisplayed() {
        return isElementPresent(usernameBy) && driver.findElement(usernameBy).isDisplayed();
    }
}


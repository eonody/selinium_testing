import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

class LoginPage extends PageBase {

    // Complex XPath for username field inside the login form
    private By usernameBy = By.xpath("//form[@action='/login']//input[@name='username' and @type='text']");
    private By passwordBy = By.xpath("//form[@action='/login']//input[@name='password' and @type='password']");
    private By submitBy = By.xpath("//form[@action='/login']//button[@type='submit']");

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
        waitAndClickable(submitBy).click();
    }

    public boolean isLoginFormDisplayed() {
        try {
            return driver.findElement(usernameBy).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}


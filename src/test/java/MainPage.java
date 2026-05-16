import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


class MainPage extends PageBase {

    private By playButtonBy = By.xpath("//div[contains(@class,'site-title-nav')]//span[contains(text(),'Play')]/..");
    private By signInBy = By.xpath("//div[contains(@class, 'signin-or-signup')]//a[contains(@class,'signin')]");
    private By userMenuBy = By.xpath("//div[contains(@class,'dasher')]//button[@id='user_tag']");
    private By footerBy = By.xpath("//div[contains(@class,'lobby__about')]");
    private By logoutBy = By.xpath("//form[contains(@class,'logout')]//button[@type='submit']");


    public MainPage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl());
    }

    public String getFooterText() {
        WebElement footer = this.waitAndReturnElement(footerBy);
        scrollToElement(footer);
        return footer.getText();
    }

    public LoginPage goToLogin() {
        this.waitAndClickable(signInBy).click();
        wait.until(ExpectedConditions.urlContains("/login"));
        return new LoginPage(this.driver);
    }

    public boolean isSignInVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(signInBy));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isUserMenuVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(userMenuBy));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void logout() {
        waitAndClickable(userMenuBy).click();
        waitAndClickable(logoutBy).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(signInBy));
    }

    public long scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        return (Long) ((JavascriptExecutor) driver).executeScript("return window.scrollY;");
    }

    public boolean isBodyVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}

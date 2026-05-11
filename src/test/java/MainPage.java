import org.junit.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;


class MainPage extends PageBase {

    // Complex XPath: nav link containing "PLAY"
    private By playButtonBy = By.xpath("//div[contains(@class,'site-title-nav')]//span[contains(text(),'Play')]/..");
    // Complex XPath: sign in link
    private By signInBy = By.xpath("//a[@href='/login' and contains(@class,'signin')]");
    // Complex XPath: user dropdown after login
    private By userMenuBy = By.xpath("//div[@id='sign-in-or-signup']//a[contains(@class,'signin')");
    private By footerBy = By.tagName("footer");

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
        return new LoginPage(this.driver);
    }

    public boolean isSignInVisible() {
        try {
            return this.driver.findElement(signInBy).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isUserMenuVisible() {
        try {
            return this.driver.findElement(userMenuBy).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


class AboutPage extends PageBase {

    private By headingBy = By.xpath("//main[contains(@class, 'page-menu')]//div[contains(@class,'box')]//h1");

    public AboutPage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/about");
    }

    public String getMainHeading() {
        return waitAndReturnElement(headingBy).getText();
    }

    public boolean hasContent() {
        return getBodyText().length() > 0;
    }

    public boolean isOnAboutPage() {
        return driver.getCurrentUrl().contains("/about");
    }

    public void navigateBack() {
        driver.navigate().back();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/about")));
    }

    public void navigateForward() {
        driver.navigate().forward();
        wait.until(ExpectedConditions.urlContains("/about"));
    }
}

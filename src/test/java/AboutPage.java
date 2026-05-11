import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

class AboutPage extends PageBase {

    public AboutPage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/about");
    }

    public String getMainHeading() {
        // Complex XPath: heading inside main content area
        By headingBy = By.xpath("//main//div[contains(@class,'box')]//h1");
        return waitAndReturnElement(headingBy).getText();
    }

    public boolean hasContent() {
        return getBodyText().length() > 0;
    }
}


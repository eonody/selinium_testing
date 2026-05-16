import org.openqa.selenium.*;

class AboutPage extends PageBase {

    private By headingBy = By.xpath("//main[contains(@class, 'page-manu')]//div[contains(@class,'box')]//h1");

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
}


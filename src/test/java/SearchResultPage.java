import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;


class SearchResultPage extends PageBase {

    private By searchToggleBy = By.xpath("//div[@class='site-buttons']//div[@id='clinput']//a[contains(@class,'link')]");
    private By searchInputBy = By.xpath("//div[@class='site-buttons']//div[@id='clinput']//input");
    private By searchResultsBy = By.xpath("//div[contains(@class,'complete-list')]//a[contains(@href,'/@/')]");
    private By noResultBy = By.xpath("//div[contains(@class,'complete-list')]//div[contains(@class,'empty')]");

    public SearchResultPage(WebDriver driver) {
        super(driver);
    }

    public SearchResultPage open() {
        this.driver.get(TestConfig.getBaseUrl());
        return this;
    }

    public SearchResultPage searchForPlayer(String query) {
        waitAndClickable(searchToggleBy).click();
        WebElement input = waitAndReturnElement(searchInputBy);
        input.clear();
        input.sendKeys(query);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(searchResultsBy),
                    ExpectedConditions.presenceOfElementLocated(noResultBy)
                ));
        } catch (TimeoutException e) {
        }
        return this;
    }

    public int getResultCount() {
        try {
            List<WebElement> results = driver.findElements(searchResultsBy);
            return results.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getFirstResultText() {
        try {
            List<WebElement> results = driver.findElements(searchResultsBy);
            if (!results.isEmpty()) {
                String text = results.get(0).getText();
                String href = results.get(0).getAttribute("href");
                return text + " " + (href != null ? href : "");
            }
        } catch (Exception e) {
            // fall through
        }
        return "";
    }

    public boolean hasNoResults() {
        try {
            return driver.findElement(noResultBy).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}

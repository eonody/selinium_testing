import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

class SearchResultPage extends PageBase {

    // Complex XPath: search result entries
    private By searchResultsBy = By.xpath("//div[contains(@class,'search')]//a[contains(@class,'result')]");

    public SearchResultPage(WebDriver driver) {
        super(driver);
    }

    public int getResultCount() {
        try {
            List<WebElement> results = driver.findElements(searchResultsBy);
            return results.size();
        } catch (Exception e) {
            return 0;
        }
    }
}

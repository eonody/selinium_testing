import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

class LeaderboardPage extends PageBase {

    // Complex XPath: table rows containing player ratings
    private By playerRowsBy = By.xpath("//table[contains(@class,'slist')]//tbody//tr[td[contains(@class,'name')]]");
    private By headerBy = By.xpath("//main//h1");

    public LeaderboardPage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/player");
    }

    public String getHeaderText() {
        return waitAndReturnElement(headerBy).getText();
    }

    public int getPlayerCount() {
        List<WebElement> rows = driver.findElements(playerRowsBy);
        return rows.size();
    }
}


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;


class LeaderboardPage extends PageBase {

    private By playerRowsBy = By.xpath("//ol[contains(@class,'user-top')]//li");
    private By headerBy = By.xpath("//main[contains(@class, 'page-menu')]//h2[contains(text(),'Online players')]");

    public LeaderboardPage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/player");
    }

    public String getHeaderText() {
        return waitAndReturnElement(headerBy).getText();
    }

    public int getPlayerCount() {
        wait.until(ExpectedConditions.presenceOfElementLocated(playerRowsBy));
        List<WebElement> rows = driver.findElements(playerRowsBy);
        return rows.size();
    }
}


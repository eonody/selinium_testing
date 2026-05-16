import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

class PuzzlePage extends PageBase {

    private By boardBy = By.xpath("//main//div[contains(@class,'puzzle')]//cg-board");

    public PuzzlePage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/training");
    }

    public boolean isPuzzleBoardVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(boardBy));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


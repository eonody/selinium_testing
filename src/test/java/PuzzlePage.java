import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

class PuzzlePage extends PageBase {

    public PuzzlePage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/training");
    }

    public boolean isPuzzleBoardVisible() {
        try {
            // Complex XPath: the chessboard inside the puzzle
            By boardBy = By.xpath("//main//div[contains(@class,'puzzle')]//cg-board");
            wait.until(ExpectedConditions.presenceOfElementLocated(boardBy));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


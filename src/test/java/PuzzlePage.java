import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

class PuzzlePage extends PageBase {

    private By boardBy = By.xpath("//main//div[contains(@class,'puzzle')]//cg-board");
    private By piecesBy = By.xpath("//main//div[contains(@class,'puzzle')]//cg-board//piece");

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

    public WebElement getBoard() {
        return waitAndReturnElement(boardBy);
    }

    public List<WebElement> getPieces() {
        wait.until(ExpectedConditions.presenceOfElementLocated(piecesBy));
        return driver.findElements(piecesBy);
    }

    public void dragPiece(WebElement piece, int xOffset, int yOffset) {
        new Actions(driver)
            .clickAndHold(piece)
            .moveByOffset(xOffset, yOffset)
            .release()
            .perform();
    }
}

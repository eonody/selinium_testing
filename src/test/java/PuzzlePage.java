import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;


class PuzzlePage extends PageBase {

    private By boardBy = By.xpath("//main//div[contains(@class,'puzzle')]//cg-board");
    private By piecesBy = By.xpath("//main//div[contains(@class,'puzzle')]//cg-board//piece");
    private By coordColorRandomBy = By.xpath("//*[@id='coord_color_random']");
    private By coordColorBlackBy = By.xpath("//*[@id='coord_color_black']");

    public PuzzlePage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/training");
    }

    public boolean isPuzzleBoardVisible() {
        return isElementPresent(boardBy);
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

    public void openCoordinateTraining() {
        this.driver.get(TestConfig.getBaseUrl() + "/training/coordinate");
        wait.until(ExpectedConditions.presenceOfElementLocated(coordColorRandomBy));
    }

    public boolean hasColorRadio(String color) {
        By locator = "black".equals(color) ? coordColorBlackBy : coordColorRandomBy;
        return isElementPresent(locator);
    }

    public WebElement getColorRadio(String color) {
        By locator = "black".equals(color) ? coordColorBlackBy : coordColorRandomBy;
        return waitAndReturnElement(locator);
    }

    public boolean isColorRadioSelected(String color) {
        return getColorRadio(color).isSelected();
    }

    public void selectColorRadio(String color) {
        WebElement radio = getColorRadio(color);
        if (!radio.isSelected()) {
            radio.click();
        }
    }
}

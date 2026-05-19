import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


class AnalysisPage extends PageBase {

    private By boardBy = By.xpath("//main//div[contains(@class,'analyse')]//cg-board");
    private By pgnTextareaBy = By.xpath("//textarea[contains(@class,'copyable') or @placeholder='Paste PGN text here' or contains(@class,'pgn')]");
    private By importLinkBy = By.xpath("//a[contains(@href,'/paste')]");
    private By importTextareaBy = By.xpath("//main//form//textarea");
    private By importSubmitBy = By.xpath("//main//form//button[@type='submit']");
    private By fileInputBy = By.xpath("//main//form//input[@type='file']");

    public AnalysisPage(WebDriver driver) {
        super(driver);
    }

    public AnalysisPage openAnalysis() {
        driver.get(TestConfig.getBaseUrl() + "/analysis");
        return this;
    }

    public AnalysisPage openImportPage() {
        driver.get(TestConfig.getBaseUrl() + "/paste");
        return this;
    }

    public boolean isBoardVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(boardBy));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasImportTextarea() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(importTextareaBy));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillImportTextarea(String pgn) {
        WebElement textarea = waitAndReturnElement(importTextareaBy);
        textarea.clear();
        textarea.sendKeys(pgn);
    }

    public String getImportTextareaValue() {
        return waitAndReturnElement(importTextareaBy).getAttribute("value");
    }

    public void submitImportForm() {
        waitAndClickable(importSubmitBy).click();
    }

    public boolean hasFileInput() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(fileInputBy));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void uploadFile(String absolutePath) {
        WebElement fileInput = driver.findElement(fileInputBy);
        fileInput.sendKeys(absolutePath);
    }
}


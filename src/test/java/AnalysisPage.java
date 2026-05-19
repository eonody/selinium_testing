import org.openqa.selenium.*;


class AnalysisPage extends PageBase {

    private By boardBy = By.xpath("//main//div[contains(@class,'analyse')]//cg-board");
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
        return isElementPresent(boardBy);
    }

    public boolean hasImportTextarea() {
        return isElementPresent(importTextareaBy);
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
        return isElementPresent(fileInputBy);
    }

    public void uploadFile(String absolutePath) {
        WebElement fileInput = driver.findElement(fileInputBy);
        fileInput.sendKeys(absolutePath);
    }
}


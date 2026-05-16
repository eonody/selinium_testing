import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

class PreferencesPage extends PageBase {

    private By radioBy = By.xpath("//form[contains(@class, 'autosubmit')]//div[not(@class)]//section//input[@type='radio']");
    private By flashBy = By.xpath("//p[contains(@class,'saved') and contains(@class,'text')]");

    public PreferencesPage(WebDriver driver) {
        super(driver);
    }

    public PreferencesPage openGameDisplay() {
        driver.get(TestConfig.getBaseUrl() + "/account/preferences/display");
        return this;
    }

    public PreferencesPage openGameBehavior() {
        driver.get(TestConfig.getBaseUrl() + "/account/preferences/game-behavior");
        return this;
    }

    public List<WebElement> getRadioButtons() {
        wait.until(ExpectedConditions.presenceOfElementLocated(radioBy));
        return driver.findElements(radioBy);
    }

    public void selectRadioButton(int index) {
        List<WebElement> radios = getRadioButtons();
        WebElement radio = radios.get(index);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
    }

    public boolean isRadioButtonSelected(int index) {
        List<WebElement> radios = getRadioButtons();
        return radios.get(index).isSelected();
    }

    public String getFlashMessage() {
        wait.until(ExpectedConditions.presenceOfElementLocated(flashBy));
        return driver.findElement(flashBy).getText();
    }

}

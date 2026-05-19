import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;


class EditorPage extends PageBase {

    private By openingDropdownBy = By.xpath("//*[@id='board-editor']/div/div[4]/select");

    public EditorPage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/editor");
    }

    public Select getOpeningDropdown() {
        WebElement dropdown = waitAndReturnElement(openingDropdownBy);
        return new Select(dropdown);
    }

    public int getOpeningOptionCount() {
        return getOpeningDropdown().getOptions().size();
    }

    public void selectOpeningByVisibleText(String text) {
        getOpeningDropdown().selectByVisibleText(text);
    }

    public String getSelectedOpeningText() {
        return getOpeningDropdown().getFirstSelectedOption().getText();
    }
}



}

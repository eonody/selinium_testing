import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

class ReportPage extends PageBase {

    private By reasonDropdownBy = By.xpath("//main[contains(@class, 'report')]//select[@id='form3-reason']");

    public ReportPage(WebDriver driver) {
        super(driver);
        this.driver.get(TestConfig.getBaseUrl() + "/report");
    }

    public Select getReasonDropdown() {
        WebElement dropdown = waitAndReturnElement(reasonDropdownBy);
        return new Select(dropdown);
    }

    public void selectReasonByValue(String value) {
        getReasonDropdown().selectByValue(value);
    }

    public void selectReasonByIndex(int index) {
        getReasonDropdown().selectByIndex(index);
    }

    public String getSelectedReason() {
        return getReasonDropdown().getFirstSelectedOption().getText();
    }

    public int getReasonOptionCount() {
        return getReasonDropdown().getOptions().size();
    }
}

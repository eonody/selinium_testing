import org.junit.*;
import org.openqa.selenium.*;
import java.util.List;


public class LoggedInUserTest extends TestBase {

    private void loginFirst() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        assumeCredentials(username, password);
        loginAndWait(username, password);
    }

    @Test
    public void testFormWithLoggedInUser() {
        loginFirst();
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();

        Assert.assertTrue("Bio textarea should exist", profilePage.hasBioTextarea());
        profilePage.fillBio("Selenium test bio");
        profilePage.fillRealName("Test User");
        profilePage.fillLocation("Test City");
        profilePage.submitForm();
        String flash = profilePage.getFlashMessage();
        Assert.assertTrue("Should show success message or page should reload",
                flash.length() > 0 || driver.getCurrentUrl().contains("/account/profile"));
        String bioValue = profilePage.getBioValue();
        Assert.assertTrue("Bio should contain what we typed", bioValue.contains("Selenium test bio"));
    }

    @Test
    public void testReportPageDropdownSelection() {
        loginFirst();
        ReportPage reportPage = new ReportPage(driver);
        int optionCount = reportPage.getReasonOptionCount();
        Assert.assertTrue("Dropdown should have multiple options", optionCount > 1);

        reportPage.selectReasonByValue("cheat");
        Assert.assertEquals("Selected option should be Cheat", "Cheat", reportPage.getSelectedReason());

        reportPage.selectReasonByIndex(0);
        String firstOption = reportPage.getSelectedReason();
        Assert.assertFalse("First option should not be empty", firstOption.isEmpty());
    }

    @Test
    public void testPreferenceChoiceSelection() {
        loginFirst();
        PreferencesPage prefsPage = new PreferencesPage(driver);
        prefsPage.openGameBehavior();
        List<WebElement> radios = prefsPage.getRadioButtons();
        Assert.assertTrue("Should have radio buttons", radios.size() > 0);
        prefsPage.selectRadioButton(0);
        Assert.assertTrue("Radio button should be selected", prefsPage.isRadioButtonSelected(0));
    }

    @Test
    public void testPreferenceDisplayPage() {
        loginFirst();
        PreferencesPage prefsPage = new PreferencesPage(driver);
        prefsPage.openGameDisplay();
        List<WebElement> radios = prefsPage.getRadioButtons();
        Assert.assertTrue("Display preferences should have radio buttons", radios.size() > 0);
    }
}


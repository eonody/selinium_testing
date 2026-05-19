import org.junit.*;
import org.openqa.selenium.*;


public class LoginTest extends TestBase {

    @Test
    public void testLoginFormIsDisplayed() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        assumeNoCloudflare();
        Assert.assertTrue("Login form should be displayed", loginPage.isLoginFormDisplayed());
    }

    @Test
    public void testLoginWithCredentials() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        assumeCredentials(username, password);
        loginAndWait(username, password);
        Assert.assertFalse(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    public void testLogout() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        assumeCredentials(username, password);
        loginAndWait(username, password);

        MainPage mainPage = new MainPage(driver);
        Assert.assertTrue("User menu should be visible after login", mainPage.isUserMenuVisible());
        mainPage.logout();
        Assert.assertTrue("Sign in link should be visible after logout", mainPage.isSignInVisible());
    }

    @Test
    public void testSignInLinkNavigation() {
        MainPage mainPage = new MainPage(driver);
        Assert.assertTrue("Sign in link should be visible", mainPage.isSignInVisible());
        LoginPage loginPage = mainPage.goToLogin();
        assumeNoCloudflare();
        Assert.assertTrue("Should navigate to login page", driver.getCurrentUrl().contains("/login"));
        Assert.assertTrue("Login form should be displayed", loginPage.isLoginFormDisplayed());
    }
}


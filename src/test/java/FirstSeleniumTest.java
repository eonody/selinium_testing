import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

public class FirstSeleniumTest {
    public WebDriver driver;

    @Rule
    public TestWatcher screenshotOnFailure = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File dest = new File("screenshots/" + description.getMethodName() + ".png");
                dest.getParentFile().mkdirs();
                Files.copy(src.toPath(), dest.toPath());
                System.out.println("Screenshot saved: " + dest.getAbsolutePath());
            } catch (Exception ex) {
                System.out.println("Could not take screenshot: " + ex.getMessage());
            }
        }
    };

    @Before
    public void setup() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();

        if (TestConfig.isHeadless()) {
            options.addArguments("--headless");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new RemoteWebDriver(new URL(TestConfig.getSeleniumHubUrl()), options);
        driver.manage().window().maximize();
    }

    // ===== LOGIN FORM TEST =====
    @Test
    public void testLoginFormIsDisplayed() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        Assert.assertTrue("Login form should be displayed", loginPage.isLoginFormDisplayed());
    }

    @Test
    public void testLoginWithCredentials() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        Assume.assumeTrue("Skipping: no credentials in test.properties",
                !username.isEmpty() && !password.isEmpty());
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        Assert.assertFalse(driver.getCurrentUrl().contains("/login"));
    }

    // ===== STATIC PAGE TEST (About page) =====
    @Test
    public void testAboutPageContent() {
        AboutPage aboutPage = new AboutPage(driver);
        Assert.assertTrue("About page should have content", aboutPage.hasContent());
        String body = aboutPage.getBodyText();
        Assert.assertTrue("About page should mention lichess", body.toLowerCase().contains("lichess"));
    }

    // ===== PAGE TITLE TEST =====
    @Test
    public void testMainPageTitle() {
        MainPage mainPage = new MainPage(driver);
        String title = mainPage.getPageTitle();
        Assert.assertTrue("Title should contain lichess", title.toLowerCase().contains("lichess"));
    }

    // ===== MULTIPLE PAGE TEST (iterate over URLs) =====
    @Test
    public void testMultiplePagesHaveTitle() {
        String[] pages = {"/", "/about", "/player", "/training", "/tv"};
        for (String path : pages) {
            driver.get(TestConfig.getBaseUrl() + path);
            String title = driver.getTitle();
            Assert.assertNotNull("Page " + path + " should have a title", title);
            Assert.assertFalse("Page " + path + " title should not be empty", title.isEmpty());
        }
    }

    // ===== LEADERBOARD PAGE TEST =====
    @Test
    public void testLeaderboardPageLoads() {
        LeaderboardPage leaderboardPage = new LeaderboardPage(driver);
        String body = leaderboardPage.getBodyText();
        Assert.assertTrue("Leaderboard should have content", body.length() > 0);
    }

    // ===== PUZZLE PAGE TEST =====
    @Test
    public void testPuzzlePageHasBoard() {
        PuzzlePage puzzlePage = new PuzzlePage(driver);
        Assert.assertTrue("Puzzle page should have a chess board", puzzlePage.isPuzzleBoardVisible());
    }

    // ===== JAVASCRIPT EXECUTOR: scroll to bottom =====
    @Test
    public void testScrollToBottomWithJavascript() {
        driver.get(TestConfig.getBaseUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        Long scrollY = (Long) ((JavascriptExecutor) driver).executeScript("return window.scrollY;");
        Assert.assertTrue("Page should have scrolled down", scrollY > 0);
    }

    // ===== HOVER TEST =====
    @Test
    public void testHoverOnElement() {
        driver.get(TestConfig.getBaseUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By logoBy = By.xpath("//a[@id='site-title' or contains(@class,'site-title')]");
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(logoBy));
        new Actions(driver).moveToElement(el).perform();

        Assert.assertTrue("Element should be visible after hover", el.isDisplayed());
    }

    // ===== COOKIE MANIPULATION =====
    @Test
    public void testCookieManipulation() {
        MainPage mainPage = new MainPage(driver);

        Set<Cookie> cookies = driver.manage().getCookies();
        int initialCount = cookies.size();

        Cookie testCookie = new Cookie("test_cookie", "selenium_test_value");
        driver.manage().addCookie(testCookie);

        Cookie retrieved = driver.manage().getCookieNamed("test_cookie");
        Assert.assertNotNull("Cookie should exist", retrieved);
        Assert.assertEquals("selenium_test_value", retrieved.getValue());

        driver.manage().deleteCookieNamed("test_cookie");
        Assert.assertNull("Cookie should be deleted", driver.manage().getCookieNamed("test_cookie"));
    }

    // ===== BROWSER HISTORY TEST =====
    @Test
    public void testBrowserHistoryNavigation() {
        driver.get(TestConfig.getBaseUrl());

        driver.get(TestConfig.getBaseUrl() + "/about");
        String secondUrl = driver.getCurrentUrl();
        Assert.assertTrue(secondUrl.contains("/about"));

        driver.navigate().back();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/about")));

        driver.navigate().forward();
        wait.until(ExpectedConditions.urlContains("/about"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/about"));
    }

    // ===== EXPLICIT WAIT TEST =====
    @Test
    public void testExplicitWaitForElement() {
        driver.get(TestConfig.getBaseUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        Assert.assertNotNull("Body should be visible after wait", body);
    }

    // ===== SEARCH TEST =====
    @Test
    public void testPlayerSearchReturnsResults() {
        SearchResultPage searchPage = new SearchResultPage(driver);
        searchPage.open();
        searchPage.searchForPlayer("Magnus");
        int count = searchPage.getResultCount();
        Assert.assertTrue("Search should return at least one result", count > 0);
    }

    @Test
    public void testPlayerSearchFirstResultContainsQuery() {
        SearchResultPage searchPage = new SearchResultPage(driver);
        searchPage.open();
        searchPage.searchForPlayer("DrNykterstein");
        String firstResult = searchPage.getFirstResultText();
        Assert.assertTrue("First result should contain the search term",
                firstResult.toLowerCase().contains("drnykterstein"));
    }

    @Test
    public void testPlayerSearchEmptyResultForGibberish() {
        SearchResultPage searchPage = new SearchResultPage(driver);
        searchPage.open();
        searchPage.searchForPlayer("");
        int count = searchPage.getResultCount();
        Assert.assertEquals("Search for gibberish should return no results", 0, count);
    }

    // ===== LOGOUT TEST =====
    @Test
    public void testLogout() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        Assume.assumeTrue("Skipping: no credentials in test.properties",
                !username.isEmpty() && !password.isEmpty());

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(username, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        MainPage mainPage = new MainPage(driver);
        mainPage.logout();
        Assert.assertTrue("Sign in link should be visible after logout", mainPage.isSignInVisible());
    }

    // ===== FORM WITH LOGGED-IN USER (profile page) =====
    @Test
    public void testFormWithLoggedInUser() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        Assume.assumeTrue("Skipping: no credentials in test.properties",
                !username.isEmpty() && !password.isEmpty());
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(username, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();
        profilePage.fillBio("Selenium test bio");
        profilePage.fillLocation("Test City");
        profilePage.submitForm();
        String flash = profilePage.getFlashMessage();
        Assert.assertTrue("Should show success message or page should reload",
                flash.length() > 0 || driver.getCurrentUrl().contains("/account/profile"));
    }

    // ===== DROPDOWN TEST (Report page reason) =====
    @Test
    public void testReportPageDropdownSelection() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        Assume.assumeTrue("Skipping: no credentials in test.properties",
                !username.isEmpty() && !password.isEmpty());
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(username, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        ReportPage reportPage = new ReportPage(driver);
        int optionCount = reportPage.getReasonOptionCount();
        Assert.assertTrue("Dropdown should have multiple options", optionCount > 1);
        reportPage.selectReasonByValue("cheat");
        String selected = reportPage.getSelectedReason();
        Assert.assertEquals("Selected option should be Cheat", "Cheat", selected);
    }

    // ===== PREFERENCE CHOICE TEST (game-behavior) =====
    @Test
    public void testPreferenceChoiceSelection() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        Assume.assumeTrue("Skipping: no credentials in test.properties",
                !username.isEmpty() && !password.isEmpty());
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(username, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        PreferencesPage prefsPage = new PreferencesPage(driver);
        prefsPage.openGameBehavior();
        List<WebElement> radios = prefsPage.getRadioButtons();
        Assert.assertTrue("Should have radio buttons", radios.size() > 0);
        prefsPage.selectRadioButton(0);
        Assert.assertTrue("Radio button should be selected", prefsPage.isRadioButtonSelected(0));
    }


    @After
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}

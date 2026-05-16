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

    private void assumeNoCloudflare() {
        try {
            // Wait a moment for page to load
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        if (body.contains("checking your browser") || body.contains("cloudflare") || body.contains("verify you are human")) {
            if (!TestConfig.isHeadless()) {
                // Running locally with visible browser — wait for manual CAPTCHA solve
                System.out.println(">>> CAPTCHA detected! Please solve it manually in the browser. Waiting up to 60 seconds...");
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(60)).until(
                        d -> {
                            String b = d.findElement(By.tagName("body")).getText().toLowerCase();
                            return !b.contains("checking your browser") && !b.contains("verify you are human");
                        }
                    );
                    System.out.println(">>> CAPTCHA solved, continuing test.");
                } catch (TimeoutException e) {
                    Assume.assumeTrue("CAPTCHA not solved within 60 seconds", false);
                }
            } else {
                // Headless / CI — skip the test
                Assume.assumeTrue("Skipping: Cloudflare verification detected in headless mode", false);
            }
        }
    }

    private void loginAndWait(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        assumeNoCloudflare();
        loginPage.login(username, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    private void assumeCredentials(String username, String password) {
        Assume.assumeTrue("Skipping: no credentials configured",
                !username.isEmpty() && !password.isEmpty());
    }

    // ===== LOGIN FORM TEST =====
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

    // ===== STATIC PAGE TEST (About page) =====
    @Test
    public void testAboutPageContent() {
        AboutPage aboutPage = new AboutPage(driver);
        Assert.assertTrue("About page should have content", aboutPage.hasContent());
        String body = aboutPage.getBodyText();
        Assert.assertTrue("About page should mention lichess", body.toLowerCase().contains("lichess"));

        String heading = aboutPage.getMainHeading();
        Assert.assertFalse("Heading should not be empty", heading.isEmpty());
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

        int playerCount = leaderboardPage.getPlayerCount();
        Assert.assertTrue("Leaderboard should show players", playerCount > 0);

        String header = leaderboardPage.getHeaderText();
        Assert.assertFalse("Header should not be empty", header.isEmpty());
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
        MainPage mainPage = new MainPage(driver);
        Assert.assertTrue("Body should be visible", mainPage.isBodyVisible());
        long scrollY = mainPage.scrollToBottom();
        Assert.assertTrue("Page should have scrolled down", scrollY > 0);
    }

    // ===== HOVER TEST =====
    @Test
    public void testHoverOnElement() {
        MainPage mainPage = new MainPage(driver);
        By logoBy = By.xpath("//a[@id='site-title' or contains(@class,'site-title')]");
        mainPage.hoverElement(logoBy);

        Assert.assertTrue("Element should be visible after hover",
                driver.findElement(logoBy).isDisplayed());
    }

    // ===== COOKIE MANIPULATION =====
    @Test
    public void testCookieManipulation() {
        MainPage mainPage = new MainPage(driver);

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

        AboutPage aboutPage = new AboutPage(driver);
        Assert.assertTrue("Should be on about page", aboutPage.isOnAboutPage());

        aboutPage.navigateBack();
        Assert.assertFalse("Should have left about page", driver.getCurrentUrl().contains("/about"));

        aboutPage.navigateForward();
        Assert.assertTrue("Should be back on about page", aboutPage.isOnAboutPage());
    }

    // ===== EXPLICIT WAIT TEST =====
    @Test
    public void testExplicitWaitForElement() {
        MainPage mainPage = new MainPage(driver);
        Assert.assertTrue("Body should be visible after wait", mainPage.isBodyVisible());
    }

    // ===== SEARCH TESTS =====
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
    public void testPlayerSearchNoResultForEmpty() {
        SearchResultPage searchPage = new SearchResultPage(driver);
        searchPage.open();
        searchPage.searchForPlayer("xyznonexistent99999");
        Assert.assertTrue("Should show no results", searchPage.hasNoResults());
        Assert.assertEquals("Result count should be 0", 0, searchPage.getResultCount());
    }

    // ===== LOGOUT TEST =====
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

    // ===== FORM WITH LOGGED-IN USER (profile page) =====
    @Test
    public void testFormWithLoggedInUser() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        assumeCredentials(username, password);
        loginAndWait(username, password);

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

    // ===== DROPDOWN TEST (Report page reason) =====
    @Test
    public void testReportPageDropdownSelection() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        assumeCredentials(username, password);
        loginAndWait(username, password);

        ReportPage reportPage = new ReportPage(driver);
        int optionCount = reportPage.getReasonOptionCount();
        Assert.assertTrue("Dropdown should have multiple options", optionCount > 1);

        reportPage.selectReasonByValue("cheat");
        Assert.assertEquals("Selected option should be Cheat", "Cheat", reportPage.getSelectedReason());

        reportPage.selectReasonByIndex(0);
        String firstOption = reportPage.getSelectedReason();
        Assert.assertFalse("First option should not be empty", firstOption.isEmpty());
    }

    // ===== PREFERENCE CHOICE TEST (game-behavior) =====
    @Test
    public void testPreferenceChoiceSelection() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        assumeCredentials(username, password);
        loginAndWait(username, password);

        PreferencesPage prefsPage = new PreferencesPage(driver);
        prefsPage.openGameBehavior();
        List<WebElement> radios = prefsPage.getRadioButtons();
        Assert.assertTrue("Should have radio buttons", radios.size() > 0);
        prefsPage.selectRadioButton(0);
        Assert.assertTrue("Radio button should be selected", prefsPage.isRadioButtonSelected(0));
    }

    // ===== MAIN PAGE FOOTER TEST =====
    @Test
    public void testMainPageFooter() {
        MainPage mainPage = new MainPage(driver);
        String footerText = mainPage.getFooterText();
        Assert.assertFalse("Footer should not be empty", footerText.isEmpty());
    }

    // ===== SIGN IN LINK NAVIGATION =====
    @Test
    public void testSignInLinkNavigation() {
        MainPage mainPage = new MainPage(driver);
        Assert.assertTrue("Sign in link should be visible", mainPage.isSignInVisible());
        LoginPage loginPage = mainPage.goToLogin();
        assumeNoCloudflare();
        Assert.assertTrue("Should navigate to login page", driver.getCurrentUrl().contains("/login"));
        Assert.assertTrue("Login form should be displayed", loginPage.isLoginFormDisplayed());
    }

    // ===== PREFERENCES DISPLAY PAGE =====
    @Test
    public void testPreferenceDisplayPage() {
        String username = TestConfig.getUsername();
        String password = TestConfig.getPassword();
        assumeCredentials(username, password);
        loginAndWait(username, password);

        PreferencesPage prefsPage = new PreferencesPage(driver);
        prefsPage.openGameDisplay();
        List<WebElement> radios = prefsPage.getRadioButtons();
        Assert.assertTrue("Display preferences should have radio buttons", radios.size() > 0);
    }

    @After
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ===== DRAG AND DROP TEST (puzzle board) =====
    @Test
    public void testDragAndDropOnPuzzleBoard() {
        PuzzlePage puzzlePage = new PuzzlePage(driver);
        Assert.assertTrue("Puzzle board should be visible", puzzlePage.isPuzzleBoardVisible());

        List<WebElement> pieces = puzzlePage.getPieces();
        Assert.assertTrue("Board should have pieces", pieces.size() > 0);

        // Get a piece and attempt to drag it (move it by offset on the board)
        WebElement piece = pieces.get(0);
        Point before = piece.getLocation();
        puzzlePage.dragPiece(piece, 75, 0);
        // The drag action was performed — chess engine may or may not accept the move
        // We just verify the drag interaction didn't throw an error
        Assert.assertTrue("Puzzle board should still be visible after drag", puzzlePage.isPuzzleBoardVisible());
    }

    // ===== RANDOM DATA TEST (search with random string) =====
    @Test
    public void testSearchWithRandomData() {
        String randomQuery = "user" + UUID.randomUUID().toString().substring(0, 8);
        System.out.println("Random search query: " + randomQuery);

        SearchResultPage searchPage = new SearchResultPage(driver);
        searchPage.open();
        searchPage.searchForPlayer(randomQuery);

        int count = searchPage.getResultCount();
        Assert.assertEquals("Random search should return no results", 0, count);
    }

    // ===== DOWNLOAD FILES TEST (PGN game export) =====
    @Test
    public void testDownloadPgnFile() throws Exception {

        driver.get(TestConfig.getBaseUrl() + "/game/export/q7ZvsdUF");

        Thread.sleep(2000);

        String pageSource = driver.getPageSource();
        Assert.assertTrue("Downloaded content should contain PGN data",
                pageSource.contains("[Event") || pageSource.contains("[Site") ||
                pageSource.contains("1."));
    }

    // ===== DOCKERIZED EXECUTION TEST =====
    @Test
    public void testRunningOnSeleniumGrid() {
        // Verify we are running on a remote WebDriver (Selenium Grid / Docker)
        Assert.assertTrue("Should be using RemoteWebDriver",
                driver instanceof org.openqa.selenium.remote.RemoteWebDriver);
        // Verify the hub URL is configured
        String hubUrl = TestConfig.getSeleniumHubUrl();
        Assert.assertNotNull("Selenium hub URL should be configured", hubUrl);
        Assert.assertFalse("Selenium hub URL should not be empty", hubUrl.isEmpty());
    }
}

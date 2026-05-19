import org.junit.*;
import org.openqa.selenium.*;
import java.util.UUID;


public class PublicPageTest extends TestBase {

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

    // ===== MULTIPLE PAGE TEST =====
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

    // ===== LEADERBOARD =====
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

    // ===== PUZZLE PAGE =====
    @Test
    public void testPuzzlePageHasBoard() {
        PuzzlePage puzzlePage = new PuzzlePage(driver);
        Assert.assertTrue("Puzzle page should have a chess board", puzzlePage.isPuzzleBoardVisible());
    }

    // ===== EDITOR DROPDOWN TEST (public, no login required) =====
    @Test
    public void testEditorOpeningDropdown() {
        EditorPage editorPage = new EditorPage(driver);
        int optionCount = editorPage.getOpeningOptionCount();
        Assert.assertTrue("Editor opening dropdown should have multiple options", optionCount > 1);

        editorPage.selectOpeningByVisibleText("C50 Italian Game: Hungarian Defense");
        String selectedText = editorPage.getSelectedOpeningText();
        Assert.assertTrue("Selected opening should be Italian Game: Hungarian Defense",
                selectedText.contains("Hungarian Defense"));
    }

    // ===== COORDINATE TRAINING RADIO BUTTON TEST (public, no login required) =====
    @Test
    public void testCoordinateTrainingColorRadioButtons() {
        PuzzlePage puzzlePage = new PuzzlePage(driver);
        puzzlePage.openCoordinateTraining();

        Assert.assertTrue("Random should be selected by default", puzzlePage.isColorRadioSelected("random"));

        puzzlePage.selectColorRadio("black");
        Assert.assertTrue("Black should be selected after clicking", puzzlePage.isColorRadioSelected("black"));
        Assert.assertFalse("Random should not be selected", puzzlePage.isColorRadioSelected("random"));
    }

    // ===== MAIN PAGE FOOTER =====
    @Test
    public void testMainPageFooter() {
        MainPage mainPage = new MainPage(driver);
        String footerText = mainPage.getFooterText();
        Assert.assertFalse("Footer should not be empty", footerText.isEmpty());
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

    // ===== RANDOM DATA TEST =====
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
}


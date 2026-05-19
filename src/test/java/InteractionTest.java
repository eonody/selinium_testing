import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import java.util.List;


public class InteractionTest extends TestBase {

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

    // ===== EXPLICIT WAIT TEST =====
    @Test
    public void testExplicitWaitForElement() {
        MainPage mainPage = new MainPage(driver);
        Assert.assertTrue("Body should be visible after wait", mainPage.isBodyVisible());
    }

    // ===== DRAG AND DROP (puzzle board) =====
    @Test
    public void testDragAndDropOnPuzzleBoard() {
        PuzzlePage puzzlePage = new PuzzlePage(driver);
        Assert.assertTrue("Puzzle board should be visible", puzzlePage.isPuzzleBoardVisible());

        List<WebElement> pieces = puzzlePage.getPieces();
        Assert.assertTrue("Board should have pieces", pieces.size() > 0);

        WebElement piece = pieces.get(0);
        puzzlePage.dragPiece(piece, 75, 0);
        Assert.assertTrue("Puzzle board should still be visible after drag", puzzlePage.isPuzzleBoardVisible());
    }

    // ===== DOCKERIZED EXECUTION TEST =====
    @Test
    public void testRunningOnSeleniumGrid() {
        Assert.assertTrue("Should be using RemoteWebDriver",
                driver instanceof org.openqa.selenium.remote.RemoteWebDriver);
        String hubUrl = TestConfig.getSeleniumHubUrl();
        Assert.assertNotNull("Selenium hub URL should be configured", hubUrl);
        Assert.assertFalse("Selenium hub URL should not be empty", hubUrl.isEmpty());
    }
}


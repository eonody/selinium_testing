import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class CrossBrowserTest {

    private WebDriver driver;
    private final String browserName;

    @Parameterized.Parameters(name = "Browser: {0}")
    public static Collection<Object[]> browsers() {
        return Arrays.asList(new Object[][]{
                {"chrome"},
                {"edge"}
        });
    }

    public CrossBrowserTest(String browserName) {
        this.browserName = browserName;
    }

    @Before
    public void setup() throws Exception {
        String hubUrl = TestConfig.getSeleniumHubUrl();

        if ("edge".equalsIgnoreCase(browserName)) {
            EdgeOptions options = new EdgeOptions();
            if (TestConfig.isHeadless()) {
                options.addArguments("--headless");
            }
            driver = new RemoteWebDriver(new URL(hubUrl), options);
        } else {
            ChromeOptions options = new ChromeOptions();
            if (TestConfig.isHeadless()) {
                options.addArguments("--headless");
            }
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            driver = new RemoteWebDriver(new URL(hubUrl), options);
        }
        driver.manage().window().maximize();
    }

    @Test
    public void testHomepageLoads() {
        driver.get(TestConfig.getBaseUrl());
        String title = driver.getTitle();
        Assert.assertTrue("Title should contain lichess on " + browserName,
                title.toLowerCase().contains("lichess"));
    }

    @Test
    public void testLeaderboardPageLoads() {
        driver.get(TestConfig.getBaseUrl() + "/player");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        String body = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue("Leaderboard should have content on " + browserName, body.length() > 0);
    }

    @Test
    public void testAnalysisBoardLoads() {
        driver.get(TestConfig.getBaseUrl() + "/analysis");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//main//div[contains(@class,'analyse')]//cg-board")));
        Assert.assertTrue("Analysis board should be visible on " + browserName,
                driver.findElement(By.xpath("//main//div[contains(@class,'analyse')]//cg-board")).isDisplayed());
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.URL;


public class DependentTests {
    private WebDriver driver;

    @BeforeMethod
    public void setup() throws Exception {
        driver = new RemoteWebDriver(new URL(TestConfig.getSeleniumHubUrl()), TestConfig.createChromeOptions());
        driver.manage().window().maximize();
    }

    @Test
    public void testNavigateToHomePage() {
        driver.get(TestConfig.getBaseUrl());
        String title = driver.getTitle();
        Assert.assertTrue(title.toLowerCase().contains("lichess"),
                "Homepage title should contain lichess");
    }

    @Test(dependsOnMethods = "testNavigateToHomePage")
    public void testNavigateToImportPage() {
        driver.get(TestConfig.getBaseUrl() + "/paste");
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/paste"),
                "Should be on the import/paste page");
    }

    @Test(dependsOnMethods = "testNavigateToImportPage")
    public void testImportFormIsAvailable() {
        driver.get(TestConfig.getBaseUrl() + "/paste");
        WebElement textarea = driver.findElement(By.xpath("//main//form//textarea"));
        Assert.assertNotNull(textarea, "Import textarea should exist");
        Assert.assertTrue(textarea.isDisplayed(), "Import textarea should be visible");
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}


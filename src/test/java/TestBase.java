import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.TimeUnit;

public class TestBase {

    protected WebDriver driver;

    @Before
    public void setup() {

        boolean headless = Boolean.parseBoolean(
                ConfigReader.get("headless")
        );

        driver = DriverFactory.createDriver("chrome", headless);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get(ConfigReader.get("baseUrl"));

        Cookie cookie = new Cookie("testCookie", "accepted");
        driver.manage().addCookie(cookie);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
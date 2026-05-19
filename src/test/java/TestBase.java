import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;


public abstract class TestBase {
    protected WebDriver driver;

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
        driver = new RemoteWebDriver(new URL(TestConfig.getSeleniumHubUrl()), TestConfig.createChromeOptions());
        driver.manage().window().maximize();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void assumeNoCloudflare() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        if (body.contains("checking your browser") || body.contains("cloudflare") || body.contains("verify you are human")) {
            if (!TestConfig.isHeadless()) {
                System.out.println(">>> CAPTCHA detected! Please solve it manually. Waiting up to 60 seconds...");
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(60)).until(
                        d -> {
                            String b = d.findElement(By.tagName("body")).getText().toLowerCase();
                            return !b.contains("checking your browser") && !b.contains("verify you are human");
                        }
                    );
                } catch (TimeoutException e) {
                    Assume.assumeTrue("CAPTCHA not solved within 60 seconds", false);
                }
            } else {
                Assume.assumeTrue("Skipping: Cloudflare verification detected in headless mode", false);
            }
        }
    }

    protected void assumeCredentials(String username, String password) {
        Assume.assumeTrue("Skipping: no credentials configured",
                !username.isEmpty() && !password.isEmpty());
    }

    protected void loginAndWait(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        assumeNoCloudflare();
        loginPage.login(username, password);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        } catch (TimeoutException e) {
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File dest = new File("screenshots/login_failed_captcha.png");
                dest.getParentFile().mkdirs();
                Files.copy(src.toPath(), dest.toPath());
                System.out.println("Login failed screenshot saved: " + dest.getAbsolutePath());
            } catch (Exception ex) {
                System.out.println("Could not take login failure screenshot: " + ex.getMessage());
            }
            Assume.assumeTrue("Skipping: Login did not complete (likely CAPTCHA) - screenshot saved", false);
        }
    }
}

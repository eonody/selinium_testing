import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver createDriver(String browser, boolean headless) {

        if (browser.equalsIgnoreCase("chrome")) {

            ChromeOptions options = new ChromeOptions();

            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");

            if (headless) {
                options.addArguments("--headless");
            }

            return new ChromeDriver(options);
        }

        throw new IllegalArgumentException(
                "Unsupported browser: " + browser
        );
    }
}
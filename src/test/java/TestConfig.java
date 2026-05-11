import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class TestConfig {
    private static Properties props;

    static {
        props = new Properties();
        try {
            props.load(new FileInputStream("test.properties"));
        } catch (IOException e) {
            System.out.println("test.properties not found, using defaults");
        }
    }

    public static String getBaseUrl() {
        return props.getProperty("base_url", "https://lichess.org");
    }

    public static String getUsername() {
        return props.getProperty("username", "");
    }

    public static String getPassword() {
        return props.getProperty("password", "");
    }

    public static String getBrowser() {
        return props.getProperty("browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(props.getProperty("headless", "false"));
    }

    public static String getSeleniumHubUrl() {
        String env = System.getenv("SELENIUM_HUB_URL");
        if (env != null && !env.isEmpty()) {
            return env;
        }
        return props.getProperty("selenium_hub_url", "http://selenium:4444/wd/hub");
    }
}


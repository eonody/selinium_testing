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
            System.out.println("test.properties not found, using env vars / defaults");
        }
    }

    private static String get(String key, String defaultValue) {
        String env = System.getenv(key.toUpperCase());
        if (env != null && !env.isEmpty()) {
            return env;
        }
        return props.getProperty(key, defaultValue);
    }

    public static String getBaseUrl() {
        return get("base_url", "https://lichess.org");
    }

    public static String getUsername() {
        return get("test_username", "");
    }

    public static String getPassword() {
        return get("test_password", "");
    }

    public static String getBrowser() {
        return get("browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public static String getSeleniumHubUrl() {
        return get("selenium_hub_url", "http://selenium:4444/wd/hub");
    }
}

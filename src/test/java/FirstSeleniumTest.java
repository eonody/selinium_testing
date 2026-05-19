import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Main test suite that runs all organized test classes.
 * - PublicPageTest: static pages, search, leaderboard, navigation (no login)
 * - InteractionTest: hover, cookies, JS executor, drag-and-drop (no login)
 * - FileAndFormTest: file upload, textarea, download (no login)
 * - LoginTest: login/logout (requires credentials)
 * - LoggedInUserTest: profile, preferences, report (requires login)
 * - CrossBrowserTest: parameterized Chrome/Firefox tests
 * - DependentTests: TestNG test dependencies
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    PublicPageTest.class,
    InteractionTest.class,
    FileAndFormTest.class,
    LoginTest.class,
    LoggedInUserTest.class,
    CrossBrowserTest.class
})
public class FirstSeleniumTest {
}

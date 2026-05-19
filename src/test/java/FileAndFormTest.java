import org.junit.*;
import org.openqa.selenium.*;
import java.io.File;


public class FileAndFormTest extends TestBase {

    // ===== FILE UPLOAD TEST (PGN import on /paste) =====
    @Test
    public void testFileUploadPgn() throws Exception {
        AnalysisPage analysisPage = new AnalysisPage(driver);
        analysisPage.openImportPage();

        Assert.assertTrue("Import page should have a file input", analysisPage.hasFileInput());

        File tempPgn = File.createTempFile("test_game", ".pgn");
        tempPgn.deleteOnExit();
        java.io.FileWriter fw = new java.io.FileWriter(tempPgn);
        fw.write("[Event \"Test\"]\n[Site \"lichess.org\"]\n[Result \"1-0\"]\n\n1. e4 e5 2. Nf3 Nc6 1-0\n");
        fw.close();

        analysisPage.uploadFile(tempPgn.getAbsolutePath());
        Thread.sleep(2000);

        String body = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue("Page should still be functional after upload", body.length() > 0);
    }

    // ===== TEXTAREA TEST (import page - no login required) =====
    @Test
    public void testImportPageTextarea() {
        AnalysisPage analysisPage = new AnalysisPage(driver);
        analysisPage.openImportPage();

        Assert.assertTrue("Import page should have textarea", analysisPage.hasImportTextarea());

        String pgn = "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6";
        analysisPage.fillImportTextarea(pgn);
        String value = analysisPage.getImportTextareaValue();
        Assert.assertTrue("Textarea should contain the PGN we typed", value.contains("1. e4 e5"));
    }

    // ===== IMPORT FORM SUBMISSION (send_form - no login) =====
    @Test
    public void testImportFormSubmission() {
        AnalysisPage analysisPage = new AnalysisPage(driver);
        analysisPage.openImportPage();

        String pgn = "[Event \"Test\"]\n[Site \"lichess.org\"]\n[Result \"1-0\"]\n\n1. e4 e5 2. Nf3 Nc6 1-0";
        analysisPage.fillImportTextarea(pgn);
        analysisPage.submitImportForm();

        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        String url = driver.getCurrentUrl();
        Assert.assertTrue("Should navigate away from /paste after import",
                !url.contains("/paste") || url.contains("/analysis") || url.contains("/game"));
    }

    // ===== DOWNLOAD FILES TEST (PGN game export) =====
    @Test
    public void testDownloadPgnFile() throws Exception {
        driver.get(TestConfig.getBaseUrl());

        String pgn = (String) ((JavascriptExecutor) driver).executeScript(
            "var response = await fetch('/game/export/q7ZvsdUF');" +
            "return await response.text();"
        );

        Assert.assertNotNull("PGN response should not be null", pgn);
        Assert.assertTrue("Downloaded content should contain PGN data",
                pgn.contains("[Event") || pgn.contains("[Site") || pgn.contains("1."));
    }
}


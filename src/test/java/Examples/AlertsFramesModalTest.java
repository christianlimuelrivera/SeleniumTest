package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlertsFramesModals;
import utils.ExtentManager;
import utils.RetryAnalyzer;
import utils.SheetName;
import java.util.Map;

public class AlertsFramesModalTest extends Main {

    @SheetName("Alerts")
    @Test(dataProvider = "excelDataProviderMapAnnotation", dataProviderClass = utils.DataProviderUtil.class,priority = 1,
            retryAnalyzer = RetryAnalyzer.class)

    public void NewTab(Map<String, String> row) {

        // ============================================================
        // STEP 1: INITIALIZE PAGE OBJECT
        // ============================================================
        AlertsFramesModals alertsPage = new AlertsFramesModals(getDriver());

        // ============================================================
        // STEP 2: LOG TEST START
        // ============================================================
        ExtentManager.getTest().log(Status.INFO, "Testing Browser Windows and New Window");

        // ============================================================
        // STEP 3: NEW TAB
        // Opens new tab, gets heading text, closes tab
        // ============================================================
        String headingText = alertsPage.NewTabs(row);
        Assert.assertEquals(headingText,
                row.get("SampleHeading"),
                "New Tab heading mismatch!");

        // ============================================================
        // STEP 4: NEW WINDOW
        // Opens new window, gets heading text, closes window
        // ============================================================
        String headingTextWindow = alertsPage.NewWindow(row);
        Assert.assertEquals(headingTextWindow,
                row.get("SampleHeading"),
                "New Window heading mismatch!");

        // ============================================================
        // STEP 5: NEW WINDOW MESSAGE
        // Opens message window and closes it
        // Note: Raw body text - no assertion applied
        // ============================================================
        alertsPage.NewWindowMessage(row);

        // ============================================================
        // STEP 6: FINALIZE SOFT ASSERTIONS
        // ============================================================
        alertsPage.assertAll();
    }
    @SheetName("Alerts")
    @Test(dataProvider = "excelDataProviderMapAnnotation",
            dataProviderClass = utils.DataProviderUtil.class,
            priority = 2,   retryAnalyzer = RetryAnalyzer.class)
    public void AlertsTypesAnd(Map<String, String> row) {

        // ============================================================
        // STEP 1: INITIALIZE PAGE OBJECT
        // ============================================================
        AlertsFramesModals alertsPage = new AlertsFramesModals(getDriver());

        // ============================================================
        // STEP 2: LOG TEST START
        // ============================================================
        ExtentManager.getTest().log(Status.INFO, "Testing Alert Types");

        // ============================================================
        // STEP 3: SIMPLE ALERT
        // Clicks alert button, gets text and accepts it
        // ============================================================
        String simpleAlertText = alertsPage.clickAlertAndAccept();
        ExtentManager.getTest().log(Status.INFO, "Simple Alert text: " + simpleAlertText);

        // ============================================================
        // STEP 4: TIMER ALERT
        // Clicks timer button, waits for delayed alert and accepts it
        // ============================================================
        String timerAlertText = alertsPage.clickTimerAlert();
        ExtentManager.getTest().log(Status.INFO, "Timer Alert text: " + timerAlertText);

        // ============================================================
        // STEP 5: CONFIRM ALERT
        // Clicks confirm button and dismisses the alert
        // ============================================================
        String confirmText = alertsPage.clickConfirmAlert();
        ExtentManager.getTest().log(Status.INFO, "Timer Alert text: " + confirmText);
        // ============================================================
        // STEP 6: PROMPT ALERT
        // Types text from Excel into the prompt alert and accepts it
        // ============================================================
        alertsPage.clickPromptAlert(row.get("Prompt"));
        ExtentManager.getTest().pass("Prompt input successful: " + row.get("Prompt"));

    }

    @SheetName("Alerts")
    @Test(dataProvider = "excelDataProviderMapAnnotation",
            dataProviderClass = utils.DataProviderUtil.class,
            priority = 2,   retryAnalyzer = RetryAnalyzer.class)
    public void FramesandNestedframes(Map<String, String> row) {
        // ============================================================
        // STEP 1: INITIALIZE PAGE OBJECT
        // ============================================================
        AlertsFramesModals framesPage = new AlertsFramesModals(getDriver());


        framesPage.navigateToFrames();
// Frame 1
        String frame1Text = framesPage.getFrameText("frame1");
        Assert.assertEquals(frame1Text, row.get("FrameText"), "Frame 1 text mismatch!");

// Frame 2
        String frame2Text = framesPage.getFrameText("frame2");
        Assert.assertEquals(frame2Text, row.get("FrameText"), "Frame 2 text mismatch!");
        framesPage.assertAll();
    }
    @SheetName("Alerts")
    @Test(dataProvider = "excelDataProviderMapAnnotation",
            dataProviderClass = utils.DataProviderUtil.class,
            priority = 3,   retryAnalyzer = RetryAnalyzer.class)
    public void Modals(Map<String, String> row) {

        // ============================================================
        // STEP 1: INITIALIZE PAGE OBJECT
        // ============================================================
        AlertsFramesModals modalPage = new AlertsFramesModals(getDriver());

        // ============================================================
        // STEP 2: LOG TEST START
        // ============================================================
        ExtentManager.getTest().log(Status.INFO, "Testing Small and Large Modals");

        // ============================================================
        // STEP 3: NAVIGATE AND HANDLE MODALS
        // Returns Map with smallBody and largeBody text
        // ============================================================
        modalPage.navigateToModal();
        Map<String, String> modalData = modalPage.handleModals();

        // ============================================================
        // STEP 4: ASSERTIONS
        // ============================================================
        Assert.assertEquals(modalData.get("smallBody"),
                row.get("SmallModalBody"),
                "Small modal text mismatch!");

        Assert.assertEquals(modalData.get("largeBody"),
                row.get("LargeModalBody"),
                "Large modal text mismatch!");

    }
    }
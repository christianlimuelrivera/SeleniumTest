package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlertsFramesModals;
import utils.ExtentManager;
import utils.SheetName;
import java.util.Map;

public class AlertsFramesModalTest extends Main {

    @SheetName("Alerts")
    @Test(dataProvider = "excelDataProviderMapAnnotation", dataProviderClass = utils.DataProviderUtil.class,priority = 1)

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
            priority = 2)
    public void AlertsTypes(Map<String, String> row) {

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
        alertsPage.clickConfirmAlert();

        // ============================================================
        // STEP 6: PROMPT ALERT
        // Types text from Excel into the prompt alert and accepts it
        // ============================================================
        alertsPage.clickPromptAlert(row.get("Prompt"));
        ExtentManager.getTest().pass("Prompt input successful: " + row.get("Prompt"));

        // ============================================================
        // STEP 7: FINALIZE SOFT ASSERTIONS
        // ============================================================
        alertsPage.assertAll();
    }
}
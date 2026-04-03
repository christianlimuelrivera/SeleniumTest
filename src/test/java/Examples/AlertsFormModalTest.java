package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlertsFramesModals;
import utils.ExtentManager;
import utils.SheetName;
import java.util.Map;

public class AlertsFormModalTest extends Main {

    @SheetName("Alerts")
    @Test(dataProvider = "excelDataProviderMapAnnotation", dataProviderClass = utils.DataProviderUtil.class)
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
}
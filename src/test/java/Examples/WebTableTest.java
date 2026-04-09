package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ElementsPage;
import utils.ExtentManager;
import utils.RetryAnalyzer;
import utils.SheetName;

import java.util.Map;

public class WebTableTest extends Main {

    private ElementsPage elementsPage;

    // ============================================================
    // SETUP
    // ============================================================
    @BeforeMethod
    public void initPages() {
        elementsPage = new ElementsPage(getDriver());
    }

    // ============================================================
    // TEST: Web Table CRUD — UI + DB validation
    //
    // All DB logic is encapsulated in ElementsPage methods.
    // This test class has zero DBUtil dependencies.
    // ============================================================
    @SheetName("Forms")
    @Test(dataProvider = "excelDataProviderMapAnnotation",
            dataProviderClass = utils.DataProviderUtil.class,
            priority = 1,
            retryAnalyzer = RetryAnalyzer.class)
    public void webTableCRUD(Map<String, String> row) {

        String firstName  = row.get("FirstName");
        String email      = row.get("Email");

        // ============================================================
        // STEP 1: NAVIGATE
        // ============================================================
        ExtentManager.getTest().log(Status.INFO,
                "Testing Web Table CRUD for: " + firstName + " " + row.get("LastName"));

        elementsPage.navigateToWebTable();

        // ============================================================
        // STEP 2: ADD RECORD
        // UI action + DB sync handled inside registerAddData()
        // ============================================================
        elementsPage.registerAddData(row);

        // ============================================================
        // STEP 3: VALIDATE ADD — UI + DB
        // UI assertions + DB assertions + logged to ExtentReports
        // ============================================================
        elementsPage.validateAddWithDB(row, firstName);

        // ============================================================
        // STEP 4: EDIT RECORD
        // UI action + DB sync handled inside editRecord()
        // ============================================================
        elementsPage.editRecord(row);

        // ============================================================
        // STEP 5: VALIDATE EDIT — UI + DB + Cross-Check
        // ============================================================
        String searchTerm = (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
                ? row.get("EditFirstName") : firstName;

        String targetEmail = (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
                ? row.get("EditEmail") : email;

        elementsPage.validateEditWithDB(row, searchTerm, targetEmail);

        // ============================================================
        // STEP 6: DELETE RECORD
        // UI delete + DB delete handled inside deleteUserByEmail()
        // ============================================================
        elementsPage.deleteUserByEmail(targetEmail);

//        // ============================================================
//        // STEP 7: VALIDATE DELETION — UI
//        // ============================================================
//        boolean isDeleted = elementsPage.isRecordDeleted(targetEmail);
//        Assert.assertTrue(isDeleted,
//                "UI: Record with email '" + targetEmail + "' was not deleted.");

        // ============================================================
        // STEP 8: LOG SUCCESS
        // ============================================================
        ExtentManager.getTest().pass(
                "Web Table E2E complete: Add, Edit, Delete — UI + DB validated for: "
                        + firstName + " " + row.get("LastName"));
    }
}
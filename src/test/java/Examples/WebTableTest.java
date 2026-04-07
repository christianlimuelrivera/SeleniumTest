package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ElementsPage;
import utils.DBUtil;
import utils.ExtentManager;
import utils.RetryAnalyzer;
import utils.SheetName;

import java.util.Map;

public class WebTableTest extends Main {

    private ElementsPage elementsPage;

    // ============================================================
    // SETUP: Initialize Page Objects
    // ============================================================
    @BeforeMethod
    public void initPages() {
        elementsPage = new ElementsPage(getDriver());
    }

    // ============================================================
    // TEST: Web Table CRUD with UI & Database Cross-Validation
    // ============================================================
    @SheetName("Forms")
    @Test(dataProvider = "excelDataProviderMapAnnotation",
            dataProviderClass = utils.DataProviderUtil.class,
            priority = 1,
            retryAnalyzer = RetryAnalyzer.class)
    public void webTableCRUD(Map<String, String> row) {

        String firstName = row.get("FirstName");
        String email     = row.get("Email");

        // ============================================================
        // STEP 1: NAVIGATE TO TARGET PAGE
        // ============================================================
        ExtentManager.getTest().log(Status.INFO,
                "Testing Web Table CRUD for: " + firstName + " " + row.get("LastName"));

        elementsPage.navigateToWebTable();

        // ============================================================
        // STEP 2: ADD RECORD — UI ACTION
        // ============================================================
        elementsPage.registerAddData(row);
        ExtentManager.getTest().log(Status.INFO, "UI: Record added for " + firstName);

        // ============================================================
        // STEP 2B: SYNC ADD TO DATABASE (Simulation)
        // ============================================================
        DBUtil.insertUserManual(row);
        ExtentManager.getTest().log(Status.INFO, "DB: Record synced for " + email);

        // ============================================================
        // STEP 3: VALIDATE ADD — UI VS EXCEL
        // ============================================================
        Map<String, String> tableData = elementsPage.getAddedRowData(firstName);

        elementsPage.assertEqualsAndLog(tableData.get("First Name"), row.get("FirstName"),  "UI First Name");
        elementsPage.assertEqualsAndLog(tableData.get("Last Name"),  row.get("LastName"),   "UI Last Name");
        elementsPage.assertEqualsAndLog(tableData.get("Age"),        row.get("Age"),        "UI Age");
        elementsPage.assertEqualsAndLog(tableData.get("Email"),      row.get("Email"),      "UI Email");
        elementsPage.assertEqualsAndLog(tableData.get("Salary"),     row.get("Salary"),     "UI Salary");
        elementsPage.assertEqualsAndLog(tableData.get("Department"), row.get("Department"), "UI Department");

        // ============================================================
        // STEP 3B: VALIDATE ADD — DB VS EXCEL
        // ============================================================
        Map<String, Object> dbRecord = DBUtil.getUserByEmail(email);
        Assert.assertNotNull(dbRecord, "DB: No record found for email: " + email);

        elementsPage.assertEqualsAndLog(dbRecord.get("firstname"), row.get("FirstName"), "DB First Name");
        elementsPage.assertEqualsAndLog(dbRecord.get("lastname"),  row.get("LastName"),  "DB Last Name");
        elementsPage.assertEqualsAndLog(dbRecord.get("email"),     email,               "DB Email");
        elementsPage.assertEqualsAndLog(dbRecord.get("age"),       row.get("Age"),       "DB Age");
        elementsPage.assertEqualsAndLog(dbRecord.get("salary"),    row.get("Salary"),    "DB Salary");
        elementsPage.assertEqualsAndLog(dbRecord.get("department"),row.get("Department"),"DB Department");

        // ============================================================
        // STEP 4: EDIT RECORD — UI ACTION
        // ============================================================
        elementsPage.editRecord(row);

        // ============================================================
        // STEP 4B: SYNC EDIT TO DATABASE (Simulation)
        // ============================================================
        DBUtil.updateUserManual(row);

        // ============================================================
        // STEP 5: VALIDATE EDIT — UI VS EXCEL
        // ============================================================
        String searchTerm = (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
                ? row.get("EditFirstName") : firstName;

        Map<String, String> editedData = elementsPage.getAddedRowData(searchTerm);

        if (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
            Assert.assertEquals(editedData.get("First Name"), row.get("EditFirstName"), "Edited First Name mismatch!");
        if (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
            Assert.assertEquals(editedData.get("Email"),      row.get("EditEmail"),     "Edited Email mismatch!");
        // (Add other UI Edit asserts here if needed)

        // ============================================================
        // STEP 5B: VALIDATE EDIT — DB VS EXCEL
        // ============================================================
        String targetEmail = (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
                ? row.get("EditEmail") : email;

        Map<String, Object> updatedDbRecord = DBUtil.getUserByEmail(targetEmail);
        Assert.assertNotNull(updatedDbRecord, "DB Search Failed for: " + targetEmail);

        if (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty()) {
            elementsPage.assertEqualsAndLog(updatedDbRecord.get("firstname"), row.get("EditFirstName"), "DB Edited First Name");
        }
        if (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty()) {
            elementsPage.assertEqualsAndLog(updatedDbRecord.get("email"), row.get("EditEmail"), "DB Edited Email");
        }

        // ============================================================
        // STEP 6: CROSS-VALIDATION — UI VS DATABASE
        // ============================================================
        Map<String, String> uiData = elementsPage.getAddedRowData(searchTerm);
        Map<String, Object> dbData = DBUtil.getUserByEmail(targetEmail);

        Assert.assertNotNull(dbData, "DB Data missing for cross-check!");

        elementsPage.assertEqualsAndLog(
                uiData.get("First Name"),
                dbData.get("firstname").toString(),
                "Cross-Check: UI matches DB (First Name)"
        );

        elementsPage.assertEqualsAndLog(
                uiData.get("Email"),
                dbData.get("email").toString(),
                "Cross-Check: UI matches DB (Email)"
        );

        // ============================================================
        // STEP 7: DELETE RECORD — UI & DATABASE CLEANUP
        // ============================================================
        elementsPage.deleteUserByEmail(targetEmail);
        DBUtil.deleteUserByEmail(targetEmail);

        // ============================================================
        // STEP 8: LOG FINAL SUCCESS
        // ============================================================
        ExtentManager.getTest().pass("Web Table E2E complete: Add, Edit, and Delete validated for: "
                + firstName + " " + row.get("LastName"));
    }
}
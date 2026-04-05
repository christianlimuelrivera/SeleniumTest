package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ElementsPage;
import utils.ExtentManager;
import utils.SheetName;

import java.util.Map;

public class WebTableTest extends Main {

    private ElementsPage elementsPage;

    // ============================================================
    // SETUP
    // Runs after Main.setup() so getDriver() is ready.
    // ============================================================
    @BeforeMethod
    public void initPages() {
        elementsPage = new ElementsPage(getDriver());
    }

    // ============================================================
    // TEST: Full E2E — Add, Edit, Delete
    // ============================================================
    @SheetName("Forms")  // ✅ was "Forms" — needs its own sheet
    @Test(dataProvider = "excelDataProviderMapAnnotation",
            dataProviderClass = utils.DataProviderUtil.class,
            priority = 1)
    public void webTableCRUD(Map<String, String> row) {

        // ============================================================
        // STEP 1: NAVIGATE
        // ============================================================
        ExtentManager.getTest().log(Status.INFO,
                "Testing Web Table CRUD for: " + row.get("FirstName") + " " + row.get("LastName"));

        elementsPage.navigateToWebTable();

        // ============================================================
        // STEP 2: ADD RECORD
        // ============================================================
        elementsPage.registerAddData(row);

        // ============================================================
        // STEP 3: VALIDATE ADDED DATA
        // ============================================================
        Map<String, String> tableData = elementsPage.getAddedRowData(row.get("FirstName"));

        Assert.assertEquals(tableData.get("First Name"), row.get("FirstName"),  "First Name mismatch!");
        Assert.assertEquals(tableData.get("Last Name"),  row.get("LastName"),   "Last Name mismatch!");
        Assert.assertEquals(tableData.get("Age"),        row.get("Age"),        "Age mismatch!");
        Assert.assertEquals(tableData.get("Email"),      row.get("Email"),      "Email mismatch!");
        Assert.assertEquals(tableData.get("Salary"),     row.get("Salary"),     "Salary mismatch!");
        Assert.assertEquals(tableData.get("Department"), row.get("Department"), "Department mismatch!");

        // ============================================================
        // STEP 4: EDIT RECORD
        // Only fields with Edit prefix values will be updated
        // ============================================================
        elementsPage.editRecord(row);

        // ============================================================
        // STEP 5: VALIDATE EDITED DATA
        // Search by updated FirstName if it was changed, else original
        // ============================================================
        String searchTerm = (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
                ? row.get("EditFirstName")
                : row.get("FirstName");

        Map<String, String> editedData = elementsPage.getAddedRowData(searchTerm);

        if (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
            Assert.assertEquals(editedData.get("First Name"), row.get("EditFirstName"), "Edited First Name mismatch!");

        if (row.get("EditLastName") != null && !row.get("EditLastName").isEmpty())
            Assert.assertEquals(editedData.get("Last Name"), row.get("EditLastName"), "Edited Last Name mismatch!");

        if (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
            Assert.assertEquals(editedData.get("Email"), row.get("EditEmail"), "Edited Email mismatch!");

        if (row.get("EditAge") != null && !row.get("EditAge").isEmpty())
            Assert.assertEquals(editedData.get("Age"), row.get("EditAge"), "Edited Age mismatch!");

        if (row.get("EditSalary") != null && !row.get("EditSalary").isEmpty())
            Assert.assertEquals(editedData.get("Salary"), row.get("EditSalary"), "Edited Salary mismatch!");

        if (row.get("EditDepartment") != null && !row.get("EditDepartment").isEmpty())
            Assert.assertEquals(editedData.get("Department"), row.get("EditDepartment"), "Edited Department mismatch!");

        // ============================================================
        // STEP 6: DELETE RECORD
        // Use EditEmail if updated, else original Email
        // ============================================================
        String targetEmail = (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
                ? row.get("EditEmail")
                : row.get("Email");

        elementsPage.deleteUserByEmail(targetEmail);

        // ============================================================
        // STEP 8: LOG SUCCESS
        // ============================================================
        ExtentManager.getTest().pass("Web Table E2E complete: Add, Edit, Delete validated for: "
                + row.get("FirstName") + " " + row.get("LastName"));
    }
}
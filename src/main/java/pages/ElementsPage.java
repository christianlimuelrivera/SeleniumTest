package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import utils.DBUtil;
import utils.ExtentManager;

import java.util.List;
import java.util.Map;

public class ElementsPage extends BasePage {

    public ElementsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ============================================================
    // LOCATORS
    // ============================================================

    @FindBy(xpath = "//a[@href='/elements']")
    private WebElement elementsForm;

    @FindBy(xpath = "//a[@href='/webtables']")
    private WebElement webTablesMenu;

    @FindBy(id = "addNewRecordButton")
    private WebElement addButton;

    @FindBy(id = "firstName")
    private WebElement firstName;

    @FindBy(id = "lastName")
    private WebElement lastName;

    @FindBy(id = "userEmail")
    private WebElement userEmail;

    @FindBy(id = "age")
    private WebElement age;

    @FindBy(id = "salary")
    private WebElement salary;

    @FindBy(id = "department")
    private WebElement department;

    @FindBy(id = "submit")
    private WebElement submitBtn;

    @FindBy(id = "searchBox")
    private WebElement searchBox;

    @FindBy(xpath = "//span[@title='Edit']")
    private List<WebElement> editButtons;

    @FindBy(xpath = "//div[@class='rt-noData']")
    private WebElement noDataMessage;

    // ============================================================
    // NAVIGATION
    // ============================================================

    public void navigateToWebTable() {
        click(elementsForm);
        click(webTablesMenu);
    }

    // ============================================================
    // ADD — UI + DB
    // ============================================================

    /**
     * Fills the Add form, submits it, then syncs the same
     * data to the WebUsers DB table.
     * Self-contained — test class does not need to call DBUtil.
     *
     * @param row Map from Excel
     */
    public void registerAddData(Map<String, String> row) {

        String firstNameVal  = row.get("FirstName");
        String lastNameVal   = row.get("LastName");
        String userEmailVal  = row.get("Email");
        String ageVal        = row.get("Age");
        String salaryVal     = row.get("Salary");
        String departmentVal = row.get("Department");

        // --- UI ---
        click(addButton);
        sendKeys(firstName,  firstNameVal);
        sendKeys(lastName,   lastNameVal);
        sendKeys(userEmail,  userEmailVal);
        sendKeys(age,        ageVal);
        sendKeys(salary,     salaryVal);
        sendKeys(department, departmentVal);
        click(submitBtn);

        ExtentManager.getTest().log(Status.INFO,
                "UI: Added record → " + firstNameVal + " " + lastNameVal);

        // --- DB SYNC ---
        DBUtil.insertUserManual(row);
        ExtentManager.getTest().log(Status.INFO,
                "DB: Synced record → " + userEmailVal);
    }

    // ============================================================
    // EDIT — UI + DB
    // ============================================================

    /**
     * Searches for a record, edits the fields that have Edit
     * prefix values in Excel, submits, then syncs the update to DB.
     *
     * @param row Map from Excel
     */
    public void editRecord(Map<String, String> row) {

        // --- UI ---
        sendKeys(searchBox, row.get("FirstName"));
        click(editButtons.get(0));

        if (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
            sendKeys(firstName, row.get("EditFirstName"));
        if (row.get("EditLastName") != null && !row.get("EditLastName").isEmpty())
            sendKeys(lastName, row.get("EditLastName"));
        if (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
            sendKeys(userEmail, row.get("EditEmail"));
        if (row.get("EditAge") != null && !row.get("EditAge").isEmpty())
            sendKeys(age, row.get("EditAge"));
        if (row.get("EditSalary") != null && !row.get("EditSalary").isEmpty())
            sendKeys(salary, row.get("EditSalary"));
        if (row.get("EditDepartment") != null && !row.get("EditDepartment").isEmpty())
            sendKeys(department, row.get("EditDepartment"));

        click(submitBtn);
        ExtentManager.getTest().log(Status.INFO,
                "UI: Edited record → " + row.get("FirstName"));

        // --- DB SYNC ---
        DBUtil.updateUserManual(row);
        ExtentManager.getTest().log(Status.INFO,
                "DB: Synced edit for → " + row.get("Email"));
    }

    // ============================================================
    // DELETE — UI + DB
    // ============================================================

    /**
     * Searches for the record by email, clicks delete on UI,
     * then removes the same record from DB.
     *
     * @param email the unique email of the record to delete
     */
    public void deleteUserByEmail(String email) {
        sendKeys(searchBox, email);
        pause(1);
        WebElement deleteIcon = getDeleteIconByEmail(email);
        hoverAndClick(deleteIcon);

        ExtentManager.getTest().log(Status.INFO,
                "UI: Deleted record → " + email);

        // --- DB SYNC ---
        DBUtil.deleteUserByEmail(email);
        ExtentManager.getTest().log(Status.INFO,
                "DB: Deleted record → " + email);
    }

    // ============================================================
    // DYNAMIC LOCATOR
    // ============================================================

    private WebElement getDeleteIconByEmail(String email) {
        By deleteLocator = By.xpath(
                "//tr[.//td[contains(., '" + email + "')]]//span[@title='Delete']"
        );
        return wait.until(ExpectedConditions.elementToBeClickable(deleteLocator));
    }

    // ============================================================
    // VALIDATION — ADD (UI + DB)
    // ============================================================

    /**
     * Validates the added record against both the UI table and the DB.
     * Logs each field assertion individually to ExtentReports.
     *
     * @param row        Map from Excel
     * @param firstNameVal the first name used to search the UI table
     */
    public void validateAddWithDB(Map<String, String> row, String firstNameVal) {

        String email = row.get("Email");

        // --- UI VALIDATION ---
        Map<String, String> tableData = getAddedRowData(firstNameVal);

        assertEqualsAndLog(tableData.get("First Name"), row.get("FirstName"),  "UI First Name");
        assertEqualsAndLog(tableData.get("Last Name"),  row.get("LastName"),   "UI Last Name");
        assertEqualsAndLog(tableData.get("Age"),        row.get("Age"),        "UI Age");
        assertEqualsAndLog(tableData.get("Email"),      row.get("Email"),      "UI Email");
        assertEqualsAndLog(tableData.get("Salary"),     row.get("Salary"),     "UI Salary");
        assertEqualsAndLog(tableData.get("Department"), row.get("Department"), "UI Department");
        ExtentManager.getTest().log(Status.PASS, "UI Add validation passed for: " + firstNameVal);

        // --- DB VALIDATION ---
        Map<String, Object> dbRecord = DBUtil.getUserByEmail("WebUsers", email);
        Assert.assertNotNull(dbRecord, "DB: No record found for email: " + email);

        assertEqualsAndLog(dbRecord.get("firstname"),  row.get("FirstName"),  "DB First Name");
        assertEqualsAndLog(dbRecord.get("lastname"),   row.get("LastName"),   "DB Last Name");
        assertEqualsAndLog(dbRecord.get("email"),      email,                 "DB Email");
        assertEqualsAndLog(String.valueOf(dbRecord.get("age")),    row.get("Age"),        "DB Age");
        assertEqualsAndLog(String.valueOf(dbRecord.get("salary")), row.get("Salary"),     "DB Salary");
        assertEqualsAndLog(dbRecord.get("department"), row.get("Department"), "DB Department");
        ExtentManager.getTest().log(Status.PASS, "DB Add validation passed for: " + email);
    }

    // ============================================================
    // VALIDATION — EDIT (UI + DB + Cross-Check)
    // ============================================================

    /**
     * Validates the edited record against UI, DB, and cross-checks
     * UI vs DB to confirm both layers are in sync.
     *
     * @param row         Map from Excel
     * @param searchTerm  FirstName to search by (edited or original)
     * @param targetEmail Email to query DB by (edited or original)
     */
    public void validateEditWithDB(Map<String, String> row,
                                   String searchTerm,
                                   String targetEmail) {

        // --- UI VALIDATION ---
        Map<String, String> editedData = getAddedRowData(searchTerm);

        if (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
            assertEqualsAndLog(editedData.get("First Name"), row.get("EditFirstName"), "UI Edited First Name");
        if (row.get("EditLastName") != null && !row.get("EditLastName").isEmpty())
            assertEqualsAndLog(editedData.get("Last Name"),  row.get("EditLastName"),  "UI Edited Last Name");
        if (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
            assertEqualsAndLog(editedData.get("Email"),      row.get("EditEmail"),     "UI Edited Email");
        if (row.get("EditAge") != null && !row.get("EditAge").isEmpty())
            assertEqualsAndLog(editedData.get("Age"),        row.get("EditAge"),       "UI Edited Age");
        if (row.get("EditSalary") != null && !row.get("EditSalary").isEmpty())
            assertEqualsAndLog(editedData.get("Salary"),     row.get("EditSalary"),    "UI Edited Salary");
        if (row.get("EditDepartment") != null && !row.get("EditDepartment").isEmpty())
            assertEqualsAndLog(editedData.get("Department"), row.get("EditDepartment"),"UI Edited Department");

        ExtentManager.getTest().log(Status.PASS, "UI Edit validation passed for: " + searchTerm);

        // --- DB VALIDATION ---
        Map<String, Object> dbRecord = DBUtil.getUserByEmail("WebUsers", targetEmail);
        Assert.assertNotNull(dbRecord, "DB: No record found after edit for: " + targetEmail);

        if (row.get("EditFirstName") != null && !row.get("EditFirstName").isEmpty())
            assertEqualsAndLog(dbRecord.get("firstname"), row.get("EditFirstName"), "DB Edited First Name");
        if (row.get("EditEmail") != null && !row.get("EditEmail").isEmpty())
            assertEqualsAndLog(dbRecord.get("email"),     row.get("EditEmail"),     "DB Edited Email");

        ExtentManager.getTest().log(Status.PASS, "DB Edit validation passed for: " + targetEmail);

        // --- CROSS-CHECK: UI vs DB ---
        assertEqualsAndLog(
                editedData.get("First Name"),
                String.valueOf(dbRecord.get("firstname")),
                "Cross-Check: First Name — UI matches DB"
        );
        assertEqualsAndLog(
                editedData.get("Email"),
                String.valueOf(dbRecord.get("email")),
                "Cross-Check: Email — UI matches DB"
        );
        ExtentManager.getTest().log(Status.PASS, "Cross-check passed: UI and DB are in sync");
    }

    // ============================================================
    // VALIDATION — DELETE (UI)
    // ============================================================

    /**
     * Confirms the record is no longer visible in the UI table.
     *
     * @param email the email of the deleted record
     */
    public boolean isRecordDeleted(String email) {
        sendKeys(searchBox, email);
        pause(1);
        try {
            wait.until(ExpectedConditions.visibilityOf(noDataMessage));
            searchBox.clear();
            return true;
        } catch (Exception e) {
            searchBox.clear();
            ExtentManager.getTest().log(Status.WARNING,
                    "No-data message not found after delete for: " + email);
            return false;
        }
    }

    // ============================================================
    // TABLE READ METHODS
    // ============================================================

    public Map<String, String> getAddedRowData(String firstNameVal) {
        return getTableRow(searchBox, firstNameVal);
    }

    public Map<String, String> getAddedRowDataEdit(String firstNameVal) {
        return getTableRow(searchBox, firstNameVal);
    }

    public Map<String, String> getRowByKeyword(String keyword) {
        return getTableRow(searchBox, keyword);
    }
}
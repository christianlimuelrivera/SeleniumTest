package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

    /**
     * Navigates to the Web Tables section from the homepage.
     */
    public void navigateToWebTable() {
        click(elementsForm);
        click(webTablesMenu);
    }

    // ============================================================
    // ACTIONS
    // ============================================================

    /**
     * Fills in the Add form and submits a new record to the table.
     * Extracts all field values from the Excel data map.
     *
     * @param row Map containing test data from Excel
     */
    public void registerAddData(Map<String, String> row) {

        // --- EXTRACT DATA FROM EXCEL ---
        String firstNameVal  = row.get("FirstName");
        String lastNameVal   = row.get("LastName");
        String userEmailVal  = row.get("Email");
        String ageVal        = row.get("Age");
        String salaryVal     = row.get("Salary");
        String departmentVal = row.get("Department");

        // --- OPEN ADD FORM ---
        click(addButton);

        // --- FILL FORM FIELDS ---
        sendKeys(firstName,   firstNameVal);
        sendKeys(lastName,    lastNameVal);
        sendKeys(userEmail,   userEmailVal);
        sendKeys(age,         ageVal);
        sendKeys(salary,      salaryVal);
        sendKeys(department,  departmentVal);

        // --- SUBMIT FORM ---
        click(submitBtn);

        ExtentManager.getTest().log(Status.INFO,
                "Added new record: " + firstNameVal + " " + lastNameVal);
    }

    /**
     * Searches for a record, clicks edit, updates fields
     * that have Edit prefix values in Excel, then submits.
     *
     * @param row Map containing test data from Excel
     */
    public void editRecord(Map<String, String> row) {

        // --- SEARCH FOR THE RECORD ---
        sendKeys(searchBox, row.get("FirstName"));

        // --- CLICK FIRST EDIT BUTTON ---
        click(editButtons.get(0));

        // --- UPDATE ONLY FIELDS THAT HAVE EDIT VALUES ---
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

        // --- SUBMIT FORM ---
        click(submitBtn);

        ExtentManager.getTest().log(Status.INFO,
                "Edited record: " + row.get("FirstName"));
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
    // DELETE ACTION
    // ============================================================

    public void deleteUserByEmail(String email) {
        sendKeys(searchBox, email);
        pause(1);
        WebElement deleteIcon = getDeleteIconByEmail(email);
        hoverAndClick(deleteIcon);
        ExtentManager.getTest().log(Status.INFO,
                "Clicked delete for email: " + email);
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    /**
     * Searches the table by first name and returns all row data.
     */
    public Map<String, String> getAddedRowData(String firstNameVal) {
        return getTableRow(searchBox, firstNameVal);
    }

    public Map<String, String> getAddedRowDataEdit(String firstNameVal) {
        return getTableRow(searchBox, firstNameVal);
    }

    /**
     * Searches the table by any keyword and returns matching row data.
     */
    public Map<String, String> getRowByKeyword(String keyword) {
        return getTableRow(searchBox, keyword);
    }
}
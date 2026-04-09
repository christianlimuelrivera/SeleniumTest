package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.PracticeFormPage;
import utils.ExtentManager;
import utils.RetryAnalyzer;
import utils.SheetName;
import java.util.Map;

public class FormTest extends Main {

    private PracticeFormPage practiceForm;
    // ============================================================
    // SETUP: Initialize Page Objects
    // ============================================================
    @BeforeMethod
    public void initPages() {
        practiceForm = new PracticeFormPage(getDriver());
    }

    @SheetName("Forms")
    @Test(dataProvider = "excelDataProviderMapAnnotation", dataProviderClass = utils.DataProviderUtil.class,
            retryAnalyzer = RetryAnalyzer.class)
    public void FormTest(Map<String, String> row) {


        // STEP 2: LOG TEST START
        ExtentManager.getTest().log(Status.INFO,
                "Testing Forms for: " + row.get("FirstName") + " " + row.get("LastName"));

        // STEP 3: NAVIGATE AND FILL THE FORM
        practiceForm.fillAndSubmit(row);

        // STEP 4: SOFT ASSERTIONS
        practiceForm.softAssertVisible(practiceForm.getNameResult(),
                "Result Name: " + row.get("FirstName") + " " + row.get("LastName"));

      // Uncomment if you want to test the Retry
//        Assert.assertEquals(row.get("FirstName"), "Zoro", "INTENTIONAL LOGIC FAILURE FOR TESTING RETRY");
        practiceForm.softAssertVisible(practiceForm.getEmailResult(),
                "Result Email: " + row.get("Email"));
        practiceForm.softAssertVisible(practiceForm.getGenderResult(),
                "Result Gender: " + row.get("Gender"));
        practiceForm.softAssertVisible(practiceForm.getPhoneResult(),
                "Result Phone: " + row.get("UserNumber"));
        practiceForm.softAssertVisible(practiceForm.getDobResult(),
                "Result DOB: " + row.get("DateOfBirth"));
        practiceForm.softAssertVisible(practiceForm.getHobbiesResult(),
                "Result Hobbies: " + row.get("Hobbies"));
        practiceForm.softAssertVisible(practiceForm.getfileResult(),
                "Result FileInput: " + row.get("fileInput"));
        practiceForm.softAssertVisible(practiceForm.getStateResult(),
                "Result State and City: " + row.get("State") + " " + row.get("City"));

        // STEP 5: LOG SUCCESS AND FINALIZE
        ExtentManager.getTest().pass("Form Validation completed for: "
                + row.get("FirstName") + " " + row.get("LastName"));

        practiceForm.assertAll();
    }
    // ============================================================
    // TEST: Practice Form — Hybrid DB + Excel
    //
    // DB  (WebUsers table) → FirstName, LastName (fetched by Email)
    // Excel (Forms sheet)  → Email, Gender, UserNumber, DateOfBirth,
    //                        Hobbies, fileInput, State, City
    //
    // DB fetch and form fill are fully handled inside
    // fillAndSubmitHybrid() — this class stays clean.
    // ============================================================
    @SheetName("Forms")
    @Test(dataProvider = "excelDataProviderMapAnnotation",
            dataProviderClass = utils.DataProviderUtil.class)
    public void FormTestHybrid(Map<String, String> row) {
        // STEP 1: INITIALIZE PAGE OBJECT
        PracticeFormPage practiceForm = new PracticeFormPage(getDriver());
        // ============================================================
        // STEP 1: LOG TEST START
        // ============================================================
        ExtentManager.getTest().log(Status.INFO,
                "Testing Hybrid Form | Email (lookup key): " + row.get("Email"));

        // ============================================================
        // STEP 2: FILL AND SUBMIT
        // DB fetch (FirstName, LastName) happens inside this method.
        // Excel provides Email, Gender, DOB, etc.
        // ============================================================
        practiceForm.fillAndSubmitHybrid(row);

        // ============================================================
        // STEP 3: SOFT ASSERTIONS — UI validation
        // Verifies the confirmation table matches what was submitted.
        // assertAll() at the end collects all failures before reporting.
        // ============================================================
        practiceForm.softAssertVisible(practiceForm.getNameResult(),
                "Result — Student Name");

        practiceForm.softAssertVisible(practiceForm.getEmailResult(),
                "Result — Email: " + row.get("Email"));

        practiceForm.softAssertVisible(practiceForm.getGenderResult(),
                "Result — Gender: " + row.get("Gender"));

        practiceForm.softAssertVisible(practiceForm.getPhoneResult(),
                "Result — Mobile: " + row.get("UserNumber"));

        practiceForm.softAssertVisible(practiceForm.getDobResult(),
                "Result — Date of Birth: " + row.get("DateOfBirth"));

        practiceForm.softAssertVisible(practiceForm.getHobbiesResult(),
                "Result — Hobbies: " + row.get("Hobbies"));

        practiceForm.softAssertVisible(practiceForm.getfileResult(),
                "Result — Picture: " + row.get("fileInput"));

        practiceForm.softAssertVisible(practiceForm.getStateResult(),
                "Result — State and City: " + row.get("State") + " " + row.get("City"));

        // ============================================================
        // STEP 4: FINALIZE SOFT ASSERTIONS + LOG SUCCESS
        // ============================================================
        ExtentManager.getTest().pass(
                "Hybrid Form validation complete for email: " + row.get("Email"));

        practiceForm.assertAll();
    }
}
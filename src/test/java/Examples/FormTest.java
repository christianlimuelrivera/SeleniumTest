package Examples;

import base.Main;
import com.aventstack.extentreports.Status;
import org.testng.annotations.Test;
import pages.PracticeFormPage;
import utils.ExtentManager;
import utils.SheetName;
import java.util.Map;

public class FormTest extends Main {

    @SheetName("Forms")
    @Test(dataProvider = "excelDataProviderMapAnnotation", dataProviderClass = utils.DataProviderUtil.class)
    public void FormTest(Map<String, String> row) {

        // STEP 1: INITIALIZE PAGE OBJECT
        PracticeFormPage practiceForm = new PracticeFormPage(getDriver());

        // STEP 2: LOG TEST START
        ExtentManager.getTest().log(Status.INFO,
                "Testing Forms for: " + row.get("FirstName") + " " + row.get("LastName"));

        // STEP 3: NAVIGATE AND FILL THE FORM
        practiceForm.fillAndSubmit(row);

        // STEP 4: SOFT ASSERTIONS
        practiceForm.softAssertVisible(practiceForm.getNameResult(),
                "Result Name: " + row.get("FirstName") + " " + row.get("LastName"));
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
}
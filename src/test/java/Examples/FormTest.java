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
        // 1. Initialize the Page Object
        PracticeFormPage practiceForm = new PracticeFormPage(getDriver());

        // 2. Extract Data from Excel
        String firstname = row.get("FirstName");
        String lastname = row.get("LastName");
        String email = row.get("Email");
        String gender = row.get("Gender");
        String usernumber = row.get("UserNumber");
        String dateofbirth = row.get("DateOfBirth");
        String filePath = row.get("fileInput");
        String state = row.get("State");
        String city = row.get("City");
        ExtentManager.getTest().log(Status.INFO, "Testing Forms for: " + firstname + " " + lastname);

        // 3. Action: Navigate and Fill the Form
        practiceForm.openForm();
        practiceForm.fillAndSubmit(firstname, lastname, email,gender, usernumber, dateofbirth,filePath,state,city);

        // 4. SOFT ASSERTIONS: Validate every element on the result screen
        // These will only show up in the report/console if they FAIL.
        practiceForm.softAssertVisible(practiceForm.getNameResult(), "Result Name: " + firstname + " " + lastname);
        practiceForm.softAssertVisible(practiceForm.getEmailResult(), "Result Email: " + email);
        practiceForm.softAssertVisible(practiceForm.getGenderResult(), "Result Gender: " + gender);
        practiceForm.softAssertVisible(practiceForm.getPhoneResult(), "Result Phone: " + usernumber);
        practiceForm.softAssertVisible(practiceForm.getDobResult(), "Result DOB: " + dateofbirth);
        practiceForm.softAssertVisible(practiceForm.getfileResult(), "Result FileInput: " + filePath);
        practiceForm.softAssertVisible(practiceForm.getStateResult(), "Result State: " + state);
        practiceForm.softAssertVisible(practiceForm.getCityResult(), "Result City: " + city);

        // 5. Logging Success to Extent Report
        ExtentManager.getTest().pass("Form Validation completed for row: " );

        practiceForm.assertAll();

    }
    }
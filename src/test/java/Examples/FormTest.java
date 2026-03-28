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
    public void loginTestSheet1(Map<String, String> row) {
        // 1. Initialize the Page Object
        PracticeFormPage practiceForm = new PracticeFormPage(getDriver());

        // 2. Extract Data from Excel
        String firstname = row.get("FirstName");
        String lastname = row.get("LastName");
        String email = row.get("Email");
        String gender = row.get("Gender");
        String usernumber = row.get("UserNumber");
        String dateofbirth = row.get("DateOfBirth");

        ExtentManager.getTest().log(Status.INFO, "Testing Forms for: " + firstname + " " + lastname);

        // 3. Action: Navigate and Fill the Form
        practiceForm.openForm();
        practiceForm.fillAndSubmit(firstname, lastname, email, usernumber, dateofbirth, gender);

        // 4. SOFT ASSERTIONS: Validate every element on the result screen
        // These will only show up in the report/console if they FAIL.
        practiceForm.softAssertVisible(practiceForm.getNameResult(), "Result Name: " + firstname + " " + lastname);
        practiceForm.softAssertVisible(practiceForm.getEmailResult(), "Result Email: " + email);
        practiceForm.softAssertVisible(practiceForm.getGenderResult(), "Result Gender: " + gender);
        practiceForm.softAssertVisible(practiceForm.getPhoneResult(), "Result Phone: " + usernumber);
        practiceForm.softAssertVisible(practiceForm.getDobResult(), "Result Date of Birth: " + dateofbirth);

        // 5. Logging Success to Extent Report
        ExtentManager.getTest().pass("Form Validation completed for row: " );

    }
    }
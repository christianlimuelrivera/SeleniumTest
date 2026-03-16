package Examples;

import base.Main;
import pages.LoginPage;
import org.testng.annotations.Test;
import org.testng.Assert;
import utils.SheetName;
import utils.ExtentManager;
import com.aventstack.extentreports.Status;
import java.util.Map;

public class LoginTest extends Main {

    @SheetName("Sheet1")
    @Test(dataProvider = "excelDataProviderMapAnnotation", dataProviderClass = utils.DataProviderUtil.class)
    public void loginTestSheet1(Map<String, String> row) {
        LoginPage login = new LoginPage(getDriver());

        String username = row.get("UserName");
        String password = row.get("Password");

        // CHANGE: Call ExtentManager.getTest() instead of using 'test'
        ExtentManager.getTest().log(Status.INFO, "Testing with user: " + username + " " + password);

        login.login(username, password);

        String expectedUrl = "logged-in-successfully";
        Assert.assertTrue(getDriver().getCurrentUrl().contains(expectedUrl),
                "Login failed for: " + username + " " + password);

        ExtentManager.getTest().pass("Login successful for: " + username);
    }

    @SheetName("Sheet2")
    @Test(dataProvider = "excelDataProviderMapAnnotation", dataProviderClass = utils.DataProviderUtil.class)
    public void loginTestSheet2(Map<String, String> row) {
        LoginPage login = new LoginPage(getDriver());

        String username = row.get("UserName");
        String password = row.get("Password");

        // CHANGE: Call ExtentManager.getTest() instead of using 'test'
        ExtentManager.getTest().log(Status.INFO, "Testing Sheet2 with user: " + username);

        login.login(username, password);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("logged-in-successfully"),
                "Login failed for Sheet2 user: " + username);

        ExtentManager.getTest().pass("Login successful for Sheet2 user.");
    }
}
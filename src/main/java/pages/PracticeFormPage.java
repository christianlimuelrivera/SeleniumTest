package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

public class PracticeFormPage extends BasePage {

    public PracticeFormPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // --- INPUT LOCATORS ---
    @FindBy(xpath = "//*[@id='root']//div[contains(text(),'Forms')]")
    WebElement formsbtn;

    @FindBy(xpath = "//span[text()='Practice Form']")
    WebElement pracformsbtn;

    @FindBy(id = "firstName")
    WebElement fname;

    @FindBy(id = "lastName")
    WebElement lname;

    @FindBy(id = "userEmail")
    WebElement email;

    @FindBy(name = "gender")
    private List<WebElement> genderOptions;

    @FindBy(id = "userNumber")
    WebElement usernumber;

    @FindBy(xpath = "//*[@id=\"dateOfBirthInput\"]")
    WebElement dateofbirth;

    @FindBy(id = "submit")
    WebElement submitBtn;

    // --- RESULT TABLE LOCATORS (For Assertions) ---
    // Logic: Find the row with 'Student Name', then get the cell next to it
    @FindBy(xpath = "//td[text()='Student Name']/following-sibling::td")
    private WebElement nameResult;

    @FindBy(xpath = "//td[text()='Student Email']/following-sibling::td")
    private WebElement emailResult;

    @FindBy(xpath = "//td[text()='Gender']/following-sibling::td")
    private WebElement genderResult;

    @FindBy(xpath = "//td[text()='Mobile']/following-sibling::td")
    private WebElement phoneResult;

    @FindBy(xpath = "//td[text()='Date of Birth']/following-sibling::td")
    private WebElement dobResult;

    // --- GETTERS (To bridge to FormTest) ---
    public WebElement getNameResult() { return nameResult; }
    public WebElement getEmailResult() { return emailResult; }
    public WebElement getGenderResult() { return genderResult; }
    public WebElement getPhoneResult() { return phoneResult; }
    public WebElement getDobResult() { return dobResult; }

    // --- ACTIONS ---
    public void openForm() {
        click(formsbtn);
        click(pracformsbtn);
    }

    public void fillAndSubmit(String Fname, String Lname, String Useremail,String genderData, String Usernumber, String Dob) {
        sendKeys(fname, Fname);
        sendKeys(lname, Lname);
        sendKeys(email, Useremail);

        // Use our smart radio button selection
        selectRadioButtonByValue(genderOptions, genderData);

        sendKeys(usernumber, Usernumber);
        typeDate(dateofbirth, Dob); // Uncomment when ready to test dates
        // 🚀 CRITICAL: We must submit for the result table to appear!
        click(submitBtn);
    }
}
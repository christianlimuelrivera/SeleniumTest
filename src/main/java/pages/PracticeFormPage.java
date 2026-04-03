package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;
import java.util.Map;

public class PracticeFormPage extends BasePage {

    public PracticeFormPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }


    // --- INPUT LOCATORS ---
    @FindBy(xpath = "//a[@href='/forms']")
    WebElement formsbtn;
    //*[@id="item-0"]/a
    @FindBy(xpath = "//a[@href='/automation-practice-form']")
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

    @FindBy(id = "uploadPicture")  // your file input element
    WebElement fileInput;

    @FindBy(id = "state")
    WebElement stateDropdown;

    @FindBy(id = "city")
    WebElement cityDropdown;

    @FindBy(xpath = "//label[contains(@for,'hobbies-checkbox')]")
    private List<WebElement> hobbiesCheckboxes;

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

    @FindBy(xpath = "//td[text()='Picture']/following-sibling::td")
    private WebElement fileResult;

    @FindBy(xpath = "//td[text()='Hobbies']/following-sibling::td")
    private WebElement hobbiesResult;

    @FindBy(xpath = "//td[text()='State and City']/following-sibling::td")
    private WebElement stateResult;

    @FindBy(xpath = "//td[text()='State and City']/following-sibling::td")
    private WebElement cityResult;

    // --- GETTERS (To bridge to FormTest) ---
    public WebElement getNameResult() { return nameResult; }
    public WebElement getEmailResult() { return emailResult; }
    public WebElement getGenderResult() { return genderResult; }
    public WebElement getPhoneResult() { return phoneResult; }
    public WebElement getDobResult() { return dobResult; }
    public WebElement getHobbiesResult() { return hobbiesResult; }
    public WebElement getfileResult() { return fileResult; }
    public WebElement getStateResult() { return stateResult; }
    public WebElement getCityResult() { return cityResult; }
    // --- ACTIONS ---
    public void fillAndSubmit(Map<String, String> row) {

        // --- EXTRACT DATA FROM MAP ---
        String firstname   = row.get("FirstName");
        String lastname    = row.get("LastName");
        String userEmail   = row.get("Email");
        String gender      = row.get("Gender");
        String userNumber  = row.get("UserNumber");
        String dob         = row.get("DateOfBirth");
        String hobbies     = row.get("Hobbies");
        String filePath    = row.get("fileInput");
        String state       = row.get("State");
        String city        = row.get("City");

        // --- NAVIGATE TO FORM ---
        click(formsbtn);
        click(pracformsbtn);

        // --- FILL FORM FIELDS ---
        sendKeys(fname, firstname);
        sendKeys(lname, lastname);
        sendKeys(email, userEmail);
        selectRadioButtonByValue(genderOptions, gender);
        sendKeys(usernumber, userNumber);
        typeDate(dateofbirth, dob);
        selectCheckboxes(hobbiesCheckboxes, hobbies);
        uploadFile(fileInput, filePath);

        // --- DROPDOWNS ---
        // State must be selected before City becomes enabled
        selectReactDropdown(stateDropdown, state);
        selectReactDropdown(cityDropdown, city);

        // --- SUBMIT ---
        click(submitBtn);
    }
}
package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.Map;

public class AlertsFramesModals extends BasePage {

    public AlertsFramesModals(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ============================================================
    // LOCATORS
    // ============================================================

    @FindBy(xpath = "//a[@href='/alertsWindows']")
    private WebElement Alertsform;

    @FindBy(xpath = "//span[text()='Browser Windows']")
    private WebElement Browserwindows;

    @FindBy(id = "tabButton")
    private WebElement NewTab;

    @FindBy(id = "windowButton")
    private WebElement NewWindow;

    @FindBy(id = "messageWindowButton")
    private WebElement NewWindowMessage;

    @FindBy(id = "sampleHeading")
    private WebElement sampleHeading;

    @FindBy(xpath = "//a[@href='/alerts']")
    private WebElement AlertsMenu;

    @FindBy(id = "alertButton")
    private WebElement Alertsbtn;

    @FindBy(id = "timerAlertButton")
    private WebElement AlertsTimerbtn;

    @FindBy(id = "confirmButton")
    private WebElement AlertsConfirmbtn;

    @FindBy(id = "promtButton")
    private WebElement AlertsPromptbtn;


    // ============================================================
    // CLASS VARIABLE
    // Stores the original tab handle for switching back after
    // opening a new tab or window
    // ============================================================
    private String originalTab;

    // ============================================================
    // GETTERS
    // ============================================================
    public WebElement getSampleHeading() { return sampleHeading; }

    // ============================================================
    // ACTIONS
    // ============================================================

    /**
     * Opens a new tab, retrieves the heading text,
     * then closes the tab and returns to the original.
     */
    public String NewTabs(Map<String, String> row){
        // Navigate to Browser Windows section
        click(Alertsform);
        click(Browserwindows);

        // Save original tab before opening new one
        originalTab = getCurrentWindowHandle();
        click(NewTab);

        // Switch to new tab and get text
        switchToNewTab();
        String headingText = getText(sampleHeading);

        // Close new tab and return to original
        closeCurrentAndSwitchBack(originalTab);

        return headingText;
    }

    /**
     * Opens a new window, retrieves the heading text,
     * then closes the window and returns to the original.
     */
    public String NewWindow(Map<String, String> row){
        // Save original window before opening new one
        originalTab = getCurrentWindowHandle();
        click(NewWindow);

        // Switch to new window and get text
        switchToNewTab();
        String headingText = getText(sampleHeading);

        // Close new window and return to original
        closeCurrentAndSwitchBack(originalTab);

        return headingText;
    }

    /**
     * Opens a new message window then closes it.
     * Message window contains raw body text with no HTML structure.
     */
    public void NewWindowMessage(Map<String, String> row){
        // Save original window before opening new one
        originalTab = getCurrentWindowHandle();
        click(NewWindowMessage);

        // Switch to new window
        switchToNewTab();

        // Close new window and return to original
        closeCurrentAndSwitchBack(originalTab);
    }
    public String clickAlertAndAccept() {
        click(Alertsform);
        click(AlertsMenu);
        click(Alertsbtn);
        String alertText = getAlertText();
        acceptAlert();
        return alertText;
    }

    public String clickTimerAlert() {
        click(AlertsTimerbtn);
        // acceptAlert() already waits up to 20 seconds — handles delayed alerts too!
        String alertText = getAlertText();
        acceptAlert();
        return alertText;
    }

    public String clickConfirmAlert() {
        click(AlertsConfirmbtn);
        // acceptAlert() already waits up to 20 seconds — handles delayed alerts too!
        String alertText = getAlertText();
        dismissAlert();
        return alertText;
    }

    public void clickPromptAlert(String text) {
        click(AlertsPromptbtn);
        // acceptAlert() already waits up to 20 seconds — handles delayed alerts too!
        typeInAlert(text);


    }

}
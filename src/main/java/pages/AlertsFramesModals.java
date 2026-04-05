package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.ExtentManager;

import java.util.HashMap;
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

    @FindBy(xpath = "//a[@href='/frames']")
    private WebElement FrameMenu;

    @FindBy(id = "frame1")
    private WebElement frame1;

    @FindBy(id = "frame2")
    private WebElement frame2;

    // Same heading locator works for both frames!
    @FindBy(id = "sampleHeading")
    private WebElement frameHeading;

    @FindBy(xpath = "//a[@href='/modal-dialogs']")
    private WebElement ModalMenu;

    @FindBy(id = "showSmallModal")
    private WebElement smallModal;

    @FindBy(xpath = "//div[@class='modal-body']")
    private WebElement smallModalBody;

    @FindBy(id = "closeSmallModal")
    private WebElement smallModalclose;

    @FindBy(id = "showLargeModal")
    private WebElement largeModal;

    @FindBy(xpath = "//div[contains(@class,'show')]//div[@class='modal-body']")
    private WebElement largeModalBody;

    @FindBy(id = "closeLargeModal")
    private WebElement largeModalClose;

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
    // ============================================================
// ALERT ACTIONS
// ============================================================

    /**
     * Navigates to Alerts section, clicks the simple alert button,
     * retrieves the alert text and accepts it.
     */
    public String clickAlertAndAccept() {
        // Navigate to Alerts section
        click(Alertsform);
        click(AlertsMenu);

        // Click alert button, get text and accept
        click(Alertsbtn);
        String alertText = getAlertText();
        acceptAlert();

        return alertText;
    }

    /**
     * Clicks the timer alert button and waits for the delayed alert.
     * Uses existing wait timeout — no extra wait needed.
     */
    public String clickTimerAlert() {
        click(AlertsTimerbtn);
        String alertText = getAlertText();
        acceptAlert();

        return alertText;
    }

    /**
     * Clicks the confirm alert button, retrieves text
     * and dismisses it by clicking Cancel.
     */
    public String clickConfirmAlert() {
        click(AlertsConfirmbtn);
        String alertText = getAlertText();
        dismissAlert();

        return alertText;
    }

    /**
     * Clicks the prompt alert button and types
     * the provided text before accepting.
     */
    public void clickPromptAlert(String text) {
        click(AlertsPromptbtn);
        typeInAlert(text);
    }

    /**
     * Switches into Frame 1, retrieves heading text,
     * then switches back to main page.
     */
    public String getFrame1Text() {
        switchToFrameById("frame1");
        String text = getText(frameHeading);
        switchToDefaultContent();
        return text;
    }

    /**
     * Switches into Frame 2, retrieves heading text,
     * then switches back to main page.
     */
    public String getFrame2Text() {
        switchToFrameById("frame2");
        String text = getText(frameHeading);
        switchToDefaultContent();
        return text;
    }

    // Navigate once
    public void navigateToFrames() {
        click(Alertsform);
        click(FrameMenu);
    }

    // Reusable for both frames — no navigation inside
    public String getFrameText(String frameId) {
        switchToFrameById(frameId);
        String text = getText(frameHeading);
        switchToDefaultContent();
        return text;
    }

    public void navigateToModal() {
        click(Alertsform);
        click(ModalMenu);
    }
    /**
     * Handles both Small and Large modal interactions.
     * Opens each modal, retrieves the body text, then closes it.
     * Returns a Map containing text from both modals.
     *
     * @return Map with keys "smallBody" and "largeBody"
     */
    public Map<String, String> handleModals() {
        Map<String, String> modalData = new HashMap<>();

        // ============================================================
        // SMALL MODAL
        // Opens the small modal, captures body text, then closes it
        // ============================================================
        click(smallModal);
        String smallText = getText(smallModalBody);
        modalData.put("smallBody", smallText);
        click(smallModalclose);
        ExtentManager.getTest().log(Status.INFO, "Small modal text: " + smallText);

        // ============================================================
        // LARGE MODAL
        // Opens the large modal, captures body text, then closes it
        // Large modal contains Lorem Ipsum — text not logged to avoid flooding report
        // ============================================================
        click(largeModal);
        String largeText = getText(largeModalBody);
        modalData.put("largeBody", largeText);
        click(largeModalClose);
        ExtentManager.getTest().log(Status.INFO, "Large modal text retrieved");

        return modalData;
    }

    }





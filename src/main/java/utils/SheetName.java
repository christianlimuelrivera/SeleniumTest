package utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 🏷️ @SheetName - The Custom Label Maker
 * This allows us to tell each test which Excel sheet to use.
 * Usage: @SheetName("LoginData")
 */

// 🕒 RETENTION POLICY: This tells Java "Keep this sticky note alive while the code is running."
// Without this, the DataProvider wouldn't be able to see the label during the test.
@Retention(RetentionPolicy.RUNTIME)

public @interface SheetName {

    /**
     * 📝 VALUE: This is where you store the actual name of the sheet.
     * When you write @SheetName("Sheet2"), "Sheet2" is stored in this value() variable.
     */
    String value();
}
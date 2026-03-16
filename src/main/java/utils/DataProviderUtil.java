package utils;

import org.testng.annotations.DataProvider;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 🔥 DATAPROVIDERUTIL - The Brain of Your Data-Driven Tests
 *
 * WHAT IT DOES:
 * 1. Reads the @SheetName annotation on your @Test method (e.g. Sheet1 or Sheet2)
 * 2. Loads the correct Excel sheet from src/resources/testdata/TestData.xlsx
 * 3. Converts every row into a Map<String, String> (column header = key)
 * 4. Skips any row where a column starting with "*" is empty (required field check)
 * 5. Returns data to your test methods
 */
public class DataProviderUtil {

    /**
     * 🔥 MAIN DATAPROVIDER
     * This method is automatically called by TestNG for every @Test that uses:
     * dataProvider = "excelDataProviderMapAnnotation"
     *
     * @param method  TestNG passes the current test method (so we can read @SheetName)
     * @return Object[][] containing Map<String, String> for each valid row
     */
    @DataProvider(name = "excelDataProviderMapAnnotation")
    public static Object[][] excelDataProviderMapAnnotation(Method method) {

        // 1️⃣ Get sheet name from @SheetName annotation (or default to Sheet1)
        SheetName sheetAnnotation = method.getAnnotation(SheetName.class);
        String sheetName = (sheetAnnotation != null)
                ? sheetAnnotation.value()
                : "Sheet1";

        // === DEBUG LOG - You will see this in console for every test ===
        System.out.println("\n=== DataProvider STARTED → Method: " + method.getName()
                + " | Sheet Requested: " + sheetName + " ===");

        // 2️⃣ Correct classpath path (matches your current folder structure)
        String filePath = "testdata/TestData.xlsx";   // ← lowercase "testdata" as in your screenshot

        // Create ExcelUtil (it will open the file safely)
        ExcelUtil excel = new ExcelUtil(filePath, sheetName);

        try {
            // Get total rows and columns from Excel
            int totalRows = excel.getRowCount();
            int totalCols = excel.getColumnCount();

            System.out.println("   → Total rows in sheet: " + totalRows);
            System.out.println("   → Total columns: " + totalCols);

            // Temporary storage for valid rows
            Object[][] tempData = new Object[totalRows - 1][1];
            int index = 0;   // counts how many valid rows we actually keep

            // 3️⃣ Read all headers from first row (Row 0)
            String[] headers = new String[totalCols];
            boolean[] requiredCols = new boolean[totalCols];

            for (int j = 0; j < totalCols; j++) {
                String header = excel.getCellData(0, j);
                header = (header != null) ? header.trim() : "";

                requiredCols[j] = header.startsWith("*");           // * = required field
                headers[j] = header.replace("*", "").trim();        // clean header name

                System.out.println("   Header[" + j + "]: '" + headers[j]
                        + "'" + (requiredCols[j] ? " (*REQUIRED)" : ""));
            }

            // 4️⃣ Process every data row (starting from row 1)
            for (int i = 1; i < totalRows; i++) {
                Map<String, String> rowMap = new HashMap<>();
                boolean skipRow = false;

                for (int j = 0; j < totalCols; j++) {
                    String value = excel.getCellData(i, j);
                    value = (value == null) ? "" : value.trim();

                    rowMap.put(headers[j], value);   // Put data into Map (key = header)

                    // If this is a required column (*) and it's empty → skip entire row
                    if (requiredCols[j] && value.isEmpty()) {
                        skipRow = true;
                    }
                }

                // Only keep rows that have all required fields filled
                if (!skipRow) {
                    tempData[index][0] = rowMap;
                    index++;
                }
            }

            // 5️⃣ Final result + debug log
            System.out.println("=== DataProvider FINISHED for '" + sheetName
                    + "' → Loaded " + index + " valid row(s) ===\n");

            // Return only the valid rows (no empty slots)
            Object[][] finalData = new Object[index][1];
            System.arraycopy(tempData, 0, finalData, 0, index);

            return finalData;

        } finally {
            // 🔥 CRITICAL: Always close Excel to free memory
            // This is what makes Sheet2 work after Sheet1!
            excel.close();
        }
    }
}
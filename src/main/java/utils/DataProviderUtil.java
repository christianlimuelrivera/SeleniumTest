package utils;

import org.testng.annotations.DataProvider;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 📦 DATAPROVIDERUTIL - The Data Delivery Service
 * Its job is to read Excel rows and turn them into "Test Data" for Selenium.
 */
public class DataProviderUtil {

    // 🚀 'parallel = true' allows TestNG to run different rows of data at the same time!
    @DataProvider(name = "excelDataProviderMapAnnotation", parallel = true)
    public static Object[][] excelDataProviderMapAnnotation(Method method) {

        // 1️⃣ FIND THE TARGET SHEET
        // It looks at your @Test method to see if you wrote @SheetName("LoginData").
        // If you didn't, it defaults to "Sheet1".
        SheetName sheetAnnotation = method.getAnnotation(SheetName.class);
        String sheetName = (sheetAnnotation != null) ? sheetAnnotation.value() : "Sheet1";

        System.out.println("\n=== DataProvider STARTED → Method: " + method.getName()
                + " | Sheet Requested: " + sheetName + " ===");

        // 2️⃣ GET THE FILE LOCATION
        // Instead of hardcoding "C:/Users/...", it asks the ConfigReader where the Excel is.
        String filePath = ConfigReader.getProperty("excelPath");

        // 3️⃣ OPEN EXCEL
        ExcelUtil excel = new ExcelUtil(filePath, sheetName);

        try {
            int totalRows = excel.getRowCount();
            int totalCols = excel.getColumnCount();

            // Object[][] is the specific container TestNG requires for DataProviders.
            Object[][] tempData = new Object[totalRows - 1][1];
            int index = 0;

            // 4️⃣ READ THE HEADERS (Row 0)
            // It identifies which columns are required (marked with *) and cleans the names.
            String[] headers = new String[totalCols];
            boolean[] requiredCols = new boolean[totalCols];

            for (int j = 0; j < totalCols; j++) {
                String header = excel.getCellData(0, j);
                header = (header != null) ? header.trim() : "";

                requiredCols[j] = header.startsWith("*"); // If it starts with *, it's mandatory
                headers[j] = header.replace("*", "").trim(); // Clean the name for the Map key
            }

            // 5️⃣ CONVERT ROWS TO MAPS
            // This loops through every row and creates a "Dictionary" (Map).
            // Example: { "Username": "student", "Password": "Password123" }
            for (int i = 1; i < totalRows; i++) {
                Map<String, String> rowMap = new HashMap<>();
                boolean skipRow = false;

                for (int j = 0; j < totalCols; j++) {
                    String value = excel.getCellData(i, j);
                    value = (value == null) ? "" : value.trim();

                    rowMap.put(headers[j], value);

                    // 🛑 VALIDATION: If a required (*) cell is empty, we throw that row away.
                    if (requiredCols[j] && value.isEmpty()) {
                        skipRow = true;
                    }
                }

                // If the row is valid, add it to our "Truck" (tempData) to be delivered to the test.
                if (!skipRow) {
                    tempData[index][0] = rowMap;
                    index++;
                }
            }

            System.out.println("=== DataProvider FINISHED for '" + sheetName
                    + "' → Loaded " + index + " valid row(s) ===\n");

            // 6️⃣ CLEAN UP THE PACKAGE
            // We resize the array to remove any empty slots from skipped rows.
            Object[][] finalData = new Object[index][1];
            System.arraycopy(tempData, 0, finalData, 0, index);

            return finalData;

        } finally {
            // 🔐 ALWAYS CLOSE: Even if the code crashes, we close the Excel file.
            excel.close();
        }
    }
}
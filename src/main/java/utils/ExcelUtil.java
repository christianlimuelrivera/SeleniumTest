package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.io.InputStream;

/**
 *  EXCELUTIL - The Heart of Your Data-Driven Framework
 *
 * WHAT IT DOES:
 * 1. Loads TestData.xlsx from src/resources/testdata/ (Maven classpath)
 * 2. Opens the exact sheet requested by @SheetName annotation
 * 3. Reads ANY cell safely and ALWAYS returns a clean String (never null)
 * 4. Handles every possible cell type: Text, Number, Boolean, Formula, Blank
 * 5. Properly closes the Workbook (prevents memory leak when running Sheet1 + Sheet2)
 */
public class ExcelUtil {

    // Stores the entire Excel workbook (needed for proper close)
    private Workbook workbook;

    // The specific sheet we are reading (Sheet1 or Sheet2)
    private Sheet sheet;

    /**
     * Constructor - Called by DataProvider for each test method
     *
     * @param classpathFilePath  Example: "testdata/TestData.xlsx"
     * @param sheetName          Example: "Sheet1" or "Sheet2" (from @SheetName)
     */
    public ExcelUtil(String classpathFilePath, String sheetName) {
        try (InputStream fis = ExcelUtil.class.getClassLoader().getResourceAsStream(classpathFilePath)) {

            // Safety check: File must exist in src/resources/testdata/
            if (fis == null) {
                throw new RuntimeException(" Excel file NOT found in classpath: " + classpathFilePath +
                        "\n→ Please confirm folder is exactly: src/resources/testdata/TestData.xlsx");
            }

            // Open the .xlsx file using Apache POI
            this.workbook = new XSSFWorkbook(fis);

            // Get the requested sheet
            this.sheet = workbook.getSheet(sheetName);

            if (this.sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in the Excel file!");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to open Excel file: " + classpathFilePath, e);
        }
    }

    /**
     * Returns total number of rows in the sheet (including header row)
     * Example: Sheet has 10 rows → returns 10
     */
    public int getRowCount() {
        return sheet.getLastRowNum() + 1;
    }

    /**
     * Returns number of columns based on the first row (header row)
     */
    public int getColumnCount() {
        Row firstRow = sheet.getRow(0);
        return firstRow != null ? firstRow.getLastCellNum() : 0;
    }

    /**
     * 🔥 MOST IMPORTANT METHOD
     * Reads any cell (rowNum, colNum) and returns a clean String.
     *
     * Handles ALL cell types safely:
     * - Text          → trimmed string
     * - Number        → removes .0 if it's a whole number (123.0 → "123")
     * - Boolean       → "true" or "false"
     * - Formula       → returns the formula as text
     * - Blank/Empty   → empty string ""
     *
     * This is why your Map in DataProvider never gets null values.
     */
    public String getCellData(int rowNum, int colNum) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) return "";               // entire row is blank

            Cell cell = row.getCell(colNum);
            if (cell == null) return "";              // cell is empty

            // Handle different cell types
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();

                case NUMERIC:
                    double numeric = cell.getNumericCellValue();
                    // Remove unnecessary .0 for integer numbers
                    return (numeric == (long) numeric)
                            ? String.valueOf((long) numeric)
                            : String.valueOf(numeric);

                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case FORMULA:
                    return cell.getCellFormula().trim();

                case BLANK:
                default:
                    return "";
            }
        } catch (Exception e) {
            // Never crash the test - just return empty
            return "";
        }
    }

    /**
     * MUST CALL THIS AFTER USE
     * Closes the Workbook to free memory.
     * Called automatically in DataProviderUtil (in finally block)
     *
     * This is what fixes the "only 1 test runs" issue!
     */
    public void close() {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException ignored) {
                // Safe to ignore - we don't want close() to break tests
            }
            workbook = null;
            sheet = null;
        }
    }
}
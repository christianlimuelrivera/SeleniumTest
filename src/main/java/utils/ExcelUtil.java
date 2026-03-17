package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 🛠️ EXCELUTIL - The File Reader
 * This class uses the Apache POI library to interact with Microsoft Excel files (.xlsx).
 */
public class ExcelUtil {

    // 📗 The Workbook represents the entire Excel file.
    private Workbook workbook;

    // 📄 The Sheet represents the specific tab (e.g., "Sheet1").
    private Sheet sheet;

    /**
     * 🏗️ CONSTRUCTOR: Opens the connection to the file.
     * @param filePath The path from config.properties.
     * @param sheetName The name of the tab we want to read.
     */
    public ExcelUtil(String filePath, String sheetName) {

        // 1️⃣ SAFETY CHECK: Make sure the path string isn't empty before trying to open it.
        if (filePath == null) {
            throw new RuntimeException("Excel path is NULL! Check your config.properties key name.");
        }

        try {
            // 2️⃣ FILE STREAM: Creates a "pipe" to the actual file on your hard drive.
            FileInputStream fis = new FileInputStream(filePath);

            // 3️⃣ OPEN WORKBOOK: Translates the file bytes into an Excel object.
            this.workbook = new XSSFWorkbook(fis);

            // 4️⃣ TARGET SHEET: Points the code to the specific tab you need.
            this.sheet = workbook.getSheet(sheetName);

            // 🛑 ERROR HANDLING: If you typed "Sheet1" but the tab is named "Data", it stops here.
            if (this.sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not find file at: " + filePath + ". Ensure path is correct in config.properties");
        }
    }

    /**
     * 📊 GET ROW COUNT: Tells the loop how many times to run.
     * sheet.getLastRowNum() returns the index (starts at 0), so we add 1 for the total.
     */
    public int getRowCount() {
        return sheet.getLastRowNum() + 1;
    }

    /**
     * 📐 GET COLUMN COUNT: Based on the header row (Row 0).
     */
    public int getColumnCount() {
        Row r = sheet.getRow(0);
        return r != null ? r.getLastCellNum() : 0;
    }

    /**
     * 🔍 GET CELL DATA: The most important method here.
     * It looks at a specific row (r) and column (c).
     */
    public String getCellData(int r, int c) {
        try {
            // 1. Find the cell
            Cell cell = sheet.getRow(r).getCell(c);

            // 2. DATA FORMATTER: This is a magic tool from Apache POI.
            // It automatically converts numbers, dates, and formulas into exactly
            // what you see on the screen in Excel as a String.
            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell).trim();
        } catch (Exception e) {
            // If the cell is completely empty, return an empty string instead of crashing.
            return "";
        }
    }

    /**
     * 🔒 CLOSE: Very important for Parallel Testing.
     * It releases the file so that other tests can access it without "File in Use" errors.
     */
    public void close() {
        try {
            if(workbook != null) workbook.close();
        } catch (IOException e) {
            // We ignore errors here because the test is already finished.
        }
    }
}
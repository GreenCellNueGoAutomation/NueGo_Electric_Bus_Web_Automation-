package nuego_automation_project;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtils {

    private Workbook workbook;
    private Sheet sheet;

    public ExcelUtils(String filePath, String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("Sheet '" + sheetName + "' not found in file: " + filePath);
        }
    }

    public int getRowCount() {
        return sheet.getPhysicalNumberOfRows();
    }

    public int getColCount() {
        return sheet.getRow(0).getPhysicalNumberOfCells();
    }

    public String getCellData(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) return "";
        Cell cell = row.getCell(colNum);
        if (cell == null) return "";

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    // 🔹 Method used by your @DataProvider
    public Object[][] getLoginDataWithRunFlag() {
        int rows = getRowCount();
        int cols = getColCount();

        int mobileCol = -1;
        int otpCol = -1;
        int runFlagCol = -1;

        // find column indexes from header row (row 0)
        for (int c = 0; c < cols; c++) {
            String header = getCellData(0, c);
            if (header.equalsIgnoreCase("MobileNumber")) {
                mobileCol = c;
            } else if (header.equalsIgnoreCase("OTP")) {
                otpCol = c;
            } else if (header.equalsIgnoreCase("RunFlag")) {
                runFlagCol = c;
            }
        }

        if (mobileCol == -1 || otpCol == -1 || runFlagCol == -1) {
            throw new RuntimeException("MobileNumber / OTP / RunFlag columns not found in LoginData sheet");
        }

        // count how many rows have RunFlag = Y
        int validRows = 0;
        for (int r = 1; r < rows; r++) {
            String flag = getCellData(r, runFlagCol);
            if ("Y".equalsIgnoreCase(flag)) {
                validRows++;
            }
        }

        Object[][] data = new Object[validRows][2];
        int index = 0;

        for (int r = 1; r < rows; r++) {
            String flag = getCellData(r, runFlagCol);
            if ("Y".equalsIgnoreCase(flag)) {
                String mobile = getCellData(r, mobileCol);
                String otp = getCellData(r, otpCol);

                data[index][0] = mobile;
                data[index][1] = otp;
                index++;
            }
        }

        return data;
    }

    public void close() throws IOException {
        if (workbook != null) {
            workbook.close();
        }
    }
}

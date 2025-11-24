package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;


public class ExcelUtils {
	
	
	private String filePath;

    public ExcelUtils(String filePath) {
        this.filePath = filePath;
    }

    public String getCellData(String sheetName, int rowNum, int colNum) {
        String cellData = null;
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(colNum);

            if (cell == null) {
                return "";
            }

            DataFormatter formatter = new DataFormatter();
            cellData = formatter.formatCellValue(cell);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cellData;
    }


}

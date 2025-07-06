package NextGenExcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelPractice {

    public static Map<String, String> getRowData(String filePath, int rowNumber) throws IOException {
        Map<String, String> rowData = new HashMap<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row is missing in the sheet.");
            }
            Row dataRow = sheet.getRow(rowNumber);
            if (dataRow == null) {
                throw new IllegalArgumentException("Row number " + rowNumber + " is missing in the sheet.");
            }
            for (Cell headerCell : headerRow) {
                int columnIndex = headerCell.getColumnIndex();
                String header = headerCell.getStringCellValue();
                Cell dataCell = dataRow.getCell(columnIndex);
                String value = getCellValueAsString(dataCell);
                rowData.put(header, value);
            }
        }
        return rowData;
    }

    public static void updateCellData(String filePath, int rowNumber, String columnHeader, String newValue) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row is missing in the sheet.");
            }
            int columnIndex = -1;
            for (Cell headerCell : headerRow) {
                if (headerCell.getStringCellValue().equalsIgnoreCase(columnHeader)) {
                    columnIndex = headerCell.getColumnIndex();
                    break;
                }
            }
            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column header \"" + columnHeader + "\" not found in the sheet.");
            }
            Row dataRow = sheet.getRow(rowNumber);
            if (dataRow == null) {
                dataRow = sheet.createRow(rowNumber);
            }
            Cell cellToUpdate = dataRow.getCell(columnIndex);
            if (cellToUpdate == null) {
                cellToUpdate = dataRow.createCell(columnIndex);
            }
            cellToUpdate.setCellValue(newValue);
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                workbook.write(fileOutputStream);
            }
        }
    }

    private static String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    static void readAllData() {
        try (FileInputStream fileInputStream = new FileInputStream("src/test/resources/data/secTypesDemo.xlsx")) {
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            System.out.print(cell.getNumericCellValue() + "\t");
                            break;
                        case STRING:
                            System.out.print(cell.getStringCellValue() + "\t");
                            break;
                    }
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath = "src/test/resources/data/secTypesDemo.xlsx";
        int rowNumber = 1;
        try {
            Map<String, String> rowData = getRowData(filePath, rowNumber);
            for (Map.Entry<String, String> entry : rowData.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
            String columnHeader = "ORDERTYPE";
            String newValue = "LIMIT";
            updateCellData(filePath, rowNumber, columnHeader, newValue);
            System.out.println("Cell updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        readAllData();
    }
}

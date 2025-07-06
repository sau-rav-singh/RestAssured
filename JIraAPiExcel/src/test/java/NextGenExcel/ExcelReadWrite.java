package NextGenExcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelReadWrite {
    public static void main(String[] args) {
        String sourceFilePath = "C:\\Users\\Singh\\OneDrive\\Desktop\\From.xlsx";
        String destinationFilePath = "C:\\Users\\Singh\\OneDrive\\Desktop\\To.xlsx";

        try (FileInputStream sourceFis = new FileInputStream(sourceFilePath);
             Workbook sourceWb = new XSSFWorkbook(sourceFis);
             Workbook destinationWb = new XSSFWorkbook()
        ) {
            File destinationFile = new File(destinationFilePath);
            if (destinationFile.exists()) {
                boolean isFileDeleted = destinationFile.delete();
                System.out.println("File Deletion is " + isFileDeleted);
            }
            Sheet sourceSheet = sourceWb.getSheetAt(0);
            Sheet destinationSheet = destinationWb.createSheet();
            for (Row sourceRow : sourceSheet) {
                Row destinationRow = destinationSheet.createRow(destinationSheet.getLastRowNum() + 1);
                for (Cell sourceCell : sourceRow) {
                    Cell destinationCell = destinationRow.createCell(sourceCell.getColumnIndex());
                    copyCellValue(sourceCell, destinationCell);
                }
            }
            try (FileOutputStream destinationFos = new FileOutputStream(destinationFilePath)) {
                destinationWb.write(destinationFos);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: IOException - " + e.getMessage());
        }
    }

    private static void copyCellValue(Cell sourceCell, Cell destinationCell) {
        switch (sourceCell.getCellType()) {
            case STRING:
                destinationCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                destinationCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case BOOLEAN:
                destinationCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                destinationCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case BLANK:
                destinationCell.setCellValue("");
                break;
            case ERROR:
                destinationCell.setCellValue(sourceCell.getErrorCellValue());
                break;
            default:
                System.out.println("Warning: Unknown Cell Type");
        }
    }
}
package br.jus.tjce.releasecontroller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class PrintExcelHeaders {
    public static void main(String[] args) {
        try {
            FileInputStream file = new FileInputStream(new File("xlx/Base_Calculo_CS2 1.xlsx"));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            System.out.println("=== HEADERS START ===");
            for (Cell cell : headerRow) {
                switch (cell.getCellType()) {
                    case STRING:
                        System.out.println(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        System.out.println(cell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        System.out.println(cell.getBooleanCellValue());
                        break;
                    default:
                        System.out.println(cell.toString());
                }
            }
            System.out.println("=== HEADERS END ===");
            workbook.close();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

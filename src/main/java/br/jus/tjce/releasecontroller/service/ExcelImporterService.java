package br.jus.tjce.releasecontroller.service;

import br.jus.tjce.releasecontroller.model.BaseCalculo;
import br.jus.tjce.releasecontroller.repository.BaseCalculoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelImporterService {

    @Autowired
    private BaseCalculoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public void importData(String filePath) {
        if (repository.count() > 0) {
            System.out.println("=== Data already imported. Skipping Excel Importer === ");
            return;
        }

        System.out.println("=== Starting Excel data import from: " + filePath + " ===");
        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                System.out.println("=== Excel file is empty or missing headers. ===");
                return;
            }

            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell != null ? cell.toString().trim() : "Column_" + i);
            }

            List<BaseCalculo> entitiesToSave = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData.put(headers.get(j), cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    rowData.put(headers.get(j), cell.getDateCellValue());
                                } else {
                                    rowData.put(headers.get(j), cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                rowData.put(headers.get(j), cell.getBooleanCellValue());
                                break;
                            default:
                                rowData.put(headers.get(j), cell.toString());
                        }
                    } else {
                        rowData.put(headers.get(j), null);
                    }
                }

                String jsonDados = objectMapper.writeValueAsString(rowData);
                entitiesToSave.add(new BaseCalculo(jsonDados));
            }

            repository.saveAll(entitiesToSave);
            System.out.println("=== Successfully imported " + entitiesToSave.size() + " records. ===");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("=== Failed to import Excel data. ===");
        }
    }
}

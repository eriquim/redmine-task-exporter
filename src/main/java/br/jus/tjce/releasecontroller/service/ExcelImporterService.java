package br.jus.tjce.releasecontroller.service;

import br.jus.tjce.releasecontroller.model.BaseCalculo;
import br.jus.tjce.releasecontroller.repository.BaseCalculoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImporterService {

    @Autowired
    private BaseCalculoRepository repository;

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

                BaseCalculo base = new BaseCalculo();

                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;

                    String header = headers.get(j);
                    String strVal = "";
                    Double numVal = null;
                    
                    if (cell.getCellType() == CellType.STRING) {
                        strVal = cell.getStringCellValue();
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        numVal = cell.getNumericCellValue();
                        strVal = String.valueOf(numVal);
                    } else {
                        strVal = cell.toString();
                    }

                    if (header.equalsIgnoreCase("Grupo de Atividades")) {
                        base.setGrupoAtividades(strVal);
                    } else if (header.equalsIgnoreCase("Atividade")) {
                        base.setAtividade(strVal);
                    } else if (header.equalsIgnoreCase("Unidade de medida")) {
                        base.setUnidadeMedida(strVal);
                    } else if (header.equalsIgnoreCase("Qauntidade base UST") || header.equalsIgnoreCase("Quantidade base UST")) {
                        base.setQuantidadeBaseUst(numVal != null ? numVal : (!strVal.isEmpty() ? Double.valueOf(strVal) : null));
                    } else if (header.equalsIgnoreCase("Produto Final")) {
                        base.setProdutoFinal(strVal);
                    } else if (header.equalsIgnoreCase("Perfil")) {
                        base.setPerfil(strVal);
                    } else if (header.equalsIgnoreCase("Valor")) {
                        base.setValor(numVal != null ? numVal : (!strVal.isEmpty() ? Double.valueOf(strVal) : null));
                    } else if (header.equalsIgnoreCase("Atributo")) {
                        base.setAtributo(strVal);
                    } else if (header.equalsIgnoreCase("Referencia Calculo")) {
                        base.setReferenciaCalculo(strVal);
                    }
                }

                entitiesToSave.add(base);
            }

            repository.saveAll(entitiesToSave);
            System.out.println("=== Successfully imported " + entitiesToSave.size() + " records. ===");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("=== Failed to import Excel data. ===");
        }
    }
}

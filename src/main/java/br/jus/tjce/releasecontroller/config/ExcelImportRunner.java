package br.jus.tjce.releasecontroller.config;

import br.jus.tjce.releasecontroller.service.ExcelImporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExcelImportRunner implements CommandLineRunner {

    @Autowired
    private ExcelImporterService excelImporterService;

    @Override
    public void run(String... args) throws Exception {
        excelImporterService.importData("xlx/Base_Calculo_CS2 1.xlsx");
    }
}

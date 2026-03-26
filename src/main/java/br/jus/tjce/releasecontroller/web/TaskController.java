package br.jus.tjce.releasecontroller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import br.jus.tjce.releasecontroller.redmine.RedmineTaskService;
import br.jus.tjce.releasecontroller.redmine.dom.Project;
import br.jus.tjce.releasecontroller.redmine.dom.Tracker;

@Controller
public class TaskController {

    @Autowired
    private RedmineTaskService taskService;

    @GetMapping("/")
    public String index(Model model) {
        List<Project> projetos = taskService.buscarProjetos();
        List<Tracker> tipos = taskService.buscarTipos();
        
        model.addAttribute("projetos", projetos);
        model.addAttribute("tipos", tipos);
        return "index";
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportTasks(
            @RequestParam(required = false) Integer projetoId,
            @RequestParam(required = false) Integer tipoId,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {

        if (dataInicio != null && dataInicio.trim().isEmpty()) dataInicio = null;
        if (dataFim != null && dataFim.trim().isEmpty()) dataFim = null;

        byte[] csvData = taskService.exportarTarefasParaCSVBytes(projetoId, tipoId, dataInicio, dataFim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tarefas_exportadas.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}

package br.jus.tjce.releasecontroller.web;

import br.jus.tjce.releasecontroller.model.BaseCalculo;
import br.jus.tjce.releasecontroller.repository.BaseCalculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/base-calculo")
public class BaseCalculoController {

    @Autowired
    private BaseCalculoRepository repository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("registros", repository.findAll());
        return "base-calculo/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("baseCalculo", new BaseCalculo("{}"));
        return "base-calculo/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        BaseCalculo baseCalculo = repository.findById(id).orElse(null);
        if (baseCalculo == null) {
            redirectAttributes.addFlashAttribute("error", "Registro não encontrado.");
            return "redirect:/base-calculo";
        }
        model.addAttribute("baseCalculo", baseCalculo);
        return "base-calculo/form";
    }

    @PostMapping
    public String save(@ModelAttribute BaseCalculo baseCalculo, RedirectAttributes redirectAttributes) {
        try {
            // Basic validation to ensure "dados" is somewhat valid JSON or empty
            if (baseCalculo.getDados() == null || baseCalculo.getDados().trim().isEmpty()) {
                baseCalculo.setDados("{}");
            }
            repository.save(baseCalculo);
            redirectAttributes.addFlashAttribute("success", "Registro salvo com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/base-calculo";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            repository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Registro removido com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao remover: " + e.getMessage());
        }
        return "redirect:/base-calculo";
    }
}

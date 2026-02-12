package br.jus.tjce.releasecontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.jus.tjce.releasecontroller.redmine.RedmineTaskService;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        RedmineTaskService service = new RedmineTaskService();
        service.imprimirTarefasApartirDoPai();
    }
}

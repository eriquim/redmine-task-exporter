package br.jus.tjce.releasecontroller.model;

import javax.persistence.*;

@Entity
@Table(name = "tb_base_calculo")
public class BaseCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String dados;

    public BaseCalculo() {}

    public BaseCalculo(String dados) {
        this.dados = dados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDados() {
        return dados;
    }

    public void setDados(String dados) {
        this.dados = dados;
    }
}

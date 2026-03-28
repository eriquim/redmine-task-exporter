package br.jus.tjce.releasecontroller.model;

import javax.persistence.*;

@Entity
@Table(name = "tb_base_calculo")
public class BaseCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupo_atividades")
    private String grupoAtividades;

    @Column(name = "atividade")
    private String atividade;

    @Column(name = "unidade_medida")
    private String unidadeMedida;

    @Column(name = "quantidade_base_ust")
    private Double quantidadeBaseUst;

    @Column(name = "produto_final", columnDefinition = "text")
    private String produtoFinal;

    @Column(name = "perfil")
    private String perfil;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "atributo")
    private String atributo;

    @Column(name = "referencia_calculo")
    private String referenciaCalculo;

    public BaseCalculo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrupoAtividades() {
        return grupoAtividades;
    }

    public void setGrupoAtividades(String grupoAtividades) {
        this.grupoAtividades = grupoAtividades;
    }

    public String getAtividade() {
        return atividade;
    }

    public void setAtividade(String atividade) {
        this.atividade = atividade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Double getQuantidadeBaseUst() {
        return quantidadeBaseUst;
    }

    public void setQuantidadeBaseUst(Double quantidadeBaseUst) {
        this.quantidadeBaseUst = quantidadeBaseUst;
    }

    public String getProdutoFinal() {
        return produtoFinal;
    }

    public void setProdutoFinal(String produtoFinal) {
        this.produtoFinal = produtoFinal;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getReferenciaCalculo() {
        return referenciaCalculo;
    }

    public void setReferenciaCalculo(String referenciaCalculo) {
        this.referenciaCalculo = referenciaCalculo;
    }
}

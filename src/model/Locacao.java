package model;

import java.sql.Date;

public class Locacao {
    private Long id;
    private Date dataLocacao;
    private Date dataVencimento;
    private Jogo jogo;
    private Cliente cliente;

    // Construtor vazio
    public Locacao() {}

    // Construtor parametrizado
    public Locacao(Long id, Date dataLocacao, Date dataVencimento, Jogo jogo, Cliente cliente) {
        this.id = id;
        this.dataLocacao = dataLocacao;
        this.dataVencimento = dataVencimento;
        this.jogo = jogo;
        this.cliente = cliente;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLocacao() {
        return dataLocacao;
    }

    public void setDataLocacao(Date dataLocacao) {
        this.dataLocacao = dataLocacao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Locacao [id=" + id + ", dataLocacao=" + dataLocacao + ", dataVencimento=" + dataVencimento + ", jogo="
                + jogo + ", cliente=" + cliente + "]";
    }
}


package model;

import java.time.LocalDate;

public class Locacao {
    private Long id;
    private LocalDate dataLocacao;
    private LocalDate dataVencimento;
    private Jogo jogo;
    private Cliente cliente;

    // Construtor vazio
    public Locacao() {}

    // Construtor parametrizado
    public Locacao(Long id, LocalDate dataLocacao, LocalDate dataVencimento, Jogo jogo, Cliente cliente) {
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

    public LocalDate getDataLocacao() {
        return dataLocacao;
    }

    public void setDataLocacao(LocalDate dataLocacao) {
        this.dataLocacao = dataLocacao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
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


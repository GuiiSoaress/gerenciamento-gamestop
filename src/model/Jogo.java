package model;

public class Jogo {
    private Long id;
    private String nome;
    private Double preco;
    private Integer idadeMinima;
    private Genero genero;
    
    // Construtor vazio
    public Jogo() {}

    // Construtor parametrizado
    public Jogo(Long id, String nome, Double preco, Integer idadeMinima, Genero genero) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.idadeMinima = idadeMinima;
        this.genero = genero;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(Integer idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "Jogo [id=" + id + ", nome=" + nome + ", preco=" + preco + ", idadeMinima=" + idadeMinima + ", genero="
                + genero + "]";
    }
}

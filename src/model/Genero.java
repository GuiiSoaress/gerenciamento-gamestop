package model;

public class Genero {
    private Long id;
    private String nome;

    // Construtor vazio
    public Genero() {}

    // Construtor parametrizado
    public Genero(Long id, String nome) {
        this.id = id;
        this.nome = nome;
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

    @Override
    public String toString() {
        return "Genero [id=" + id + ", nome=" + nome + "]";
    }
}


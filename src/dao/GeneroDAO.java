package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Genero;
import util.ConnectionFactory;

public class GeneroDAO {

    // READ
     public List<Genero> buscarTodos() {
        List<Genero> generos = new ArrayList<>();
        String sql = "SELECT id, nome FROM genero";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Genero genero = new Genero(
                        rs.getLong("id"),
                        rs.getString("nome"));
                generos.add(genero);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar os gêneros: " + e.getMessage());
            e.printStackTrace();
        }
        return generos;
    }


    // READ BY ID
     public Genero buscarPorId(Long id) {
        Genero genero = null;
        String sql = "SELECT id, nome FROM genero WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    genero = new Genero(
                            rs.getLong("id"),
                            rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar gênero por ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return genero;
    }

    // CREATE
    public void criarGenero(Genero genero) {
        String sql = "INSERT INTO genero (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, genero.getNome());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    genero.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir o gênero: " + genero.getNome() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // UPDATE
    public void atualizarGenero(Genero genero) {
        String sql = "UPDATE genero SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, genero.getNome());
            stmt.setLong(2, genero.getId()); 

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar gênero ID: " + genero.getId() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // DELETE
     public void deletar(Long id) {
        String sql = "DELETE FROM genero WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao deletar gênero com ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

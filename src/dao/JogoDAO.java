package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Jogo;
import model.Genero;
import util.ConnectionFactory;

public class JogoDAO {

    // ------------------------------------
    // READ: Buscar Todos
    // ------------------------------------
    public List<Jogo> buscarTodos() {
        List<Jogo> jogos = new ArrayList<>();

        // Left Join para buscar os campos de genero
        String sql = "SELECT j.id, j.nome, j.preco, j.idadeMinima, " +
                "g.id as id_genero, g.nome as nome_genero " +
                "FROM jogo j " +
                "LEFT JOIN genero g ON j.genero_id = g.id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Genero genero = null;

                // Lendo o generoId como Long
                Long generoId = rs.getLong("id_genero");

                if (!rs.wasNull()) {
                    genero = new Genero(
                            generoId,
                            rs.getString("nome_genero"));
                }

                Jogo jogo = new Jogo(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getInt("idadeMinima"),
                        genero);
                jogos.add(jogo);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar jogos: " + e.getMessage());
            e.printStackTrace();
        }
        return jogos;
    }

    // ------------------------------------
    // READ BY ID: Buscar por ID
    // ------------------------------------
    public Jogo buscarPorId(Long id) {
        Jogo jogo = null;

        // Left Join para buscar os campos de genero
        String sql = "SELECT j.id, j.nome, j.preco, j.idadeMinima, " +
                "g.id as id_genero, g.nome as nome_genero " +
                "FROM jogo j " +
                "LEFT JOIN genero g ON j.genero_id = g.id " +
                "WHERE j.id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Genero genero = null;

                    // Lendo o generoId como Long
                    Long generoId = rs.getLong("id_genero");

                    if (!rs.wasNull()) {
                        genero = new Genero(
                                generoId,
                                rs.getString("nome_genero"));
                    }

                    jogo = new Jogo(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getDouble("preco"),
                            rs.getInt("idadeMinima"),
                            genero);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar jogo por ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return jogo;
    }

    // ------------------------------------
    // CREATE: Inserir
    // ------------------------------------
    public void inserir(Jogo jogo) {
        String sql = "INSERT INTO jogo (nome, preco, idadeMinima, genero_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, jogo.getNome());
            stmt.setDouble(2, jogo.getPreco());
            stmt.setInt(3, jogo.getIdadeMinima());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    jogo.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir o jogo: " + jogo.getNome() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE: Atualizar
    // ------------------------------------
    public void atualizar(Jogo jogo) {
        String sql = "UPDATE jogo SET nome = ?, preco = ?, idadeMinima = ?, genero_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jogo.getNome());
            stmt.setDouble(2, jogo.getPreco());
            stmt.setInt(3, jogo.getIdadeMinima());

            stmt.setLong(5, jogo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Jogo ID " + jogo.getId() + " atualizado. Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar jogo ID: " + jogo.getId() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // DELETE: Deletar por ID
    // ------------------------------------
    public void deletar(Long id) {
        String sql = "DELETE FROM jogo WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Tentativa de deletar Jogo ID " + id + ". Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao deletar jogo com ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Cliente;
import model.Jogo;
import model.Locacao;
import util.ConnectionFactory;

public class LocacaoDAO{
    // ------------------------------------
    // READ
    // ------------------------------------
    public List<Locacao> buscarTodos() {
        List<Locacao> locacoes = new ArrayList<>();
        // query SQL para selecionar todos os campos
        String sql = "SELECT l.id, l.data_locacao, l.data_devolucao, " +
                     "j.id AS id_jogo, j.nome AS nome_jogo, " +
                     "c.id AS id_cliente, c.nome AS nome_cliente " +
                     "FROM locacao l " +
                     "INNER JOIN jogo j ON l.jogo_id = j.id " + 
                     "INNER JOIN cliente c ON l.cliente_id = c.id";
                 
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            
            // itera sobre cada linha retornada pelo banco
            while (rs.next()) {
                // Mapeia o Cliente
                Cliente cliente = null;
                Long idCliente = rs.getLong("id_cliente");
                
                //mapeia o jogo
                Jogo jogo = null;
                Long idJogo = rs.getLong("id_jogo");
                
                // mapeia os items caso nao sejem null
                if (!rs.wasNull()) {
                    cliente = new Cliente(idCliente, rs.getString("nome_cliente"));
                    jogo = new Jogo(idJogo, rs.getString("nome_jogo"));
                }
                
                // cria um novo objeto Locacao a partir dos dados da linha atual do ResultSet
                Locacao locacao = new Locacao(
                        rs.getLong("id"),
                        rs.getDate("data_locacao"),
                        rs.getDate("data_devolucao"), 
                        jogo, cliente);
                locacoes.add(locacao);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar as Locações: " + e.getMessage());
            e.printStackTrace();
        }
        return locacoes;
    }
    
    // ------------------------------------
    // READ BY ID
    // ------------------------------------
    public Locacao buscarPorId(Long id) {
        
        Locacao locacao = null;
        
        String sql = "SELECT l.id, l.data_locacao, l.data_devolucao, " +
                     "j.id AS id_jogo, j.nome AS nome_jogo, " +
                     "c.id AS id_cliente, c.nome AS nome_cliente " +
                     "FROM locacao l " +
                     "INNER JOIN jogo j ON l.jogo_id = j.id " + 
                     "INNER JOIN cliente c ON l.cliente_id = c.id " +
                     "WHERE l.id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // define o ID no WHERE
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                // itera sobre cada linha retornada pelo banco
                if (rs.next()) {
                    // Mapeia o Cliente
                    Cliente cliente = null;
                    Long idCliente = rs.getLong("id_cliente");
                    
                    //mapeia o jogo
                    Jogo jogo = null;
                    Long idJogo = rs.getLong("id_jogo");
                    
                    // mapeia os items caso nao sejem null
                    if (!rs.wasNull()) {
                        cliente = new Cliente(idCliente, rs.getString("nome_cliente"));
                        jogo = new Jogo(idJogo, rs.getString("nome_jogo"));
                    }
                    
                    // cria um novo objeto Locacao a partir dos dados da linha atual do ResultSet
                    locacao = new Locacao(
                            rs.getLong("id"),
                            rs.getDate("data_locacao"),
                            rs.getDate("data_devolucao"), 
                            jogo, cliente);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar a Locação: " + e.getMessage());
            e.printStackTrace();
        }
        return locacao;
    }

    // ------------------------------------
    // CREATE
    // ------------------------------------
    public static void inserir(Locacao locacao) {
        
        // usa Statement.RETURN_GENERATED_KEYS para solicitar o ID gerado
        String sql = "INSERT INTO locacao (data_locacao, data_devolucao, jogo_id, cliente_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // define os parâmetros da query
            stmt.setDate(1, locacao.getDataLocacao());
            stmt.setDate(2, locacao.getDataVencimento());
            
            // define o ID do cliente e do jogo
            // precisamos checar se os objetos nao sao null e se tem id
            
            if (locacao.getJogo() != null && locacao.getJogo().getId() != null) {
                stmt.setLong(3, locacao.getJogo().getId());
            } else {
                // Se for nula, inserimos NULL no banco
                stmt.setNull(3, java.sql.Types.BIGINT);
            }
            
            if (locacao.getCliente() != null && locacao.getCliente().getId() != null) {
                stmt.setLong(4, locacao.getCliente().getId());
            } else {
                // Se for nula, inserimos NULL no banco
                stmt.setNull(4, java.sql.Types.BIGINT);
            }
            
            // executa a inserção
            stmt.executeUpdate();
            
            // recupera a chave gerada (o novo ID)
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // define o ID no objeto Locacao que foi passado (importante para a API)
                    locacao.setId(rs.getLong(1));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir Locacao. Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE
    // ------------------------------------
    public void atualizar(Locacao locacao) {

        String sql = "UPDATE locacao SET data_locacao = ?, data_devolucao = ?, jogo_id = ?, cliente_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define os parâmetros (os novos valores)
            stmt.setDate(1, locacao.getDataLocacao());
            stmt.setDate(2, locacao.getDataVencimento());

            // Define o ID do jogo (parâmetro 3)
            if (locacao.getJogo() != null && locacao.getJogo().getId() != null) {
                stmt.setLong(3, locacao.getJogo().getId());
            } else {
                stmt.setNull(3, java.sql.Types.BIGINT);
            }

            // Define o ID do cliente (parâmetro 4)
            if (locacao.getCliente() != null && locacao.getCliente().getId() != null) {
                stmt.setLong(4, locacao.getCliente().getId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }

            // define o ID no WHERE (o último '?')
            stmt.setLong(5, locacao.getId());

            // executa a atualização
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Locacao ID " + locacao.getId() + " atualizada. Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar locacao ID: " + locacao.getId() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------
    public void deletar(Long id) {

        // a exclusão precisa do ID no WHERE
        String sql = "DELETE FROM locacao WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define o ID da locacao a ser deletada
            stmt.setLong(1, id);

            // executa a exclusão
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Tentativa de deletar Locacao ID " + id + ". Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao deletar locacao ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
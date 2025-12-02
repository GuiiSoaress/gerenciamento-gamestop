package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Cliente;
import util.ConnectionFactory;

public class ClienteDAO {
    // ------------------------------------
    // READ
    // ------------------------------------
    public List<Cliente> buscarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        // query SQL para selecionar todos os campos
        String sql = "SELECT id, nome, idade FROM cliente";
                 
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            
            // itera sobre cada linha retornada pelo banco
            while (rs.next()) {
                // cria um novo objeto Cliente a partir dos dados da linha atual do ResultSet
                Cliente cliente = new Cliente(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getInt("idade"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar os Clientes: " + e.getMessage());
            e.printStackTrace();
        }
        return clientes;
    }
    
    // ------------------------------------
    // READ BY ID
    // ------------------------------------
    public Cliente buscarPorId(Long id) {
        
        Cliente cliente = null;
        
        String sql = "SELECT id, nome, idade FROM cliente WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // define o ID no WHERE
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                // itera sobre cada linha retornada pelo banco
                if (rs.next()) {
                    // cria um novo objeto Cliente a partir dos dados da linha atual do ResultSet
                    cliente = new Cliente(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getInt("idade"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar o Cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return cliente;
    }

    // ------------------------------------
    // CREATE
    // ------------------------------------
    public static void inserir(Cliente cliente) {
        
        // usa Statement.RETURN_GENERATED_KEYS para solicitar o ID gerado
        String sql = "INSERT INTO cliente (nome, idade) VALUES (?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // define os parâmetros da query
            stmt.setString(1, cliente.getNome());
            
            // verifica se idade não é null antes de inserir
            if (cliente.getIdade() != null) {
                stmt.setInt(2, cliente.getIdade());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            // executa a inserção
            stmt.executeUpdate();
            
            // recupera a chave gerada (o novo ID)
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // define o ID no objeto Cliente que foi passado (importante para a API)
                    cliente.setId(rs.getLong(1));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir Cliente. Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE
    // ------------------------------------
    public void atualizar(Cliente cliente) {

        String sql = "UPDATE cliente SET nome = ?, idade = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define os parâmetros (os novos valores)
            stmt.setString(1, cliente.getNome());
            
            // verifica se idade não é null antes de atualizar
            if (cliente.getIdade() != null) {
                stmt.setInt(2, cliente.getIdade());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            // define o ID no WHERE (o último '?')
            stmt.setLong(3, cliente.getId());

            // executa a atualização
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Cliente ID " + cliente.getId() + " atualizado. Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente ID: " + cliente.getId() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------
    public void deletar(Long id) {

        // a exclusão precisa do ID no WHERE
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define o ID do cliente a ser deletado
            stmt.setLong(1, id);

            // executa a exclusão
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Tentativa de deletar Cliente ID " + id + ". Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao deletar cliente ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
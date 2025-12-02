package api;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

import java.sql.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import dao.ClienteDAO;
import dao.GeneroDAO;
import dao.JogoDAO;
import dao.LocacaoDAO;
import model.Cliente;
import model.Genero;
import model.Jogo;
import model.Locacao;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApiLocadora {

    private static final LocacaoDAO locacaoDAO = new LocacaoDAO();
    private static final JogoDAO jogoDAO = new JogoDAO();
    private static final ClienteDAO clienteDAO = new ClienteDAO();
    private static final GeneroDAO generoDAO = new GeneroDAO();
    
    // Gson com adaptador para java.sql.Date
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.sql.Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                return java.sql.Date.valueOf(json.getAsString());
            })
            .create();
    
    private static final String APPLICATION_JSON = "application/json";

    public static void main(String[] args) {
        port(4567);

        // Filtro CORS
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        // Filtro para JSON
        after(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                if (!request.pathInfo().equals("/")) {
                    response.type(APPLICATION_JSON);
                }
            }
        });

        // Rota raiz
        get("/", (req, res) -> {
            res.type("text/html");
            return "<h1>API de Locadora de Jogos</h1>" +
                    "<h2>Endpoints disponíveis:</h2>" +
                    "<h3>Locações</h3>" +
                    "<ul>" +
                    "<li>GET /locacoes - Listar todas as locações</li>" +
                    "<li>GET /locacoes/:id - Buscar locação por ID</li>" +
                    "<li>POST /locacoes - Criar nova locação</li>" +
                    "<li>PUT /locacoes/:id - Atualizar locação</li>" +
                    "<li>DELETE /locacoes/:id - Deletar locação</li>" +
                    "</ul>" +
                    "<h3>Jogos</h3>" +
                    "<ul>" +
                    "<li>GET /jogos - Listar todos os jogos</li>" +
                    "<li>GET /jogos/:id - Buscar jogo por ID</li>" +
                    "<li>POST /jogos - Criar novo jogo</li>" +
                    "<li>PUT /jogos/:id - Atualizar jogo</li>" +
                    "<li>DELETE /jogos/:id - Deletar jogo</li>" +
                    "</ul>" +
                    "<h3>Clientes</h3>" +
                    "<ul>" +
                    "<li>GET /clientes - Listar todos os clientes</li>" +
                    "<li>GET /clientes/:id - Buscar cliente por ID</li>" +
                    "<li>POST /clientes - Criar novo cliente</li>" +
                    "<li>PUT /clientes/:id - Atualizar cliente</li>" +
                    "<li>DELETE /clientes/:id - Deletar cliente</li>" +
                    "</ul>" +
                    "<h3>Gêneros</h3>" +
                    "<ul>" +
                    "<li>GET /generos - Listar todos os gêneros</li>" +
                    "<li>GET /generos/:id - Buscar gênero por ID</li>" +
                    "<li>POST /generos - Criar novo gênero</li>" +
                    "<li>PUT /generos/:id - Atualizar gênero</li>" +
                    "<li>DELETE /generos/:id - Deletar gênero</li>" +
                    "</ul>";
        });

        // ========================================
        // ROTAS DE LOCAÇÕES
        // ========================================
        
        // GET /locacoes - Buscar todas
        get("/locacoes", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                return gson.toJson(locacaoDAO.buscarTodos());
            }
        });

        // GET /locacoes/:id - Buscar por ID
        get("/locacoes/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id"));
                    Locacao locacao = locacaoDAO.buscarPorId(id);

                    if (locacao != null) {
                        return gson.toJson(locacao);
                    } else {
                        response.status(404);
                        return "{\"mensagem\": \"Locação com ID " + id + " não encontrada\"}";
                    }
                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\": \"Formato de ID inválido.\"}";
                }
            }
        });

        // POST /locacoes - Criar nova locação
        post("/locacoes", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Locacao novaLocacao = gson.fromJson(request.body(), Locacao.class);
                    LocacaoDAO.inserir(novaLocacao);

                    response.status(201);
                    return gson.toJson(novaLocacao);
                } catch (Exception e) {
                    response.status(500);
                    System.err.println("Erro ao processar requisição POST: " + e.getMessage());
                    e.printStackTrace();
                    return "{\"mensagem\": \"Erro ao criar locação.\"}";
                }
            }
        });

        // PUT /locacoes/:id - Atualizar locação existente
        put("/locacoes/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id"));

                    if (locacaoDAO.buscarPorId(id) == null) {
                        response.status(404);
                        return "{\"mensagem\": \"Locação não encontrada para atualização.\"}";
                    }

                    Locacao locacaoParaAtualizar = gson.fromJson(request.body(), Locacao.class);
                    locacaoParaAtualizar.setId(id);

                    locacaoDAO.atualizar(locacaoParaAtualizar);

                    response.status(200);
                    return gson.toJson(locacaoParaAtualizar);

                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\": \"Formato de ID inválido.\"}";
                } catch (Exception e) {
                    response.status(500);
                    System.err.println("Erro ao processar requisição PUT: " + e.getMessage());
                    e.printStackTrace();
                    return "{\"mensagem\": \"Erro ao atualizar locação.\"}";
                }
            }
        });

        // DELETE /locacoes/:id - Deletar uma locação
        delete("/locacoes/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id"));

                    if (locacaoDAO.buscarPorId(id) == null) {
                        response.status(404);
                        return "{\"mensagem\": \"Locação não encontrada para exclusão.\"}";
                    }

                    locacaoDAO.deletar(id);

                    response.status(204);
                    return "";

                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\": \"Formato de ID inválido.\"}";
                }
            }
        });

        // ========================================
        // ROTAS DE JOGOS
        // ========================================
        
        // GET /jogos - Buscar todos
        get("/jogos", (request, response) -> gson.toJson(jogoDAO.buscarTodos()));

        // GET /jogos/:id - Buscar por ID
        get("/jogos/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));
                Jogo jogo = jogoDAO.buscarPorId(id);

                if (jogo != null) {
                    return gson.toJson(jogo);
                } else {
                    response.status(404);
                    return "{\"mensagem\": \"Jogo com ID " + id + " não encontrado\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            }
        });

        // POST /jogos - Criar novo jogo
        post("/jogos", (request, response) -> {
            try {
                Jogo novoJogo = gson.fromJson(request.body(), Jogo.class);
                jogoDAO.inserir(novoJogo);

                response.status(201);
                return gson.toJson(novoJogo);
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao criar jogo.\"}";
            }
        });

        // PUT /jogos/:id - Atualizar jogo
        put("/jogos/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));

                if (jogoDAO.buscarPorId(id) == null) {
                    response.status(404);
                    return "{\"mensagem\": \"Jogo não encontrado para atualização.\"}";
                }

                Jogo jogoParaAtualizar = gson.fromJson(request.body(), Jogo.class);
                jogoParaAtualizar.setId(id);

                jogoDAO.atualizar(jogoParaAtualizar);

                response.status(200);
                return gson.toJson(jogoParaAtualizar);

            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao atualizar jogo.\"}";
            }
        });

        // DELETE /jogos/:id - Deletar jogo
        delete("/jogos/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));

                if (jogoDAO.buscarPorId(id) == null) {
                    response.status(404);
                    return "{\"mensagem\": \"Jogo não encontrado para exclusão.\"}";
                }

                jogoDAO.deletar(id);

                response.status(204);
                return "";

            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            } catch (Exception e) {
                if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    response.status(409);
                    return "{\"mensagem\": \"Não é possível excluir o jogo. Ele está sendo usado em uma ou mais locações.\"}";
                }
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao deletar jogo.\"}";
            }
        });

        // ========================================
        // ROTAS DE CLIENTES
        // ========================================
        
        // GET /clientes - Buscar todos
        get("/clientes", (request, response) -> gson.toJson(clienteDAO.buscarTodos()));

        // GET /clientes/:id - Buscar por ID
        get("/clientes/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));
                Cliente cliente = clienteDAO.buscarPorId(id);

                if (cliente != null) {
                    return gson.toJson(cliente);
                } else {
                    response.status(404);
                    return "{\"mensagem\": \"Cliente com ID " + id + " não encontrado\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            }
        });

        // POST /clientes - Criar novo cliente
        post("/clientes", (request, response) -> {
            try {
                Cliente novoCliente = gson.fromJson(request.body(), Cliente.class);
                ClienteDAO.inserir(novoCliente);

                response.status(201);
                return gson.toJson(novoCliente);
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao criar cliente.\"}";
            }
        });

        // PUT /clientes/:id - Atualizar cliente
        put("/clientes/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));

                if (clienteDAO.buscarPorId(id) == null) {
                    response.status(404);
                    return "{\"mensagem\": \"Cliente não encontrado para atualização.\"}";
                }

                Cliente clienteParaAtualizar = gson.fromJson(request.body(), Cliente.class);
                clienteParaAtualizar.setId(id);

                clienteDAO.atualizar(clienteParaAtualizar);

                response.status(200);
                return gson.toJson(clienteParaAtualizar);

            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao atualizar cliente.\"}";
            }
        });

        // DELETE /clientes/:id - Deletar cliente
        delete("/clientes/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));

                if (clienteDAO.buscarPorId(id) == null) {
                    response.status(404);
                    return "{\"mensagem\": \"Cliente não encontrado para exclusão.\"}";
                }

                clienteDAO.deletar(id);

                response.status(204);
                return "";

            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            } catch (Exception e) {
                if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    response.status(409);
                    return "{\"mensagem\": \"Não é possível excluir o cliente. Ele possui uma ou mais locações.\"}";
                }
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao deletar cliente.\"}";
            }
        });

        // ========================================
        // ROTAS DE GÊNEROS
        // ========================================
        
        // GET /generos - Buscar todos
        get("/generos", (request, response) -> gson.toJson(generoDAO.buscarTodos()));

        // GET /generos/:id - Buscar por ID
        get("/generos/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));
                Genero genero = generoDAO.buscarPorId(id);

                if (genero != null) {
                    return gson.toJson(genero);
                } else {
                    response.status(404);
                    return "{\"mensagem\": \"Gênero com ID " + id + " não encontrado\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            }
        });

        // POST /generos - Criar novo gênero
        post("/generos", (request, response) -> {
            try {
                Genero novoGenero = gson.fromJson(request.body(), Genero.class);
                generoDAO.criarGenero(novoGenero);

                response.status(201);
                return gson.toJson(novoGenero);
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao criar gênero.\"}";
            }
        });

        // PUT /generos/:id - Atualizar gênero
        put("/generos/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));

                if (generoDAO.buscarPorId(id) == null) {
                    response.status(404);
                    return "{\"mensagem\": \"Gênero não encontrado para atualização.\"}";
                }

                Genero generoParaAtualizar = gson.fromJson(request.body(), Genero.class);
                generoParaAtualizar.setId(id);

                generoDAO.atualizarGenero(generoParaAtualizar);

                response.status(200);
                return gson.toJson(generoParaAtualizar);

            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao atualizar gênero.\"}";
            }
        });

        // DELETE /generos/:id - Deletar gênero
        delete("/generos/:id", (request, response) -> {
            try {
                Long id = Long.parseLong(request.params(":id"));

                if (generoDAO.buscarPorId(id) == null) {
                    response.status(404);
                    return "{\"mensagem\": \"Gênero não encontrado para exclusão.\"}";
                }

                generoDAO.deletar(id);

                response.status(204);
                return "";

            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"mensagem\": \"Formato de ID inválido.\"}";
            } catch (Exception e) {
                if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    response.status(409);
                    return "{\"mensagem\": \"Não é possível excluir o gênero. Ele está sendo usado por um ou mais jogos.\"}";
                }
                response.status(500);
                e.printStackTrace();
                return "{\"mensagem\": \"Erro ao deletar gênero.\"}";
            }
        });

        System.out.println("=========================================");
        System.out.println("API de Locadora iniciada na porta 4567");
        System.out.println("Acesse: http://localhost:4567");
        System.out.println("=========================================");
    }
}
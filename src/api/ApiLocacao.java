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

import dao.LocacaoDAO;
import model.Locacao;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApiLocacao {

    private static final LocacaoDAO locacaoDAO = new LocacaoDAO();
    
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
            return "<h1>API de Locações</h1>" +
                    "<p>Endpoints disponíveis:</p>" +
                    "<ul>" +
                    "<li>GET /locacoes - Listar todas as locações</li>" +
                    "<li>GET /locacoes/:id - Buscar locação por ID</li>" +
                    "<li>POST /locacoes - Criar nova locação</li>" +
                    "<li>PUT /locacoes/:id - Atualizar locação</li>" +
                    "<li>DELETE /locacoes/:id - Deletar locação</li>" +
                    "</ul>";
        });

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

        System.out.println("=========================================");
        System.out.println("API de Locações iniciada na porta 4567");
        System.out.println("Acesse: http://localhost:4567");
        System.out.println("=========================================");
    }
}
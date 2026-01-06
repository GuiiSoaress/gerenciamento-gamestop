# 🎮 Sistema de Gerenciamento de Locadora de Jogos

Sistema back-end REST API desenvolvido em Java para gerenciar operações de uma locadora de jogos físicos.

## 📋 Sobre

Sistema que automatiza o controle de locações de jogos, resolvendo problemas de gestão manual:

- Controle de jogos alugados e prazos de devolução
- Validação automática de idade mínima
- Cálculo de dias restantes para devolução
- Proteção de integridade referencial

## 🛠 Tecnologias

- Java
- Spark Framework
- MySQL
- Gson (JSON)

## 📦 Pré-requisitos

- Java JDK 8+
- MySQL 8.0+

## 🚀 Instalação e Execução

### 1. Configure o Banco de Dados

```bash
mysql -u root -p < bd/CreateScritpt.sql
```

### 2. Configure a Conexão

Edite `src/util/ConnectionFactory.java` com suas credenciais MySQL.

### 3. Compile e Execute

```bash
# Compilar
javac -cp "lib/*" -d bin src/**/*.java

# Executar
java -cp "bin:lib/*" api.ApiLocadora
```

A API estará disponível em: `http://localhost:4567`

## 📚 Documentação da API

### Base URL

```
http://localhost:4567
```

### Endpoints

Todos os recursos seguem o padrão REST:

| Recurso                    | Endpoints                                            |
| -------------------------- | ---------------------------------------------------- |
| **Gêneros** (`/generos`)   | `GET`, `GET /:id`, `POST`, `PUT /:id`, `DELETE /:id` |
| **Jogos** (`/jogos`)       | `GET`, `GET /:id`, `POST`, `PUT /:id`, `DELETE /:id` |
| **Clientes** (`/clientes`) | `GET`, `GET /:id`, `POST`, `PUT /:id`, `DELETE /:id` |
| **Locações** (`/locacoes`) | `GET`, `GET /:id`, `POST`, `PUT /:id`, `DELETE /:id` |

### Estruturas de Dados

**Gênero:**

```json
{ "id": 1, "nome": "RPG" }
```

**Jogo:**

```json
{
  "id": 1,
  "nome": "The Witcher 3",
  "preco": 79.9,
  "idadeMinima": 18,
  "genero": { "id": 5, "nome": "RPG" }
}
```

**Cliente:**

```json
{ "id": 1, "nome": "João Silva", "idade": 26 }
```

**Locação (criação):**

```json
{
  "dataLocacao": "2025-12-01",
  "dataVencimento": "2026-01-20",
  "jogo": { "id": 1 },
  "cliente": { "id": 1 }
}
```

**Locação (resposta):**

```json
{
  "id": 1,
  "dataLocacao": "2025-12-01",
  "dataVencimento": "2026-01-20",
  "diasRestantes": 40,
  "jogo": {
    /* objeto completo */
  },
  "cliente": {
    /* objeto completo */
  }
}
```

> **Nota:** O campo `diasRestantes` é calculado automaticamente nas respostas GET de locações.

## ⚠️ Validações

- **Idade mínima:** Impede locação de jogos para clientes menores de idade (status 400)
- **Integridade referencial:** Impede exclusão de recursos em uso (status 409)
  - Jogos em locações ativas
  - Clientees com locações
  - Gêneros associados a jogos

## 📊 Códigos de Status

| Código | Descrição               |
| ------ | ----------------------- |
| 200    | OK                      |
| 201    | Created                 |
| 204    | No Content              |
| 400    | Bad Request (validação) |
| 404    | Not Found               |
| 409    | Conflict (integridade)  |
| 500    | Internal Server Error   |

## 💡 Exemplo de Uso

```bash
# Criar locação
POST http://localhost:4567/locacoes
Content-Type: application/json

{
  "dataLocacao": "2025-12-01",
  "dataVencimento": "2026-01-20",
  "jogo": { "id": 1 },
  "cliente": { "id": 1 }
}
```

## 📁 Estrutura do Projeto

```
src/
├── api/ApiLocadora.java      # Rotas da API
├── dao/                       # Acesso a dados
├── model/                     # Modelos de dados
└── util/ConnectionFactory.java  # Conexão BD
```

## 🔗 Coleção de Requisições

Coleção do Insomnia disponível em: `collections/Insomnia_2025-12-01.yaml`

---

**Desenvolvido para modernizar o gerenciamento de locadoras de jogos**

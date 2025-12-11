# A Dor e A Solução

## O Problema

Locadoras de jogos físicos ainda existem e atendem um nicho importante: pessoas sem internet rápida, colecionadores e quem prefere mídias físicas. Porém, muitas operam de forma **manual e desorganizada**, usando cadernos e planilhas para controlar tudo.

Isso gera:
- **Perda de controle** sobre quais jogos estão alugados e com quem
- **Dificuldade em acompanhar prazos** de devolução
- **Impossibilidade de validar** se um cliente pode alugar determinado jogo (idade mínima)
- **Atendimento lento** por falta de acesso rápido às informações

## Nossa Solução

Desenvolvemos um **sistema back-end** que organiza toda a logística de locações de jogos.

O sistema permite:
- Cadastrar, visualizar, editar e deletar clientes, jogos, gêneros e locações
- Calcular automaticamente quantos dias faltam para cada devolução
- Validar se o cliente tem idade suficiente para alugar um jogo
- Acessar todas as informações de forma rápida e organizada através de uma API REST

**Resultado:** A locadora deixa de ser caótica e passa a ter controle total sobre suas operações.

---

## As APIs Desenvolvidas

O sistema expõe **4 grupos de endpoints REST** para gerenciar todos os recursos:

### 1. **Gêneros** (`/generos`)
- Criar, listar, buscar por ID, atualizar e deletar gêneros de jogos
- Exemplos: RPG, Ação, Aventura, Estratégia

### 2. **Jogos** (`/jogos`)
- Gerenciar o catálogo completo de jogos
- Cada jogo possui: nome, preço, idade mínima e gênero
- Impede exclusão de jogos que estão em locações ativas

### 3. **Clientes** (`/clientes`)
- Cadastro completo de clientes com nome e idade
- Validação automática de idade ao criar locações

### 4. **Locações** (`/locacoes`)
- Controle completo das locações ativas
- Registra data de locação e data de vencimento
- **Calcula automaticamente** os dias restantes para devolução
- Valida se o cliente tem idade suficiente para o jogo

---

## Demonstração

**API Base:** `http://localhost:4567`

**Exemplo de uso - Criar uma locação:**
```json
POST /locacoes
{
  "dataLocacao": "2025-12-01",
  "dataVencimento": "2026-01-20",
  "jogo": { "id": 1 },
  "cliente": { "id": 1 }
}
```

**Resposta - Listar locações:**
```json
GET /locacoes
[
  {
    "id": 1,
    "dataLocacao": "2025-12-01",
    "dataVencimento": "2026-01-20",
    "diasRestantes": 40,
    "jogo": {
      "id": 1,
      "nome": "The Witcher 3",
      "idadeMinima": 18
    },
    "cliente": {
      "id": 1,
      "nome": "João Silva",
      "idade": 26
    }
  }
]
```

O campo **`diasRestantes`** é calculado automaticamente usando a API java.time, facilitando o controle de prazos e identificação de atrasos.

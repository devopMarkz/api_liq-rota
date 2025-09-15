## ✨ Funcionalidades

### 🔐 Autenticação (JWT)
- Login com **JWT Bearer**.
- Interceptor **anexa o token** e, em **401**, redireciona para o login.
- *(Opcional)* tela de cadastro usando `POST /usuarios` (público).

### 🚚 Gestão de Viagens
- **Criar individualmente** ou **em lote** (lista de viagens).
- **Editar** e **excluir** viagens salvas.
- **Filtros** por **origem** e **destino**.
- **Listagem paginada** (Spring Page).

### 🧮 Simulações de Frete (sem persistir)
- Simulação **individual** (`/calculos/frete`) e **em lote** (`/calculos/frete/lote`).
- Exibição de métricas:
  - Distância considerada (ida/volta)
  - Custo de combustível
  - Gastos adicionais
  - Valor do frete
  - Valor **líquido**
  - **Ganho por km**
- **Recalcular viagem salva** sem alterar o banco: `POST /trips/{id}/calcular`.

### 📊 Relatórios
- **Por intervalo** (início/fim).
- **Mensal** (por ano).
- **Anual** (faixa de anos).
- Cartões/tabelas responsivos para leitura rápida em mobile.

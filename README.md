### Autenticação
| Método | Endpoint | Body | Observações |
|---|---|---|---|
| `POST` | `/auth/login` | `{ "username": "...", "senha": "..." }` | Guarda `access_token`, `role`, `expires_in` |
| `POST` | `/usuarios` | `{ "username": "...", "senha": "..." }` | Público; 201 sem corpo |

> Nas rotas autenticadas enviar `Authorization: Bearer <token>` e `Content-Type: application/json`.

### Viagens & Simulações
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/trips?origem=&destino=&page=0&size=10` | Lista paginada (page inicia em 0) |
| `GET` | `/api/v1/trips/{id}` | Detalhe |
| `POST` | `/api/v1/trips` | Criar viagem (individual) |
| `POST` | `/api/v1/trips/lote` | Criar em lote |
| `PUT` | `/api/v1/trips/{id}` | Atualizar |
| `DELETE` | `/api/v1/trips/{id}` | Remover |
| `POST` | `/api/v1/trips/{id}/calcular` | **Recalcular (prévia)** sem persistir |
| `POST` | `/api/v1/calculos/frete` | **Simulação** individual |
| `POST` | `/api/v1/calculos/frete/lote` | **Simulação** em lote (retorna itens + **totais**) |

### Relatórios
| Método | Endpoint | Exemplo |
|---|---|---|
| `GET` | `/api/v1/relatorios/intervalo?inicio=YYYY-MM-DD&fim=YYYY-MM-DD` | `/intervalo?inicio=2025-08-01&fim=2025-08-31` |
| `GET` | `/api/v1/relatorios/mensal?ano=YYYY` | `/mensal?ano=2025` |
| `GET` | `/api/v1/relatorios/anual?de=YYYY&ate=YYYY` | `/anual?de=2024&ate=2025` |

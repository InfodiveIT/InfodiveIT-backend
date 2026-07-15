# Infodive API — Guia de Desenvolvimento

## Visão Geral do Projeto

API REST do site Infodive IT — integradora de tecnologia B2B sediada em Porto Alegre, fundada em 2003.
Esta API serve dois consumidores:
1. **Site público** (Next.js 14) — endpoints de leitura públicos
2. **Painel admin** (Payload CMS) — endpoints de escrita protegidos por JWT

---

## Stack Tecnológica

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.5.x | Framework principal |
| Spring Data JPA | 3.5.x | ORM / repositórios |
| Spring Security | 3.5.x | Autenticação JWT + CORS |
| PostgreSQL | 16 | Banco de dados (Supabase) |
| Supabase Storage | v1 REST | Armazenamento e hospedagem de imagens |
| Hibernate | 6.x | Implementação JPA |
| Lombok | latest | Redução de boilerplate |
| Maven | 3.9.x | Build e dependências |

---

## Arquitetura — Layered Architecture

```
HTTP Request
     ↓
┌─────────────┐
│  Controller │  ← Recebe HTTP, valida entrada, devolve ResponseEntity
└─────────────┘
     ↓
┌─────────────┐
│   Service   │  ← Lógica de negócio, orquestra operações
└─────────────┘
     ↓
┌─────────────┐
│ Repository  │  ← Acesso ao banco via Spring Data JPA
└─────────────┘
     ↓
┌─────────────┐
│   Entity    │  ← Mapeamento JPA das tabelas do banco
└─────────────┘
```

### Por que Layered e não DDD/Clean?
A API é essencialmente um CMS sofisticado — CRUD com regras de negócio simples.
DDD e Clean Architecture adicionariam Use Cases, Ports & Adapters e Domain Events desnecessários.
Layered é suficiente, conhecida por todo dev Java e fácil de manter.

---

## Estrutura de Pacotes

```
br.com.infodive.infodiveapi
├── config/
│   ├── SecurityConfig.java         ← CORS, JWT, rotas públicas/protegidas
│   ├── JwtConfig.java              ← Configuração do token JWT
│   └── OpenApiConfig.java          ← Swagger/OpenAPI
├── controller/
│   ├── FabricanteController.java
│   ├── SolucaoController.java
│   ├── ProdutoController.java
│   ├── ServicoController.java
│   ├── ConteudoController.java
│   ├── CaseController.java
│   ├── LeadController.java
│   └── ...
├── service/
│   ├── FabricanteService.java
│   ├── SolucaoService.java
│   └── ...
├── repository/
│   ├── FabricanteRepository.java
│   ├── SolucaoRepository.java
│   └── ...
├── entity/
│   ├── Fabricante.java
│   ├── Solucao.java
│   ├── Produto.java
│   ├── Servico.java
│   ├── Conteudo.java
│   ├── Case.java
│   ├── Lead.java
│   └── ...
├── dto/
│   ├── request/
│   │   ├── FabricanteRequest.java
│   │   └── ...
│   └── response/
│       ├── FabricanteResponse.java
│       ├── ProdutoResumoResponse.java  ← versão curta para listagens
│       ├── ProdutoDetalheResponse.java ← versão completa para [slug]
│       └── ...
├── mapper/
│   ├── FabricanteMapper.java
│   └── ...
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BusinessException.java
└── InfodiveApiApplication.java
```

---

## Convenções de Código

### Nomenclatura

| Artefato | Convenção | Exemplo |
|---|---|---|
| Classes | PascalCase | `FabricanteService` |
| Métodos | camelCase | `findBySlug()` |
| Variáveis | camelCase | `fabricanteId` |
| Constantes | UPPER_SNAKE | `MAX_PAGE_SIZE` |
| Pacotes | lowercase | `br.com.infodive` |
| Tabelas banco | snake_case | `fabricantes` |
| Colunas banco | snake_case | `logo_url` |
| Endpoints URL | kebab-case | `/solucoes/{slug}` |

### Controllers
- Sempre retornam `ResponseEntity<T>`
- Usam `@RestController` + `@RequestMapping`
- Nunca contêm lógica de negócio
- Validação de entrada via `@Valid`

```java
@RestController
@RequestMapping("/fabricantes")
@RequiredArgsConstructor
public class FabricanteController {

    private final FabricanteService fabricanteService;

    @GetMapping
    public ResponseEntity<List<FabricanteResponse>> findAll() {
        return ResponseEntity.ok(fabricanteService.findAll());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<FabricanteResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(fabricanteService.findBySlug(slug));
    }
}
```

### Services
- Anotados com `@Service`
- Métodos de leitura com `@Transactional(readOnly = true)`
- Métodos de escrita com `@Transactional`
- Lançam `ResourceNotFoundException` quando entidade não encontrada

```java
@Service
@RequiredArgsConstructor
public class FabricanteService {

    private final FabricanteRepository fabricanteRepository;
    private final FabricanteMapper fabricanteMapper;

    @Transactional(readOnly = true)
    public List<FabricanteResponse> findAll() {
        return fabricanteRepository.findAllByAtivoTrueOrderByOrdem()
            .stream()
            .map(fabricanteMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public FabricanteResponse findBySlug(String slug) {
        return fabricanteRepository.findBySlugAndAtivoTrue(slug)
            .map(fabricanteMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Fabricante não encontrado: " + slug));
    }
}
```

### Entities
- Anotadas com `@Entity` + `@Table(name = "...")`
- ID sempre UUID gerado automaticamente
- `@CreationTimestamp` e `@UpdateTimestamp` para auditoria
- Usar Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

```java
@Entity
@Table(name = "fabricantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fabricante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "descricao_curta")
    private String descricaoCurta;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "site_oficial")
    private String siteOficial;

    @Builder.Default
    private boolean destaque = false;

    @Builder.Default
    private int ordem = 0;

    @Builder.Default
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### DTOs
- Records Java para imutabilidade
- Request: validação com Bean Validation (`@NotBlank`, `@Size`, etc.)
- Response: nunca expõe campos internos como `ativo`, `createdAt`
- Dois tipos de response quando necessário: `Resumo` (listagem) e `Detalhe` (slug)

```java
// Request
public record FabricanteRequest(
    @NotBlank String slug,
    @NotBlank String nome,
    String descricao,
    String descricaoCurta,
    String logoUrl,
    String siteOficial,
    boolean destaque,
    int ordem
) {}

// Response
public record FabricanteResponse(
    UUID id,
    String nome,
    String slug,
    String descricao,
    String siteOficial,
    boolean destaque,
    int ordem,
    boolean ativo
) {}
```

### Mappers
- Classes simples com métodos estáticos ou Spring `@Component`
- Nunca usar MapStruct (adiciona complexidade desnecessária)
- Conversão manual e explícita

```java
@Component
public class FabricanteMapper {

    public FabricanteResponse toResponse(Fabricante entity) {
        return new FabricanteResponse(
            entity.getId(),
            entity.getNome(),
            entity.getSlug(),
            entity.getDescricao(),
            entity.getSiteOficial(),
            entity.isDestaque(),
            entity.getOrdem(),
            entity.isAtivo()
        );
    }

    public Fabricante toEntity(FabricanteRequest request) {
        return Fabricante.builder()
            .slug(request.slug())
            .nome(request.nome())
            .descricao(request.descricao())
            .descricaoCurta(request.descricaoCurta())
            .logoUrl(request.logoUrl())
            .siteOficial(request.siteOficial())
            .destaque(request.destaque())
            .ordem(request.ordem())
            .build();
    }
}
```

---

## Endpoints da API

### Padrão de URLs
```
GET    /fabricantes              ← lista todos ativos
GET    /fabricantes/{slug}       ← busca por slug
POST   /fabricantes              ← cria (protegido)
PUT    /fabricantes/{id}         ← atualiza (protegido)
DELETE /fabricantes/{id}         ← desativa (soft delete) (protegido)
```

### Paginação
Endpoints de listagem com muitos registros usam `Page` do Spring:
```
GET /produtos?page=0&size=12&categoria=seguranca&fabricante=ibm
```

Resposta paginada segue o padrão Spring `Page<T>`:
```json
{
  "content": [...],
  "totalPages": 3,
  "totalElements": 28,
  "size": 12,
  "number": 0,
  "first": true,
  "last": false
}
```

### Endpoints públicos (sem autenticação)
```
GET /fabricantes
GET /fabricantes/{slug}
GET /solucoes
GET /solucoes/{slug}
GET /produtos
GET /produtos/{slug}
GET /servicos
GET /conteudos
GET /conteudos/{slug}
GET /cases
GET /paginas-hero/{pagina}
GET /ctas/{pagina}
GET /config-footer
GET /config-blog
GET /contato-info
GET /faq
POST /leads                      ← captura de lead (público)
```

### Endpoints protegidos (requer JWT)
```
POST/PUT/DELETE em todos os recursos acima
GET /leads                       ← listagem para o admin
GET /admins-autorizados
POST /admins-autorizados
DELETE /admins-autorizados/{id}
```

---

## Banco de Dados

### Conexão (Neon PostgreSQL)
```properties
spring.datasource.url=jdbc:postgresql://ep-delicate-bread-actvkpjw.sa-east-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Estratégia de migração
- `spring.jpa.hibernate.ddl-auto=validate` em produção
- `spring.jpa.hibernate.ddl-auto=update` em desenvolvimento
- Flyway para migrações controladas (adicionar futuramente)

### Campos JSONB
Campos como `diferenciais`, `features`, `conteudo`, etc. são armazenados como JSONB no PostgreSQL.
Usar `@JdbcTypeCode(SqlTypes.JSON)` do Hibernate 6:

```java
@JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "jsonb")
private List<DiferencialItem> diferenciais;
```

### Soft Delete
Nunca deletar registros diretamente. Sempre usar `ativo = false`.
Repositories filtram automaticamente por `ativo = true`.

---

## Segurança

### Estratégia de autenticação
- **Leitura pública:** sem autenticação
- **Escrita:** JWT gerado após login com Microsoft Entra ID (OAuth2/OIDC)
- **Allowlist:** tabela `admins_autorizados` — só emails cadastrados recebem JWT

### CORS
Origens permitidas:
- `http://localhost:3000` (dev frontend)
- `https://infodive.com.br` (produção)
- `https://admin.infodive.com.br` (admin)

### Headers de segurança
Sempre incluir no SecurityConfig:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `Strict-Transport-Security`

---

## Tratamento de Erros

### GlobalExceptionHandler
Todas as exceções são tratadas centralmente e retornam o mesmo formato:

```json
{
  "timestamp": "2026-06-22T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Fabricante não encontrado: ibm-inexistente",
  "path": "/fabricantes/ibm-inexistente"
}
```

### Exceções customizadas
- `ResourceNotFoundException` → HTTP 404
- `BusinessException` → HTTP 400
- `ValidationException` → HTTP 422
- Qualquer `Exception` não tratada → HTTP 500

---

## Variáveis de Ambiente

Nunca commitar credenciais. Usar variáveis de ambiente:

```bash
DB_PASSWORD=sua_senha_neon
JWT_SECRET=chave_secreta_jwt_minimo_256bits
ALLOWED_ORIGINS=http://localhost:3000,https://infodive.com.br
```

Em desenvolvimento, criar `.env` na raiz (já no `.gitignore`).

---

## Ordem de Implementação

Implementar nesta ordem — do mais simples ao mais complexo:

1. **Setup** — `application.properties`, `SecurityConfig` básico, `GlobalExceptionHandler`
2. **Fabricantes** — entidade mais simples, boa para validar o setup
3. **Soluções** — sem relações complexas
4. **Produtos** — tem relação com Fabricante e Solucao
5. **Serviços** — tabela simples + relação N:N com Produtos
6. **Conteúdos** — campos JSONB
7. **Cases** — simples
8. **Leads** — só POST público + GET protegido
9. **Tabelas de configuração** — paginas_hero, ctas, secoes_home, etc.
10. **Segurança completa** — JWT + Microsoft Entra ID

---

## Regras Importantes

- **NUNCA** retornar a entidade diretamente no controller — sempre usar DTOs
- **NUNCA** colocar lógica no controller — só no service
- **NUNCA** fazer `findAll()` sem filtro `ativo = true`
- **NUNCA** deletar registros — sempre soft delete (`ativo = false`)
- **SEMPRE** usar `@Transactional(readOnly = true)` em métodos de leitura
- **SEMPRE** validar entrada com Bean Validation nos requests
- **SEMPRE** tratar exceções no `GlobalExceptionHandler`
- **SEMPRE** usar UUID como ID — nunca Long auto-increment
- Campos `created_at` e `updated_at` são gerenciados automaticamente pelo Hibernate
- Slugs são únicos e imutáveis após criação

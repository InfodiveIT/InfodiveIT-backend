package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.PoliticaRequest;
import br.com.infodive.infodive_api.dto.response.PoliticaResponse;
import br.com.infodive.infodive_api.entity.Politica;
import br.com.infodive.infodive_api.exception.BusinessException;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.PoliticaMapper;
import br.com.infodive.infodive_api.repository.PoliticaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PoliticaService {

    private final PoliticaRepository repository;
    private final PoliticaMapper mapper;

    @Cacheable(value = "politicas")
    @Transactional
    public List<PoliticaResponse> findAll() {
        ensureDefaultPoliciesExist();
        return repository.findAllByOrderByTituloAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Cacheable(value = "politica", key = "#slug")
    @Transactional
    public PoliticaResponse findBySlug(String slug) {
        ensureDefaultPoliciesExist();
        return repository.findBySlugAndAtivoTrue(slug)
                .or(() -> repository.findBySlug(slug))
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Politica não encontrada: " + slug));
    }

    @Transactional
    public PoliticaResponse findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Politica não encontrada: " + id));
    }

    @CacheEvict(value = {"politicas", "politica"}, allEntries = true)
    @Transactional
    public PoliticaResponse create(PoliticaRequest request) {
        repository.findBySlug(request.slug()).ifPresent(p -> {
            throw new BusinessException("Já existe uma política com o slug: " + request.slug());
        });

        Politica entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @CacheEvict(value = {"politicas", "politica"}, allEntries = true)
    @Transactional
    public PoliticaResponse update(UUID id, PoliticaRequest request) {
        Politica entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Politica não encontrada: " + id));

        if (!entity.getSlug().equalsIgnoreCase(request.slug())) {
            repository.findBySlug(request.slug()).ifPresent(p -> {
                throw new BusinessException("Já existe outra política cadastrada com o slug: " + request.slug());
            });
        }

        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @CacheEvict(value = {"politicas", "politica"}, allEntries = true)
    @Transactional
    public void delete(UUID id) {
        Politica entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Politica não encontrada: " + id));
        repository.delete(entity);
    }

    /** Garante que as 3 políticas padrão do site existam no banco com texto inicial. */
    @Transactional
    public void ensureDefaultPoliciesExist() {
        if (repository.findBySlug("politica-de-privacidade").isEmpty()) {
            repository.save(Politica.builder()
                    .slug("politica-de-privacidade")
                    .titulo("Política de Privacidade")
                    .subtitulo("Termos e Condições")
                    .ultimaAtualizacao("28 de Julho de 2026")
                    .ativo(true)
                    .conteudo("""
                            Na **Infodive IT**, privacidade e segurança são prioridades e nos comprometemos com a transparência do tratamento de dados pessoais dos nossos usuários e clientes.

                            ### 1. Quais dados coletamos e com qual finalidade?
                            Nosso site coleta e utiliza dados pessoais para viabilizar a prestação de serviços e aprimorar a experiência de navegação:
                            - **Dados de contato:** Nome, e-mail, telefone e empresa fornecidos voluntariamente através de formulários.
                            - **Dados de navegação:** Cookies e endereço IP coletados automatizadamente para análise de performance e segurança.

                            ### 2. Consentimento e LGPD
                            Tratamos dados conforme as bases legais da Lei Geral de Proteção de Dados (LGPD - Lei nº 13.709/2018).

                            ### 3. Direitos dos Titulares
                            Você pode a qualquer momento confirmar a existência, acessar, corrigir ou solicitar a eliminação dos seus dados pessoais.
                            """)
                    .build());
        }

        if (repository.findBySlug("termos-de-uso").isEmpty()) {
            repository.save(Politica.builder()
                    .slug("termos-de-uso")
                    .titulo("Termos de Uso")
                    .subtitulo("Termos e Condições")
                    .ultimaAtualizacao("28 de Julho de 2026")
                    .ativo(true)
                    .conteudo("""
                            Bem-vindo ao portal da **Infodive IT**. Ao acessar e utilizar este website, você concorda em cumprir os presentes termos e condições.

                            ### 1. Aceitação dos Termos
                            O uso continuado deste site confirma sua aceitação tácita e integral destes termos.

                            ### 2. Propriedade Intelectual
                            Todo o conteúdo deste site (textos, logotipos, imagens, códigos e marcas) é de propriedade exclusiva da Infodive IT e parceiros homologados.

                            ### 3. Limitações de Responsabilidade
                            Os materiais são fornecidos 'como estão', sem garantias implícitas ou explícitas além das formalizadas contratualmente.
                            """)
                    .build());
        }

        if (repository.findBySlug("politica-de-cookies").isEmpty()) {
            repository.save(Politica.builder()
                    .slug("politica-de-cookies")
                    .titulo("Política de Cookies")
                    .subtitulo("Privacidade & Preferências de Navegação")
                    .ultimaAtualizacao("28 de Julho de 2026")
                    .ativo(true)
                    .conteudo("""
                            A **Infodive IT** utiliza cookies e tecnologias semelhantes para aprimorar a sua experiência de navegação, analisar o desempenho de nossas páginas e garantir a segurança das sessões.

                            ### 1. O que são Cookies?
                            Cookies são pequenos arquivos de texto armazenados no seu navegador ou dispositivo ao visitar um site. Eles ajudam a reconhecer seu dispositivo em acessos futuros.

                            ### 2. Tipos de Cookies Utilizados
                            - **Cookies Essenciais:** Necessários para o funcionamento básico e segurança do portal.
                            - **Cookies Analíticos:** Utilizados para contabilizar acessos e mapear o tráfego de navegação anonimamente.
                            - **Cookies de Funcionalidade:** Guardam preferências de usuário como idioma e formulários pré-preenchidos.

                            ### 3. Gerenciamento de Cookies
                            Você pode alterar as permissões de cookies ou bloqueá-los a qualquer momento através das configurações do seu navegador (Google Chrome, Firefox, Safari, Edge).
                            """)
                    .build());
        }
    }
}

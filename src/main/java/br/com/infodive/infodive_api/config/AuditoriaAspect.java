package br.com.infodive.infodive_api.config;

import br.com.infodive.infodive_api.service.LogAuditoriaService;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class AuditoriaAspect {

    private final LogAuditoriaService logAuditoriaService;

    private static final Map<String, String> MODULO_NAMES = Map.ofEntries(
            Map.entry("ProdutoService", "Produtos"),
            Map.entry("CategoriaService", "Categorias"),
            Map.entry("FabricanteService", "Fabricantes"),
            Map.entry("SolucaoService", "Soluções"),
            Map.entry("ServicoService", "Serviços"),
            Map.entry("ConteudoService", "Conteúdos / Blog"),
            Map.entry("CaseService", "Cases de Sucesso"),
            Map.entry("HeroHomeCarouselService", "Carrossel Hero"),
            Map.entry("CtaService", "CTAs de Conversão"),
            Map.entry("FaqService", "Perguntas Frequentes (FAQ)"),
            Map.entry("PaginaHeroService", "Hero das Páginas"),
            Map.entry("HomeSolucoesBentoService", "Home Soluções Bento Grid"),
            Map.entry("HomeProblemasService", "Home Problemas & Soluções"),
            Map.entry("HomeSegurancaMarqueeService", "Home Segurança Marquee"),
            Map.entry("HomeTrustStatsService", "Home Estatísticas de Confiança"),
            Map.entry("ConfigBlogService", "Configurações do Blog"),
            Map.entry("ConfigFooterService", "Configurações do Rodapé"),
            Map.entry("ContatoInfoService", "Informações de Contato"),
            Map.entry("SecaoHomeService", "Seções da Home"),
            Map.entry("ServicosEtapasService", "Serviços - Etapas"),
            Map.entry("ServicosMetodologiaService", "Serviços - Metodologia"),
            Map.entry("SobreCulturaService", "Sobre Nós - Cultura"),
            Map.entry("SobreNumerosService", "Sobre Nós - Números"),
            Map.entry("SobreTimelineService", "Sobre Nós - Linha do Tempo"),
            Map.entry("SobreValoresService", "Sobre Nós - Valores")
    );

    @AfterReturning(
        value = "execution(* br.com.infodive.infodive_api.service.*.create*(..)) || execution(* br.com.infodive.infodive_api.service.*.save*(..))",
        returning = "result"
    )
    public void auditCreate(JoinPoint joinPoint, Object result) {
        String serviceClassName = joinPoint.getTarget().getClass().getSimpleName();
        if (isIgnoredService(serviceClassName)) return;

        String recurso = MODULO_NAMES.getOrDefault(serviceClassName, serviceClassName.replace("Service", ""));
        String entityId = extractIdOrName(result);
        String detalhes = "Criou novo registro em " + recurso + (entityId != null ? " (" + entityId + ")" : "");

        logAuditoriaService.registrar("CRIACAO", recurso, entityId, detalhes);
    }

    @AfterReturning(
        value = "execution(* br.com.infodive.infodive_api.service.*.update*(..))",
        returning = "result"
    )
    public void auditUpdate(JoinPoint joinPoint, Object result) {
        String serviceClassName = joinPoint.getTarget().getClass().getSimpleName();
        if (isIgnoredService(serviceClassName)) return;

        String recurso = MODULO_NAMES.getOrDefault(serviceClassName, serviceClassName.replace("Service", ""));
        String entityId = extractIdOrName(result);
        String detalhes = "Atualizou registro em " + recurso + (entityId != null ? " (" + entityId + ")" : "");

        logAuditoriaService.registrar("ATUALIZACAO", recurso, entityId, detalhes);
    }

    @AfterReturning(
        value = "execution(* br.com.infodive.infodive_api.service.*.delete*(..))"
    )
    public void auditDelete(JoinPoint joinPoint) {
        String serviceClassName = joinPoint.getTarget().getClass().getSimpleName();
        if (isIgnoredService(serviceClassName)) return;

        String recurso = MODULO_NAMES.getOrDefault(serviceClassName, serviceClassName.replace("Service", ""));
        Object[] args = joinPoint.getArgs();
        String idStr = args.length > 0 && args[0] != null ? args[0].toString() : null;
        String detalhes = "Excluiu registro em " + recurso + (idStr != null ? " (ID: " + idStr + ")" : "");

        logAuditoriaService.registrar("EXCLUSAO", recurso, idStr, detalhes);
    }

    private boolean isIgnoredService(String serviceClassName) {
        return "LogAuditoriaService".equals(serviceClassName) ||
               "MicrosoftEntraIdService".equals(serviceClassName) ||
               "JwtService".equals(serviceClassName) ||
               "SupabaseStorageService".equals(serviceClassName) ||
               "AdminAutorizadoService".equals(serviceClassName);
    }

    private String extractIdOrName(Object object) {
        if (object == null) return null;
        try {
            // Tenta obter getId()
            Method getIdMethod = null;
            try {
                getIdMethod = object.getClass().getMethod("id");
            } catch (NoSuchMethodException e) {
                try {
                    getIdMethod = object.getClass().getMethod("getId");
                } catch (NoSuchMethodException ignored) {}
            }
            if (getIdMethod != null) {
                Object val = getIdMethod.invoke(object);
                if (val != null) return val.toString();
            }

            // Tenta obter getNome() ou nome() ou getTitulo() ou titulo() ou getSlug()
            for (String fieldName : new String[]{"nome", "getNome", "titulo", "getTitulo", "slug", "getSlug"}) {
                try {
                    Method m = object.getClass().getMethod(fieldName);
                    Object val = m.invoke(object);
                    if (val != null) return val.toString();
                } catch (NoSuchMethodException ignored) {}
            }
        } catch (Exception e) {
            log.trace("Não foi possível extrair ID ou nome do objeto de resultado: {}", e.getMessage());
        }
        return null;
    }
}

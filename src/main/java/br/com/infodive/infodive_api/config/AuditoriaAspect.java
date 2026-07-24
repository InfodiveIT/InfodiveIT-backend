package br.com.infodive.infodive_api.config;

import br.com.infodive.infodive_api.service.LogAuditoriaService;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditoriaAspect {

    private final LogAuditoriaService logAuditoriaService;

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

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
        String itemLabel = extractHumanLabel(result, joinPoint.getArgs());
        String entityId = extractUuid(result, joinPoint.getArgs());

        String detalhes = itemLabel != null 
                ? "Criou " + recurso + ": '" + itemLabel + "'"
                : "Criou novo registro em " + recurso;

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
        String itemLabel = extractHumanLabel(result, joinPoint.getArgs());
        String entityId = extractUuid(result, joinPoint.getArgs());

        String detalhes = itemLabel != null 
                ? "Atualizou " + recurso + ": '" + itemLabel + "'"
                : "Atualizou registro em " + recurso;

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
        String itemLabel = extractHumanLabel(null, args);

        String detalhes = itemLabel != null 
                ? "Excluiu " + recurso + ": '" + itemLabel + "'"
                : "Excluiu registro em " + recurso + (idStr != null ? " (ID: " + idStr + ")" : "");

        logAuditoriaService.registrar("EXCLUSAO", recurso, idStr, detalhes);
    }

    private boolean isIgnoredService(String serviceClassName) {
        return "LogAuditoriaService".equals(serviceClassName) ||
               "MicrosoftEntraIdService".equals(serviceClassName) ||
               "JwtService".equals(serviceClassName) ||
               "SupabaseStorageService".equals(serviceClassName) ||
               "AdminAutorizadoService".equals(serviceClassName);
    }

    private String extractHumanLabel(Object resultObj, Object[] args) {
        String label = searchFields(resultObj);
        if (label != null) return label;

        if (args != null) {
            for (Object arg : args) {
                label = searchFields(arg);
                if (label != null) return label;
            }
        }
        return null;
    }

    private String searchFields(Object obj) {
        if (obj == null) return null;
        String[] candidateMethods = {
            "pagina", "getPagina",
            "nome", "getNome",
            "titulo", "getTitulo",
            "headline", "getHeadline",
            "eyebrow", "getEyebrow",
            "pergunta", "getPergunta",
            "secao", "getSecao",
            "empresaNome", "getEmpresaNome",
            "empresa", "getEmpresa",
            "email", "getEmail",
            "slug", "getSlug"
        };

        for (String methodName : candidateMethods) {
            try {
                Method m = obj.getClass().getMethod(methodName);
                Object val = m.invoke(obj);
                if (val != null) {
                    String str = val.toString().trim();
                    if (!str.isBlank() && !UUID_PATTERN.matcher(str).matches()) {
                        return str;
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String extractUuid(Object resultObj, Object[] args) {
        if (resultObj != null) {
            String id = searchIdMethod(resultObj);
            if (id != null) return id;
        }
        if (args != null) {
            for (Object arg : args) {
                if (arg != null && UUID_PATTERN.matcher(arg.toString()).matches()) {
                    return arg.toString();
                }
                String id = searchIdMethod(arg);
                if (id != null) return id;
            }
        }
        return null;
    }

    private String searchIdMethod(Object obj) {
        if (obj == null) return null;
        for (String mName : new String[]{"id", "getId"}) {
            try {
                Method m = obj.getClass().getMethod(mName);
                Object val = m.invoke(obj);
                if (val != null) return val.toString();
            } catch (Exception ignored) {}
        }
        return null;
    }
}

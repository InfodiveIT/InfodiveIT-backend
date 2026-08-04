package br.com.infodive.infodive_api.config;

import br.com.infodive.infodive_api.service.SocialMediaSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SocialPostScheduler {

    private final SocialMediaSyncService socialMediaSyncService;

    /**
     * Executa a sincronização de redes sociais a cada 6 horas (ou conforme propriedade cron).
     */
    @Scheduled(cron = "${social.sync.cron:0 0 */6 * * *}")
    public void runScheduledSync() {
        log.info("SocialPostScheduler: executando sincronização periódica das redes sociais...");
        try {
            socialMediaSyncService.syncAll();
        } catch (Exception e) {
            log.error("SocialPostScheduler: falha ao sincronizar redes sociais: {}", e.getMessage(), e);
        }
    }
}

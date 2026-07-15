package br.com.infodive.infodive_api.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SupabaseStorageService {

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:images}")
    private String supabaseBucket;

    public boolean isConfigured() {
        return supabaseUrl != null && !supabaseUrl.trim().isEmpty() &&
               supabaseKey != null && !supabaseKey.trim().isEmpty();
    }

    public String uploadFile(byte[] fileBytes, String filename, String contentType) throws Exception {
        if (!isConfigured()) {
            throw new IllegalStateException("Configurações do Supabase Storage (supabase.url, supabase.key) não foram fornecidas.");
        }

        // Remover barra final da URL base, se presente
        String baseUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;

        // URL para upload no Supabase Storage: POST /storage/v1/object/{bucket}/{filename}
        String uploadEndpoint = String.format("%s/storage/v1/object/%s/%s", baseUrl, supabaseBucket, filename);
        
        log.info("Iniciando upload de imagem para o Supabase Storage: {} (Bucket: {})", filename, supabaseBucket);

        URL url = URI.create(uploadEndpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(15000);

        conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
        conn.setRequestProperty("apiKey", supabaseKey);
        conn.setRequestProperty("Content-Type", contentType != null ? contentType : "application/octet-stream");
        conn.setRequestProperty("x-upsert", "true");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(fileBytes);
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", baseUrl, supabaseBucket, filename);
            log.info("Upload realizado com sucesso no Supabase Storage: {}", publicUrl);
            return publicUrl;
        } else {
            String errorMsg = "";
            try (var is = conn.getErrorStream()) {
                if (is != null) {
                    errorMsg = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            log.error("Erro no upload para Supabase Storage. HTTP Status: {}, Resposta: {}", responseCode, errorMsg);
            throw new RuntimeException("Falha ao enviar arquivo para o Supabase Storage. HTTP Status: " + responseCode + " - " + errorMsg);
        }
    }

    public void deleteFile(String fileUrl) {
        if (!isConfigured() || fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }

        String marker = "/storage/v1/object/public/" + supabaseBucket + "/";
        if (!fileUrl.contains(marker)) {
            return;
        }

        String remotePath = fileUrl.substring(fileUrl.indexOf(marker) + marker.length());
        String baseUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;
        String deleteEndpoint = String.format("%s/storage/v1/object/%s/%s", baseUrl, supabaseBucket, remotePath);

        try {
            log.info("Excluindo arquivo antigo do Supabase Storage: {}", remotePath);
            URL url = URI.create(deleteEndpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
            conn.setRequestProperty("apiKey", supabaseKey);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                log.info("Arquivo excluído com sucesso do Supabase Storage: {}", remotePath);
            } else {
                log.warn("Tentativa de exclusão no Supabase Storage retornou código HTTP: {}", responseCode);
            }
        } catch (Exception e) {
            log.error("Falha ao excluir arquivo antigo do Supabase Storage ({}): {}", fileUrl, e.getMessage());
        }
    }
}

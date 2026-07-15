package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.service.SupabaseStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads";

    private final SupabaseStorageService supabaseStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "O arquivo está vazio"));
        }

        String contentType = file.getContentType();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = getFileExtension(originalFilename);

        // Validar tipos de arquivos permitidos (SVG, PNG, WEBP, JPG, JPEG)
        boolean isValidType = (contentType != null && (
                contentType.equals("image/svg+xml") ||
                contentType.equals("image/png") ||
                contentType.equals("image/webp") ||
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg")
        )) || (
                extension.equals("svg") ||
                extension.equals("png") ||
                extension.equals("webp") ||
                extension.equals("jpg") ||
                extension.equals("jpeg")
        );

        if (!isValidType) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(Map.of("error", "Apenas imagens SVG, PNG, WEBP, JPG e JPEG são permitidas"));
        }

        String uniqueFileName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);

        // Se o Supabase Storage estiver configurado, realizar upload para o Supabase
        if (supabaseStorageService.isConfigured()) {
            try {
                String publicUrl = supabaseStorageService.uploadFile(file.getBytes(), uniqueFileName, contentType);
                return ResponseEntity.ok(Map.of("url", publicUrl));
            } catch (Exception e) {
                log.error("Erro ao enviar imagem para o Supabase Storage: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Erro ao fazer upload para o Supabase Storage: " + e.getMessage()));
            }
        }

        // Fallback local se as variáveis do Supabase não estiverem preenchidas
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(uniqueFileName)
                    .toUriString();

            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (IOException e) {
            log.error("Erro ao salvar arquivo no disco local: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao salvar o arquivo: " + e.getMessage()));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}


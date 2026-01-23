package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "Gestion des Fichiers", description = "Upload et téléchargement de fichiers")
@SecurityRequirement(name = "Bearer Authentication")
public class FileController {

    private final FileStorageService fileStorageService;

    private static final String[] ALLOWED_EXTENSIONS = {"pdf", "doc", "docx", "ppt", "pptx", "mp4", "avi", "mov", "jpg", "jpeg", "png"};
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    @PostMapping("/upload/support")
    @Operation(summary = "Upload un support de cours (PDF, PPT, VIDEO)")
    public ResponseEntity<Map<String, String>> uploadSupport(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        String fileName = fileStorageService.storeFile(file, "supports");

        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("fileUrl", "/files/download/" + fileName);
        response.put("fileType", fileStorageService.getFileExtension(file.getOriginalFilename()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/devoir")
    @Operation(summary = "Upload une soumission de devoir")
    public ResponseEntity<Map<String, String>> uploadDevoir(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        String fileName = fileStorageService.storeFile(file, "devoirs");

        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("fileUrl", "/files/download/" + fileName);
        response.put("fileType", fileStorageService.getFileExtension(file.getOriginalFilename()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{category}/{filename:.+}")
    @Operation(summary = "Télécharger un fichier")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String category,
            @PathVariable String filename) {
        try {
            String fullPath = category + "/" + filename;
            Path filePath = fileStorageService.loadFile(fullPath);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Fichier non trouvé: " + filename);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors du téléchargement du fichier: " + filename, ex);
        }
    }

    @DeleteMapping("/{category}/{filename:.+}")
    @Operation(summary = "Supprimer un fichier")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String category,
            @PathVariable String filename) {
        String fullPath = category + "/" + filename;
        fileStorageService.deleteFile(fullPath);
        return ResponseEntity.noContent().build();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Le fichier est trop volumineux. Taille maximale: 50MB");
        }

        String filename = file.getOriginalFilename();
        if (!fileStorageService.isValidFileType(filename, ALLOWED_EXTENSIONS)) {
            throw new RuntimeException("Type de fichier non autorisé. Extensions autorisées: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private String determineContentType(String filename) {
        String extension = fileStorageService.getFileExtension(filename);

        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/msword";
            case "ppt", "pptx" -> "application/vnd.ms-powerpoint";
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mov" -> "video/quicktime";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}

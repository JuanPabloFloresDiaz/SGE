package com.example.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.example.api.dto.AuditLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Servicio para manejar el almacenamiento de archivos en MinIO (S3).
 * Los archivos se organizan en "carpetas" (prefijos) por tipo de recurso.
 */
@Service
public class StorageService {

    private final S3Client s3Client;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public StorageService(S3Client s3Client, AuditProducer auditProducer, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Enum para definir los tipos de carpetas donde se guardarán los archivos.
     */
    public enum FileCategory {
        ACTIVIDADES("actividades"),
        ESTUDIANTES("estudiantes"),
        CURSOS("cursos"),
        ASIGNATURAS("asignaturas"),
        CLASES("clases"),
        UNIDADES("unidades"),
        TEMAS("temas"),
        EVALUACIONES("evaluaciones"),
        PROFESORES("profesores"),
        USUARIOS("usuarios"),
        MATERIALES("materiales"),
        OTROS("otros");

        private final String folderName;

        FileCategory(String folderName) {
            this.folderName = folderName;
        }

        public String getFolderName() {
            return folderName;
        }
    }

    /**
     * Almacena un archivo en la categoría especificada en MinIO.
     * 
     * @param file     El archivo a almacenar
     * @param category La categoría donde se guardará (define el prefijo)
     * @return El path relativo del archivo guardado (ej: "actividades/uuid.jpg")
     */
    public String storeFile(MultipartFile file, FileCategory category, HttpServletRequest httpRequest) {
        // Validar archivo
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        // Obtener extensión original
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("El nombre del archivo es inválido");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // Generar nombre único
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Construir la clave del objeto (key)
        String objectKey = category.getFolderName() + "/" + uniqueFileName;

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Audit Log
            logStorageAction("UPLOAD", objectKey, category.name(), httpRequest);

            return objectKey;

        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el archivo para subirlo", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error al subir el archivo a MinIO: " + objectKey, ex);
        }
    }

    /**
     * Elimina un archivo de MinIO.
     * 
     * @param relativePath El path relativo del archivo (ej: "actividades/uuid.jpg")
     * @return true si se eliminó exitosamente (o si no existía, S3 es idempotente
     *         en deletes,
     *         pero intentaremos verificar antes si queremos ser estrictos, aunque
     *         usualmente delete siempre retorna éxito si no hay error de red)
     */
    public boolean deleteFile(String relativePath, HttpServletRequest httpRequest) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        try {
            // Verificar si existe primero para retornar false si no existe (opcional, pero
            // mantiene comportamiento anterior)
            if (!fileExists(relativePath)) {
                return false;
            }

            DeleteObjectRequest deleteOb = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(relativePath)
                    .build();

            s3Client.deleteObject(deleteOb);

            // Audit Log
            logStorageAction("DELETE", relativePath, "UNKNOWN", httpRequest);

            return true;

        } catch (Exception ex) {
            throw new RuntimeException("Error al eliminar el archivo de MinIO: " + relativePath, ex);
        }
    }

    /**
     * Carga un archivo como recurso (InputStream).
     * 
     * @param relativePath El path relativo del archivo
     * @return InputStream del archivo
     */
    public InputStream loadFileAsResource(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            throw new RuntimeException("El path del archivo es inválido");
        }

        try {
            return s3Client.getObject(builder -> builder.bucket(bucketName).key(relativePath));
        } catch (NoSuchKeyException ex) {
            throw new RuntimeException("El archivo no existe: " + relativePath, ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error al descargar el archivo de MinIO: " + relativePath, ex);
        }
    }

    /**
     * Verifica si un archivo existe en MinIO.
     * 
     * @param relativePath El path relativo del archivo
     * @return true si el archivo existe
     */
    public boolean fileExists(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        try {
            HeadObjectRequest headOb = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(relativePath)
                    .build();

            s3Client.headObject(headOb);
            return true;
        } catch (NoSuchKeyException ex) {
            return false;
        } catch (Exception ex) {
            // Si hay otro error (ej: permisos), asumimos que no se puede acceder
            return false;
        }
    }

    private void logStorageAction(String action, String fileKey, String category, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request != null ? request.getRequestURI() : "UNKNOWN");
            log.setIpAddress(request != null ? request.getRemoteAddr() : "UNKNOWN");
            log.setDevice(request != null ? request.getHeader("User-Agent") : "UNKNOWN");
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("fileKey", fileKey);
            bodyMap.put("category", category);

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

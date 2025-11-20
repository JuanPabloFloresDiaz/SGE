package com.example.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para manejar el almacenamiento de archivos en el sistema de archivos local.
 * Los archivos se organizan en carpetas por tipo de recurso.
 */
@Service
public class StorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

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
     * Inicializa el directorio base de uploads si no existe.
     */
    private void initStorage() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento: " + uploadDir, ex);
        }
    }

    /**
     * Inicializa un subdirectorio específico para una categoría.
     */
    private Path initCategoryFolder(FileCategory category) {
        try {
            Path categoryPath = Paths.get(uploadDir, category.getFolderName())
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(categoryPath);
            return categoryPath;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "No se pudo crear el directorio de categoría: " + category.getFolderName(), ex);
        }
    }

    /**
     * Almacena un archivo en la categoría especificada.
     * 
     * @param file     El archivo a almacenar
     * @param category La categoría donde se guardará (define la carpeta)
     * @return El path relativo del archivo guardado (ej: "actividades/uuid.jpg")
     */
    public String storeFile(MultipartFile file, FileCategory category) {
        // Inicializar storage base
        initStorage();

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

        // Obtener/crear carpeta de categoría
        Path categoryFolder = initCategoryFolder(category);

        try {
            // Ruta completa donde se guardará
            Path targetLocation = categoryFolder.resolve(uniqueFileName);

            // Copiar archivo al disco
            Files.copy(file.getInputStream(), targetLocation);

            // Retornar path relativo (lo que se guarda en DB)
            return category.getFolderName() + "/" + uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Error al almacenar el archivo: " + uniqueFileName, ex);
        }
    }

    /**
     * Elimina un archivo del sistema de archivos.
     * 
     * @param relativePath El path relativo del archivo (ej: "actividades/uuid.jpg")
     * @return true si se eliminó exitosamente
     */
    public boolean deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        try {
            Path filePath = Paths.get(uploadDir, relativePath)
                    .toAbsolutePath()
                    .normalize();

            // Verificar que el archivo existe
            if (!Files.exists(filePath)) {
                return false;
            }

            // Eliminar archivo
            Files.delete(filePath);
            return true;

        } catch (IOException ex) {
            throw new RuntimeException("Error al eliminar el archivo: " + relativePath, ex);
        }
    }

    /**
     * Carga un archivo como recurso (útil para lectura interna).
     * 
     * @param relativePath El path relativo del archivo
     * @return El Path absoluto del archivo
     */
    public Path loadFileAsResource(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            throw new RuntimeException("El path del archivo es inválido");
        }

        return Paths.get(uploadDir, relativePath)
                .toAbsolutePath()
                .normalize();
    }

    /**
     * Verifica si un archivo existe.
     * 
     * @param relativePath El path relativo del archivo
     * @return true si el archivo existe
     */
    public boolean fileExists(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        Path filePath = Paths.get(uploadDir, relativePath)
                .toAbsolutePath()
                .normalize();

        return Files.exists(filePath);
    }
}
